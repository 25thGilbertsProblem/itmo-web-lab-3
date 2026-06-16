package ru.ifmo.web.monitoring;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;

@WebListener
public class MonitoringBootstrapListener implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(MonitoringBootstrapListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("Initializing JMX monitoring");
        MonitoringRegistry.initialize();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Shutting down JMX monitoring");
        MonitoringRegistry.shutdown();
    }
}
