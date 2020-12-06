package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.model.Trip;
import com.myprojects.truckmanager.truckManagerApp.model.User;

import java.util.List;

public interface TripService {

    List<Trip> prepareTripsForUser(User user);

}
