package com.avit.apnamzp_partner.ui.order_notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.models.shop.ShopItemData;

import java.util.List;

public class OrderNotificationItemsAdapter extends RecyclerView.Adapter<OrderNotificationItemsAdapter.OrderNotificationItemsAdapterViewHolder>{

    private ShopItemData[] orderItems;
    private Context context;

    public OrderNotificationItemsAdapter(ShopItemData[] orderItems, Context context) {
        this.orderItems = orderItems;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderNotificationItemsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_items,parent,false);
        return new OrderNotificationItemsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderNotificationItemsAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return orderItems.length;
    }

    public class OrderNotificationItemsAdapterViewHolder extends RecyclerView.ViewHolder {

        public OrderNotificationItemsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
