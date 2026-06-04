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

    // For revenue analysis
    @Query("SELECT FUNCTION('DATE', p.paidAt), SUM(p.amount) FROM Payment p WHERE p.paidAt BETWEEN :start AND :end AND p.status = 'SUCCESS' GROUP BY FUNCTION('DATE', p.paidAt) ORDER BY FUNCTION('DATE', p.paidAt)")
    List<Object[]> getDailyRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT FUNCTION('MONTH', p.paidAt), SUM(p.amount) FROM Payment p WHERE p.paidAt >= :since AND p.status = 'SUCCESS' GROUP BY FUNCTION('MONTH', p.paidAt) ORDER BY FUNCTION('MONTH', p.paidAt)")
    List<Object[]> getMonthlyRevenueSince(@Param("since") LocalDateTime since);
}