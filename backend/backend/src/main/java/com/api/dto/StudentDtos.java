package com.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public final class StudentDtos {

    private StudentDtos() {
    }

    public record StudentRequest(
            @NotBlank(message = "Full name is required")
            @Size(max = 120, message = "Full name must be at most 120 characters")
            String name,
            @Size(max = 120) String fatherName,
            @Size(max = 120) String motherName,
            @NotBlank(message = "Roll number is required")
            @Size(max = 50, message = "Roll number must be at most 50 characters")
            String rollNumber,
            @NotBlank(message = "Email is required")
            @Email(message = "Email must be valid")
            @Size(max = 150, message = "Email must be at most 150 characters")
            String email,
            @NotBlank(message = "Phone is required")
            @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone must contain 10 to 15 digits")
            String phone,
            @NotBlank(message = "Gender is required")
            @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female, or Other")
            String gender,
            @NotBlank(message = "Address is required")
            @Size(max = 255, message = "Address must be at most 255 characters")
            String address,
            @Size(max = 25) String fatherPhone,
            @Size(max = 25) String motherPhone,
            @Size(max = 120) String fatherOccupation,
            @NotNull(message = "Year is required")
            @Positive(message = "Year must be greater than 0")
            @Max(value = 10, message = "Year must be 10 or less")
            Integer year,
            @NotNull(message = "Semester is required")
            @Positive(message = "Semester must be greater than 0")
            @Max(value = 12, message = "Semester must be 12 or less")
            Integer semester,
            Boolean status,
            @NotNull(message = "Department is required")
            Long departmentId,
            @NotNull(message = "Course is required")
            Long courseId
    ) {
    }

    public record StudentResponse(
            Long id,
            String name,
            String fatherName,
            String motherName,
            String rollNumber,
            String email,
            String phone,
            String gender,
            String address,
            String fatherPhone,
            String motherPhone,
            String fatherOccupation,
            Integer year,
            Integer semester,
            Boolean status,
            Double attendancePercentage,
            Long departmentId,
            String departmentName,
            Long courseId,
            String courseName
    ) {
    }
}
