package com.hcms.employee.controller;

import com.hcms.employee.dto.EmployeeRequest;
import com.hcms.employee.dto.EmployeeResponse;
import com.hcms.employee.service.EmployeeService;
import com.hcms.employee.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        SecurityUtil.checkAdminAccess();
        log.info("Creating employee: {}", request.getEmail());
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        SecurityUtil.checkAccess(id);
        EmployeeResponse response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee-id/{employeeId}")
    public ResponseEntity<EmployeeResponse> getEmployeeByEmployeeId(@PathVariable String employeeId) {
        EmployeeResponse response = employeeService.getEmployeeByEmployeeId(employeeId);
        SecurityUtil.checkAccess(response.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeResponse>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDir) {
        SecurityUtil.checkManagerAccess();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, sortBy));
        Page<EmployeeResponse> response = employeeService.getAllEmployees(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<EmployeeResponse>> searchEmployees(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        SecurityUtil.checkManagerAccess();
        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeeResponse> response = employeeService.searchEmployees(keyword, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<Page<EmployeeResponse>> getEmployeesByDepartment(
            @PathVariable Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        SecurityUtil.checkManagerAccess();
        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeeResponse> response = employeeService.getEmployeesByDepartment(departmentId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<Page<EmployeeResponse>> getEmployeesByManager(
            @PathVariable Long managerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        SecurityUtil.checkManagerAccess();
        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeeResponse> response = employeeService.getEmployeesByManager(managerId, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {
        SecurityUtil.checkAdminAccess();
        log.info("Updating employee with ID: {}", id);
        EmployeeResponse response = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        SecurityUtil.checkAdminAccess();
        log.info("Deleting employee with ID: {}", id);
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}

