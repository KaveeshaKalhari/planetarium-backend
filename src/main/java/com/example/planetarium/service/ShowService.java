package com.example.planetarium.service;

import com.example.planetarium.dto.ShowDTO;
import com.example.planetarium.model.Show;
import com.example.planetarium.repo.BookedSeatRepo;
import com.example.planetarium.repo.ShowRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShowService {

    @Autowired
    private ShowRepo showRepo;

    @Autowired
    private BookedSeatRepo bookedSeatRepo;

    // ── Public: shows for the next 21 days ───────────────────────────────────
    public List<ShowDTO> getUpcomingShows() {
        LocalDate today = LocalDate.now();
        LocalDate end = today.plusDays(21);
        return showRepo.findAvailableShowsInRange(today, end)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Public: single show + its booked seat IDs (for SeatSelectionPage) ───
    public ShowDTO getShowWithSeats(Long showId) {
        Show show = showRepo.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found with id: " + showId));
        ShowDTO dto = toDTO(show);
        List<String> bookedSeats = bookedSeatRepo.findSeatIdByShow(show);
        dto.setBookedSeatIds(bookedSeats);
        return dto;
    }

    // ── Admin operations ─────────────────────────────────────────────────────

    public ShowDTO createShow(ShowDTO dto) {
        Show show = toEntity(dto);
        show.setAvailableSeats(dto.getTotalSeats() > 0 ? dto.getTotalSeats() : 224);
        show.setTotalSeats(dto.getTotalSeats() > 0 ? dto.getTotalSeats() : 224);
        show.setStatus("UPCOMING");
        return toDTO(showRepo.save(show));
    }

    public ShowDTO updateShow(Long id, ShowDTO dto) {
        Show existing = showRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Show not found with id: " + id));
        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setShowDate(dto.getShowDate());
        existing.setShowTime(dto.getShowTime());
        existing.setAudienceType(dto.getAudienceType());
        existing.setProgramType(dto.getProgramType());
        existing.setLanguage(dto.getLanguage());
        existing.setGrade(dto.getGrade());
        existing.setTotalSeats(dto.getTotalSeats());
        existing.setPricePerSeat(dto.getPricePerSeat());
        existing.setTicketPrice(dto.getPricePerSeat());
        existing.setStatus(dto.getStatus());
        return toDTO(showRepo.save(existing));
    }

    public void deleteShow(Long id) {
        showRepo.deleteById(id);
    }

    public List<ShowDTO> getAllShowsForAdmin() {
        return showRepo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ShowDTO toDTO(Show show) {
        ShowDTO dto = new ShowDTO();
        dto.setId(show.getId());
        dto.setTitle(show.getTitle());
        dto.setDescription(show.getDescription());
        dto.setShowDate(show.getShowDate());
        dto.setShowTime(show.getShowTime());
        dto.setAudienceType(show.getAudienceType());
        dto.setProgramType(show.getProgramType());
        dto.setLanguage(show.getLanguage());
        dto.setGrade(show.getGrade());
        dto.setTotalSeats(show.getTotalSeats());
        dto.setAvailableSeats(show.getAvailableSeats());
        dto.setPricePerSeat(show.getPricePerSeat());
        dto.setStatus(show.getStatus());
        dto.setDuration(show.getDuration());
        return dto;
    }

    private Show toEntity(ShowDTO dto) {
        Show show = new Show();
        show.setTitle(dto.getTitle());
        show.setDescription(dto.getDescription());
        show.setShowDate(dto.getShowDate());
        show.setShowTime(dto.getShowTime());
        show.setAudienceType(dto.getAudienceType());
        show.setProgramType(dto.getProgramType());
        show.setLanguage(dto.getLanguage());
        show.setGrade(dto.getGrade());
        show.setTotalSeats(dto.getTotalSeats());
        show.setPricePerSeat(dto.getPricePerSeat());
        show.setTicketPrice(dto.getPricePerSeat());
        return show;
    }
}