package com.finexs.voyages.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Voyageur
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // Route choisie
    @ManyToOne(optional = false)
    @JoinColumn(name = "route_id")
    private Route route;

    // Schedule (date + prix)
    @ManyToOne(optional = false)
    @JoinColumn(name = "schedule_id")
    private RouteSchedule schedule;

    private Integer seats;

    private Integer totalPrice;

    private Instant createdAt = Instant.now();
}
