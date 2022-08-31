package com.avit.apnamzp_partner.models.shop;

import java.util.ArrayList;
import java.util.List;

public class ShopItemData {
    private String name;
    private String _id;
    private List<ShopPricingData> pricings;
    private String imageURL;
    public int quantity;
    public String taxOrPackigingPrice;
    public String discount;
    public Boolean available;
    private Boolean isVeg;
    private ItemAvailableTimings availableTimings;

    public ShopItemData(String name, String _id, List<ShopPricingData> pricings, String imageURL, int quantity, String taxOrPackigingPrice, String discount, Boolean available, Boolean isVeg, ItemAvailableTimings availableTimings) {
        this.name = name;
        this._id = _id;
        this.pricings = pricings;
        this.imageURL = imageURL;
        this.quantity = quantity;
        this.taxOrPackigingPrice = taxOrPackigingPrice;
        this.discount = discount;
        this.available = available;
        this.isVeg = isVeg;
        this.availableTimings = availableTimings;
    }

    public ShopItemData(){
        pricings = new ArrayList<>();
        discount = "0";
        taxOrPackigingPrice = "0";
        imageURL = "";
        available = false;
        isVeg = false;
    }

    public ShopItemData(String name, List<ShopPricingData> pricings, String imageURL) {
        this.name = name;
        this.pricings = pricings;
        this.imageURL = imageURL;
    }

    public void setAvailableTimings(ItemAvailableTimings availableTimings) {
        this.availableTimings = availableTimings;
    }

    public ItemAvailableTimings getAvailableTimings() {
        return availableTimings;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addPricing(String priceType, String price){
        pricings.add(new ShopPricingData(priceType,price));
    }

    public void removePricing(ShopPricingData shopPricingData){
        pricings.remove(shopPricingData);
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setTaxOrPackigingPrice(String taxOrPackigingPrice) {
        this.taxOrPackigingPrice = taxOrPackigingPrice;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public void setVeg(Boolean veg) {
        isVeg = veg;
    }

    public Boolean getAvailable() {
        return available;
    }

    public Boolean getVeg() {
        return isVeg;
    }

    public String getDiscount() {
        return discount;
    }

    public String getTaxOrPackigingPrice() {
        return taxOrPackigingPrice;
    }

    public String get_id() {
        return _id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPricings(List<ShopPricingData> pricings) {
        this.pricings = pricings;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getName() {
        return name;
    }

    public List<ShopPricingData> getPricings() {
        return pricings;
    }
}
