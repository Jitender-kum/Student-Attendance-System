package com.api.service;

import com.api.dto.NoteDtos;

import java.time.LocalDate;
import java.util.List;

public interface NoteService {

    List<NoteDtos.NoteResponse> list(Long departmentId, Long courseId, LocalDate noteDate, LocalDate startDate, LocalDate endDate);

    NoteDtos.NoteResponse create(NoteDtos.NoteRequest request);

    NoteDtos.NoteResponse update(Long id, NoteDtos.NoteRequest request);

    void delete(Long id);
}
