package com.ajudaqui.model;

import java.math.BigDecimal;

public class Totals {
    private BigDecimal totalProducts;
    private BigDecimal totalInvoice;
    private BigDecimal discount;
    private BigDecimal totalPaid;
    private BigDecimal change;

    public Totals() {}

    public Totals(BigDecimal totalProducts, BigDecimal totalInvoice, BigDecimal discount, BigDecimal totalPaid, BigDecimal change) {
        this.totalProducts = totalProducts;
        this.totalInvoice = totalInvoice;
        this.discount = discount;
        this.totalPaid = totalPaid;
        this.change = change;
    }

    public BigDecimal getTotalProducts() { return totalProducts; }
    public void setTotalProducts(BigDecimal totalProducts) { this.totalProducts = totalProducts; }
    public BigDecimal getTotalInvoice() { return totalInvoice; }
    public void setTotalInvoice(BigDecimal totalInvoice) { this.totalInvoice = totalInvoice; }
    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
    public BigDecimal getTotalPaid() { return totalPaid; }
    public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }
    public BigDecimal getChange() { return change; }
    public void setChange(BigDecimal change) { this.change = change; }
}
