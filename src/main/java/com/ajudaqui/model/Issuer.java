package com.ajudaqui.model;

public class Issuer {
    private String businessName;
    private String tradeName;
    private String cnpj;
    private String address;

    public Issuer() {}

    public Issuer(String businessName, String tradeName, String cnpj, String address) {
        this.businessName = businessName;
        this.tradeName = tradeName;
        this.cnpj = cnpj;
        this.address = address;
    }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getTradeName() { return tradeName; }
    public void setTradeName(String tradeName) { this.tradeName = tradeName; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
