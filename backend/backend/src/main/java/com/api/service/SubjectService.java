package com.api.service;

import com.api.dto.SubjectDtos;

import java.util.List;

public interface SubjectService {

    List<SubjectDtos.SubjectResponse> list(Long departmentId, Long courseId, String search);

    SubjectDtos.SubjectResponse create(SubjectDtos.SubjectRequest request);

    SubjectDtos.SubjectResponse update(Long id, SubjectDtos.SubjectRequest request);

    void delete(Long id);
}
