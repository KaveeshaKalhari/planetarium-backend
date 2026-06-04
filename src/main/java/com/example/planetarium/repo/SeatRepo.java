package com.example.planetarium.repo;

import com.example.planetarium.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeatRepo extends JpaRepository<Seat, Long> {

    List<Seat> findByShowId(Long showId);

    List<Seat> findByShowIdAndStatus(Long showId, String status);

    @Modifying
    @Query("UPDATE Seat s SET s.status = 'AVAILABLE', s.heldUntil = null, s.heldBy = null " +
           "WHERE s.status = 'HELD' AND s.heldUntil < :now")
    int releaseExpiredHolds(@Param("now") LocalDateTime now);
}