package com.hcms.leave.repository;

import com.hcms.leave.entity.LeaveBalance;
import com.hcms.leave.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    Optional<LeaveBalance> findByEmployeeIdAndLeaveType(Long employeeId, LeaveType leaveType);
    List<LeaveBalance> findByEmployeeId(Long employeeId);
    boolean existsByEmployeeIdAndLeaveType(Long employeeId, LeaveType leaveType);
}

