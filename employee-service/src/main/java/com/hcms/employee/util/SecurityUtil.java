package com.hcms.employee.util;

import com.hcms.employee.exception.ForbiddenException;
import com.hcms.employee.exception.ResourceNotFoundException;
import com.hcms.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private static EmployeeRepository employeeRepository;

    public SecurityUtil(EmployeeRepository employeeRepository) {
        SecurityUtil.employeeRepository = employeeRepository;
    }

    public static String getCurrentUserRole() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest().getHeader("X-User-Role");
        }
        return null;
    }

    public static Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String userId = attributes.getRequest().getHeader("X-User-Id");
            return userId != null ? Long.parseLong(userId) : null;
        }
        return null;
    }

    public static void checkAdminAccess() {
        String role = getCurrentUserRole();
        if (role == null || (!role.equals("ADMIN") && !role.equals("SUPER_ADMIN"))) {
            throw new ForbiddenException("Access denied. Admin privileges required.");
        }
    }

    public static void checkManagerAccess() {
        String role = getCurrentUserRole();
        if (role == null || (!role.equals("ADMIN") && !role.equals("SUPER_ADMIN") && !role.equals("MANAGER"))) {
            throw new ForbiddenException("Access denied. Manager privileges required.");
        }
    }

    public static void checkAccess(Long employeeId) {
        String role = getCurrentUserRole();
        Long currentUserId = getCurrentUserId();

        if (role == null) {
            throw new ForbiddenException("Access denied. Authentication required.");
        }

        if (role.equals("ADMIN") || role.equals("SUPER_ADMIN")) {
            return; // Full access
        }

        if (role.equals("MANAGER")) {
            // Check if employee is in manager's team
            if (employeeRepository != null) {
                boolean isTeamMember = employeeRepository.findById(employeeId)
                        .map(emp -> emp.getManagerId() != null && emp.getManagerId().equals(currentUserId))
                        .orElse(false);
                if (!isTeamMember) {
                    throw new ForbiddenException("Access denied. You can only access your team members.");
                }
            }
            return;
        }

        if (role.equals("EMPLOYEE")) {
            // Check if accessing own record
            if (currentUserId == null || !currentUserId.equals(employeeId)) {
                throw new ForbiddenException("Access denied. You can only access your own information.");
            }
            return;
        }

        throw new ForbiddenException("Access denied.");
    }
}

