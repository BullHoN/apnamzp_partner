package com.avit.apnamzp_partner.models.shop;

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

    public ShopItemData(String name, List<ShopPricingData> pricings, String imageURL) {
        this.name = name;
        this.pricings = pricings;
        this.imageURL = imageURL;
    }

    public void setName(String name) {
        this.name = name;
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
