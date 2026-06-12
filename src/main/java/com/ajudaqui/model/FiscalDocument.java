package com.ajudaqui.model;

import java.util.List;

public class FiscalDocument {
    private DocumentType documentType;
    private State state;
    private Issuer issuer;
    private DocumentInfo document;
    private Totals totals;
    private List<Payment> payments;
    private List<Item> items;

    public FiscalDocument() {}

    public FiscalDocument(DocumentType documentType, State state, Issuer issuer, DocumentInfo document, Totals totals, List<Payment> payments, List<Item> items) {
        this.documentType = documentType;
        this.state = state;
        this.issuer = issuer;
        this.document = document;
        this.totals = totals;
        this.payments = payments;
        this.items = items;
    }

    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    public Issuer getIssuer() { return issuer; }
    public void setIssuer(Issuer issuer) { this.issuer = issuer; }
    public DocumentInfo getDocument() { return document; }
    public void setDocument(DocumentInfo document) { this.document = document; }
    public Totals getTotals() { return totals; }
    public void setTotals(Totals totals) { this.totals = totals; }
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
}
