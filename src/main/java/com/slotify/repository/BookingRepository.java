package com.slotify.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.slotify.model.Booking;
import com.slotify.model.Resource;
import com.slotify.model.User;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    List<Booking> findByStatus(String status);

    @Query("""
           SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
           FROM Booking b
           WHERE b.resource = :resource
             AND b.status = 'APPROVED'
             AND b.startAt < :endAt
             AND b.endAt > :startAt
           """)
    boolean existsApprovedOverlap(
            @Param("resource") Resource resource,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );
}