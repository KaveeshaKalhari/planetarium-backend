package com.example.planetarium.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.planetarium.model.Event;

public interface EventRepo extends JpaRepository<Event, Long> {
    List<Event> findByStatusOrderByIdDesc(String status);
}
