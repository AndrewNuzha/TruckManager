package com.myprojects.truckmanager.truckManagerApp.dto;

import com.myprojects.truckmanager.truckManagerApp.validation.shipment.CompanyExists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewShipmentDTO {

    @CompanyExists
    private Long companyId;
    private Long truckId;
    private Long departureLocationId;
    private Long arrivalLocationId;
    private String arrivalLocationCity;
    private Float distance;
    private Integer income;
    private String category;
}
