package com.avit.apnamzp_partner.models.orders;

public class BillingDetails {
    private int  deliveryCharge, itemTotal, offerDiscountedAmount, totalDiscount, totalTaxesAndPackingCharge, itemsOnTheWayTotalCost ,totalPay;
    private int freeDeliveryPrice;
    private String taxPercentage;
    private Boolean isDeliveryService;

    public BillingDetails(Boolean isDeliveryService) {
        this.isDeliveryService = isDeliveryService;
    }

    public BillingDetails(int deliveryCharge, int itemTotal, int offerDiscountedAmount, int totalDiscount, int totalTaxesAndPackingCharge, int itemsOnTheWayTotalCost, int totalPay, int freeDeliveryPrice, String taxPercentage, Boolean isDeliveryService) {
        this.deliveryCharge = deliveryCharge;
        this.itemTotal = itemTotal;
        this.offerDiscountedAmount = offerDiscountedAmount;
        this.totalDiscount = totalDiscount;
        this.totalTaxesAndPackingCharge = totalTaxesAndPackingCharge;
        this.itemsOnTheWayTotalCost = itemsOnTheWayTotalCost;
        this.totalPay = totalPay;
        this.freeDeliveryPrice = freeDeliveryPrice;
        this.taxPercentage = taxPercentage;
        this.isDeliveryService = isDeliveryService;
    }

    public BillingDetails(int deliveryCharge, int itemTotal, int offerDiscountedAmount, int totalDiscount, int totalTaxesAndPackingCharge, int itemsOnTheWayTotalCost, int totalPay, String taxPercentage, Boolean isDeliveryService) {
        this.deliveryCharge = deliveryCharge;
        this.itemTotal = itemTotal;
        this.offerDiscountedAmount = offerDiscountedAmount;
        this.totalDiscount = totalDiscount;
        this.totalTaxesAndPackingCharge = totalTaxesAndPackingCharge;
        this.itemsOnTheWayTotalCost = itemsOnTheWayTotalCost;
        this.totalPay = totalPay;
        this.taxPercentage = taxPercentage;
        this.isDeliveryService = isDeliveryService;
    }

    public BillingDetails(int deliveryCharge, int itemTotal, int offerDiscountedAmount, int totalDiscount, int totalTaxesAndPackingCharge, int itemsOnTheWayTotalCost, int totalPay, Boolean isDeliveryService) {
        this.deliveryCharge = deliveryCharge;
        this.itemTotal = itemTotal;
        this.offerDiscountedAmount = offerDiscountedAmount;
        this.totalDiscount = totalDiscount;
        this.totalTaxesAndPackingCharge = totalTaxesAndPackingCharge;
        this.itemsOnTheWayTotalCost = itemsOnTheWayTotalCost;
        this.totalPay = totalPay;
        this.isDeliveryService = isDeliveryService;
    }

    public int getFreeDeliveryPrice() {
        return freeDeliveryPrice;
    }

    public String getTaxPercentage() {
        return taxPercentage;
    }

    public int getItemsOnTheWayTotalCost() {
        return itemsOnTheWayTotalCost;
    }

    public int getDeliveryCharge() {
        return deliveryCharge;
    }

    public int getItemTotal() {
        return itemTotal;
    }

    public int getOfferDiscountedAmount() {
        return offerDiscountedAmount;
    }

    public int getTotalDiscount() {
        return totalDiscount;
    }

    public int getTotalTaxesAndPackingCharge() {
        return totalTaxesAndPackingCharge;
    }

    public int getTotalPay() {
        return totalPay;
    }

    public Boolean getDeliveryService() {
        return isDeliveryService;
    }
}
