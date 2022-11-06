package com.avit.apnamzp_partner.ui.reviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.models.shop.ReviewData;
import com.google.android.material.button.MaterialButton;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder>{

    public interface ReviewsActions {
        void openReviewOrder(String orderId);
    }

    private Context context;
    private List<ReviewData> reviewDataList;
    private ReviewsActions reviewsActions;

    public ReviewsAdapter(Context context, List<ReviewData> reviewDataList,ReviewsActions reviewsActions) {
        this.context = context;
        this.reviewDataList = reviewDataList;
        this.reviewsActions = reviewsActions;
    }

    @NonNull
    @Override
    public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_showreview,parent,false);
        return new ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsViewHolder holder, int position) {
        ReviewData curr = reviewDataList.get(position);

        holder.nameView.setText(curr.getUserName());
        holder.bodyView.setText(curr.getUserMessage());
        holder.scaleRatingBarView.setRating(Integer.parseInt(curr.getRating()));
        holder.scaleRatingBarView.setClickable(false);

        holder.initialView.setText(String.valueOf(curr.getUserName().charAt(0)));

        holder.showOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewsActions.openReviewOrder(curr.getOrderId());
            }
        });

    }

    public void changeData(List<ReviewData> newData){
        reviewDataList = newData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return reviewDataList.size();
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder{

        public TextView nameView,bodyView,initialView;
        public ScaleRatingBar scaleRatingBarView;
        public MaterialButton showOrderButton;

        public ReviewsViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.nameView);
            bodyView = itemView.findViewById(R.id.reviewBody);
            scaleRatingBarView = itemView.findViewById(R.id.ratingsBar);
            initialView = itemView.findViewById(R.id.initial_view);
            showOrderButton = itemView.findViewById(R.id.show_order_button);
        }
    }

}
