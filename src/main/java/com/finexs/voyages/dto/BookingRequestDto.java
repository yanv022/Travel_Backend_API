package com.finexs.voyages.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequestDto {

    private Long routeId;
    private Long scheduleId;
    private Integer seats;

}
