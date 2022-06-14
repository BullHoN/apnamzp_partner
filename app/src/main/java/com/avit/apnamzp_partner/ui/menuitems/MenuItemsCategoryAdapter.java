package com.avit.apnamzp_partner.ui.menuitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.models.shop.ShopCategoryData;

import java.util.List;

public class MenuItemsCategoryAdapter extends RecyclerView.Adapter<MenuItemsCategoryAdapter.MenuItemsCategoriesViewHolder>{

    private List<ShopCategoryData> shopCategoryData;
    private Context context;

    public MenuItemsCategoryAdapter(List<ShopCategoryData> shopCategoryData, Context context) {
        this.shopCategoryData = shopCategoryData;
        this.context = context;
    }

    @NonNull
    @Override
    public MenuItemsCategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.item_menu_items_categories,parent,false);
        return new MenuItemsCategoriesViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemsCategoriesViewHolder holder, int position) {
        ShopCategoryData curr = shopCategoryData.get(position);

        holder.categoryNameView.setText(curr.getCategoryName());

        holder.openMenuItemsForCategoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return shopCategoryData.size();
    }

    public void updateItems(List<ShopCategoryData> newShopCategoryData){
        this.shopCategoryData = newShopCategoryData;
        notifyDataSetChanged();
    }

    public class MenuItemsCategoriesViewHolder extends RecyclerView.ViewHolder {

        private TextView categoryNameView;
        private CardView openMenuItemsForCategoryView;

        public MenuItemsCategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameView = itemView.findViewById(R.id.category_name);
            openMenuItemsForCategoryView = itemView.findViewById(R.id.open_all_items_in_category);
        }
    }

}
