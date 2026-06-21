package com.example.planetarium.dto;

import lombok.Data;

@Data
public class SchoolFormDTO {
    private String schoolName;
    private String schoolAddress;
    private String teacherName;
    private String contactNumber;
    private String email;
    private int studentCount;
    private String gradeLevel;
    private String otherInfo;
}
