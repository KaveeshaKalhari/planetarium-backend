package com.example.planetarium.repo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.planetarium.model.BookedSeat;
import com.example.planetarium.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookedSeatRepo extends JpaRepository<BookedSeat, Long> {

    List<BookedSeat> findByShow(Show show);

    @Query("SELECT bs.seatId FROM BookedSeat bs WHERE bs.show = :show")
    List<String> findSeatIdByShow(@Param("show") Show show);

    boolean existsByShowAndSeatId(Show show, String seatId);
}