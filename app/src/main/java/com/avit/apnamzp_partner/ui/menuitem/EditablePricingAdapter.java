package com.avit.apnamzp_partner.ui.menuitem;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.models.shop.ShopPricingData;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class EditablePricingAdapter extends RecyclerView.Adapter<EditablePricingAdapter.EditablePricingViewHolder>{

    public interface EditablePricingActionsInterface {
        void removePricing(ShopPricingData shopPricingData);
    }

    private Context context;
    private List<ShopPricingData> itemPricingList;
    private EditablePricingActionsInterface editablePricingActionsInterface;

    public EditablePricingAdapter(Context context, List<ShopPricingData> itemPricingList, EditablePricingActionsInterface editablePricingActionsInterface) {
        this.context = context;
        this.itemPricingList = itemPricingList;
        this.editablePricingActionsInterface = editablePricingActionsInterface;
    }


    @NonNull
    @Override
    public EditablePricingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_editable_pricings,parent,false);
        return new EditablePricingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditablePricingViewHolder holder, int position) {
        ShopPricingData curr = itemPricingList.get(position);

        holder.priceView.setText(curr.getPrice());
        holder.priceTypeView.setText(curr.getType());

        holder.priceView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                curr.setPrice(editable.toString());
            }
        });

        holder.priceTypeView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                curr.setType(editable.toString());
            }
        });

        holder.removePriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editablePricingActionsInterface.removePricing(curr);
            }
        });

    }

    public void updateItemsPricingList(List<ShopPricingData> newItemPricingList){
        this.itemPricingList = newItemPricingList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemPricingList.size();
    }

    public class EditablePricingViewHolder extends RecyclerView.ViewHolder {

        public TextInputEditText priceTypeView, priceView;
        public ImageButton removePriceButton;

        public EditablePricingViewHolder(@NonNull View itemView) {
            super(itemView);

            priceTypeView = itemView.findViewById(R.id.price_type);
            priceView = itemView.findViewById(R.id.price);

            removePriceButton = itemView.findViewById(R.id.remove_price);
        }
    }
}
