package com.myprojects.truckmanager.truckManagerApp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Route {

    private Long arrivalLocationId;
    private String arrivalLocationCity;
    private Float distance;

}
