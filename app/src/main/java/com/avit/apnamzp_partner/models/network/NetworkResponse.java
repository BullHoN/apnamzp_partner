package com.avit.apnamzp_partner.models.network;

public class NetworkResponse {
    private int status;
    private boolean success;
    private String desc;
    private String data;

    public NetworkResponse(boolean success) {
        this.success = success;
    }

    public NetworkResponse(boolean success, String desc) {
        this.success = success;
        this.desc = desc;
    }

    public NetworkResponse(boolean success, String desc, String data) {
        this.success = success;
        this.desc = desc;
        this.data = data;
    }

    public NetworkResponse(boolean success, String desc, int status) {
        this.success = success;
        this.desc = desc;
        this.status = status;
    }

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
