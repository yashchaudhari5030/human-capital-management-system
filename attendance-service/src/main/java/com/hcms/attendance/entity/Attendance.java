package com.hcms.attendance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "attendances", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "attendance_date"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "clock_in_time")
    private LocalTime clockInTime;

    @Column(name = "clock_out_time")
    private LocalTime clockOutTime;

    @Column(name = "total_hours")
    private Double totalHours;

    @Column(name = "is_late")
    private Boolean isLate = false;

    @Column(name = "is_early_departure")
    private Boolean isEarlyDeparture = false;

    @Column(name = "late_minutes")
    private Integer lateMinutes = 0;

    @Column(name = "early_departure_minutes")
    private Integer earlyDepartureMinutes = 0;

    private String remarks;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

