package com.avit.apnamzp_partner.ui.menuitems;

import android.app.AlertDialog;
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
import com.avit.apnamzp_partner.models.shop.ShopItemData;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MenuItemsCategoryAdapter extends RecyclerView.Adapter<MenuItemsCategoryAdapter.MenuItemsCategoriesViewHolder>{

    public interface CategoriesActions {
        void openCategoryMenuItems(ShopCategoryData shopCategoryData, String query);
        void deleteCategory(ShopCategoryData shopCategoryData);
        void editCategoryName(ShopCategoryData shopCategoryData, String newCategoryName);
    }

    private List<ShopCategoryData> shopCategoryData, allShopCategoryData;
    private Context context;
    private CategoriesActions categoriesActions;
    private String query;

    public MenuItemsCategoryAdapter(List<ShopCategoryData> shopCategoryData, Context context, CategoriesActions categoriesActions) {
        this.shopCategoryData = shopCategoryData;
        this.context = context;
        this.categoriesActions = categoriesActions;
        this.allShopCategoryData = shopCategoryData;
        this.query = "";
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
                categoriesActions.openCategoryMenuItems(curr,query);
            }
        });

        holder.openMenuItemsForCategoryView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                openEditDialog(curr);
                return true;
            }
        });

    }

    private void openEditDialog(ShopCategoryData shopCategoryData){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_category,null,false);

        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        TextInputEditText newCategoryNameView = view.findViewById(R.id.new_category_name);
        newCategoryNameView.setText(shopCategoryData.getCategoryName());

        MaterialButton saveChanges = view.findViewById(R.id.save_changes_button);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newCategoryName = newCategoryNameView.getText().toString();
                categoriesActions.editCategoryName(shopCategoryData,newCategoryName);
                dialog.dismiss();
            }
        });

        MaterialButton deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoriesActions.deleteCategory(shopCategoryData);
                dialog.dismiss();
            }
        });

    }

    @Override
    public int getItemCount() {
        return shopCategoryData.size();
    }

    public void updateItems(List<ShopCategoryData> newShopCategoryData){
        this.shopCategoryData = newShopCategoryData;
        this.allShopCategoryData = newShopCategoryData;
        notifyDataSetChanged();
    }

    public void searchItems(String query){
        this.query = query;
        List<ShopCategoryData> filteredCategoryData = new ArrayList<>();

        for(ShopCategoryData categoryData : allShopCategoryData){
            for(ShopItemData shopItemData : categoryData.getShopItemDataList()){
                if(shopItemData.getName().toLowerCase().contains(query.toLowerCase())){
                    filteredCategoryData.add(categoryData);
                    break;
                }
            }
        }

        shopCategoryData = filteredCategoryData;
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
