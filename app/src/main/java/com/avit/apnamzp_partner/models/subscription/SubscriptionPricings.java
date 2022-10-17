package com.avit.apnamzp_partner.models.subscription;

public class SubscriptionPricings {
    private int from;
    private int to;
    private int amount;

    public SubscriptionPricings(int from, int to, int amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getAmount() {
        return amount;
    }
}
