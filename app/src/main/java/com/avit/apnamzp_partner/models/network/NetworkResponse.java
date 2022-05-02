package com.avit.apnamzp_partner.models.network;

public class NetworkResponse {
    private int success;

    public NetworkResponse(int success) {
        this.success = success;
    }

    public int getStatus() {
        return success;
    }
}
