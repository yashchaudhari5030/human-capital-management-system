package com.hcms.employee.dto;

import com.hcms.employee.entity.EmploymentStatus;
import com.hcms.employee.entity.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Address is required")
    private String address;

    private String city;
    private String state;
    private String zipCode;
    private String country;

    @NotNull(message = "Hire date is required")
    private LocalDate hireDate;

    private String designation;
    private Long departmentId;
    private Long managerId;

    private EmploymentStatus status = EmploymentStatus.ACTIVE;
}

