package com.avit.apnamzp_partner.ui.categoryitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.models.shop.ShopItemData;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CategoryItemsAdapter extends RecyclerView.Adapter<CategoryItemsAdapter.CategoryItemsViewHolder>{

    public interface CategoryItemsActions {
        void openEditFragment(ShopItemData shopItemData);
    }

    private List<ShopItemData> shopItemDataList;
    private Context context;
    private CategoryItemsActions categoryItemsActions;

    public CategoryItemsAdapter(List<ShopItemData> shopItemDataList, Context context, CategoryItemsActions categoryItemsActions) {
        this.shopItemDataList = shopItemDataList;
        this.context = context;
        this.categoryItemsActions = categoryItemsActions;
    }

    @NonNull
    @Override
    public CategoryItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_items,parent,false);
        return  new CategoryItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryItemsViewHolder holder, int position) {
        ShopItemData curr = shopItemDataList.get(position);

        holder.itemPricingsRecyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        ItemPricingsAdapter itemPricingsAdapter = new ItemPricingsAdapter(context,curr.getPricings());
        holder.itemPricingsRecyclerView.setAdapter(itemPricingsAdapter);

        holder.itemNameTextView.setText(curr.getName());

        if(curr.getVeg()){
            holder.isVegImageView.setImageResource(R.drawable.veg_item_icon);
        }
        else {
            holder.isVegImageView.setImageResource(R.drawable.ic_nonveg);
        }

        if(curr.getImageURL() != null && curr.getImageURL().length() > 1){
            Glide.with(context)
                    .load(curr.getImageURL())
                    .into(holder.itemImageView);
        }
        else {
            holder.itemImageView.setVisibility(View.GONE);
        }

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryItemsActions.openEditFragment(curr);
            }
        });

    }


    @Override
    public int getItemCount() {
        return shopItemDataList.size();
    }

    public class CategoryItemsViewHolder extends RecyclerView.ViewHolder {

        public ImageView isVegImageView, itemImageView;
        public TextView itemNameTextView;
        public RecyclerView itemPricingsRecyclerView;
        public MaterialButton editButton;

        public CategoryItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemPricingsRecyclerView = itemView.findViewById(R.id.item_pricing_recyclerview);

            isVegImageView = itemView.findViewById(R.id.isVeg);
            itemImageView = itemView.findViewById(R.id.item_image);

            itemNameTextView = itemView.findViewById(R.id.itemName);

            editButton = itemView.findViewById(R.id.editButton);
        }
    }
}
