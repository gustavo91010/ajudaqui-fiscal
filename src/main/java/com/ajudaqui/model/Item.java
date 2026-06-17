package com.ajudaqui.model;

public class Item {
    private String description;
    private String code;
    private String quantity;
    private String unit;
    private String unitValue;
    private String totalValue;

    public Item() {}

    public Item(String description, String code, String quantity, String unit, String unitValue, String totalValue) {
        this.description = description;
        this.code = code;
        this.quantity = quantity;
        this.unit = unit;
        this.unitValue = unitValue;
        this.totalValue = totalValue;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getUnitValue() { return unitValue; }
    public void setUnitValue(String unitValue) { this.unitValue = unitValue; }
    public String getTotalValue() { return totalValue; }
    public void setTotalValue(String totalValue) { this.totalValue = totalValue; }
}
