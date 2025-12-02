package com.hcms.leave.service;

import com.hcms.leave.dto.LeaveBalanceResponse;
import com.hcms.leave.entity.LeaveBalance;
import com.hcms.leave.entity.LeaveType;
import com.hcms.leave.exception.ResourceNotFoundException;
import com.hcms.leave.repository.LeaveBalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;

    public List<LeaveBalanceResponse> getLeaveBalances(Long employeeId) {
        List<LeaveBalance> balances = leaveBalanceRepository.findByEmployeeId(employeeId);
        
        // Ensure all leave types have balances
        for (LeaveType type : LeaveType.values()) {
            if (balances.stream().noneMatch(b -> b.getLeaveType() == type)) {
                LeaveBalance balance = createDefaultBalance(employeeId, type);
                balances.add(balance);
            }
        }
        
        return balances.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public LeaveBalanceResponse getLeaveBalance(Long employeeId, LeaveType leaveType) {
        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveType(employeeId, leaveType)
                .orElseGet(() -> createDefaultBalance(employeeId, leaveType));
        return mapToResponse(balance);
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

    private LeaveBalanceResponse mapToResponse(LeaveBalance balance) {
        return LeaveBalanceResponse.builder()
                .id(balance.getId())
                .employeeId(balance.getEmployeeId())
                .leaveType(balance.getLeaveType())
                .totalDays(balance.getTotalDays())
                .usedDays(balance.getUsedDays())
                .pendingDays(balance.getPendingDays())
                .availableDays(balance.getAvailableDays())
                .build();
    }
}

