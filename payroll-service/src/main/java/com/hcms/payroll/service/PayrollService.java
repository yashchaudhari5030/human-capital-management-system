package com.hcms.payroll.service;

import com.hcms.payroll.client.EmployeeServiceClient;
import com.hcms.payroll.dto.PayrollRequest;
import com.hcms.payroll.dto.PayrollResponse;
import com.hcms.payroll.entity.Payroll;
import com.hcms.payroll.entity.PayrollStatus;
import com.hcms.payroll.exception.BadRequestException;
import com.hcms.payroll.exception.ResourceNotFoundException;
import com.hcms.payroll.repository.PayrollRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeServiceClient employeeServiceClient;
    private static final BigDecimal TAX_RATE = new BigDecimal("0.20"); // 20% tax
    private static final BigDecimal PF_RATE = new BigDecimal("0.12"); // 12% PF

    @Transactional
    public PayrollResponse createPayroll(PayrollRequest request) {
        log.info("Creating payroll for employee: {}", request.getEmployeeId());

        // Validate employee exists
        try {
            employeeServiceClient.getEmployeeById(request.getEmployeeId());
        } catch (FeignException.NotFound e) {
            throw new BadRequestException("Employee not found with id: " + request.getEmployeeId());
        } catch (FeignException e) {
            log.error("Error calling employee service: {}", e.getMessage());
            throw new BadRequestException("Unable to validate employee");
        }

        YearMonth currentPeriod = YearMonth.now();
        int month = request.getPaymentDate() != null 
                ? request.getPaymentDate().getMonthValue() 
                : currentPeriod.getMonthValue();
        int year = request.getPaymentDate() != null 
                ? request.getPaymentDate().getYear() 
                : currentPeriod.getYear();

        // Check if payroll already exists for this period
        if (payrollRepository.findByEmployeeIdAndPayPeriodMonthAndPayPeriodYear(
                request.getEmployeeId(), month, year).isPresent()) {
            throw new BadRequestException("Payroll already exists for this period");
        }

        BigDecimal baseSalary = request.getBaseSalary();
        BigDecimal allowances = request.getAllowances() != null ? request.getAllowances() : BigDecimal.ZERO;
        BigDecimal bonus = request.getBonus() != null ? request.getBonus() : BigDecimal.ZERO;
        BigDecimal overtime = request.getOvertime() != null ? request.getOvertime() : BigDecimal.ZERO;

        // Calculate gross salary
        BigDecimal grossSalary = baseSalary
                .add(allowances)
                .add(bonus)
                .add(overtime);

        // Calculate deductions
        BigDecimal taxDeduction = calculateTax(grossSalary);
        BigDecimal providentFund = request.getProvidentFund() != null 
                ? request.getProvidentFund() 
                : baseSalary.multiply(PF_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal otherDeductions = request.getOtherDeductions() != null 
                ? request.getOtherDeductions() 
                : BigDecimal.ZERO;

        BigDecimal totalDeductions = taxDeduction
                .add(providentFund)
                .add(otherDeductions);

        // Calculate net salary
        BigDecimal netSalary = grossSalary.subtract(totalDeductions);

        Payroll payroll = Payroll.builder()
                .employeeId(request.getEmployeeId())
                .payPeriodMonth(month)
                .payPeriodYear(year)
                .baseSalary(baseSalary)
                .allowances(allowances)
                .bonus(bonus)
                .overtime(overtime)
                .grossSalary(grossSalary)
                .taxDeduction(taxDeduction)
                .providentFund(providentFund)
                .otherDeductions(otherDeductions)
                .totalDeductions(totalDeductions)
                .netSalary(netSalary)
                .paymentDate(request.getPaymentDate() != null ? request.getPaymentDate() : LocalDate.now())
                .status(PayrollStatus.PENDING)
                .build();

        payroll = payrollRepository.save(payroll);
        log.info("Payroll created: {}", payroll.getId());

        return mapToResponse(payroll);
    }

    @Transactional
    public PayrollResponse processPayroll(Long id) {
        log.info("Processing payroll: {}", id);

        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll not found with id: " + id));

        if (payroll.getStatus() != PayrollStatus.PENDING) {
            throw new BadRequestException("Payroll is not in PENDING status");
        }

        payroll.setStatus(PayrollStatus.PROCESSED);
        payroll = payrollRepository.save(payroll);

        log.info("Payroll processed: {}", id);
        return mapToResponse(payroll);
    }

    @Transactional
    public PayrollResponse markAsPaid(Long id) {
        log.info("Marking payroll as paid: {}", id);

        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll not found with id: " + id));

        if (payroll.getStatus() != PayrollStatus.PROCESSED) {
            throw new BadRequestException("Payroll must be processed before marking as paid");
        }

        payroll.setStatus(PayrollStatus.PAID);
        payroll = payrollRepository.save(payroll);

        log.info("Payroll marked as paid: {}", id);
        return mapToResponse(payroll);
    }

    public PayrollResponse getPayrollById(Long id) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll not found with id: " + id));
        return mapToResponse(payroll);
    }

    public Page<PayrollResponse> getPayrollsByEmployee(Long employeeId, Pageable pageable) {
        return payrollRepository.findByEmployeeId(employeeId, pageable)
                .map(this::mapToResponse);
    }

    public Page<PayrollResponse> getPayrollsByPeriod(Integer month, Integer year, Pageable pageable) {
        return payrollRepository.findByPayPeriod(month, year, pageable)
                .map(this::mapToResponse);
    }

    private BigDecimal calculateTax(BigDecimal grossSalary) {
        // Simple tax calculation - 20% of gross salary above threshold
        BigDecimal threshold = new BigDecimal("50000");
        if (grossSalary.compareTo(threshold) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal taxableAmount = grossSalary.subtract(threshold);
        return taxableAmount.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    private PayrollResponse mapToResponse(Payroll payroll) {
        return PayrollResponse.builder()
                .id(payroll.getId())
                .employeeId(payroll.getEmployeeId())
                .payPeriodMonth(payroll.getPayPeriodMonth())
                .payPeriodYear(payroll.getPayPeriodYear())
                .baseSalary(payroll.getBaseSalary())
                .allowances(payroll.getAllowances())
                .bonus(payroll.getBonus())
                .overtime(payroll.getOvertime())
                .grossSalary(payroll.getGrossSalary())
                .taxDeduction(payroll.getTaxDeduction())
                .providentFund(payroll.getProvidentFund())
                .otherDeductions(payroll.getOtherDeductions())
                .totalDeductions(payroll.getTotalDeductions())
                .netSalary(payroll.getNetSalary())
                .paymentDate(payroll.getPaymentDate())
                .status(payroll.getStatus())
                .createdAt(payroll.getCreatedAt())
                .updatedAt(payroll.getUpdatedAt())
                .build();
    }
}

