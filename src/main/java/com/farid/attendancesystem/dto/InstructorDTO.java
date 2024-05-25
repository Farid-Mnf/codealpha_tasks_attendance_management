package com.farid.attendancesystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstructorDTO {
    private UUID id;
    private String name;
    private String email;
    private String password;
    private List<CourseDTO> courseDTOS;
}
