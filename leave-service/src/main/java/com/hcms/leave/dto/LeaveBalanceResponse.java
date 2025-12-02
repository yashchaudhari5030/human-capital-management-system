package com.hcms.leave.dto;

import com.hcms.leave.entity.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalanceResponse {
    private Long id;
    private Long employeeId;
    private LeaveType leaveType;
    private Integer totalDays;
    private Integer usedDays;
    private Integer pendingDays;
    private Integer availableDays;
}

