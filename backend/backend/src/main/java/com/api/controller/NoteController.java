package com.api.controller;

import com.api.dto.NoteDtos;
import com.api.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<List<NoteDtos.NoteResponse>> list(@RequestParam(required = false) Long departmentId,
                                                            @RequestParam(required = false) Long courseId,
                                                            @RequestParam(required = false) LocalDate noteDate,
                                                            @RequestParam(required = false) LocalDate startDate,
                                                            @RequestParam(required = false) LocalDate endDate) {
        return ResponseEntity.ok(noteService.list(departmentId, courseId, noteDate, startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<NoteDtos.NoteResponse> create(@Valid @RequestBody NoteDtos.NoteRequest request) {
        return ResponseEntity.ok(noteService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDtos.NoteResponse> update(@PathVariable Long id,
                                                        @Valid @RequestBody NoteDtos.NoteRequest request) {
        return ResponseEntity.ok(noteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
