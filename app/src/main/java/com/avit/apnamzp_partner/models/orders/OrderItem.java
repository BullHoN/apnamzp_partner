package com.avit.apnamzp_partner.models.orders;

import com.avit.apnamzp_partner.models.shop.ShopItemData;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class OrderItem {
    private int itemTotal;
    private int totalTaxesAndPackingCharge;
    private int deliveryCharge;
    private int totalDiscount;
    private int totalPay;
    private Boolean isDeliveryService;
    private String specialInstructions;
    private Boolean isPaid;

    private List<ShopItemData> orderItems;

    private String userId;
    private DeliveryAddress deliveryAddress;

    private int offerDiscountedAmount;
    private String offerCode;
    private BillingDetails billingDetails;
    private String _id;
    private int orderStatus;
    @SerializedName("created_at")
    private Date createdAt;
    private int orderType;
    private String assignedDeliveryBoy;
    private boolean paymentReceivedToShop;

    public OrderItem(int itemTotal, int totalTaxesAndPackingCharge, int deliveryCharge, int totalDiscount, int totalPay, Boolean isDeliveryService, String specialInstructions, Boolean isPaid, List<ShopItemData> orderItems, String userId, DeliveryAddress deliveryAddress, int offerDiscountedAmount, String offerCode, BillingDetails billingDetails, String _id, int orderStatus, Date createdAt, int orderType, String assignedDeliveryBoy, boolean paymentReceivedToShop) {
        this.itemTotal = itemTotal;
        this.totalTaxesAndPackingCharge = totalTaxesAndPackingCharge;
        this.deliveryCharge = deliveryCharge;
        this.totalDiscount = totalDiscount;
        this.totalPay = totalPay;
        this.isDeliveryService = isDeliveryService;
        this.specialInstructions = specialInstructions;
        this.isPaid = isPaid;
        this.orderItems = orderItems;
        this.userId = userId;
        this.deliveryAddress = deliveryAddress;
        this.offerDiscountedAmount = offerDiscountedAmount;
        this.offerCode = offerCode;
        this.billingDetails = billingDetails;
        this._id = _id;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.orderType = orderType;
        this.assignedDeliveryBoy = assignedDeliveryBoy;
        this.paymentReceivedToShop = paymentReceivedToShop;
    }

    public OrderItem(int itemTotal, int totalTaxesAndPackingCharge, int deliveryCharge, int totalDiscount, int totalPay, Boolean isDeliveryService, String specialInstructions, Boolean isPaid, List<ShopItemData> orderItems, String userId, DeliveryAddress deliveryAddress, int offerDiscountedAmount, String offerCode, BillingDetails billingDetails, String _id, int orderStatus, Date createdAt, int orderType, String assignedDeliveryBoy) {
        this.itemTotal = itemTotal;
        this.totalTaxesAndPackingCharge = totalTaxesAndPackingCharge;
        this.deliveryCharge = deliveryCharge;
        this.totalDiscount = totalDiscount;
        this.totalPay = totalPay;
        this.isDeliveryService = isDeliveryService;
        this.specialInstructions = specialInstructions;
        this.isPaid = isPaid;
        this.orderItems = orderItems;
        this.userId = userId;
        this.deliveryAddress = deliveryAddress;
        this.offerDiscountedAmount = offerDiscountedAmount;
        this.offerCode = offerCode;
        this.billingDetails = billingDetails;
        this._id = _id;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.orderType = orderType;
        this.assignedDeliveryBoy = assignedDeliveryBoy;
    }

    public OrderItem(int itemTotal, int totalTaxesAndPackingCharge, int deliveryCharge, int totalDiscount, int totalPay, Boolean isDeliveryService, String specialInstructions, Boolean isPaid, String shopID, String shopCategory, List<ShopItemData> orderItems, String userId, DeliveryAddress deliveryAddress, int offerDiscountedAmount, String offerCode, BillingDetails billingDetails, String _id, int orderStatus, Date createdAt, int orderType) {
        this.itemTotal = itemTotal;
        this.totalTaxesAndPackingCharge = totalTaxesAndPackingCharge;
        this.deliveryCharge = deliveryCharge;
        this.totalDiscount = totalDiscount;
        this.totalPay = totalPay;
        this.isDeliveryService = isDeliveryService;
        this.specialInstructions = specialInstructions;
        this.isPaid = isPaid;
        this.orderItems = orderItems;
        this.userId = userId;
        this.deliveryAddress = deliveryAddress;
        this.offerDiscountedAmount = offerDiscountedAmount;
        this.offerCode = offerCode;
        this.billingDetails = billingDetails;
        this._id = _id;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.orderType = orderType;
    }

    public OrderItem(int itemTotal, List<ShopItemData> orderItems, String userId, String _id) {
        this.itemTotal = itemTotal;
        this.orderItems = orderItems;
        this.userId = userId;
        this._id = _id;
    }

    public void setIsDeliveryServiceForActionNeededOrders(boolean isDeliveryService){
        this.billingDetails = new BillingDetails(isDeliveryService);
    }

    public boolean isPaymentReceivedToShop() {
        return paymentReceivedToShop;
    }

    public boolean isFreeDelivery(){
        if(getBillingDetails().getItemTotal() >= getBillingDetails().getFreeDeliveryPrice()){
            return true;
        }

        return false;
    }

    public String getAssignedDeliveryBoy() {
        return assignedDeliveryBoy;
    }

    public int getTotalReceivingAmount(){

        if(orderStatus == 7) return 0;

        int totalReceivingAmount = 0;
        if(!isShopOfferApplied()){
            totalReceivingAmount =  billingDetails.getItemTotal() +
                    billingDetails.getTotalTaxesAndPackingCharge()  - billingDetails.getTotalDiscount();
        }
        else {
            totalReceivingAmount =  billingDetails.getItemTotal() +  billingDetails.getTotalTaxesAndPackingCharge()
                    - billingDetails.getTotalDiscount() - billingDetails.getOfferDiscountedAmount();
        }
        
        if(isFreeDelivery()){
            totalReceivingAmount -= billingDetails.getDeliveryCharge();
        }

        return  totalReceivingAmount;
    }

    public boolean isShopOfferApplied(){
        if(offerCode == null) return false;
        if(offerCode.contains("APNAMZP")) return false;
        return true;
    }


    public int getItemTotal() {
        return itemTotal;
    }

    public int getTotalTaxesAndPackingCharge() {
        return totalTaxesAndPackingCharge;
    }

    public int getDeliveryCharge() {
        return deliveryCharge;
    }

    public int getTotalDiscount() {
        return totalDiscount;
    }

    public int getTotalPay() {
        return totalPay;
    }

    public Boolean getDeliveryService() {
        return isDeliveryService;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public Boolean getPaid() {
        return isPaid;
    }

    public List<ShopItemData> getOrderItems() {
        return orderItems;
    }

    public String getUserId() {
        return userId;
    }

    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public int getOfferDiscountedAmount() {
        return offerDiscountedAmount;
    }

    public String getOfferCode() {
        return offerCode;
    }

    public BillingDetails getBillingDetails() {
        return billingDetails;
    }

    public String get_id() {
        return _id;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public int getOrderType() {
        return orderType;
    }
}
