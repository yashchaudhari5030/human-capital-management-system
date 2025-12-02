package com.hcms.department.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponse {
    private Long id;
    private String name;
    private String description;
    private String code;
    private Long parentDepartmentId;
    private Long managerId;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

