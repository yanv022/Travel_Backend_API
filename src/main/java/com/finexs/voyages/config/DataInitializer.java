package com.finexs.voyages.config;

import com.finexs.voyages.entity.Route;
import com.finexs.voyages.entity.User;
import com.finexs.voyages.entity.UserRole;
import com.finexs.voyages.repository.RouteRepository;
import com.finexs.voyages.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.time.LocalDate;
import com.finexs.voyages.entity.RouteSchedule;
import com.finexs.voyages.repository.RouteScheduleRepository;
import com.finexs.voyages.entity.Agency;
import com.finexs.voyages.repository.AgencyRepository;



@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AgencyRepository agencyRepository;


    @Override
    public void run(String... args) throws Exception {
        initializeUsers();
        initializeRoutes();
    }

    private void initializeUsers() {

        Agency agency = initializeAgency(); // 👈 ICI

        if (userRepository.findByEmail("admin@system.com").isEmpty()) {
            User admin = new User();
            admin.setName("Admin User");
            admin.setEmail("admin@system.com");
            admin.setPassword(passwordEncoder.encode("password123"));
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
        }

        if (userRepository.findByEmail("manager@finexs.com").isEmpty()) {
            User manager = new User();
            manager.setName("Manager User");
            manager.setEmail("manager@finexs.com");
            manager.setPassword(passwordEncoder.encode("password123"));
            manager.setRole(UserRole.MANAGER);
            manager.setAgency(agency);
            userRepository.save(manager);
        }

        if (userRepository.findByEmail("traveler@email.com").isEmpty()) {
            User traveler = new User();
            traveler.setName("Traveler User");
            traveler.setEmail("traveler@email.com");
            traveler.setPassword(passwordEncoder.encode("password123"));
            traveler.setRole(UserRole.TRAVELER);
            userRepository.save(traveler);
        }
    }

    private void initializeRoutes() {
        Agency agency = initializeAgency();

        if (routeRepository.count() == 0) {
            LocalDateTime now = LocalDateTime.now();

            Route route1 = new Route();
            route1.setDepartureCity("Douala");
            route1.setArrivalCity("Yaoundé");
            route1.setDepartureTime(now.plusHours(2));
            route1.setArrivalTime(now.plusHours(5));
            route1.setDuration(180);
            route1.setAmenities(Arrays.asList("WiFi", "Climatisation", "Toilettes"));
            route1.setCompany("Finexs Voyages");
            route1.setAgency(agency);
            routeRepository.save(route1);

            Route route2 = new Route();
            route2.setDepartureCity("Bafoussam");
            route2.setArrivalCity("Douala");
            route2.setDepartureTime(now.plusHours(3));
            route2.setArrivalTime(now.plusHours(7));
            route2.setDuration(240);
            route2.setAmenities(Arrays.asList("WiFi", "Climatisation", "Repas"));
            route2.setAgency(agency);
            route2.setCompany("Finexs Voyages");
            routeRepository.save(route2);

            Route route3 = new Route();
            route3.setDepartureCity("Ngaoundéré");
            route3.setArrivalCity("Yaoundé");
            route3.setDepartureTime(now.plusHours(4));
            route3.setArrivalTime(now.plusHours(10));
            route3.setDuration(360);
            route3.setAmenities(Arrays.asList("WiFi", "Climatisation", "Toilettes", "Repas"));
            route3.setCompany("Finexs Voyages");
            route3.setAgency(agency);
            routeRepository.save(route3);

            Route route4 = new Route();
            route4.setDepartureCity("Yaoundé");
            route4.setArrivalCity("Douala");
            route4.setDepartureTime(now.plusHours(5));
            route4.setArrivalTime(now.plusHours(8));
            route4.setDuration(180);
            route4.setAmenities(Arrays.asList("WiFi", "Climatisation", "Toilettes"));
            route4.setAgency(agency);
            route4.setCompany("Finexs Voyages");
            routeRepository.save(route4);
        }
    }
    @Bean
    CommandLineRunner loadSchedules(
            RouteRepository routeRepository,
            RouteScheduleRepository scheduleRepository
    ) {
        return args -> {

            if (scheduleRepository.count() == 0) {

                Route doualaYaounde = routeRepository
                        .findAll()
                        .stream()
                        .filter(r ->
                                r.getDepartureCity().equals("Douala")
                                        && r.getArrivalCity().equals("Yaoundé")
                        )
                        .findFirst()
                        .orElse(null);

                if (doualaYaounde != null) {
                    scheduleRepository.save(createSchedule(
                            doualaYaounde,
                            LocalDate.now().plusDays(1),
                            8000,
                            40
                    ));
                    scheduleRepository.save(createSchedule(
                            doualaYaounde,
                            LocalDate.now().plusDays(2),
                            7500,
                            25
                    ));
                }
            }
        };
    }

    private RouteSchedule createSchedule(
            Route route,
            LocalDate date,
            int price,
            int seats
    ) {
        RouteSchedule s = new RouteSchedule();
        s.setRoute(route);
        s.setTravelDate(date);
        s.setPrice(price);
        s.setAvailableSeats(seats);
        return s;
    }

    private Agency initializeAgency() {
        return agencyRepository.findByName("Finexs Voyages")
                .orElseGet(() -> {
                    Agency agency = new Agency();
                    agency.setName("Finexs Voyages");
                    agency.setDescription("Agence de transport interurbain");
                    return agencyRepository.save(agency);
                });
    }





}
