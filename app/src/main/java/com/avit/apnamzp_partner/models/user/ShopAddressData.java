package com.avit.apnamzp_partner.models.user;

public class ShopAddressData {
    private String mainAddress;
    private String latitude;
    private String longitude;

    public ShopAddressData(String mainAddress, String latitude, String longitude) {
        this.mainAddress = mainAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getMainAddress() {
        return mainAddress;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
