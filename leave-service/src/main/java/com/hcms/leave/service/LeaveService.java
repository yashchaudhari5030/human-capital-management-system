package com.hcms.leave.service;

import com.hcms.leave.client.EmployeeServiceClient;
import com.hcms.leave.client.NotificationServiceClient;
import com.hcms.leave.client.dto.NotificationRequest;
import com.hcms.leave.dto.LeaveApprovalRequest;
import com.hcms.leave.dto.LeaveRequest;
import com.hcms.leave.dto.LeaveResponse;
import com.hcms.leave.entity.Leave;
import com.hcms.leave.entity.LeaveBalance;
import com.hcms.leave.entity.LeaveStatus;
import com.hcms.leave.entity.LeaveType;
import com.hcms.leave.exception.BadRequestException;
import com.hcms.leave.exception.ResourceNotFoundException;
import com.hcms.leave.repository.LeaveBalanceRepository;
import com.hcms.leave.repository.LeaveRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeServiceClient employeeServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    @Transactional
    public LeaveResponse applyLeave(LeaveRequest request) {
        log.info("Applying leave for employee: {}", request.getEmployeeId());

        // Validate employee exists
        try {
            employeeServiceClient.getEmployeeById(request.getEmployeeId());
        } catch (FeignException.NotFound e) {
            throw new BadRequestException("Employee not found with id: " + request.getEmployeeId());
        } catch (FeignException e) {
            log.error("Error calling employee service: {}", e.getMessage());
            throw new BadRequestException("Unable to validate employee");
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("Start date cannot be after end date");
        }

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Start date cannot be in the past");
        }

        // Check for overlapping leaves
        List<Leave> overlappingLeaves = leaveRepository.findOverlappingLeaves(
                request.getEmployeeId(),
                request.getStartDate(),
                request.getEndDate(),
                LeaveStatus.PENDING
        );
        if (!overlappingLeaves.isEmpty()) {
            throw new BadRequestException("You have a pending leave request for the same period");
        }

        // Calculate number of days
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        // Check leave balance
        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveType(request.getEmployeeId(), request.getLeaveType())
                .orElseGet(() -> createDefaultBalance(request.getEmployeeId(), request.getLeaveType()));

        if (balance.getAvailableDays() < days) {
            throw new BadRequestException("Insufficient leave balance. Available: " + balance.getAvailableDays());
        }

        // Create leave request
        Leave leave = Leave.builder()
                .employeeId(request.getEmployeeId())
                .leaveType(request.getLeaveType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .numberOfDays((int) days)
                .reason(request.getReason())
                .status(LeaveStatus.PENDING)
                .build();

        leave = leaveRepository.save(leave);

        // Update pending days
        balance.setPendingDays(balance.getPendingDays() + (int) days);
        leaveBalanceRepository.save(balance);

        // Send notification to manager
        try {
            sendLeaveNotification(leave, "LEAVE_APPLIED", "New leave request submitted");
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
        }

        log.info("Leave request created: {}", leave.getId());
        return mapToResponse(leave);
    }

    @Transactional
    public LeaveResponse approveLeave(Long leaveId, Long approverId, LeaveApprovalRequest request) {
        log.info("Processing leave approval: {} by {}", leaveId, approverId);

        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with id: " + leaveId));

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Leave request is not pending");
        }

        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveType(leave.getEmployeeId(), leave.getLeaveType())
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));

        if ("APPROVED".equalsIgnoreCase(request.getStatus())) {
            leave.setStatus(LeaveStatus.APPROVED);
            leave.setApprovedBy(approverId);
            leave.setApprovedAt(LocalDateTime.now());

            // Update balance: move from pending to used
            balance.setPendingDays(balance.getPendingDays() - leave.getNumberOfDays());
            balance.setUsedDays(balance.getUsedDays() + leave.getNumberOfDays());
        } else if ("REJECTED".equalsIgnoreCase(request.getStatus())) {
            leave.setStatus(LeaveStatus.REJECTED);
            leave.setApprovedBy(approverId);
            leave.setApprovedAt(LocalDateTime.now());
            leave.setRejectionReason(request.getRejectionReason());

            // Update balance: remove from pending
            balance.setPendingDays(balance.getPendingDays() - leave.getNumberOfDays());
        } else {
            throw new BadRequestException("Invalid status. Use APPROVED or REJECTED");
        }

        leaveBalanceRepository.save(balance);
        leave = leaveRepository.save(leave);

        // Send notification to employee
        try {
            String notificationType = "APPROVED".equalsIgnoreCase(request.getStatus()) 
                    ? "LEAVE_APPROVED" : "LEAVE_REJECTED";
            String message = "APPROVED".equalsIgnoreCase(request.getStatus())
                    ? "Your leave request has been approved"
                    : "Your leave request has been rejected";
            sendLeaveNotification(leave, notificationType, message);
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
        }

        log.info("Leave {} {} by {}", leaveId, request.getStatus(), approverId);
        return mapToResponse(leave);
    }

    public LeaveResponse getLeaveById(Long id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with id: " + id));
        return mapToResponse(leave);
    }

    public Page<LeaveResponse> getLeavesByEmployee(Long employeeId, Pageable pageable) {
        return leaveRepository.findByEmployeeId(employeeId, pageable)
                .map(this::mapToResponse);
    }

    public Page<LeaveResponse> getPendingLeaves(Pageable pageable) {
        return leaveRepository.findByStatus(LeaveStatus.PENDING, pageable)
                .map(this::mapToResponse);
    }

    public Page<LeaveResponse> getLeavesByApprover(Long approverId, Pageable pageable) {
        return leaveRepository.findByApprovedBy(approverId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public void cancelLeave(Long leaveId, Long employeeId) {
        log.info("Cancelling leave: {} by employee: {}", leaveId, employeeId);

        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with id: " + leaveId));

        if (!leave.getEmployeeId().equals(employeeId)) {
            throw new BadRequestException("You can only cancel your own leave requests");
        }

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Only pending leaves can be cancelled");
        }

        leave.setStatus(LeaveStatus.CANCELLED);

        // Update balance: remove from pending
        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveType(leave.getEmployeeId(), leave.getLeaveType())
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));

        balance.setPendingDays(balance.getPendingDays() - leave.getNumberOfDays());
        leaveBalanceRepository.save(balance);
        leaveRepository.save(leave);

        log.info("Leave cancelled: {}", leaveId);
    }

    private LeaveBalance createDefaultBalance(Long employeeId, LeaveType leaveType) {
        int defaultDays = switch (leaveType) {
            case ANNUAL -> 20;
            case SICK -> 10;
            case CASUAL -> 5;
            default -> 0;
        };

        LeaveBalance balance = LeaveBalance.builder()
                .employeeId(employeeId)
                .leaveType(leaveType)
                .totalDays(defaultDays)
                .usedDays(0)
                .pendingDays(0)
                .build();

        return leaveBalanceRepository.save(balance);
    }

    private void sendLeaveNotification(Leave leave, String notificationType, String message) {
        try {
            // Get employee details to find manager
            var employee = employeeServiceClient.getEmployeeById(leave.getEmployeeId());
            Long recipientId = employee.getManagerId() != null ? employee.getManagerId() : leave.getEmployeeId();

            NotificationRequest notificationRequest = NotificationRequest.builder()
                    .recipientId(recipientId)
                    .notificationType(notificationType)
                    .channel("IN_APP")
                    .subject("Leave Request Update")
                    .message(message + " - Leave ID: " + leave.getId())
                    .build();

            notificationServiceClient.createNotification(notificationRequest);
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage());
        }
    }

    private LeaveResponse mapToResponse(Leave leave) {
        return LeaveResponse.builder()
                .id(leave.getId())
                .employeeId(leave.getEmployeeId())
                .leaveType(leave.getLeaveType())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .numberOfDays(leave.getNumberOfDays())
                .reason(leave.getReason())
                .status(leave.getStatus())
                .approvedBy(leave.getApprovedBy())
                .approvedAt(leave.getApprovedAt())
                .rejectionReason(leave.getRejectionReason())
                .createdAt(leave.getCreatedAt())
                .updatedAt(leave.getUpdatedAt())
                .build();
    }
}

