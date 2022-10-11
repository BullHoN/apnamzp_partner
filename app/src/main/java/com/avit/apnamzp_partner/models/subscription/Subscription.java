package com.avit.apnamzp_partner.models.subscription;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Subscription {

    @SerializedName("_id")
    private String id;
    private boolean isPaid;
    private Date startDate;
    private Date endDate;
    private boolean isFree;
    private int totalEarning;
    private List<SubscriptionPricings> subscriptionPricings;

    public Subscription(String id, boolean isPaid, Date startDate, Date endDate, boolean isFree, int totalEarning, List<SubscriptionPricings> subscriptionPricings) {
        this.id = id;
        this.isPaid = isPaid;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isFree = isFree;
        this.totalEarning = totalEarning;
        this.subscriptionPricings = subscriptionPricings;
    }

    public int getTotalEarning() {
        return totalEarning;
    }

    public List<SubscriptionPricings> getSubscriptionPricings() {
        return subscriptionPricings;
    }

    public String getId() {
        return id;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public boolean isFree() {
        return isFree;
    }
}
