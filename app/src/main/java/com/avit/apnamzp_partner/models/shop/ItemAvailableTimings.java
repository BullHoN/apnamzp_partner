package com.avit.apnamzp_partner.models.shop;

public class ItemAvailableTimings {
    private String from;
    private String to;

    public ItemAvailableTimings(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "ItemAvailableTimings{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
