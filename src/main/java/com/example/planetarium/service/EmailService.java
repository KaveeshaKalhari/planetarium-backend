package com.example.planetarium.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${planetarium.email}")
    private String planetariumEmail;

    // ── existing sendContactEmail() stays unchanged ────────────────────────────

    public void sendContactEmail(String userName, String userEmail, String userMessage) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(planetariumEmail);
            helper.setReplyTo(userEmail);
            helper.setSubject("New Contact Message from " + userName);

            String htmlBody = "<div style='font-family: Arial, sans-serif; max-width: 600px;'>"
                    + "<div style='background-color: #0A1128; padding: 20px; text-align: center;'>"
                    + "<h1 style='color: white;'>Smart Planetarium</h1>"
                    + "<p style='color: #1282A2;'>New Contact Form Submission</p>"
                    + "</div>"
                    + "<div style='padding: 30px; background-color: #f9f9f9;'>"
                    + "<table style='width: 100%;'>"
                    + "<tr><td style='padding: 10px; font-weight: bold; color: #034078;'>Name:</td>"
                    + "<td style='padding: 10px;'>" + userName + "</td></tr>"
                    + "<tr style='background-color: #eef4f8;'>"
                    + "<td style='padding: 10px; font-weight: bold; color: #034078;'>Email:</td>"
                    + "<td style='padding: 10px;'><a href='mailto:" + userEmail + "'>" + userEmail + "</a></td></tr>"
                    + "<tr><td style='padding: 10px; font-weight: bold; color: #034078; vertical-align: top;'>Message:</td>"
                    + "<td style='padding: 10px;'>" + userMessage.replace("\n", "<br>") + "</td></tr>"
                    + "</table></div>"
                    + "<div style='background-color: #0A1128; padding: 15px; text-align: center;'>"
                    + "<p style='color: #aaa; font-size: 12px;'>Reply to this email to respond to " + userName + "</p>"
                    + "</div></div>";

            helper.setText(htmlBody, true);
            mailSender.send(message);

        } catch (Exception e) {
            System.err.println("Failed to send contact email: " + e.getMessage());
        }
    }

    // ── existing sendConfirmationEmail() stays unchanged ──────────────────────

    public void sendConfirmationEmail(String userName, String userEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(userEmail);
            helper.setSubject("We received your message — Smart Planetarium");

            String htmlBody = "<div style='font-family: Arial, sans-serif; max-width: 600px;'>"
                    + "<div style='background-color: #0A1128; padding: 20px; text-align: center;'>"
                    + "<h1 style='color: white;'>Smart Planetarium</h1>"
                    + "</div>"
                    + "<div style='padding: 30px;'>"
                    + "<h2 style='color: #0A1128;'>Thank you, " + userName + "!</h2>"
                    + "<p style='color: #555;'>We have received your message and our team will get back to you as soon as possible.</p>"
                    + "<p style='color: #555;'>If your matter is urgent, you can also reach us at:</p>"
                    + "<ul style='color: #555;'>"
                    + "<li>Email: srilanka.smartplanetarium@gmail.com</li>"
                    + "<li>Phone: +94 11 123 4567</li>"
                    + "</ul></div>"
                    + "<div style='background-color: #0A1128; padding: 15px; text-align: center;'>"
                    + "<p style='color: #aaa; font-size: 12px;'>Smart Planetarium — Sri Lanka</p>"
                    + "</div></div>";

            helper.setText(htmlBody, true);
            mailSender.send(message);

        } catch (Exception e) {
            System.err.println("Failed to send confirmation email: " + e.getMessage());
        }
    }

    // ── NEW: sendBlogApprovedEmail() ───────────────────────────────────────────

    public void sendBlogApprovedEmail(String authorName, String authorEmail, String blogTitle) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(authorEmail);
            helper.setSubject("🎉 Your Blog Has Been Approved — Smart Planetarium");

            String htmlBody = "<div style='font-family: Arial, sans-serif; max-width: 600px;'>"
                    + "<div style='background-color: #0A1128; padding: 20px; text-align: center;'>"
                    + "<h1 style='color: white; margin: 0;'>Smart Planetarium</h1>"
                    + "<p style='color: #1282A2; margin: 5px 0 0;'>Blog Notification</p>"
                    + "</div>"
                    + "<div style='padding: 30px; background-color: #f9f9f9;'>"
                    + "<div style='text-align: center; margin-bottom: 24px;'>"
                    + "<div style='display: inline-block; background-color: #d1fae5; border-radius: 50%; width: 60px; height: 60px; line-height: 60px; font-size: 30px;'>✅</div>"
                    + "</div>"
                    + "<h2 style='color: #0A1128; text-align: center;'>Congratulations, " + authorName + "!</h2>"
                    + "<p style='color: #555; font-size: 15px; text-align: center;'>Your blog post has been <strong style='color: #059669;'>approved</strong> and is now live on our website.</p>"
                    + "<div style='background-color: #eef4f8; border-left: 4px solid #059669; border-radius: 6px; padding: 16px 20px; margin: 24px 0;'>"
                    + "<p style='margin: 0; color: #034078; font-weight: bold; font-size: 14px;'>Published Blog</p>"
                    + "<p style='margin: 6px 0 0; color: #0A1128; font-size: 16px;'>\"" + blogTitle + "\"</p>"
                    + "</div>"
                    + "<p style='color: #555; font-size: 14px;'>Your article is now visible to all visitors on the Smart Planetarium blog. Thank you for contributing to our community!</p>"
                    + "</div>"
                    + "<div style='background-color: #0A1128; padding: 15px; text-align: center;'>"
                    + "<p style='color: #aaa; font-size: 12px; margin: 0;'>Smart Planetarium — Sri Lanka</p>"
                    + "</div></div>";

            helper.setText(htmlBody, true);
            mailSender.send(message);

        } catch (Exception e) {
            System.err.println("Failed to send blog approved email: " + e.getMessage());
        }
    }

    // ── NEW: sendBlogRejectedEmail() ───────────────────────────────────────────

    public void sendBlogRejectedEmail(String authorName, String authorEmail, String blogTitle) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(authorEmail);
            helper.setSubject("Blog Submission Update — Smart Planetarium");

            String htmlBody = "<div style='font-family: Arial, sans-serif; max-width: 600px;'>"
                    + "<div style='background-color: #0A1128; padding: 20px; text-align: center;'>"
                    + "<h1 style='color: white; margin: 0;'>Smart Planetarium</h1>"
                    + "<p style='color: #1282A2; margin: 5px 0 0;'>Blog Notification</p>"
                    + "</div>"
                    + "<div style='padding: 30px; background-color: #f9f9f9;'>"
                    + "<div style='text-align: center; margin-bottom: 24px;'>"
                    + "<div style='display: inline-block; background-color: #fee2e2; border-radius: 50%; width: 60px; height: 60px; line-height: 60px; font-size: 30px;'>❌</div>"
                    + "</div>"
                    + "<h2 style='color: #0A1128; text-align: center;'>Hi " + authorName + ",</h2>"
                    + "<p style='color: #555; font-size: 15px; text-align: center;'>After review, your blog post was <strong style='color: #dc2626;'>not approved</strong> at this time.</p>"
                    + "<div style='background-color: #eef4f8; border-left: 4px solid #dc2626; border-radius: 6px; padding: 16px 20px; margin: 24px 0;'>"
                    + "<p style='margin: 0; color: #034078; font-weight: bold; font-size: 14px;'>Submitted Blog</p>"
                    + "<p style='margin: 6px 0 0; color: #0A1128; font-size: 16px;'>\"" + blogTitle + "\"</p>"
                    + "</div>"
                    + "<p style='color: #555; font-size: 14px;'>Don't be discouraged — we encourage you to review our content guidelines and resubmit an updated version. If you have any questions, feel free to reach out to us.</p>"
                    + "<p style='color: #555; font-size: 14px;'>You can contact us at <a href='mailto:" + planetariumEmail + "' style='color: #034078;'>" + planetariumEmail + "</a>.</p>"
                    + "</div>"
                    + "<div style='background-color: #0A1128; padding: 15px; text-align: center;'>"
                    + "<p style='color: #aaa; font-size: 12px; margin: 0;'>Smart Planetarium — Sri Lanka</p>"
                    + "</div></div>";

            helper.setText(htmlBody, true);
            mailSender.send(message);

        } catch (Exception e) {
            System.err.println("Failed to send blog rejected email: " + e.getMessage());
        }
    }

    // ── NEW: sendBookingTicketEmail() — sends QR ticket after payment ──────────

    public void sendBookingTicketEmail(
            String userName,
            String userEmail,
            String bookingReference,
            String showTitle,
            String showDate,
            String showTime,
            int numberOfSeats,
            String seatNumbers,
            double totalAmount,
            String qrCodeBase64) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(userEmail);
            helper.setSubject("Your Ticket - " + showTitle + " | Smart Planetarium");

            // QR served via backend URL — Gmail loads external images correctly
            String qrImageUrl = "http://localhost:8080/api/v1/qr/" + bookingReference;

            String htmlBody = "<div style='font-family: Arial, sans-serif; max-width: 620px; margin: 0 auto;'>"
                + "<div style='background-color: #0A1128; padding: 24px; text-align: center;'>"
                + "<h1 style='color: white; margin: 0; font-size: 24px;'>Smart Planetarium</h1>"
                + "<p style='color: #1282A2; margin: 6px 0 0; font-size: 13px;'>Booking Confirmation &amp; Entry Ticket</p>"
                + "</div>"
                + "<div style='background-color: #d1fae5; padding: 16px; text-align: center; border-bottom: 2px solid #059669;'>"
                + "<p style='margin: 0; color: #065f46; font-size: 16px; font-weight: bold;'>Payment Successful - You're all set!</p>"
                + "</div>"
                + "<div style='padding: 30px; background-color: #f9f9f9;'>"
                + "<p style='color: #333; font-size: 15px;'>Hi <strong>" + userName + "</strong>,</p>"
                + "<p style='color: #555;'>Your booking is confirmed. Please show the QR code below at the entrance when you arrive.</p>"
                + "<div style='background: white; border: 1px solid #e2e8f0; border-radius: 10px; padding: 20px; margin: 20px 0;'>"
                + "<h3 style='color: #0A1128; margin: 0 0 16px; font-size: 16px; border-bottom: 1px solid #e2e8f0; padding-bottom: 10px;'>Booking Details</h3>"
                + "<table style='width: 100%; border-collapse: collapse;'>"
                + "<tr><td style='padding: 8px 0; color: #666; font-size: 13px; width: 40%;'>Booking Reference</td>"
                + "<td style='padding: 8px 0; color: #0A1128; font-weight: bold; font-size: 13px;'>" + bookingReference + "</td></tr>"
                + "<tr style='background-color: #f8fafc;'><td style='padding: 8px; color: #666; font-size: 13px;'>Show</td>"
                + "<td style='padding: 8px; color: #0A1128; font-size: 13px;'>" + showTitle + "</td></tr>"
                + "<tr><td style='padding: 8px 0; color: #666; font-size: 13px;'>Date</td>"
                + "<td style='padding: 8px 0; color: #0A1128; font-size: 13px;'>" + showDate + "</td></tr>"
                + "<tr style='background-color: #f8fafc;'><td style='padding: 8px; color: #666; font-size: 13px;'>Time</td>"
                + "<td style='padding: 8px; color: #0A1128; font-size: 13px;'>" + showTime + "</td></tr>"
                + "<tr><td style='padding: 8px 0; color: #666; font-size: 13px;'>Seats</td>"
                + "<td style='padding: 8px 0; color: #0A1128; font-size: 13px;'>" + numberOfSeats + " seat(s) - " + seatNumbers + "</td></tr>"
                + "<tr style='background-color: #f8fafc;'><td style='padding: 8px; color: #666; font-size: 13px;'>Total Paid</td>"
                + "<td style='padding: 8px; color: #059669; font-weight: bold; font-size: 14px;'>Rs. " + String.format("%.2f", totalAmount) + "</td></tr>"
                + "</table></div>"
                + "<div style='text-align: center; margin: 24px 0;'>"
                + "<p style='color: #0A1128; font-weight: bold; font-size: 15px; margin-bottom: 12px;'>Your Entry QR Code</p>"
                + "<div style='display: inline-block; background: white; border: 3px solid #0A1128; border-radius: 12px; padding: 16px;'>"
                + "<img src='" + qrImageUrl + "' alt='Entry QR Code' width='200' height='200' style='display: block;'/>"
                + "</div>"
                + "<p style='color: #666; font-size: 12px; margin-top: 10px;'>Ref: <strong>" + bookingReference + "</strong></p>"
                + "</div>"
                + "<div style='background-color: #eff6ff; border-left: 4px solid #3b82f6; border-radius: 6px; padding: 14px 18px; margin-top: 16px;'>"
                + "<p style='margin: 0; color: #1e40af; font-weight: bold; font-size: 13px;'>Instructions</p>"
                + "<ul style='margin: 8px 0 0; padding-left: 18px; color: #1e3a8a; font-size: 13px; line-height: 1.8;'>"
                + "<li>Please arrive at least 15 minutes before the show.</li>"
                + "<li>Show this QR code at the entrance (printed or on your phone).</li>"
                + "<li>This ticket is valid for one-time entry only.</li>"
                + "<li>Tickets are non-transferable.</li>"
                + "</ul></div></div>"
                + "<div style='background-color: #0A1128; padding: 16px; text-align: center;'>"
                + "<p style='color: #aaa; font-size: 12px; margin: 0;'>Smart Planetarium - Sri Lanka &nbsp;|&nbsp; "
                + "<a href='mailto:" + planetariumEmail + "' style='color: #1282A2;'>" + planetariumEmail + "</a></p>"
                + "</div></div>";

            helper.setText(htmlBody, true);
            mailSender.send(message);

        } catch (Exception e) {
            System.err.println("Failed to send booking ticket email: " + e.getMessage());
        }
    }

        // ── NEW: sendOtpEmail() for forgot password ────────────────────────────────

    public void sendOtpEmail(String userEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(userEmail);
            helper.setSubject("Password Reset OTP — Smart Planetarium");

            String htmlBody = "<div style='font-family: Arial, sans-serif; max-width: 600px;'>"
                    + "<div style='background-color: #0A1128; padding: 20px; text-align: center;'>"
                    + "<h1 style='color: white;'>Smart Planetarium</h1>"
                    + "</div>"
                    + "<div style='padding: 30px; text-align: center;'>"
                    + "<h2 style='color: #0A1128;'>Password Reset Request</h2>"
                    + "<p style='color: #555; font-size: 16px;'>Use the OTP below to reset your password.</p>"
                    + "<p style='color: #555;'>This code expires in <strong>10 minutes</strong>.</p>"
                    + "<div style='margin: 30px auto; display: inline-block; background-color: #eef4f8;"
                    + " border: 2px dashed #1282A2; border-radius: 12px; padding: 20px 40px;'>"
                    + "<span style='font-size: 36px; font-weight: bold; letter-spacing: 10px; color: #034078;'>"
                    + otp + "</span>"
                    + "</div>"
                    + "<p style='color: #999; font-size: 13px; margin-top: 20px;'>"
                    + "If you did not request a password reset, you can safely ignore this email.</p>"
                    + "</div>"
                    + "<div style='background-color: #0A1128; padding: 15px; text-align: center;'>"
                    + "<p style='color: #aaa; font-size: 12px;'>Smart Planetarium — Sri Lanka</p>"
                    + "</div></div>";

            helper.setText(htmlBody, true);
            mailSender.send(message);

        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
        }
    }

    // ── Event reminder email ──────────────────────────────────────────────────

    public void sendEventReminderEmail(
            String toEmail,
            String eventTitle,
            String eventDescription,
            String eventDate,
            String startTime,
            String endTime,
            String badge,
            String icon,
            String when) { // "Tomorrow" or "In 1 Hour"

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Reminder: " + eventTitle + " is " + when + " | Smart Planetarium");

            String badgeHtml = (badge != null && !badge.isEmpty())
                ? "<span style='background:#1282A2;color:white;padding:3px 10px;border-radius:20px;font-size:11px;font-weight:600;'>" + badge + "</span>"
                : "";

            String iconHtml = (icon != null && !icon.isEmpty()) ? icon + " " : "🌌 ";

            String htmlBody = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>"
                + "<div style='background-color: #0A1128; padding: 24px; text-align: center;'>"
                + "<h1 style='color: white; margin: 0; font-size: 22px;'>Smart Planetarium</h1>"
                + "<p style='color: #1282A2; margin: 6px 0 0; font-size: 13px;'>Natural Phenomena Reminder</p>"
                + "</div>"

                + "<div style='background: linear-gradient(135deg, #001F54, #0A1128); padding: 30px; text-align: center;'>"
                + "<p style='color: #1282A2; font-size: 13px; font-weight: 600; letter-spacing: 2px; text-transform: uppercase; margin: 0 0 8px;'>UPCOMING EVENT — " + when.toUpperCase() + "</p>"
                + "<h2 style='color: white; font-size: 26px; margin: 0 0 12px;'>" + iconHtml + eventTitle + "</h2>"
                + badgeHtml
                + "</div>"

                + "<div style='padding: 28px; background: #f9f9f9;'>"
                + "<div style='background: white; border-radius: 10px; border: 1px solid #e2e8f0; padding: 20px; margin-bottom: 20px;'>"
                + "<table style='width: 100%; border-collapse: collapse;'>"
                + "<tr><td style='padding: 10px 0; color: #666; font-size: 13px; width: 35%;'>Date</td>"
                + "<td style='padding: 10px 0; color: #0A1128; font-weight: bold; font-size: 13px;'>" + eventDate + "</td></tr>"
                + "<tr style='background:#f8fafc'><td style='padding: 10px; color: #666; font-size: 13px;'>Time</td>"
                + "<td style='padding: 10px; color: #0A1128; font-size: 13px;'>" + startTime + " – " + endTime + "</td></tr>"
                + "</table>"
                + "</div>"

                + (eventDescription != null && !eventDescription.isEmpty()
                    ? "<p style='color: #444; font-size: 14px; line-height: 1.7; margin: 0 0 20px;'>" + eventDescription + "</p>"
                    : "")

                + "<div style='text-align: center; margin: 24px 0;'>"
                + "<a href='http://localhost:5173/events' style='background: #1282A2; color: white; padding: 12px 32px; border-radius: 8px; text-decoration: none; font-weight: 600; font-size: 14px;'>View Event Details</a>"
                + "</div>"

                + "<div style='background: #eff6ff; border-left: 4px solid #3b82f6; padding: 12px 16px; border-radius: 6px;'>"
                + "<p style='margin: 0; color: #1e40af; font-size: 12px;'>You're receiving this because you subscribed to event reminders at Smart Planetarium. "
                + "<a href='http://localhost:5173/events' style='color: #1282A2;'>Manage preferences</a></p>"
                + "</div>"
                + "</div>"

                + "<div style='background: #0A1128; padding: 16px; text-align: center;'>"
                + "<p style='color: #aaa; font-size: 12px; margin: 0;'>Smart Planetarium — Sri Lanka &nbsp;|&nbsp; "
                + "<a href='mailto:" + planetariumEmail + "' style='color: #1282A2;'>" + planetariumEmail + "</a></p>"
                + "</div></div>";

            helper.setText(htmlBody, true);
            mailSender.send(message);

        } catch (Exception e) {
            System.err.println("Failed to send event reminder email to " + toEmail + ": " + e.getMessage());
        }
    }

public void sendSubscriptionConfirmationEmail(String toEmail, boolean alertDayBefore, boolean alertHourBefore) {
    try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("You're subscribed to event alerts — Smart Planetarium");

        String preferences = "";
        if (alertDayBefore) preferences += "<li>📅 1 day before each event</li>";
        if (alertHourBefore) preferences += "<li>⏰ 1 hour before each event</li>";
        if (preferences.isEmpty()) preferences = "<li>No reminders selected</li>";

        String htmlBody = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>"
            + "<div style='background-color: #0A1128; padding: 24px; text-align: center;'>"
            + "<h1 style='color: white; margin: 0; font-size: 22px;'>Smart Planetarium</h1>"
            + "<p style='color: #1282A2; margin: 6px 0 0; font-size: 13px;'>Event Subscription Confirmed</p>"
            + "</div>"
            + "<div style='padding: 32px; background-color: #f9f9f9; text-align: center;'>"
            + "<div style='display: inline-block; background-color: #d1fae5; border-radius: 50%; width: 64px; height: 64px; line-height: 64px; font-size: 32px; margin-bottom: 16px;'>🔔</div>"
            + "<h2 style='color: #0A1128; margin: 0 0 8px;'>You're all set!</h2>"
            + "<p style='color: #555; font-size: 15px; margin: 0 0 24px;'>You'll receive reminders for upcoming celestial events at:</p>"
            + "<p style='color: #0A1128; font-weight: bold; font-size: 16px; margin: 0 0 24px;'>" + toEmail + "</p>"
            + "<div style='background: white; border: 1px solid #e2e8f0; border-radius: 10px; padding: 20px; text-align: left; margin-bottom: 24px;'>"
            + "<p style='color: #034078; font-weight: bold; font-size: 13px; margin: 0 0 10px;'>Your Alert Preferences</p>"
            + "<ul style='color: #555; font-size: 14px; margin: 0; padding-left: 20px; line-height: 2;'>"
            + preferences
            + "</ul></div>"
            + "<p style='color: #999; font-size: 12px;'>To unsubscribe at any time, contact us at "
            + "<a href='mailto:" + planetariumEmail + "' style='color: #1282A2;'>" + planetariumEmail + "</a></p>"
            + "</div>"
            + "<div style='background-color: #0A1128; padding: 16px; text-align: center;'>"
            + "<p style='color: #aaa; font-size: 12px; margin: 0;'>Smart Planetarium — Sri Lanka &nbsp;|&nbsp; "
            + "<a href='mailto:" + planetariumEmail + "' style='color: #1282A2;'>" + planetariumEmail + "</a></p>"
            + "</div></div>";

        helper.setText(htmlBody, true);
        mailSender.send(message);

    } catch (Exception e) {
        System.err.println("Failed to send subscription confirmation email: " + e.getMessage());
    }
    }
}