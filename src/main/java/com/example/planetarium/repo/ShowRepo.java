package com.example.planetarium.repo;

import com.example.planetarium.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowRepo extends JpaRepository<Show, Long> {

    List<Show> findByShowDateBetweenOrderByShowDateAscShowTimeAsc(LocalDate start, LocalDate end);

    List<Show> findByShowDateAndAudienceTypeOrderByShowTime(LocalDate date, String audienceType);

    List<Show> findByShowDateOrderByShowTimeAsc(LocalDate date);

    @Query("SELECT s FROM Show s WHERE s.showDate >= :today AND s.status = 'UPCOMING' ORDER BY s.showDate ASC, s.showTime ASC")
    List<Show> findUpcomingShows(@Param("today") LocalDate today);

    @Query("SELECT s FROM Show s WHERE s.showDate BETWEEN :start AND :end AND s.status = 'UPCOMING' ORDER BY s.showDate, s.showTime")
    List<Show> findAvailableShowsInRange(@Param("start") LocalDate start, @Param("end") LocalDate end);
}