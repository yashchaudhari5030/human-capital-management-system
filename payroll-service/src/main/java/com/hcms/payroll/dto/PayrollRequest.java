package com.hcms.payroll.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollRequest {
    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Base salary is required")
    private BigDecimal baseSalary;

    private BigDecimal allowances;
    private BigDecimal bonus;
    private BigDecimal overtime;
    private BigDecimal providentFund;
    private BigDecimal otherDeductions;
    private LocalDate paymentDate;
}

