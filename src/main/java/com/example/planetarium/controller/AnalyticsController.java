package com.example.planetarium.controller;

import com.example.planetarium.repo.BookingRepo;
import com.example.planetarium.repo.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private PaymentRepo paymentRepo;

    // BookingAnalysis page — daily booking counts for a given range
    @GetMapping("/bookings")
    public ResponseEntity<Map<String, Object>> getBookingAnalytics(
            @RequestParam(defaultValue = "7") int days) {

        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Object[]> raw = bookingRepo.getDailyBookingCountsSince(since);

        List<Map<String, Object>> trend = new ArrayList<>();
        for (Object[] row : raw) {
            Map<String, Object> point = new HashMap<>();
            point.put("date", row[0].toString());
            point.put("bookings", row[1]);
            trend.add(point);
        }

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        Long monthTotal = bookingRepo.countConfirmedBookingsBetween(startOfMonth, LocalDateTime.now());

        Map<String, Object> result = new HashMap<>();
        result.put("trend", trend);
        result.put("totalThisMonth", monthTotal != null ? monthTotal : 0);

        return ResponseEntity.ok(result);
    }

    // RevenueAnalysis page — daily + monthly revenue
    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueAnalytics() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);

        List<Object[]> dailyRaw = paymentRepo.getDailyRevenueBetween(sevenDaysAgo, LocalDateTime.now());
        List<Object[]> monthlyRaw = paymentRepo.getMonthlyRevenueSince(sixMonthsAgo);

        List<Map<String, Object>> daily = new ArrayList<>();
        for (Object[] row : dailyRaw) {
            Map<String, Object> point = new HashMap<>();
            point.put("date", row[0].toString());
            point.put("revenue", row[1]);
            daily.add(point);
        }

        List<Map<String, Object>> monthly = new ArrayList<>();
        String[] monthNames = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        for (Object[] row : monthlyRaw) {
            Map<String, Object> point = new HashMap<>();
            int monthNum = Integer.parseInt(row[0].toString());
            point.put("month", monthNames[monthNum - 1]);
            point.put("revenue", row[1]);
            monthly.add(point);
        }

        // Total revenue this month
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        Double monthRevenue = bookingRepo.sumRevenueBetween(startOfMonth, LocalDateTime.now());

        Map<String, Object> result = new HashMap<>();
        result.put("daily", daily);
        result.put("monthly", monthly);
        result.put("totalThisMonth", monthRevenue != null ? monthRevenue : 0.0);

        return ResponseEntity.ok(result);
    }

    // AdminDashboard summary cards
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);

        Long bookingsToday = bookingRepo.countConfirmedBookingsBetween(startOfDay, LocalDateTime.now());
        Long bookingsThisMonth = bookingRepo.countConfirmedBookingsBetween(startOfMonth, LocalDateTime.now());
        Double revenueThisMonth = bookingRepo.sumRevenueBetween(startOfMonth, LocalDateTime.now());

        Map<String, Object> result = new HashMap<>();
        result.put("bookingsToday", bookingsToday != null ? bookingsToday : 0);
        result.put("bookingsThisMonth", bookingsThisMonth != null ? bookingsThisMonth : 0);
        result.put("revenueThisMonth", revenueThisMonth != null ? revenueThisMonth : 0.0);

        return ResponseEntity.ok(result);
    }
}