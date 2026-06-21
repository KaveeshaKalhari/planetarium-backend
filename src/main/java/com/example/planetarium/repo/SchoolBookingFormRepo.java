package com.example.planetarium.repo;

import com.example.planetarium.model.SchoolBookingForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolBookingFormRepo extends JpaRepository<SchoolBookingForm, Long> {
    Optional<SchoolBookingForm> findByBookingId(Long bookingId);
}
