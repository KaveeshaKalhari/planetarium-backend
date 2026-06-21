package com.example.planetarium.repo;

import com.example.planetarium.model.EventSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventSubscriptionRepo extends JpaRepository<EventSubscription, Long> {
    Optional<EventSubscription> findByEmail(String email);
    boolean existsByEmail(String email);
    List<EventSubscription> findByAlertOneDayBeforeTrue();
    List<EventSubscription> findByAlertOneHourBeforeTrue();
}
