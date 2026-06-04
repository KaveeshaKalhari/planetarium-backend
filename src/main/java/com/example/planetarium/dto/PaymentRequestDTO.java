package com.example.planetarium.dto;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private Long bookingId;
    private String paymentMethod;   // "CARD", "BANK_TRANSFER", "CASH"
    private String cardLastFour;    // Optional, only for card payments
    private String cardHolderName;  // Optional, only for card payments
}