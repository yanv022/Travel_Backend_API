package com.finexs.voyages.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter

public class BookingResponseDto {

    private Long id;
    private String departureCity;
    private String arrivalCity;
    private LocalDate travelDate;
    private Integer seats;
    private Integer totalPrice;
    private Instant createdAt;

}
