package ru.ifmo.web.monitoring;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PointStatistics extends NotificationBroadcasterSupport implements PointStatisticsMBean {
    private static final Logger LOGGER = Logger.getLogger(PointStatistics.class.getName());
    private static final String NOTIFICATION_TYPE = "ru.ifmo.web.monitoring.three-misses";

    private final String sessionId;

    private long totalPoints;
    private long hitPoints;
    private long missPoints;
    private long currentMissStreak;
    private long sequenceNumber;
    private String lastNotificationMessage = "";

    public PointStatistics(String sessionId) {
        this.sessionId = sessionId;
    }

    public synchronized void recordAttempt(boolean hit) {
        totalPoints++;

        if (hit) {
            hitPoints++;
            currentMissStreak = 0;
            return;
        }

        missPoints++;
        currentMissStreak++;

        if (currentMissStreak >= 3) {
            String message = "Пользователь " + sessionId + " совершил 3 промаха подряд";
            Notification notification = new Notification(
                    NOTIFICATION_TYPE,
                    this,
                    ++sequenceNumber,
                    System.currentTimeMillis(),
                    message
            );
            notification.setUserData(sessionId);
            sendNotification(notification);
            lastNotificationMessage = message;
            LOGGER.log(Level.INFO, message);
            currentMissStreak = 0;
        }
    }

    @Override
    public synchronized long getTotalPoints() {
        return totalPoints;
    }

    @Override
    public synchronized long getHitPoints() {
        return hitPoints;
    }

    @Override
    public synchronized long getMissPoints() {
        return missPoints;
    }

    @Override
    public synchronized long getCurrentMissStreak() {
        return currentMissStreak;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public synchronized String getLastNotificationMessage() {
        return lastNotificationMessage;
    }

    @Override
    public synchronized double getHitRate() {
        return totalPoints == 0 ? 0.0 : (double) hitPoints / totalPoints;
    }

    @Override
    public synchronized void reset() {
        totalPoints = 0;
        hitPoints = 0;
        missPoints = 0;
        currentMissStreak = 0;
        lastNotificationMessage = "";
    }
}
