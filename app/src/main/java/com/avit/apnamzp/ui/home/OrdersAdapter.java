package com.avit.apnamzp.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avit.apnamzp.R;
import com.avit.apnamzp.models.orders.OrderItem;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>{



    private Context context;
    private List<OrderItem> orderItemList;
    private String TAG = "OrderItems";

    public OrdersAdapter(Context context, List<OrderItem> orderItemList) {
        this.context = context;
        this.orderItemList = orderItemList;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_orders,parent,false);
        return new OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        OrderItem curr = orderItemList.get(position);
        OrderItemsAdapter orderItemsAdapter = new OrderItemsAdapter(curr.getOrderItems(),context);

        holder.ordersItemRecyclerView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,false));
        holder.ordersItemRecyclerView.setAdapter(orderItemsAdapter);

//        1 -> preparing, 2 -> ready, 3 -> delivered
        // TODO: Change Button Text
        if(curr.getOrderStatus() == 1){
            holder.orderDeliveryStatus.setText("Preparing");
            holder.orderNextActionButton.setText("order ready");
        }
        else if(curr.getOrderStatus() == 2){
            holder.orderDeliveryStatus.setText("Ready");
            holder.orderNextActionButton.setText("send for delivery");
        }
        else if(curr.getOrderStatus() == 3){
            holder.orderDeliveryStatus.setText("Completed");
            holder.orderNextActionButton.setVisibility(View.GONE);
        }

        holder.orderId.setText("Order Id: #" + curr.get_id().substring(0,5));

        // TODO: Change Behaviour & Text of the Next action button
        if(curr.getBillingDetails().getDeliveryService()) {
            holder.orderDeliveryType.setText("Delivery");
        }
        else{
            holder.orderDeliveryType.setText("Takeaway");
        }

        // TODO: DO SOMETHING ABOUT ONLINE DELIVERY SYSTEM
        SimpleDateFormat localDate = new SimpleDateFormat("hh:mm a");
        String time = localDate.format(curr.getCreatedAt());
        holder.orderArrivalTime.setText(time);

        holder.orderTotalPrice.setText("Total Amount: ₹" + curr.getBillingDetails().getTotalPay());

    }

    public void replaceItems(List<OrderItem> newItems){
        orderItemList = newItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }


    public class OrdersViewHolder extends RecyclerView.ViewHolder {

        public RecyclerView ordersItemRecyclerView;
        public TextView orderId, orderDeliveryType, orderDeliveryStatus, orderTotalPrice, orderPaymentStatus
                , orderArrivalTime;
        public MaterialButton orderNextActionButton;

        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            ordersItemRecyclerView = itemView.findViewById(R.id.order_items_recycler_view);
            orderId = itemView.findViewById(R.id.order_id);
            orderDeliveryType = itemView.findViewById(R.id.order_delivery_type);
            orderDeliveryStatus = itemView.findViewById(R.id.order_delivery_status);
            orderArrivalTime = itemView.findViewById(R.id.order_arrival_time);
            orderTotalPrice = itemView.findViewById(R.id.order_total_price);
            orderDeliveryStatus = itemView.findViewById(R.id.order_delivery_status);

            orderNextActionButton = itemView.findViewById(R.id.order_next_action_button);

        }
    }

}
