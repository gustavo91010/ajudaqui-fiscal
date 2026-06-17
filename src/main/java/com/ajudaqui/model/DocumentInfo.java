package com.ajudaqui.model;

public class DocumentInfo {
    private DocumentType type;
    private State state;
    private String accessKey;
    private String number;
    private String series;
    private String emissionDate;
    private String protocol;

    public DocumentInfo() {}

    public DocumentInfo(DocumentType type, State state, String accessKey, String number, String series, String emissionDate, String protocol) {
        this.type = type;
        this.state = state;
        this.accessKey = accessKey;
        this.number = number;
        this.series = series;
        this.emissionDate = emissionDate;
        this.protocol = protocol;
    }

    public DocumentType getType() { return type; }
    public void setType(DocumentType type) { this.type = type; }
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    public String getAccessKey() { return accessKey; }
    public void setAccessKey(String accessKey) { this.accessKey = accessKey; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public String getSeries() { return series; }
    public void setSeries(String series) { this.series = series; }
    public String getEmissionDate() { return emissionDate; }
    public void setEmissionDate(String emissionDate) { this.emissionDate = emissionDate; }
    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }
}
