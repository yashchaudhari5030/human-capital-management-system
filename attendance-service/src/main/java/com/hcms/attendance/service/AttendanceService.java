package com.hcms.attendance.service;

import com.hcms.attendance.client.EmployeeServiceClient;
import com.hcms.attendance.dto.AttendanceReportResponse;
import com.hcms.attendance.dto.AttendanceResponse;
import com.hcms.attendance.dto.ClockInRequest;
import com.hcms.attendance.dto.ClockOutRequest;
import com.hcms.attendance.entity.Attendance;
import com.hcms.attendance.exception.BadRequestException;
import com.hcms.attendance.exception.ResourceNotFoundException;
import com.hcms.attendance.repository.AttendanceRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeServiceClient employeeServiceClient;
    private static final LocalTime EXPECTED_CLOCK_IN = LocalTime.of(9, 0);
    private static final LocalTime EXPECTED_CLOCK_OUT = LocalTime.of(18, 0);
    private static final int LATE_THRESHOLD_MINUTES = 15;

    @Transactional
    public AttendanceResponse clockIn(Long employeeId, ClockInRequest request) {
        log.info("Clock in for employee: {}", employeeId);

        // Validate employee exists
        try {
            employeeServiceClient.getEmployeeById(employeeId);
        } catch (FeignException.NotFound e) {
            throw new BadRequestException("Employee not found with id: " + employeeId);
        } catch (FeignException e) {
            log.error("Error calling employee service: {}", e.getMessage());
            throw new BadRequestException("Unable to validate employee");
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        Attendance attendance = attendanceRepository
                .findByEmployeeIdAndAttendanceDate(employeeId, today)
                .orElse(null);

        if (attendance != null && attendance.getClockInTime() != null) {
            throw new BadRequestException("Already clocked in for today");
        }

        if (attendance == null) {
            attendance = Attendance.builder()
                    .employeeId(employeeId)
                    .attendanceDate(today)
                    .clockInTime(now)
                    .remarks(request.getRemarks())
                    .build();
        } else {
            attendance.setClockInTime(now);
            if (request.getRemarks() != null) {
                attendance.setRemarks(request.getRemarks());
            }
        }

        // Check if late
        if (now.isAfter(EXPECTED_CLOCK_IN.plusMinutes(LATE_THRESHOLD_MINUTES))) {
            attendance.setIsLate(true);
            long lateMinutes = ChronoUnit.MINUTES.between(EXPECTED_CLOCK_IN, now);
            attendance.setLateMinutes((int) lateMinutes);
            log.warn("Employee {} clocked in late by {} minutes", employeeId, lateMinutes);
        }

        attendance = attendanceRepository.save(attendance);
        log.info("Employee {} clocked in at {}", employeeId, now);

        return mapToResponse(attendance);
    }

    @Transactional
    public AttendanceResponse clockOut(Long employeeId, ClockOutRequest request) {
        log.info("Clock out for employee: {}", employeeId);

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        Attendance attendance = attendanceRepository
                .findByEmployeeIdAndAttendanceDate(employeeId, today)
                .orElseThrow(() -> new ResourceNotFoundException("No clock-in record found for today"));

        if (attendance.getClockInTime() == null) {
            throw new BadRequestException("Cannot clock out without clocking in");
        }

        if (attendance.getClockOutTime() != null) {
            throw new BadRequestException("Already clocked out for today");
        }

        attendance.setClockOutTime(now);

        // Calculate total hours
        long minutes = ChronoUnit.MINUTES.between(attendance.getClockInTime(), now);
        double hours = minutes / 60.0;
        attendance.setTotalHours(hours);

        // Check if early departure
        if (now.isBefore(EXPECTED_CLOCK_OUT)) {
            attendance.setIsEarlyDeparture(true);
            long earlyMinutes = ChronoUnit.MINUTES.between(now, EXPECTED_CLOCK_OUT);
            attendance.setEarlyDepartureMinutes((int) earlyMinutes);
            log.warn("Employee {} clocked out early by {} minutes", employeeId, earlyMinutes);
        }

        if (request.getRemarks() != null) {
            attendance.setRemarks(request.getRemarks());
        }

        attendance = attendanceRepository.save(attendance);
        log.info("Employee {} clocked out at {}", employeeId, now);

        return mapToResponse(attendance);
    }

    public AttendanceResponse getTodayAttendance(Long employeeId) {
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository
                .findByEmployeeIdAndAttendanceDate(employeeId, today)
                .orElse(Attendance.builder()
                        .employeeId(employeeId)
                        .attendanceDate(today)
                        .build());
        return mapToResponse(attendance);
    }

    public AttendanceResponse getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id: " + id));
        return mapToResponse(attendance);
    }

    public Page<AttendanceResponse> getAttendanceByEmployee(Long employeeId, Pageable pageable) {
        return attendanceRepository.findByEmployeeId(employeeId, pageable)
                .map(this::mapToResponse);
    }

    public AttendanceReportResponse getAttendanceReport(Long employeeId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceRepository
                .findByEmployeeIdAndDateRange(employeeId, startDate, endDate);

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long presentDays = attendances.stream()
                .filter(a -> a.getClockInTime() != null)
                .count();
        long absentDays = totalDays - presentDays;
        long lateDays = attendances.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsLate()))
                .count();
        long earlyDepartureDays = attendances.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsEarlyDeparture()))
                .count();
        double totalHours = attendances.stream()
                .filter(a -> a.getTotalHours() != null)
                .mapToDouble(Attendance::getTotalHours)
                .sum();

        return AttendanceReportResponse.builder()
                .employeeId(employeeId)
                .startDate(startDate)
                .endDate(endDate)
                .totalDays(totalDays)
                .presentDays(presentDays)
                .absentDays(absentDays)
                .lateDays(lateDays)
                .earlyDepartureDays(earlyDepartureDays)
                .totalHours(totalHours)
                .attendances(attendances.stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    public List<AttendanceResponse> getLateAttendances(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findLateAttendances(employeeId, startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<AttendanceResponse> getEarlyDepartures(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findEarlyDepartures(employeeId, startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AttendanceResponse mapToResponse(Attendance attendance) {
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .employeeId(attendance.getEmployeeId())
                .attendanceDate(attendance.getAttendanceDate())
                .clockInTime(attendance.getClockInTime())
                .clockOutTime(attendance.getClockOutTime())
                .totalHours(attendance.getTotalHours())
                .isLate(attendance.getIsLate())
                .isEarlyDeparture(attendance.getIsEarlyDeparture())
                .lateMinutes(attendance.getLateMinutes())
                .earlyDepartureMinutes(attendance.getEarlyDepartureMinutes())
                .remarks(attendance.getRemarks())
                .createdAt(attendance.getCreatedAt())
                .updatedAt(attendance.getUpdatedAt())
                .build();
    }
}

