package com.bookstore.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BusinessCharges {
    private int chargesId;
    private BigDecimal platformFee;
    private BigDecimal deliveryFee;
    private BigDecimal taxesPercent;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public int getChargesId() {
        return chargesId;
    }

    public void setChargesId(int chargesId) {
        this.chargesId = chargesId;
    }

    public BigDecimal getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(BigDecimal platformFee) {
        this.platformFee = platformFee;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public BigDecimal getTaxesPercent() {
        return taxesPercent;
    }

    public void setTaxesPercent(BigDecimal taxesPercent) {
        this.taxesPercent = taxesPercent;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
