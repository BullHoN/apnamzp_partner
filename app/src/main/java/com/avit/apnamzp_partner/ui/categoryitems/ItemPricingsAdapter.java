package com.avit.apnamzp_partner.ui.categoryitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.models.shop.ShopPricingData;
import com.avit.apnamzp_partner.utils.PrettyStrings;

import java.util.List;

public class ItemPricingsAdapter extends RecyclerView.Adapter<ItemPricingsAdapter.ItemPricingsViewHolder>{

    private Context context;
    private List<ShopPricingData> itemPricingsData;

    public ItemPricingsAdapter(Context context, List<ShopPricingData> itemPricingsData) {
        this.context = context;
        this.itemPricingsData = itemPricingsData;
    }

    @NonNull
    @Override
    public ItemPricingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pircing,parent,false);
        return new ItemPricingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemPricingsViewHolder holder, int position) {
        ShopPricingData curr = itemPricingsData.get(position);

        holder.priceView.setText(PrettyStrings.getCostInINR(curr.getPrice()));
        holder.priceTypeView.setText(curr.getType());

    }

    @Override
    public int getItemCount() {
        return itemPricingsData.size();
    }

    public class ItemPricingsViewHolder extends RecyclerView.ViewHolder {

        public TextView priceTypeView, priceView;

        public ItemPricingsViewHolder(@NonNull View itemView) {
            super(itemView);

            priceTypeView = itemView.findViewById(R.id.price_type);
            priceView = itemView.findViewById(R.id.price);
        }
    }
}
