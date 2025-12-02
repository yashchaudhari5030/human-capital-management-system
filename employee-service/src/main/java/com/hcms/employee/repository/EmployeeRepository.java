package com.hcms.employee.repository;

import com.hcms.employee.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmployeeId(String employeeId);
    Optional<Employee> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmployeeId(String employeeId);

    @Query("SELECT e FROM Employee e WHERE " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "e.employeeId LIKE CONCAT('%', :keyword, '%')")
    Page<Employee> searchEmployees(@Param("keyword") String keyword, Pageable pageable);

    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);
    Page<Employee> findByManagerId(Long managerId, Pageable pageable);
    Page<Employee> findByStatus(com.hcms.employee.entity.EmploymentStatus status, Pageable pageable);

    @Query("SELECT MAX(CAST(SUBSTRING(e.employeeId, 4) AS int)) FROM Employee e WHERE e.employeeId LIKE 'EMP%'")
    Integer findMaxEmployeeNumber();
}

