package com.hcms.department.controller;

import com.hcms.department.dto.DepartmentRequest;
import com.hcms.department.dto.DepartmentResponse;
import com.hcms.department.service.DepartmentService;
import com.hcms.department.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Slf4j
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        SecurityUtil.checkAdminAccess();
        log.info("Creating department: {}", request.getName());
        DepartmentResponse response = departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id) {
        DepartmentResponse response = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        List<DepartmentResponse> response = departmentService.getAllDepartments();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<DepartmentResponse>> getActiveDepartments() {
        List<DepartmentResponse> response = departmentService.getActiveDepartments();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<DepartmentResponse>> getSubDepartments(@PathVariable Long parentId) {
        List<DepartmentResponse> response = departmentService.getSubDepartments(parentId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request) {
        SecurityUtil.checkAdminAccess();
        log.info("Updating department with ID: {}", id);
        DepartmentResponse response = departmentService.updateDepartment(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        SecurityUtil.checkAdminAccess();
        log.info("Deleting department with ID: {}", id);
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}

