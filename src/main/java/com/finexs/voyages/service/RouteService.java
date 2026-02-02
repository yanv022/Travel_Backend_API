package com.finexs.voyages.service;

import com.finexs.voyages.dto.RouteDto;
import com.finexs.voyages.entity.Route;
import com.finexs.voyages.entity.User;
import com.finexs.voyages.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finexs.voyages.exception.NotFoundException;
import com.finexs.voyages.entity.User;
import com.finexs.voyages.exception.ForbiddenException;
import com.finexs.voyages.repository.UserRepository;




import java.util.List;
import java.util.stream.Collectors;


@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private UserRepository userRepository;

    public List<RouteDto> getAllRoutes() {
        return routeRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public RouteDto createRoute(RouteDto routeDto, User manager) {

        Route route = convertToEntity(routeDto);
        route.setAgency(manager.getAgency());

        Route savedRoute = routeRepository.save(route);
        return convertToDto(savedRoute);
    }


    public RouteDto createRoute(RouteDto routeDto) {

        Route route = convertToEntity(routeDto);
        Route savedRoute = routeRepository.save(route);
        return convertToDto(savedRoute);
    }

    public RouteDto updateRoute(Long id, RouteDto routeDto) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Route not found"));

        route.setDepartureCity(routeDto.getDepartureCity());
        route.setArrivalCity(routeDto.getArrivalCity());
        route.setDepartureTime(routeDto.getDepartureTime());
        route.setArrivalTime(routeDto.getArrivalTime());
        route.setDuration(routeDto.getDuration());
        //route.setCompany(routeDto.getCompany());
        route.setAmenities(routeDto.getAmenities());
        route.setUpdatedAt(System.currentTimeMillis());

        Route updatedRoute = routeRepository.save(route);
        return convertToDto(updatedRoute);
    }

    public void deleteRoute(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new NotFoundException("Route not found");
        }
        routeRepository.deleteById(id);
    }

    private RouteDto convertToDto(Route route) {
        return new RouteDto(
                route.getId(),
                route.getDepartureCity(),
                route.getArrivalCity(),
                route.getDepartureTime(),
                route.getArrivalTime(),
                route.getDuration(),
                //route.getCompany(),
                route.getAmenities()
        );
    }

    public RouteDto getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Route not found"));
        return convertToDto(route);
    }


    private Route convertToEntity(RouteDto routeDto) {
        Route route = new Route();
        route.setDepartureCity(routeDto.getDepartureCity());
        route.setArrivalCity(routeDto.getArrivalCity());
        route.setDepartureTime(routeDto.getDepartureTime());
        route.setArrivalTime(routeDto.getArrivalTime());
        route.setDuration(routeDto.getDuration());
        //route.setCompany(routeDto.getCompany());
        route.setAmenities(routeDto.getAmenities());
        return route;
    }

    public List<RouteDto> getRoutesForAgency(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ForbiddenException("User not found"));

        if (user.getAgency() == null) {
            throw new ForbiddenException("Manager has no agency");
        }

        return routeRepository
                .findByAgencyId(user.getAgency().getId())
                .stream()
                .map(this::convertToDto)
                .toList();
    }



}
