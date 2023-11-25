package ru.itmo.lab3;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.primefaces.PrimeFaces;
import ru.itmo.lab3.database.DAOFactory;

import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;

@Named
@ApplicationScoped
public class AreaCheckBean implements Serializable {
    private LinkedList<ResultBean> resultList;
    @PersistenceContext
    private EntityManager entityManager;

    public AreaCheckBean() {
        resultList = new LinkedList<>();
        try {
            resultList = new LinkedList<>(DAOFactory.getInstance().getResultDAO().getAllResults());
        } catch (SQLException ignored) {}
    }

    public void newResult(final double x, String y, final double r) {

        double numY = 0;

        if (!y.isEmpty()) {
            numY = Double.parseDouble(y);
        }
        final ResultBean currentResult = new ResultBean();
        final long startExec = System.nanoTime();
        final boolean result = checkArea(x, numY, r);
        final long endExec = System.nanoTime();
        final long executionTime = endExec - startExec;
        currentResult.setX(x);
        currentResult.setY(y);
        currentResult.setR(r);
        currentResult.setResult(result);
        currentResult.setExecutedAt(LocalDateTime.now());
        currentResult.setExecutionTime(executionTime);

        try {
            DAOFactory.getInstance().getResultDAO().addNewResult(currentResult);
        } catch (SQLException ignored) {}

        resultList.addFirst(currentResult);
        PrimeFaces.current().executeScript("drawPoint(" + x + ", " + numY + ", '" + result + "')");
    }


    public boolean checkSquare(double x, double y, double r) {
        return (x <= 0 && y <= 0 && Math.abs(x) <= r && Math.abs(y) <= r);
    }

    public boolean checkCircle(double x, double y, double r) {
        return (x >= 0 && y <= 0 && (Math.abs(x) + Math.abs(y) <= r / 2));
    }


    public boolean checkTriangle(double x, double y, double r) {
        return (x >= 0 && y >= 0 && x + 2 * y <= r);
    }

    public boolean checkArea(double x, double y, double r) {
        return checkCircle(x, y, r) || checkTriangle(x, y, r) || checkSquare(x, y, r);
    }

    @Named("resultList")
    public LinkedList<ResultBean> getResultList() {
        return resultList;
    }

    public void setResultList(LinkedList<ResultBean> resultList) {
        this.resultList = resultList;
    }

    public void drawPoints(LinkedList<ResultBean> resultList) {
        for (int i = 0; i < resultList.size(); i++) {
            for (ResultBean result : resultList) {
                FacesContext.getCurrentInstance().getPartialViewContext().getEvalScripts()
                        .add("drawShapesByR(" + result.getX() + "," + result.getY() + "," + result.getResult() + ");");
            }
        }
    }
}
