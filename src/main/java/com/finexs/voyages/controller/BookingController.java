package com.finexs.voyages.controller;

import com.finexs.voyages.dto.BookingRequestDto;
import com.finexs.voyages.dto.BookingResponseDto;
import com.finexs.voyages.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // TRAVELER
    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestBody BookingRequestDto dto,
            Authentication authentication
    ) {
        BookingResponseDto booking =
                bookingService.createBooking(authentication.getName(), dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "message", "Booking confirmed",
                        "data", booking
                )
        );
    }

    // TRAVELER
    @GetMapping("/me")
    public ResponseEntity<List<BookingResponseDto>> myBookings(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                bookingService.getMyBookings(authentication.getName())
        );
    }
}
