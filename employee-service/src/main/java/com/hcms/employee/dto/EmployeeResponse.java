package com.hcms.employee.dto;

import com.hcms.employee.entity.EmploymentStatus;
import com.hcms.employee.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {
    private Long id;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private LocalDate hireDate;
    private String designation;
    private Long departmentId;
    private Long managerId;
    private EmploymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

