package com.avit.apnamzp_partner.ui.order_notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.models.shop.ShopItemData;
import com.avit.apnamzp_partner.models.shop.ShopPricingData;

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
        ShopItemData curr = orderItems[position];

        ShopPricingData pricingData = curr.getPricings().get(0);
        String orderItemDetails = curr.getQuantity() + " x " + curr.getName() + " (" + pricingData.getType() + ") ";
        holder.orderItemDetailsView.setText(orderItemDetails);

        String price = String.valueOf(Integer.parseInt(pricingData.getPrice()) - Integer.parseInt(curr.getDiscount()));
        holder.orderItemPriceView.setText("₹" + price);

    }

    @Override
    public int getItemCount() {
        return orderItems.length;
    }

    public class OrderNotificationItemsAdapterViewHolder extends RecyclerView.ViewHolder {

        public TextView orderItemDetailsView;
        public TextView orderItemPriceView;

        public OrderNotificationItemsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            orderItemDetailsView = itemView.findViewById(R.id.order_item_detail);
            orderItemPriceView = itemView.findViewById(R.id.order_item_price);

        }
    }

}
