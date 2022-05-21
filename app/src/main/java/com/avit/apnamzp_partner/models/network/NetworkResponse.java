package com.avit.apnamzp_partner.models.network;

public class NetworkResponse {
    private boolean success;

    public NetworkResponse(boolean success) {
        this.success = success;
    }

    public boolean getStatus() {
        return success;
    }
}
