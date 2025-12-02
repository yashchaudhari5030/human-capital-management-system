package com.hcms.department.service;

import com.hcms.department.client.EmployeeServiceClient;
import com.hcms.department.dto.DepartmentRequest;
import com.hcms.department.dto.DepartmentResponse;
import com.hcms.department.entity.Department;
import com.hcms.department.exception.ResourceNotFoundException;
import com.hcms.department.repository.DepartmentRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeServiceClient employeeServiceClient;

    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        log.info("Creating department: {}", request.getName());

        if (request.getCode() != null && departmentRepository.findByCode(request.getCode()).isPresent()) {
            throw new IllegalArgumentException("Department code already exists");
        }

        if (departmentRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Department name already exists");
        }

        if (request.getParentDepartmentId() != null) {
            departmentRepository.findById(request.getParentDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent department not found"));
        }

        // Validate manager exists if provided
        if (request.getManagerId() != null) {
            try {
                employeeServiceClient.getEmployeeById(request.getManagerId());
            } catch (FeignException.NotFound e) {
                throw new IllegalArgumentException("Manager not found with id: " + request.getManagerId());
            } catch (FeignException e) {
                log.error("Error calling employee service: {}", e.getMessage());
                throw new IllegalArgumentException("Unable to validate manager");
            }
        }

        Department department = Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .code(request.getCode())
                .parentDepartmentId(request.getParentDepartmentId())
                .managerId(request.getManagerId())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        department = departmentRepository.save(department);
        log.info("Department created with ID: {}", department.getId());

        return mapToResponse(department);
    }

    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        return mapToResponse(department);
    }

    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<DepartmentResponse> getActiveDepartments() {
        return departmentRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<DepartmentResponse> getSubDepartments(Long parentId) {
        return departmentRepository.findByParentDepartmentId(parentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        log.info("Updating department with ID: {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        if (request.getCode() != null && !request.getCode().equals(department.getCode())) {
            if (departmentRepository.findByCode(request.getCode()).isPresent()) {
                throw new IllegalArgumentException("Department code already exists");
            }
        }

        if (!request.getName().equals(department.getName()) && 
            departmentRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Department name already exists");
        }

        if (request.getParentDepartmentId() != null && !request.getParentDepartmentId().equals(department.getParentDepartmentId())) {
            if (request.getParentDepartmentId().equals(id)) {
                throw new IllegalArgumentException("Department cannot be its own parent");
            }
            departmentRepository.findById(request.getParentDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent department not found"));
        }

        department.setName(request.getName());
        department.setDescription(request.getDescription());
        department.setCode(request.getCode());
        department.setParentDepartmentId(request.getParentDepartmentId());
        department.setManagerId(request.getManagerId());
        if (request.getActive() != null) {
            department.setActive(request.getActive());
        }

        department = departmentRepository.save(department);
        log.info("Department updated: {}", department.getName());

        return mapToResponse(department);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        log.info("Deleting department with ID: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        
        List<Department> subDepartments = departmentRepository.findByParentDepartmentId(id);
        if (!subDepartments.isEmpty()) {
            throw new IllegalArgumentException("Cannot delete department with sub-departments");
        }

        departmentRepository.delete(department);
        log.info("Department deleted: {}", department.getName());
    }

    private DepartmentResponse mapToResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .code(department.getCode())
                .parentDepartmentId(department.getParentDepartmentId())
                .managerId(department.getManagerId())
                .active(department.getActive())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }
}

