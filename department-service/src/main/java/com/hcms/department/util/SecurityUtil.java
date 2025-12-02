package com.hcms.department.util;

import com.hcms.department.exception.ForbiddenException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SecurityUtil {

    public static String getCurrentUserRole() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest().getHeader("X-User-Role");
        }
        return null;
    }

    public static void checkAdminAccess() {
        String role = getCurrentUserRole();
        if (role == null || (!role.equals("ADMIN") && !role.equals("SUPER_ADMIN"))) {
            throw new ForbiddenException("Access denied. Admin privileges required.");
        }
    }
}

