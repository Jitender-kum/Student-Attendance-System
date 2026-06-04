package com.api.service;

import com.api.dto.CourseDtos;

import java.util.List;

public interface CourseService {

    CourseDtos.CourseResponse create(CourseDtos.CourseRequest request);

    CourseDtos.CourseResponse update(Long id, CourseDtos.CourseRequest request);

    void delete(Long id);

    List<CourseDtos.CourseResponse> list(Long departmentId);
}
