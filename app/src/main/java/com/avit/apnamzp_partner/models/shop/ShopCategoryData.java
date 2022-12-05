package com.avit.apnamzp_partner.models.shop;

import java.util.List;

public class ShopCategoryData {
    private String categoryName;
    private List<ShopItemData> shopItemDataList;
    private boolean isCategoryAvailable;

    public ShopCategoryData(String categoryName, List<ShopItemData> shopItemDataList, boolean isCategoryAvailable) {
        this.categoryName = categoryName;
        this.shopItemDataList = shopItemDataList;
        this.isCategoryAvailable = isCategoryAvailable;
    }

    public boolean isCategoryAvailable() {
        return isCategoryAvailable;
    }

    public ShopCategoryData(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<ShopItemData> getShopItemDataList() {
        return shopItemDataList;
    }

    @Override
    public String toString() {
        return "ShopCategoryData{" +
                "categoryName='" + categoryName + '\'' +
                ", expanded=" +
                '}';
    }
}
