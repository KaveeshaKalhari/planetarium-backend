package com.example.planetarium.repo;

import com.example.planetarium.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingId(Long bookingId);

    @Query(value = "SELECT DATE(paid_at), SUM(amount) FROM payments WHERE paid_at BETWEEN :start AND :end AND payment_status = 'SUCCESS' GROUP BY DATE(paid_at) ORDER BY DATE(paid_at)", nativeQuery = true)
    List<Object[]> getDailyRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT EXTRACT(MONTH FROM paid_at), SUM(amount) FROM payments WHERE paid_at >= :since AND payment_status = 'SUCCESS' GROUP BY EXTRACT(MONTH FROM paid_at) ORDER BY EXTRACT(MONTH FROM paid_at)", nativeQuery = true)
    List<Object[]> getMonthlyRevenueSince(@Param("since") LocalDateTime since);
}