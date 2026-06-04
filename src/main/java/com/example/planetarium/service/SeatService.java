package com.example.planetarium.service;

import com.example.planetarium.model.Seat;
import com.example.planetarium.repo.SeatRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatService {

    private final SeatRepo seatRepo;

    public List<Seat> getSeatsForShow(Long showId) {
        return seatRepo.findByShowId(showId);
    }

    @Scheduled(fixedRate = 60000)
    public void releaseExpiredHolds() {
        int released = seatRepo.releaseExpiredHolds(LocalDateTime.now());
        if (released > 0) System.out.println("Released " + released + " expired seat holds");
    }
}