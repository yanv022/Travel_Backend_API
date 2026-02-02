package com.finexs.voyages.service;

import com.finexs.voyages.dto.RouteScheduleDto;
import com.finexs.voyages.entity.Route;
import com.finexs.voyages.entity.RouteSchedule;
import com.finexs.voyages.exception.NotFoundException;
import com.finexs.voyages.repository.RouteRepository;
import com.finexs.voyages.repository.RouteScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteScheduleService {

    private final RouteScheduleRepository scheduleRepository;
    private final RouteRepository routeRepository;

    public RouteScheduleService(RouteScheduleRepository scheduleRepository,
                                RouteRepository routeRepository) {
        this.scheduleRepository = scheduleRepository;
        this.routeRepository = routeRepository;
    }

    public List<RouteScheduleDto> getSchedulesByRoute(Long routeId) {
        return scheduleRepository.findByRouteId(routeId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public RouteScheduleDto createSchedule(Long routeId, RouteScheduleDto dto) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new NotFoundException("Route not found"));

        RouteSchedule schedule = new RouteSchedule();
        schedule.setRoute(route);
        schedule.setTravelDate(dto.getTravelDate());
        schedule.setPrice(dto.getPrice());
        schedule.setAvailableSeats(dto.getAvailableSeats());

        return toDto(scheduleRepository.save(schedule));
    }

    private RouteScheduleDto toDto(RouteSchedule s) {
        RouteScheduleDto dto = new RouteScheduleDto();
        dto.setId(s.getId());
        dto.setTravelDate(s.getTravelDate());
        dto.setPrice(s.getPrice());
        dto.setAvailableSeats(s.getAvailableSeats());
        return dto;
    }
}
