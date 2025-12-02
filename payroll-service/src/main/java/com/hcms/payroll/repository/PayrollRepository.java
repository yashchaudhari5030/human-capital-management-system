package com.hcms.payroll.repository;

import com.hcms.payroll.entity.Payroll;
import com.hcms.payroll.entity.PayrollStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    Optional<Payroll> findByEmployeeIdAndPayPeriodMonthAndPayPeriodYear(
            Long employeeId, Integer month, Integer year);
    
    Page<Payroll> findByEmployeeId(Long employeeId, Pageable pageable);
    
    @Query("SELECT p FROM Payroll p WHERE p.payPeriodMonth = :month AND p.payPeriodYear = :year")
    Page<Payroll> findByPayPeriod(@Param("month") Integer month, 
                                   @Param("year") Integer year, 
                                   Pageable pageable);
    
    List<Payroll> findByStatus(PayrollStatus status);
}

