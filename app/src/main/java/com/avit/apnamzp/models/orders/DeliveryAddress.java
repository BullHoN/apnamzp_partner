package com.avit.apnamzp.models.orders;

public class DeliveryAddress {
    private String latitude;
    private String longitude;
    private String rawAddress;
    private String houseNo;
    private String landmark;

    public DeliveryAddress(String latitude, String longitude, String rawAddress, String houseNo, String landmark) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.rawAddress = rawAddress;
        this.houseNo = houseNo;
        this.landmark = landmark;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getRawAddress() {
        return rawAddress;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public String getLandmark() {
        return landmark;
    }
}
