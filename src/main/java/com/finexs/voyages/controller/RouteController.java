package com.finexs.voyages.controller;

import com.finexs.voyages.dto.RouteDto;
import com.finexs.voyages.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.finexs.voyages.entity.User;
import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;


@RestController
@RequestMapping("/routes")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping
    public ResponseEntity<List<RouteDto>> getAllRoutes() {
        List<RouteDto> routes = routeService.getAllRoutes();
        return ResponseEntity.ok(routes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteDto> getRouteById(@PathVariable Long id) {
        RouteDto route = routeService.getRouteById(id);
        return ResponseEntity.ok(route);
    }

    @PostMapping
    public ResponseEntity<RouteDto> createRoute(
            @RequestBody RouteDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(routeService.createRoute(dto));
    }


    @PutMapping("/{id}")
    public ResponseEntity<RouteDto> updateRoute(@PathVariable Long id, @RequestBody RouteDto routeDto) {
        RouteDto updatedRoute = routeService.updateRoute(id, routeDto);
        return ResponseEntity.ok(updatedRoute);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/agency")
    public ResponseEntity<List<RouteDto>> getRoutesForAgency() {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = auth.getName(); // ✅ email depuis JWT

        return ResponseEntity.ok(
                routeService.getRoutesForAgency(email)
        );
    }



}
