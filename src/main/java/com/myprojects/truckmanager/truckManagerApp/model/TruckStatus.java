package com.myprojects.truckmanager.truckManagerApp.model;

public enum TruckStatus {
    AVAILABLE("Available"),
    TRIP("trip");

    public final String getStatus;

    private TruckStatus(String status) {
        this.getStatus = status;
    }
}
