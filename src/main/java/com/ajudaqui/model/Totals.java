package com.ajudaqui.model;

public class Totals {
    private String totalProducts;
    private String totalInvoice;
    private String discount;
    private String totalPaid;
    private String change;

    public Totals() {}

    public Totals(String totalProducts, String totalInvoice, String discount, String totalPaid, String change) {
        this.totalProducts = totalProducts;
        this.totalInvoice = totalInvoice;
        this.discount = discount;
        this.totalPaid = totalPaid;
        this.change = change;
    }

    public String getTotalProducts() { return totalProducts; }
    public void setTotalProducts(String totalProducts) { this.totalProducts = totalProducts; }
    public String getTotalInvoice() { return totalInvoice; }
    public void setTotalInvoice(String totalInvoice) { this.totalInvoice = totalInvoice; }
    public String getDiscount() { return discount; }
    public void setDiscount(String discount) { this.discount = discount; }
    public String getTotalPaid() { return totalPaid; }
    public void setTotalPaid(String totalPaid) { this.totalPaid = totalPaid; }
    public String getChange() { return change; }
    public void setChange(String change) { this.change = change; }
}
