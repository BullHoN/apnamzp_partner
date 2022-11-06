package com.avit.apnamzp_partner.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.models.orders.OrderItem;
import com.avit.apnamzp_partner.models.orders.OrderStatus;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>{

    public interface NextStepInterface {
        void updateOrderStatus(String orderId, int newOrderStatus, boolean shopReceivedPayment);
        void openOrderDetailsFragment(OrderItem orderItem);
        void cancelOrder(String orderId, String reason);
    }

    private Context context;
    private List<OrderItem> orderItemList;
    private String TAG = "OrderItems";
    private NextStepInterface nextStepInterface;

    public OrdersAdapter(Context context, List<OrderItem> orderItemList, NextStepInterface nextStepInterface) {
        this.context = context;
        this.orderItemList = orderItemList;
        this.nextStepInterface = nextStepInterface;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_orders,parent,false);
        return new OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, @SuppressLint("RecyclerView") int position) {
        OrderItem curr = orderItemList.get(position);
        OrderItemsAdapter orderItemsAdapter = new OrderItemsAdapter(curr.getOrderItems(),context);

        holder.ordersItemRecyclerView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,false));
        holder.ordersItemRecyclerView.setAdapter(orderItemsAdapter);


        if(curr.getSpecialInstructions() != null && curr.getSpecialInstructions().length() > 0){
            holder.specialInstructionsContainer.setVisibility(View.VISIBLE);
            holder.specialInstructions.setText("Special Instructions: " + curr.getSpecialInstructions());
        }

//        1 -> preparing, 2 -> ready, 3 -> delivered
        // TODO: Change Button Text
        holder.orderNextActionButton.setVisibility(View.VISIBLE);
        if(curr.getOrderStatus() == OrderStatus.ORDER_PREPARING){
            holder.orderDeliveryStatus.setText("Preparing");
            holder.orderNextActionButton.setText("order ready");
        }
        else if(curr.getOrderStatus() == OrderStatus.ORDER_READY){
            holder.orderDeliveryStatus.setText("Ready");
            if(curr.getBillingDetails().getDeliveryService()){
                holder.orderNextActionButton.setText("send for delivery");
            }
            else {
                holder.orderNextActionButton.setText("Recieved By Customer");
            }
        }
        else if(curr.getOrderStatus() == OrderStatus.ORDER_COMPLETED){
            holder.orderDeliveryStatus.setText("Completed");
            holder.orderNextActionButton.setVisibility(View.GONE);
        }
        else if(curr.getOrderStatus() == OrderStatus.ORDER_CANCELLED){
            holder.orderDeliveryStatus.setText("Cancelled");
            holder.orderNextActionButton.setVisibility(View.GONE);
            holder.orderPaymentStatus.setText("Cancelled");
            holder.orderPaymentStatus.setBackgroundColor(context.getResources().getColor(R.color.failure));
        }

        holder.orderId.setText("Order Id: #" + curr.get_id().substring(curr.get_id().length()-5));

        // Change Behaviour & Text of the Next action button
        if(curr.getBillingDetails().getDeliveryService()) {
            holder.orderDeliveryType.setText("Delivery");
        }
        else{
            holder.orderDeliveryType.setText("Takeaway");
        }



        Log.i(TAG, "onBindViewHolder: " + curr.isShopOfferApplied());
        Log.i(TAG, "onBindViewHolder: " + curr.getOfferCode());

        if(curr.isShopOfferApplied()){
            holder.appliedOfferView.setVisibility(View.VISIBLE);
            holder.appliedOfferView.setText(curr.getOfferCode());
        }

        SimpleDateFormat localDate = new SimpleDateFormat("hh:mm a");
        String time = localDate.format(curr.getCreatedAt());
        holder.orderArrivalTime.setText(time);


        holder.orderTotalPrice.setText("Total Amount: ₹" + curr.getTotalReceivingAmount());

        if(curr.getOrderStatus() != OrderStatus.ORDER_CANCELLED){
            if(curr.isPaymentReceivedToShop()){
                holder.orderPaymentStatus.setText("RECEIVED");
            }
            else {
                holder.orderPaymentStatus.setText("NOT RECEIVED");
                holder.orderPaymentStatus.setBackgroundColor(context.getResources().getColor(R.color.failure));
            }
        }

        holder.orderNextActionButton.setCheckable(true);
        holder.orderNextActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int orderStatus = curr.getOrderStatus()+1;

                if(curr.getOrderStatus() == 2){
                    orderStatus = 4;
                }

                if(orderStatus == 4){
                    showPaymentReceivedDialog(curr.get_id(),orderStatus);
                }
                else {
                    nextStepInterface.updateOrderStatus(curr.get_id(),orderStatus,false);
                }

                orderItemList.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.moreActionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context,view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Log.i(TAG, "onMenuItemClick: " + menuItem.getItemId());

                        if(menuItem.getItemId() == R.id.more_order_info){
                            nextStepInterface.openOrderDetailsFragment(curr);
                        }

//                        else if(menuItem.getItemId() == R.id.cancel_order){
//
//                        }

                        return false;
                    }
                });
                popupMenu.inflate(R.menu.more_actions_menu);
                popupMenu.show();
            }
        });

    }

    private void showPaymentReceivedDialog(String orderId,int orderStatus){
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AlertDialogTheme);
        builder.setTitle("Payment Received For This Order ?");

        builder.setPositiveButton("Received", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                nextStepInterface.updateOrderStatus(orderId,orderStatus,true);
            }
        });
        builder.setNegativeButton("Not Received", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                nextStepInterface.updateOrderStatus(orderId,orderStatus,false);
            }
        });

        builder.show();

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
                , orderArrivalTime, appliedOfferView, specialInstructions;
        public MaterialButton orderNextActionButton;
        public ImageButton moreActionsButton;
        public LinearLayout specialInstructionsContainer;

        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            ordersItemRecyclerView = itemView.findViewById(R.id.order_items_recycler_view);
            orderId = itemView.findViewById(R.id.order_id);
            orderDeliveryType = itemView.findViewById(R.id.order_delivery_type);
            orderDeliveryStatus = itemView.findViewById(R.id.order_delivery_status);
            orderArrivalTime = itemView.findViewById(R.id.order_arrival_time);
            orderTotalPrice = itemView.findViewById(R.id.order_total_price);
            orderDeliveryStatus = itemView.findViewById(R.id.order_delivery_status);
            appliedOfferView = itemView.findViewById(R.id.applied_offer);
            orderPaymentStatus = itemView.findViewById(R.id.order_payment_status);

            orderNextActionButton = itemView.findViewById(R.id.order_next_action_button);
            moreActionsButton = itemView.findViewById(R.id.more_action_button);

            specialInstructionsContainer = itemView.findViewById(R.id.special_instruction_container);
            specialInstructions = itemView.findViewById(R.id.special_instruction);

        }
    }

}
