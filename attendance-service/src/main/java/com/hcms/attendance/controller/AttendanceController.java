package com.hcms.attendance.controller;

import com.hcms.attendance.dto.AttendanceReportResponse;
import com.hcms.attendance.dto.AttendanceResponse;
import com.hcms.attendance.dto.ClockInRequest;
import com.hcms.attendance.dto.ClockOutRequest;
import com.hcms.attendance.service.AttendanceService;
import com.hcms.attendance.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Slf4j
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/clock-in")
    public ResponseEntity<AttendanceResponse> clockIn(@Valid @RequestBody ClockInRequest request) {
        Long employeeId = SecurityUtil.getCurrentUserId();
        AttendanceResponse response = attendanceService.clockIn(employeeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/clock-out")
    public ResponseEntity<AttendanceResponse> clockOut(@Valid @RequestBody ClockOutRequest request) {
        Long employeeId = SecurityUtil.getCurrentUserId();
        AttendanceResponse response = attendanceService.clockOut(employeeId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/today")
    public ResponseEntity<AttendanceResponse> getTodayAttendance() {
        Long employeeId = SecurityUtil.getCurrentUserId();
        AttendanceResponse response = attendanceService.getTodayAttendance(employeeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceResponse> getAttendanceById(@PathVariable Long id) {
        AttendanceResponse response = attendanceService.getAttendanceById(id);
        SecurityUtil.checkAttendanceAccess(response.getEmployeeId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Page<AttendanceResponse>> getAttendanceByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        SecurityUtil.checkAttendanceAccess(employeeId);
        Pageable pageable = PageRequest.of(page, size);
        Page<AttendanceResponse> response = attendanceService.getAttendanceByEmployee(employeeId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}/report")
    public ResponseEntity<AttendanceReportResponse> getAttendanceReport(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        SecurityUtil.checkAttendanceAccess(employeeId);
        AttendanceReportResponse response = attendanceService.getAttendanceReport(employeeId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}/late")
    public ResponseEntity<List<AttendanceResponse>> getLateAttendances(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        SecurityUtil.checkManagerAccess();
        List<AttendanceResponse> response = attendanceService.getLateAttendances(employeeId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}/early-departure")
    public ResponseEntity<List<AttendanceResponse>> getEarlyDepartures(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        SecurityUtil.checkManagerAccess();
        List<AttendanceResponse> response = attendanceService.getEarlyDepartures(employeeId, startDate, endDate);
        return ResponseEntity.ok(response);
    }
}

