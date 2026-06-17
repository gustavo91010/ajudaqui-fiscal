package com.ajudaqui.model;

import java.util.ArrayList;
import java.util.List;

public class ParseResult {
    private boolean success;
    private List<String> warnings = new ArrayList<>();
    private String error;
    private FiscalDocument data;

    public ParseResult() {}

    public static ParseResult success(FiscalDocument data) {
        ParseResult result = new ParseResult();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }

    public static ParseResult failure(String error) {
        ParseResult result = new ParseResult();
        result.setSuccess(false);
        result.setError(error);
        return result;
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public FiscalDocument getData() { return data; }
    public void setData(FiscalDocument data) { this.data = data; }
}
