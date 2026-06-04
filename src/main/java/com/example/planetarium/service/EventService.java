package com.example.planetarium.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.planetarium.dto.EventDTO;
import com.example.planetarium.model.Event;
import com.example.planetarium.repo.EventRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepo eventRepo;

    public List<EventDTO> getUpcomingEvents() {
        return eventRepo.findByStatusOrderByIdDesc("upcoming")
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<EventDTO> getAllForAdmin() {
        return eventRepo.findAll(Sort.by(Sort.Direction.DESC, "id"))
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public EventDTO create(EventDTO dto) {
        Event e = new Event();
        copyFields(dto, e);
        e.setStatus("upcoming");
        return toDTO(eventRepo.save(e));
    }

    public EventDTO update(Long id, EventDTO dto) {
        Event e = eventRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        copyFields(dto, e);
        return toDTO(eventRepo.save(e));
    }

    public void delete(Long id) {
        eventRepo.deleteById(id);
    }

    private void copyFields(EventDTO dto, Event e) {
        e.setTitle(dto.getTitle());
        e.setDescription(dto.getDescription());
        e.setEventDate(dto.getEventDate());
        e.setStartTime(dto.getStartTime());
        e.setEndTime(dto.getEndTime());
        e.setType(dto.getType());
        e.setIcon(dto.getIcon());
        e.setBadge(dto.getBadge());
        if (dto.getStatus() != null) e.setStatus(dto.getStatus());
    }

    private EventDTO toDTO(Event e) {
        EventDTO dto = new EventDTO();
        dto.setId(e.getId());
        dto.setTitle(e.getTitle());
        dto.setDescription(e.getDescription());
        dto.setEventDate(e.getEventDate());
        dto.setStartTime(e.getStartTime());
        dto.setEndTime(e.getEndTime());
        dto.setType(e.getType());
        dto.setIcon(e.getIcon());
        dto.setBadge(e.getBadge());
        dto.setStatus(e.getStatus());
        return dto;
    }
}