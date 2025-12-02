package com.hcms.department.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentRequest {
    @NotBlank(message = "Department name is required")
    private String name;

    private String description;
    private String code;
    private Long parentDepartmentId;
    private Long managerId;
    private Boolean active = true;
}

