package com.example.planetarium.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "school_booking_forms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolBookingForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @Column(name = "school_name", nullable = false)
    private String schoolName;

    @Column(name = "school_address", nullable = false)
    private String schoolAddress;

    @Column(name = "teacher_name", nullable = false)
    private String teacherName;

    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    @Column(nullable = false)
    private String email;

    @Column(name = "student_count", nullable = false)
    private int studentCount;

    @Column(name = "grade_level", nullable = false)
    private String gradeLevel;

    @Column(name = "other_info")
    private String otherInfo;
}
