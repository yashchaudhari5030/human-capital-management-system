package com.hcms.leave.repository;

import com.hcms.leave.entity.Leave;
import com.hcms.leave.entity.LeaveStatus;
import com.hcms.leave.entity.LeaveType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    Page<Leave> findByEmployeeId(Long employeeId, Pageable pageable);
    Page<Leave> findByStatus(LeaveStatus status, Pageable pageable);
    Page<Leave> findByApprovedBy(Long approvedBy, Pageable pageable);
    
    @Query("SELECT l FROM Leave l WHERE l.employeeId = :employeeId AND " +
           "l.status = :status AND " +
           "((l.startDate <= :endDate AND l.endDate >= :startDate))")
    List<Leave> findOverlappingLeaves(@Param("employeeId") Long employeeId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      @Param("status") LeaveStatus status);
    
    @Query("SELECT l FROM Leave l WHERE l.employeeId = :employeeId AND " +
           "l.leaveType = :leaveType AND l.status = 'APPROVED'")
    List<Leave> findApprovedLeavesByType(@Param("employeeId") Long employeeId,
                                         @Param("leaveType") LeaveType leaveType);
}

