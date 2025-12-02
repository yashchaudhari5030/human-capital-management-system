package com.hcms.employee.service;

import com.hcms.employee.dto.EmployeeRequest;
import com.hcms.employee.dto.EmployeeResponse;
import com.hcms.employee.entity.Employee;
import com.hcms.employee.entity.EmploymentStatus;
import com.hcms.employee.exception.ResourceNotFoundException;
import com.hcms.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        log.info("Creating new employee: {}", request.getEmail());

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        String employeeId = generateEmployeeId();

        Employee employee = Employee.builder()
                .employeeId(employeeId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .country(request.getCountry())
                .hireDate(request.getHireDate())
                .designation(request.getDesignation())
                .departmentId(request.getDepartmentId())
                .managerId(request.getManagerId())
                .status(request.getStatus() != null ? request.getStatus() : EmploymentStatus.ACTIVE)
                .build();

        employee = employeeRepository.save(employee);
        log.info("Employee created with ID: {}", employee.getEmployeeId());

        return mapToResponse(employee);
    }

    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return mapToResponse(employee);
    }

    public EmployeeResponse getEmployeeByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with employeeId: " + employeeId));
        return mapToResponse(employee);
    }

    public Page<EmployeeResponse> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(this::mapToResponse);
    }

    public Page<EmployeeResponse> searchEmployees(String keyword, Pageable pageable) {
        return employeeRepository.searchEmployees(keyword, pageable).map(this::mapToResponse);
    }

    public Page<EmployeeResponse> getEmployeesByDepartment(Long departmentId, Pageable pageable) {
        return employeeRepository.findByDepartmentId(departmentId, pageable).map(this::mapToResponse);
    }

    public Page<EmployeeResponse> getEmployeesByManager(Long managerId, Pageable pageable) {
        return employeeRepository.findByManagerId(managerId, pageable).map(this::mapToResponse);
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        log.info("Updating employee with ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (!employee.getEmail().equals(request.getEmail()) && employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setGender(request.getGender());
        employee.setAddress(request.getAddress());
        employee.setCity(request.getCity());
        employee.setState(request.getState());
        employee.setZipCode(request.getZipCode());
        employee.setCountry(request.getCountry());
        employee.setHireDate(request.getHireDate());
        employee.setDesignation(request.getDesignation());
        employee.setDepartmentId(request.getDepartmentId());
        employee.setManagerId(request.getManagerId());
        if (request.getStatus() != null) {
            employee.setStatus(request.getStatus());
        }

        employee = employeeRepository.save(employee);
        log.info("Employee updated: {}", employee.getEmployeeId());

        return mapToResponse(employee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        log.info("Deleting employee with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        employeeRepository.delete(employee);
        log.info("Employee deleted: {}", employee.getEmployeeId());
    }

    private String generateEmployeeId() {
        Integer maxNumber = employeeRepository.findMaxEmployeeNumber();
        int nextNumber = (maxNumber == null) ? 1 : maxNumber + 1;
        return String.format("EMP%03d", nextNumber);
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeId(employee.getEmployeeId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phoneNumber(employee.getPhoneNumber())
                .dateOfBirth(employee.getDateOfBirth())
                .gender(employee.getGender())
                .address(employee.getAddress())
                .city(employee.getCity())
                .state(employee.getState())
                .zipCode(employee.getZipCode())
                .country(employee.getCountry())
                .hireDate(employee.getHireDate())
                .designation(employee.getDesignation())
                .departmentId(employee.getDepartmentId())
                .managerId(employee.getManagerId())
                .status(employee.getStatus())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}

