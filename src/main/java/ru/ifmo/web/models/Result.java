package ru.ifmo.web.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.time.ZoneId;
import java.util.Date;

public class Result implements Serializable{

    private static final long serialVersionUID = 1L;

    private Long id;
    private double x;
    private double y;
    private double r;
    private boolean hit;
    private LocalDateTime currentTime;
    private long executionTime;
    private String userSession;

    public Result() {}

    public Result(double x, double y, double r, boolean hit,
                  LocalDateTime currentTime, long executionTime, String userSession) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.currentTime = currentTime;
        this.executionTime = executionTime;
        this.userSession = userSession;
    }

    public Result(Long id, double x, double y, double r, boolean hit,
                  LocalDateTime currentTime, long executionTime, String userSession) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.currentTime = currentTime;
        this.executionTime = executionTime;
        this.userSession = userSession;
    }

    public Date getCurrentTimeAsDate() {
        if (currentTime == null) {
            return null;
        }
        return Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant());
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public LocalDateTime getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(LocalDateTime currentTime) {
        this.currentTime = currentTime;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public String getUserSession() {
        return userSession;
    }

    public void setUserSession(String userSession) {
        this.userSession = userSession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return Double.compare(x, result.x) == 0 &&
                Double.compare(y, result.y) == 0 &&
                Double.compare(r, result.r) == 0 &&
                hit == result.hit &&
                executionTime == result.executionTime &&
                Objects.equals(id, result.id) &&
                Objects.equals(currentTime, result.currentTime) &&
                Objects.equals(userSession, result.userSession);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, x, y, r, hit, currentTime, executionTime, userSession);
    }

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", r=" + r +
                ", hit=" + hit +
                ", currentTime=" + currentTime +
                ", executionTime=" + executionTime +
                ", userSession='" + userSession + '\'' +
                '}';
    }
}
