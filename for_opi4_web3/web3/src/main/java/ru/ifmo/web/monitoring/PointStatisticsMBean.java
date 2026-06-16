package ru.ifmo.web.monitoring;

public interface PointStatisticsMBean {
    long getTotalPoints();
    long getHitPoints();
    long getMissPoints();
    long getCurrentMissStreak();
    String getSessionId();
    String getLastNotificationMessage();
    double getHitRate();
    void reset();
}
