package com.ma3ka.vilka.domain;

public class CarDetailedCSV {

    private String id;
    private String notificationreferencenumber;
    private String vehicleproperty_vin;
    private String vehicleproperty_description_short;
    private String nBody;
    private String nChas;

    public String getVehicleproperty_vin() {
        return vehicleproperty_vin;
    }

    public void setVehicleproperty_vin(String vehicleproperty_vin) {
        this.vehicleproperty_vin = vehicleproperty_vin;
    }

    public String getVehicleproperty_description_short() {
        return vehicleproperty_description_short;
    }

    public void setVehicleproperty_description_short(String vehicleproperty_description_short) {
        this.vehicleproperty_description_short = vehicleproperty_description_short;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotificationreferencenumber() {
        return notificationreferencenumber;
    }

    public void setNotificationreferencenumber(String notificationreferencenumber) {
        this.notificationreferencenumber = notificationreferencenumber;
    }

    public String getnBody() {
        return nBody;
    }

    public void setnBody(String nBody) {
        this.nBody = nBody;
    }

    public String getnChas() {
        return nChas;
    }

    public void setnChas(String nChas) {
        this.nChas = nChas;
    }
}
