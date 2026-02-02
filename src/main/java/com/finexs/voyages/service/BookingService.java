package com.finexs.voyages.service;

import com.finexs.voyages.dto.BookingRequestDto;
import com.finexs.voyages.dto.BookingResponseDto;
import com.finexs.voyages.entity.*;
import com.finexs.voyages.exception.BadRequestException;
import com.finexs.voyages.exception.NotFoundException;
import com.finexs.voyages.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final RouteScheduleRepository scheduleRepository;

    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            RouteRepository routeRepository,
            RouteScheduleRepository scheduleRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public BookingResponseDto createBooking(String userEmail, BookingRequestDto dto) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Route route = routeRepository.findById(dto.getRouteId())
                .orElseThrow(() -> new NotFoundException("Route not found"));

        RouteSchedule schedule = scheduleRepository.findById(dto.getScheduleId())
                .orElseThrow(() -> new NotFoundException("Schedule not found"));

        if (dto.getSeats() <= 0) {
            throw new BadRequestException("Seats must be greater than 0");
        }

        if (schedule.getAvailableSeats() < dto.getSeats()) {
            throw new BadRequestException("Not enough available seats");
        }

        // décrémenter les places
        schedule.setAvailableSeats(
                schedule.getAvailableSeats() - dto.getSeats()
        );

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoute(route);
        booking.setSchedule(schedule);
        booking.setSeats(dto.getSeats());
        booking.setTotalPrice(dto.getSeats() * schedule.getPrice());

        return toDto(bookingRepository.save(booking));
    }

    public List<BookingResponseDto> getMyBookings(String userEmail) {
        return bookingRepository.findByUserEmail(userEmail)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private BookingResponseDto toDto(Booking b) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(b.getId());
        dto.setDepartureCity(b.getRoute().getDepartureCity());
        dto.setArrivalCity(b.getRoute().getArrivalCity());
        dto.setTravelDate(b.getSchedule().getTravelDate());
        dto.setSeats(b.getSeats());
        dto.setTotalPrice(b.getTotalPrice());
        dto.setCreatedAt(b.getCreatedAt());
        return dto;
    }
}
