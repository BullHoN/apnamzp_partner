package com.avit.apnamzp_partner.models.orders;

import com.avit.apnamzp_partner.models.shop.ShopItemData;

import java.util.List;

public class OrderItemsJsonConversion {
    List<ShopItemData> orderItems;

    public OrderItemsJsonConversion(List<ShopItemData> orderItems) {
        this.orderItems = orderItems;
    }

    public List<ShopItemData> getOrderItems() {
        return orderItems;
    }
}
