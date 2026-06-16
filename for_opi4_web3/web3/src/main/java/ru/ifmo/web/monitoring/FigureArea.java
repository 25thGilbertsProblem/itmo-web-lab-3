package ru.ifmo.web.monitoring;

import java.util.logging.Logger;

public class FigureArea implements FigureAreaMBean {
    private static final Logger LOGGER = Logger.getLogger(FigureArea.class.getName());

    private volatile double radius = 1.0;

    @Override
    public double getRadius() {
        return radius;
    }

    @Override
    public void setRadius(double radius) {
        if (radius > 0.0) {
            this.radius = radius;
            LOGGER.info("Figure area radius updated to " + radius);
        }
    }

    @Override
    public double getArea() {
        double r = radius;
        return (20.0 + Math.PI) * r * r / 16.0;
    }

    @Override
    public String getFormula() {
        return "S(r) = r² + r²/4 + πr²/16 = (20 + π)r² / 16";
    }
}
