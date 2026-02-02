package com.finexs.voyages.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RouteScheduleDto {

    private Long id;
    private LocalDate travelDate;
    private Integer price;
    private Integer availableSeats;
}
