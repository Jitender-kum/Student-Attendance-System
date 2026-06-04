package com.api.service;

import com.api.dto.DepartmentDtos;

import java.util.List;

public interface DepartmentService {

    DepartmentDtos.DepartmentResponse create(DepartmentDtos.DepartmentRequest request);

    DepartmentDtos.DepartmentResponse update(Long id, DepartmentDtos.DepartmentRequest request);

    void delete(Long id);

    List<DepartmentDtos.DepartmentResponse> list();
}
