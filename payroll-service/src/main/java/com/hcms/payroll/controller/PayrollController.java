package com.hcms.payroll.controller;

import com.hcms.payroll.dto.PayrollRequest;
import com.hcms.payroll.dto.PayrollResponse;
import com.hcms.payroll.service.PayrollService;
import com.hcms.payroll.service.PdfService;
import com.hcms.payroll.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
@Slf4j
public class PayrollController {

    private final PayrollService payrollService;
    private final PdfService pdfService;

    @PostMapping
    public ResponseEntity<PayrollResponse> createPayroll(@Valid @RequestBody PayrollRequest request) {
        SecurityUtil.checkPayrollAccess();
        log.info("Creating payroll for employee: {}", request.getEmployeeId());
        PayrollResponse response = payrollService.createPayroll(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayrollResponse> getPayrollById(@PathVariable Long id) {
        PayrollResponse response = payrollService.getPayrollById(id);
        SecurityUtil.checkPayrollAccess(response.getEmployeeId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Page<PayrollResponse>> getPayrollsByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        SecurityUtil.checkPayrollAccess(employeeId);
        Pageable pageable = PageRequest.of(page, size);
        Page<PayrollResponse> response = payrollService.getPayrollsByEmployee(employeeId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/period")
    public ResponseEntity<Page<PayrollResponse>> getPayrollsByPeriod(
            @RequestParam Integer month,
            @RequestParam Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        SecurityUtil.checkPayrollAccess();
        Pageable pageable = PageRequest.of(page, size);
        Page<PayrollResponse> response = payrollService.getPayrollsByPeriod(month, year, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<PayrollResponse> processPayroll(@PathVariable Long id) {
        SecurityUtil.checkPayrollAccess();
        PayrollResponse response = payrollService.processPayroll(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/mark-paid")
    public ResponseEntity<PayrollResponse> markAsPaid(@PathVariable Long id) {
        SecurityUtil.checkPayrollAccess();
        PayrollResponse response = payrollService.markAsPaid(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/payslip")
    public ResponseEntity<byte[]> generatePayslip(@PathVariable Long id) {
        PayrollResponse payroll = payrollService.getPayrollById(id);
        SecurityUtil.checkPayrollAccess(payroll.getEmployeeId());
        
        byte[] pdfBytes = pdfService.generatePayslip(payroll);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", 
                String.format("payslip_%d_%02d_%d.pdf", 
                        payroll.getEmployeeId(), 
                        payroll.getPayPeriodMonth(), 
                        payroll.getPayPeriodYear()));
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}

