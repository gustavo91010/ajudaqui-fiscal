package com.ajudaqui.model;

import java.math.BigDecimal;

public class Payment {
    private String method;
    private BigDecimal amount;

    public Payment() {}

    public Payment(String method, BigDecimal amount) {
        this.method = method;
        this.amount = amount;
    }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
