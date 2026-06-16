package ru.ifmo.web.monitoring;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MonitoringRegistry {
    private static final Logger LOGGER = Logger.getLogger(MonitoringRegistry.class.getName());
    private static final String DOMAIN = "ru.ifmo.web";

    private static final MBeanServer MBEAN_SERVER = ManagementFactory.getPlatformMBeanServer();
    private static final FigureArea FIGURE_AREA = new FigureArea();
    private static final ObjectName FIGURE_AREA_NAME = objectName("type=FigureArea");
    private static final Map<String, PointStatistics> STATISTICS = new ConcurrentHashMap<>();
    private static final Map<String, ObjectName> STATISTICS_NAMES = new ConcurrentHashMap<>();

    private MonitoringRegistry() {
    }

    public static void initialize() {
        registerFigureArea();
    }

    public static FigureAreaMBean getFigureAreaMBean() {
        return FIGURE_AREA;
    }

    public static void updateRadius(double radius) {
        FIGURE_AREA.setRadius(radius);
    }

    public static PointStatisticsMBean getOrCreateStatistics(String sessionId) {
        String id = normalizeSessionId(sessionId);
        PointStatistics bean = STATISTICS.computeIfAbsent(id, PointStatistics::new);
        registerStatisticsIfNeeded(id, bean);
        return bean;
    }

    public static void recordAttempt(String sessionId, boolean hit) {
        PointStatistics bean = (PointStatistics) getOrCreateStatistics(sessionId);
        bean.recordAttempt(hit);
    }

    public static void resetStatistics(String sessionId) {
        PointStatistics bean = STATISTICS.get(normalizeSessionId(sessionId));
        if (bean != null) {
            bean.reset();
        }
    }

    public static void shutdown() {
        unregisterStatistics();
        unregisterFigureArea();
        STATISTICS.clear();
    }

    private static void registerFigureArea() {
        try {
            if (!MBEAN_SERVER.isRegistered(FIGURE_AREA_NAME)) {
                MBEAN_SERVER.registerMBean(FIGURE_AREA, FIGURE_AREA_NAME);
                LOGGER.info("FigureArea MBean registered: " + FIGURE_AREA_NAME);
            }
        } catch (InstanceAlreadyExistsException e) {
            LOGGER.log(Level.FINE, "FigureArea MBean already registered", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to register FigureArea MBean", e);
        }
    }

    private static void unregisterFigureArea() {
        try {
            if (MBEAN_SERVER.isRegistered(FIGURE_AREA_NAME)) {
                MBEAN_SERVER.unregisterMBean(FIGURE_AREA_NAME);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to unregister FigureArea MBean", e);
        }
    }

    private static void registerStatisticsIfNeeded(String sessionId, PointStatistics bean) {
        ObjectName name = STATISTICS_NAMES.computeIfAbsent(sessionId, key -> objectName("type=PointStatistics,session=" + ObjectName.quote(key)));
        try {
            if (!MBEAN_SERVER.isRegistered(name)) {
                MBEAN_SERVER.registerMBean(bean, name);
                LOGGER.info("PointStatistics MBean registered: " + name);
            }
        } catch (InstanceAlreadyExistsException e) {
            LOGGER.log(Level.FINE, "PointStatistics MBean already registered", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to register PointStatistics MBean for session " + sessionId, e);
        }
    }

    private static void unregisterStatistics() {
        for (ObjectName name : STATISTICS_NAMES.values()) {
            try {
                if (MBEAN_SERVER.isRegistered(name)) {
                    MBEAN_SERVER.unregisterMBean(name);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to unregister PointStatistics MBean: " + name, e);
            }
        }
        STATISTICS_NAMES.clear();
    }

    private static ObjectName objectName(String suffix) {
        try {
            return new ObjectName(DOMAIN + ":" + suffix);
        } catch (MalformedObjectNameException e) {
            throw new IllegalStateException("Invalid ObjectName: " + DOMAIN + ":" + suffix, e);
        }
    }

    private static String normalizeSessionId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return "unknown-session";
        }
        return sessionId;
    }
}
