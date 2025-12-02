package com.hcms.leave.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveApprovalRequest {
    @NotNull(message = "Status is required")
    private String status; // APPROVED or REJECTED

    private String rejectionReason;
}

