package com.example.planetarium.dto;

import lombok.Data;

@Data
public class PaymentResponseDTO {
    private Long id;
    private String bookingReference;
    private double amount;
    private String paymentMethod;
    private String status;
    private String paidAt;
}