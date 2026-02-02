package com.finexs.voyages.repository;

import com.finexs.voyages.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByAgencyId(Long agencyId);

}
