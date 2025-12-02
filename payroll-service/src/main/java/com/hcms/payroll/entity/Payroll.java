package com.hcms.payroll.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payrolls", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "pay_period_month", "pay_period_year"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "pay_period_month", nullable = false)
    private Integer payPeriodMonth;

    @Column(name = "pay_period_year", nullable = false)
    private Integer payPeriodYear;

    @Column(name = "base_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "allowances", precision = 10, scale = 2)
    private BigDecimal allowances = BigDecimal.ZERO;

    @Column(name = "bonus", precision = 10, scale = 2)
    private BigDecimal bonus = BigDecimal.ZERO;

    @Column(name = "overtime", precision = 10, scale = 2)
    private BigDecimal overtime = BigDecimal.ZERO;

    @Column(name = "gross_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal grossSalary;

    @Column(name = "tax_deduction", precision = 10, scale = 2)
    private BigDecimal taxDeduction = BigDecimal.ZERO;

    @Column(name = "provident_fund", precision = 10, scale = 2)
    private BigDecimal providentFund = BigDecimal.ZERO;

    @Column(name = "other_deductions", precision = 10, scale = 2)
    private BigDecimal otherDeductions = BigDecimal.ZERO;

    @Column(name = "total_deductions", precision = 10, scale = 2)
    private BigDecimal totalDeductions = BigDecimal.ZERO;

    @Column(name = "net_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal netSalary;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PayrollStatus status = PayrollStatus.PENDING;

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

