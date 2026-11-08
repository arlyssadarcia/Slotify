package com.slotify.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.slotify.model.Booking;
import com.slotify.model.Resource;
import com.slotify.model.User;
import com.slotify.repository.BookingRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ResourceService resourceService;

    public BookingService(BookingRepository bookingRepository, ResourceService resourceService) {
        this.bookingRepository = bookingRepository;
        this.resourceService = resourceService;
    }

    public Booking requestBooking(User user, Long resourceId, LocalDateTime startAt,
                                  LocalDateTime endAt, String purpose) {
        if (!endAt.isAfter(startAt)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        Resource resource = resourceService.getById(resourceId);
        if (!"ACTIVE".equals(resource.getStatus())) {
            throw new IllegalArgumentException("Resource is not available for booking");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setResource(resource);
        booking.setStartAt(startAt);
        booking.setEndAt(endAt);
        booking.setPurpose(purpose);
        booking.setStatus("PENDING");
        return bookingRepository.save(booking);
    }

    public Booking approve(Long bookingId) {
        Booking booking = getById(bookingId);
        if (!"PENDING".equals(booking.getStatus())) {
            throw new IllegalArgumentException("Only PENDING bookings can be approved");
        }
        boolean overlap = bookingRepository.existsApprovedOverlap(
                booking.getResource(), booking.getStartAt(), booking.getEndAt());
        if (overlap) {
            throw new IllegalArgumentException("Overlaps an approved booking for this resource");
        }
        booking.setStatus("APPROVED");
        return bookingRepository.save(booking);
    }

    public Booking reject(Long bookingId, String reason) {
        Booking booking = getById(bookingId);
        if (!"PENDING".equals(booking.getStatus())) {
            throw new IllegalArgumentException("Only PENDING bookings can be rejected");
        }
        booking.setStatus("REJECTED");
        booking.setRejectReason(reason);
        return bookingRepository.save(booking);
    }

    public Booking cancel(Long bookingId, User user) {
        Booking booking = getById(bookingId);
        if (!booking.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("You can only cancel your own booking");
        }
        if (!"PENDING".equals(booking.getStatus())) {
            throw new IllegalArgumentException("Only PENDING bookings can be cancelled");
        }
        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }

    public List<Booking> myBookings(User user) {
        return bookingRepository.findByUser(user);
    }

    public List<Booking> byStatus(String status) {
        return bookingRepository.findByStatus(status);
    }

    public List<Booking> all() {
        return bookingRepository.findAll();
    }

    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));
    }
}
