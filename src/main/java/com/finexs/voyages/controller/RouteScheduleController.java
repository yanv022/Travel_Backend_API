package com.finexs.voyages.controller;

import com.finexs.voyages.dto.RouteScheduleDto;
import com.finexs.voyages.service.RouteScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/routes/{routeId}/schedules")
public class RouteScheduleController {

    private final RouteScheduleService scheduleService;

    public RouteScheduleController(RouteScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // PUBLIC — consulter les schedules d’une route
    @GetMapping
    public ResponseEntity<List<RouteScheduleDto>> getSchedules(
            @PathVariable Long routeId
    ) {
        return ResponseEntity.ok(
                scheduleService.getSchedulesByRoute(routeId)
        );
    }

    // MANAGER — ajouter un schedule à une route
    @PostMapping
    public ResponseEntity<?> createSchedule(
            @PathVariable Long routeId,
            @RequestBody RouteScheduleDto dto
    ) {
        RouteScheduleDto created = scheduleService.createSchedule(routeId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "message", "Schedule created successfully",
                        "data", created
                )
        );
    }
}
