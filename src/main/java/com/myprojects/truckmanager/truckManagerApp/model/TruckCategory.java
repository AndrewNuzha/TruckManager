package com.myprojects.truckmanager.truckManagerApp.model;

public enum TruckCategory {
    VAN("Trailer"),
    CONTAINER("Container");

    public final String getCategory;

    private TruckCategory(String category) {
        this.getCategory = category;
    }
}
