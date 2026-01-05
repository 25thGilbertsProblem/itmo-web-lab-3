package ru.ifmo.web.beans;

import ru.ifmo.web.models.Result;
import ru.ifmo.web.utils.HitChecker;
//import org.primefaces.PrimeFaces;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PointBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(PointBean.class.getName());

    private double x;
    private double y;
    private double r;


    private ResultHistoryBean resultHistoryBean;


    public PointBean() {
        LOGGER.info("PointBean created");
    }


    public void checkPoint() {
        LOGGER.log(Level.INFO, "Checking point: x={0}, y={1}, r={2}",
                new Object[]{x, y, r});

        if (!validateInput()) {
            return;
        }

        long startTime = System.nanoTime();

        boolean hit = HitChecker.checkHit(x, y, r);

        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;

        Result result = new Result(
                x, y, r, hit,
                LocalDateTime.now(),
                executionTime,
                resultHistoryBean.getSessionId()
        );

        resultHistoryBean.addResult(result);


        String message = hit
                ? String.format("Точка (%.2f, %.2f) попала в область!", x, y)
                : String.format("Точка (%.2f, %.2f) не попала в область.", x, y);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Результат", message));

        LOGGER.log(Level.INFO, "Check result: hit={0}, execution time={1}ns",
                new Object[]{hit, executionTime});
    }


    private boolean validateInput() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (x < -5 || x > 5) {
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Ошибка", "X должен быть в диапазоне [-5, 5]"));
            return false;
        }

        if (y < -3 || y > 3) {
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Ошибка", "Y должен быть в диапазоне [-3, 3]"));
            return false;
        }

        if (r < 0.1 || r > 3) {
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Ошибка", "R должен быть в диапазоне [0.1, 3]"));
            return false;
        }

        return true;
    }

    public void checkPointFromCanvas() {
        LOGGER.log(Level.INFO, "Checking point from canvas: x={0}, y={1}, r={2}",
                new Object[]{x, y, r});

        checkPoint();
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

    public ResultHistoryBean getResultHistoryBean() {
        return resultHistoryBean;
    }

    public void setResultHistoryBean(ResultHistoryBean resultHistoryBean) {
        this.resultHistoryBean = resultHistoryBean;
    }
}
