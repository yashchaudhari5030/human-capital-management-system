package com.hcms.attendance.client;

import com.hcms.attendance.client.dto.EmployeeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "employee-service", path = "/api/employees")
public interface EmployeeServiceClient {
    
    @GetMapping("/{id}")
    EmployeeResponse getEmployeeById(@PathVariable Long id);
}

