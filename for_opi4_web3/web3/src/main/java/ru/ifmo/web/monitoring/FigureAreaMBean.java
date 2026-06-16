package ru.ifmo.web.monitoring;

public interface FigureAreaMBean {
    double getRadius();
    void setRadius(double radius);
    double getArea();
    String getFormula();
}
