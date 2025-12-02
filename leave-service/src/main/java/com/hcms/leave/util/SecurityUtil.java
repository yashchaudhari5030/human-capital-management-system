package com.hcms.leave.util;

import com.hcms.leave.exception.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@Slf4j
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

    public static void checkManagerAccess() {
        String role = getCurrentUserRole();
        if (!isManagerOrAdmin(role)) {
            throw new ForbiddenException("Access denied. Manager or Admin role required.");
        }
    }

    public static void checkAdminAccess() {
        String role = getCurrentUserRole();
        if (!isAdmin(role)) {
            throw new ForbiddenException("Access denied. Admin role required.");
        }
    }

    public static void checkLeaveAccess(Long employeeId) {
        Long currentUserId = getCurrentUserId();
        String role = getCurrentUserRole();
        
        if (isAdmin(role) || isManagerOrAdmin(role)) {
            return; // Admins and managers can access any leave
        }
        
        if (!currentUserId.equals(employeeId)) {
            throw new ForbiddenException("Access denied. You can only access your own leaves.");
        }
    }

    private static boolean isAdmin(String role) {
        return "ADMIN".equals(role) || "SUPER_ADMIN".equals(role);
    }

    private static boolean isManagerOrAdmin(String role) {
        return "MANAGER".equals(role) || isAdmin(role);
    }

    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("Request context not available");
        }
        return attributes.getRequest();
    }
}

