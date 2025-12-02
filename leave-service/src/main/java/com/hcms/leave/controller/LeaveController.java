package com.hcms.leave.controller;

import com.hcms.leave.dto.LeaveApprovalRequest;
import com.hcms.leave.dto.LeaveBalanceResponse;
import com.hcms.leave.dto.LeaveRequest;
import com.hcms.leave.dto.LeaveResponse;
import com.hcms.leave.service.LeaveBalanceService;
import com.hcms.leave.service.LeaveService;
import com.hcms.leave.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@Slf4j
public class LeaveController {

    private final LeaveService leaveService;
    private final LeaveBalanceService leaveBalanceService;

    @PostMapping
    public ResponseEntity<LeaveResponse> applyLeave(@Valid @RequestBody LeaveRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        request.setEmployeeId(userId);
        log.info("Employee {} applying for leave", userId);
        LeaveResponse response = leaveService.applyLeave(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveResponse> getLeaveById(@PathVariable Long id) {
        LeaveResponse response = leaveService.getLeaveById(id);
        SecurityUtil.checkLeaveAccess(response.getEmployeeId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Page<LeaveResponse>> getLeavesByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        SecurityUtil.checkLeaveAccess(employeeId);
        Pageable pageable = PageRequest.of(page, size);
        Page<LeaveResponse> response = leaveService.getLeavesByEmployee(employeeId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<LeaveResponse>> getPendingLeaves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        SecurityUtil.checkManagerAccess();
        Pageable pageable = PageRequest.of(page, size);
        Page<LeaveResponse> response = leaveService.getPendingLeaves(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<LeaveResponse> approveLeave(
            @PathVariable Long id,
            @Valid @RequestBody LeaveApprovalRequest request) {
        SecurityUtil.checkManagerAccess();
        Long approverId = SecurityUtil.getCurrentUserId();
        LeaveResponse response = leaveService.approveLeave(id, approverId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelLeave(@PathVariable Long id) {
        Long employeeId = SecurityUtil.getCurrentUserId();
        leaveService.cancelLeave(id, employeeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/balance/{employeeId}")
    public ResponseEntity<List<LeaveBalanceResponse>> getLeaveBalances(@PathVariable Long employeeId) {
        SecurityUtil.checkLeaveAccess(employeeId);
        List<LeaveBalanceResponse> response = leaveBalanceService.getLeaveBalances(employeeId);
        return ResponseEntity.ok(response);
    }
}

