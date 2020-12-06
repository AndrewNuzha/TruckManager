package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripServiceImpl implements TripService {

    @Autowired
    LocationService locationService;

    @Override
    public List<Trip> prepareTripsForUser(User user) {

        List<Trip> availableTrips = new ArrayList<>();
        List<Truck> trucks = user.getCompany().getTrucks();
        for (Truck truck : trucks) {
            availableTrips.addAll(createAvailableTrips(truck));
        }
        return availableTrips;
    }


    /**
     * Creates all trips for truck with available arrival points
     * @param truck current truck
     * @return list of trips
     */
    public List<Trip> createAvailableTrips(Truck truck) {

        List<Trip> tripsForTruck = new ArrayList<>();
        List<Location> allLocations = locationService.getAllLocations();
        String city = truck.getDetails().getCurrentLocation().getCity();
        List<Location> destinationLocations = allLocations.stream().filter(loc -> !loc.getCity().equals(city))
                .collect(Collectors.toList());

        for (Location location : destinationLocations) {
            Trip trip = new Trip();
            trip.setDepartureLocation(truck.getDetails().getCurrentLocation().getCity());
            trip.setArrivalLocation(location.getCity());
            trip.setDeliveryCost(600.55f); //TODO add delivery cost calculator
            trip.setDistance(locationService.calculateDistance(truck.getDetails().getCurrentLocation(), location));
            trip.setCategory("Simple"); //TODO add categories
            tripsForTruck.add(trip);
        }
        return tripsForTruck;
    }
}
