package com.example.planetarium.repo;

import com.example.planetarium.model.Booking;
import com.example.planetarium.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {

    List<Booking> findByUserOrderByCreatedAtDesc(User user);

    Optional<Booking> findByBookingReference(String reference);

    List<Booking> findByStatusOrderByCreatedAtDesc(String status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt BETWEEN :start AND :end AND b.status = 'CONFIRMED'")
    Long countConfirmedBookingsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.createdAt BETWEEN :start AND :end AND b.status = 'CONFIRMED'")
    Double sumRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // For admin booking analysis — daily counts
    @Query("SELECT FUNCTION('DATE', b.createdAt), COUNT(b) FROM Booking b WHERE b.createdAt >= :since AND b.status = 'CONFIRMED' GROUP BY FUNCTION('DATE', b.createdAt) ORDER BY FUNCTION('DATE', b.createdAt)")
    List<Object[]> getDailyBookingCountsSince(@Param("since") LocalDateTime since);
}