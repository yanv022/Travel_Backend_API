package com.finexs.voyages.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "route_schedules")
@Getter
@Setter
public class RouteSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "route_id")
    private Route route;

    private LocalDate travelDate;

    private Integer price; // FCFA
    private Integer availableSeats;

    // getters & setters

}
