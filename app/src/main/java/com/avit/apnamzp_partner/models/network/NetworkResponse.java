package com.avit.apnamzp_partner.models.network;

public class NetworkResponse {
    private boolean success;
    private String desc;
    private int status;
    private String data;

    public NetworkResponse(boolean success, String desc, int status, String data) {
        this.success = success;
        this.desc = desc;
        this.status = status;
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getDesc() {
        return desc;
    }

    public boolean getStatus() {
        return success;
    }
}
