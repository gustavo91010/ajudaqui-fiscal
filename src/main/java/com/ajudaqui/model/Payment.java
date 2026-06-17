package com.ajudaqui.model;

public class Payment {
    private String method;
    private String amount;

    public Payment() {}

    public Payment(String method, String amount) {
        this.method = method;
        this.amount = amount;
    }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
}
