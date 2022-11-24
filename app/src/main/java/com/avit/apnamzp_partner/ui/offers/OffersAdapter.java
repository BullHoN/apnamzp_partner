package com.avit.apnamzp_partner.ui.offers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.models.offer.OfferItem;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OffersViewHolder> {

    public interface deleteOfferInterface {
        void applyOffer(OfferItem offerItem);
        void setDisplayOffer(OfferItem offerItem);
    }

    private Context context;
    private List<OfferItem> offerItemList;
    private deleteOfferInterface applyOfferInterface;

    public OffersAdapter(Context context, List<OfferItem> offerItemList, deleteOfferInterface applyOfferInterface) {
        this.context = context;
        this.offerItemList = offerItemList;
        this.applyOfferInterface = applyOfferInterface;
    }

    @NonNull
    @Override
    public OffersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_offer, parent, false);
        return new OffersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OffersViewHolder holder, int position) {
        OfferItem offerItem = offerItemList.get(position);
        String offerType = offerItem.getOfferType();



        if (offerType.equals("percent")) {
            holder.discountTypeView.setText(offerItem.getDiscountPercentage() + "% Discount");

            String descOfOffer = "Get " + offerItem.getDiscountPercentage() + "% Discount on your Orders above Rs. " + offerItem.getDiscountAbove() + "\n"
                    + "Maximum Discount Cap: Rs" + offerItem.getMaxDiscount();
            holder.offerConditionsView.setText(descOfOffer);

        } else if (offerType.equals("flat")) {
            holder.discountTypeView.setText("Flat ₹" + offerItem.getDiscountAmount() + " Discount");

            String descOfOffer = "Get Flat " + offerItem.getDiscountAmount() + " Rupees Discount on orders above Rs." + offerItem.getDiscountAbove();
            holder.offerConditionsView.setText(descOfOffer);

        }

        holder.codeView.setText(offerItem.getCode());

        holder.applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyOfferInterface.applyOffer(offerItem);
            }
        });

        holder.setDisplayOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyOfferInterface.setDisplayOffer(offerItem);
            }
        });

    }

    @Override
    public int getItemCount() {
        return offerItemList.size();
    }

    public void changeValues(List<OfferItem> newItems) {
        offerItemList = newItems;
        notifyDataSetChanged();
    }

    class OffersViewHolder extends RecyclerView.ViewHolder {

        public TextView discountTypeView, offerConditionsView, codeView;
        public MaterialButton applyButton, setDisplayOfferButton;
        public ImageView offerImageView;

        public OffersViewHolder(@NonNull View itemView) {
            super(itemView);
            discountTypeView = itemView.findViewById(R.id.discountType);
            offerConditionsView = itemView.findViewById(R.id.offerConditions);
            codeView = itemView.findViewById(R.id.code);

            applyButton = itemView.findViewById(R.id.removeButton);
            offerImageView = itemView.findViewById(R.id.offerImage);

            setDisplayOfferButton = itemView.findViewById(R.id.setAsDisplay);

        }
    }

}

