package com.myprojects.truckmanager.truckManagerApp.model;

public enum TruckStatus {
    AVAILABLE("Available"),
    TRIP("Trip"),
    SERVICE("Need service");

    public final String getStatus;

    private TruckStatus(String status) {
        this.getStatus = status;
    }
}
