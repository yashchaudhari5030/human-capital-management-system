package com.hcms.payroll.dto;

import com.hcms.payroll.entity.PayrollStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollResponse {
    private Long id;
    private Long employeeId;
    private Integer payPeriodMonth;
    private Integer payPeriodYear;
    private BigDecimal baseSalary;
    private BigDecimal allowances;
    private BigDecimal bonus;
    private BigDecimal overtime;
    private BigDecimal grossSalary;
    private BigDecimal taxDeduction;
    private BigDecimal providentFund;
    private BigDecimal otherDeductions;
    private BigDecimal totalDeductions;
    private BigDecimal netSalary;
    private LocalDate paymentDate;
    private PayrollStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

