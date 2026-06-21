package com.example.planetarium.service;

import com.example.planetarium.model.Event;
import com.example.planetarium.model.EventSubscription;
import com.example.planetarium.repo.EventRepo;
import com.example.planetarium.repo.EventSubscriptionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EventSubscriptionService {

    private final EventSubscriptionRepo subscriptionRepo;
    private final EventRepo eventRepo;
    private final EmailService emailService;

    // ── Subscribe ─────────────────────────────────────────────────────────────

    public String subscribe(String email, boolean alertDayBefore, boolean alertHourBefore) {
        if (subscriptionRepo.existsByEmail(email)) {
            EventSubscription existing = subscriptionRepo.findByEmail(email).get();
            existing.setAlertOneDayBefore(alertDayBefore);
            existing.setAlertOneHourBefore(alertHourBefore);
            subscriptionRepo.save(existing);
            return "Preferences updated for " + email;
        }

        EventSubscription sub = new EventSubscription();
        sub.setEmail(email);
        sub.setAlertOneDayBefore(alertDayBefore);
        sub.setAlertOneHourBefore(alertHourBefore);
        sub.setSubscribedAt(LocalDateTime.now());
        subscriptionRepo.save(sub);

        emailService.sendSubscriptionConfirmationEmail(email, alertDayBefore, alertHourBefore);

        return "Subscribed successfully";
    }

    public void unsubscribe(String email) {
        subscriptionRepo.findByEmail(email).ifPresent(subscriptionRepo::delete);
    }

    // ── Scheduled email jobs ──────────────────────────────────────────────────

    // Runs every day at 9:00 AM — sends "1 day before" reminders
    @Scheduled(cron = "0 0 9 * * *")
    public void sendOneDayBeforeReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Event> tomorrowEvents = getEventsOnDate(tomorrow);
        if (tomorrowEvents.isEmpty())
            return;

        List<EventSubscription> subs = subscriptionRepo.findByAlertOneDayBeforeTrue();
        for (EventSubscription sub : subs) {
            for (Event event : tomorrowEvents) {
                emailService.sendEventReminderEmail(
                        sub.getEmail(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getEventDate(),
                        event.getStartTime(),
                        event.getEndTime(),
                        event.getBadge(),
                        event.getIcon(),
                        "Tomorrow");
            }
        }
        System.out.println("[EventScheduler] Sent 1-day reminders to " + subs.size() + " subscribers for "
                + tomorrowEvents.size() + " events.");
    }

    // Runs every hour at :00 — sends "1 hour before" reminders
    @Scheduled(cron = "0 0 * * * *")
    public void sendOneHourBeforeReminders() {
        // Check events starting in ~60 minutes (within this hour)
        LocalDateTime oneHourFromNow = LocalDateTime.now().plusHours(1);
        LocalDate targetDate = oneHourFromNow.toLocalDate();
        int targetHour = oneHourFromNow.getHour();

        List<Event> todayEvents = getEventsOnDate(targetDate);
        List<Event> soonEvents = todayEvents.stream()
                .filter(e -> parseEventHour(e.getStartTime()) == targetHour)
                .toList();

        if (soonEvents.isEmpty())
            return;

        List<EventSubscription> subs = subscriptionRepo.findByAlertOneHourBeforeTrue();
        for (EventSubscription sub : subs) {
            for (Event event : soonEvents) {
                emailService.sendEventReminderEmail(
                        sub.getEmail(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getEventDate(),
                        event.getStartTime(),
                        event.getEndTime(),
                        event.getBadge(),
                        event.getIcon(),
                        "In 1 Hour");
            }
        }
        System.out.println("[EventScheduler] Sent 1-hour reminders to " + subs.size() + " subscribers.");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private List<Event> getEventsOnDate(LocalDate date) {
        // eventDate is stored as e.g. "July 6, 2024" or "2024-07-06"
        return eventRepo.findByStatusOrderByIdDesc("upcoming").stream()
                .filter(e -> parseEventDate(e.getEventDate()).equals(date))
                .toList();
    }

    private LocalDate parseEventDate(String dateStr) {
        if (dateStr == null)
            return LocalDate.MIN;
        // Try ISO format first: "2026-06-19"
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException ignored) {
        }
        // Try "June 19, 2026" or "July 6, 2026"
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
            return LocalDate.parse(dateStr, fmt);
        } catch (DateTimeParseException ignored) {
        }
        return LocalDate.MIN;
    }

    private int parseEventHour(String timeStr) {
        if (timeStr == null)
            return -1;
        try {
            // e.g. "2:30 PM" or "10:00 AM"
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
            LocalTime t = LocalTime.parse(timeStr.trim().toUpperCase(), fmt);
            return t.getHour();
        } catch (Exception e) {
            return -1;
        }
    }
}
