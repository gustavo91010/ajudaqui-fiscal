package com.ajudaqui.model;

import java.math.BigDecimal;

public class Item {
    private String description;
    private String code;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal unitValue;
    private BigDecimal totalValue;

    public Item() {}

    public Item(String description, String code, BigDecimal quantity, String unit, BigDecimal unitValue, BigDecimal totalValue) {
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
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public BigDecimal getUnitValue() { return unitValue; }
    public void setUnitValue(BigDecimal unitValue) { this.unitValue = unitValue; }
    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
}
