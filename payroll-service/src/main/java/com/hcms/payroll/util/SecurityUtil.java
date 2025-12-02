package com.hcms.payroll.util;

import com.hcms.payroll.exception.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class SecurityUtil {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    public static Long getCurrentUserId() {
        HttpServletRequest request = getRequest();
        String userIdStr = request.getHeader(USER_ID_HEADER);
        if (userIdStr == null || userIdStr.isEmpty()) {
            throw new ForbiddenException("User ID not found in request");
        }
        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new ForbiddenException("Invalid user ID format");
        }
    }

    public static String getCurrentUserRole() {
        HttpServletRequest request = getRequest();
        String role = request.getHeader(USER_ROLE_HEADER);
        if (role == null || role.isEmpty()) {
            throw new ForbiddenException("User role not found in request");
        }
        return role;
    }

    public static void checkPayrollAccess() {
        String role = getCurrentUserRole();
        if (!isPayrollAdmin(role)) {
            throw new ForbiddenException("Access denied. HR or Payroll Admin role required.");
        }
    }

    public static void checkPayrollAccess(Long employeeId) {
        Long currentUserId = getCurrentUserId();
        String role = getCurrentUserRole();
        
        if (isPayrollAdmin(role)) {
            return; // HR and Payroll Admins can access any payroll
        }
        
        if (!currentUserId.equals(employeeId)) {
            throw new ForbiddenException("Access denied. You can only access your own payroll.");
        }
    }

    private static boolean isPayrollAdmin(String role) {
        return "ADMIN".equals(role) || "SUPER_ADMIN".equals(role) || "PAYROLL_ADMIN".equals(role);
    }

    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("Request context not available");
        }
        return attributes.getRequest();
    }
}

