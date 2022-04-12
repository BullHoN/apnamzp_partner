package com.avit.apnamzp_partner.ui.home;

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

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.OrderItemsViewHolder>{

    private List<ShopItemData> orderItems;
    private Context context;

    public OrderItemsAdapter(List<ShopItemData> orderItems, Context context) {
        this.orderItems = orderItems;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_items,parent,false);
        return new OrderItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemsViewHolder holder, int position) {
        ShopItemData curr = orderItems.get(position);
        ShopPricingData pricingData = curr.getPricings().get(0);
        String orderItemDetails = curr.getQuantity() + " x " + curr.getName() + " (" + pricingData.getType() + ") ";
        holder.orderItemDetail.setText(orderItemDetails);

        String price = String.valueOf(Integer.parseInt(pricingData.getPrice()) - Integer.parseInt(curr.getDiscount()));
        holder.orderItemPrice.setText("₹" + price);

    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public class OrderItemsViewHolder extends RecyclerView.ViewHolder {

        public TextView orderItemDetail, orderItemPrice;

        public OrderItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            orderItemDetail = itemView.findViewById(R.id.order_item_detail);
            orderItemPrice = itemView.findViewById(R.id.order_item_price);
        }
    }

}
