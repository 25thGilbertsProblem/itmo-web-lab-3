package ru.ifmo.web.beans;

import ru.ifmo.web.models.Result;
import ru.ifmo.web.utils.DatabaseManager;
//import org.primefaces.PrimeFaces;

import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ResultHistoryBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ResultHistoryBean.class.getName());

    private List<Result> results;
    private String sessionId;


    public ResultHistoryBean() {
        this.results = new ArrayList<>();
        this.sessionId = getHttpSessionId();
        LOGGER.log(Level.INFO, "ResultHistoryBean created for session: {0}", sessionId);

        loadResultsFromDatabase();
    }

    private String getHttpSessionId() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null && context.getExternalContext() != null) {
            return context.getExternalContext().getSessionId(true);
        }
        return "unknown-session";
    }

    private void loadResultsFromDatabase() {
        try {
            List<Result> dbResults = DatabaseManager.getResultsBySession(sessionId);
            if (dbResults != null && !dbResults.isEmpty()) {
                this.results = new ArrayList<>(dbResults);
                LOGGER.log(Level.INFO, "Loaded {0} results from database", results.size());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading results from database", e);
        }
    }

    public void addResult(Result result) {
        if (result == null) {
            LOGGER.warning("Attempted to add null result");
            return;
        }

        if (result.getUserSession() == null || result.getUserSession().isEmpty()) {
            result.setUserSession(sessionId);
        }

        boolean saved = DatabaseManager.saveResult(result);

        if (saved) {
            results.add(result);
            LOGGER.log(Level.INFO, "Result added: {0}", result);
        } else {
            LOGGER.warning("Failed to save result to database");
        }
    }


    public void clearResults() {
        boolean cleared = DatabaseManager.clearResultsBySession(sessionId);

        if (cleared) {
            results.clear();

//            PrimeFaces.current().ajax().addCallbackParam("resultsJson", "[]");

            LOGGER.log(Level.INFO, "Results cleared for session: {0}", sessionId);
        } else {
            LOGGER.warning("Failed to clear results from database");
        }
    }


    public String getResultsAsJson() {
        if (results == null || results.isEmpty()) {
            LOGGER.info("getResultsAsJson: No results, returning empty array");
            return "[]";
        }

        LOGGER.log(Level.INFO, "getResultsAsJson: Generating JSON for {0} results", results.size());

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < results.size(); i++) {
            Result r = results.get(i);
            json.append("{");
            json.append("\"x\":").append(r.getX()).append(",");
            json.append("\"y\":").append(r.getY()).append(",");
            json.append("\"r\":").append(r.getR()).append(",");
            json.append("\"hit\":").append(r.isHit() ? "true" : "false");
            json.append("}");

            if (i < results.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");

        String result = json.toString();
        LOGGER.log(Level.INFO, "getResultsAsJson: Generated JSON with {0} characters for {1} results",
                new Object[]{result.length(), results.size()});

        return result;
    }

    public int getResultsCount() {
        return results.size();
    }

    public void setResultsAsJson(String ignored) {
        //
    }

    public boolean hasResults() {
        return !results.isEmpty();
    }


    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
