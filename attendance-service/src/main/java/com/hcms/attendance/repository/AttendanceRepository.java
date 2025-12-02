package com.hcms.attendance.repository;

import com.hcms.attendance.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate date);
    
    Page<Attendance> findByEmployeeId(Long employeeId, Pageable pageable);
    
    @Query("SELECT a FROM Attendance a WHERE a.employeeId = :employeeId AND " +
           "a.attendanceDate BETWEEN :startDate AND :endDate ORDER BY a.attendanceDate DESC")
    List<Attendance> findByEmployeeIdAndDateRange(@Param("employeeId") Long employeeId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);
    
    @Query("SELECT a FROM Attendance a WHERE a.attendanceDate BETWEEN :startDate AND :endDate")
    Page<Attendance> findByDateRange(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate,
                                     Pageable pageable);
    
    @Query("SELECT a FROM Attendance a WHERE a.employeeId = :employeeId AND a.isLate = true AND " +
           "a.attendanceDate BETWEEN :startDate AND :endDate")
    List<Attendance> findLateAttendances(@Param("employeeId") Long employeeId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
    
    @Query("SELECT a FROM Attendance a WHERE a.employeeId = :employeeId AND a.isEarlyDeparture = true AND " +
           "a.attendanceDate BETWEEN :startDate AND :endDate")
    List<Attendance> findEarlyDepartures(@Param("employeeId") Long employeeId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
}

