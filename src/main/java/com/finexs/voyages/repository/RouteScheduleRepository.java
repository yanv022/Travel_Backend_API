package com.finexs.voyages.repository;

import com.finexs.voyages.entity.RouteSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteScheduleRepository extends JpaRepository<RouteSchedule, Long> {

    List<RouteSchedule> findByRouteId(Long routeId);
}
