package ru.ifmo.web.utils;

public final class HitChecker {

    private HitChecker() {}

    public static boolean checkHit(double x, double y, double r) {

        if (x > 0 && y > 0) {

            if (x > r || y > r / 2.0) {
                return false;
            }

            double lineY = r / 2.0 - (r / 2.0) * x / r;
            lineY = r / 2.0 - x / 2.0;
            return y <= lineY;
        }


        if (x < 0 && y > 0) {
            return false;
        }

        if (x < 0 && y < 0) {
            double radius = r / 2.0;
            return x * x + y * y <= radius * radius;
        }

        if (x > 0 && y < 0) {
            return x <= r && y >= -r;
        }


        if (x == 0 && y == 0) {
            return true;
        }

        if (x == 0 && y > 0) {
            return y <= r / 2.0;
        }

        if (x > 0 && y == 0) {
            return x <= r;
        }

        if (x == 0 && y < 0) {
            return y >= -(r / 2.0);
        }

        if (x < 0 && y == 0) {
            return x >= -(r / 2.0);
        }

        return false;
    }
}
