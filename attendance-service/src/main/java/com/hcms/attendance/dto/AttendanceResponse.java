package com.hcms.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponse {
    private Long id;
    private Long employeeId;
    private LocalDate attendanceDate;
    private LocalTime clockInTime;
    private LocalTime clockOutTime;
    private Double totalHours;
    private Boolean isLate;
    private Boolean isEarlyDeparture;
    private Integer lateMinutes;
    private Integer earlyDepartureMinutes;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

