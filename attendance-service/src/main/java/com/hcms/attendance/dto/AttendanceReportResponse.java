package com.hcms.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceReportResponse {
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long totalDays;
    private Long presentDays;
    private Long absentDays;
    private Long lateDays;
    private Long earlyDepartureDays;
    private Double totalHours;
    private List<AttendanceResponse> attendances;
}

