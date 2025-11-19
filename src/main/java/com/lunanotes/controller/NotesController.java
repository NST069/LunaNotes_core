package com.lunanotes.controller;

import com.lunanotes.mapper.NoteDTO;
import com.lunanotes.mapper.NoteDTOToNoteConverter;
import com.lunanotes.mapper.NoteToNoteDTOConverter;
import com.lunanotes.model.Note;
import com.lunanotes.service.NotesDataService;
import com.lunanotes.util.Result;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notes")
public class NotesController {

    private final NotesDataService notesDataService;

    private final NoteToNoteDTOConverter noteToNoteDTOConverter;

    private final NoteDTOToNoteConverter noteDTOToNoteConverter;

    public NotesController(NotesDataService notesDataService, NoteToNoteDTOConverter noteToNoteDTOConverter, NoteDTOToNoteConverter noteDTOToNoteConverter) {
        this.notesDataService = notesDataService;
        this.noteToNoteDTOConverter = noteToNoteDTOConverter;
        this.noteDTOToNoteConverter = noteDTOToNoteConverter;
    }

    @GetMapping(value = "/{noteId}")
    public Result findNoteById(@PathVariable String noteId) {
        Note foundNote = this.notesDataService.findById(noteId);
        NoteDTO noteDTO = this.noteToNoteDTOConverter.convert(foundNote);
        return new Result(true, HttpStatus.OK.value(), "Find One Success", noteDTO);
    }

    @GetMapping
    public Result findAllNotes() {
        List<Note> foundNotes = this.notesDataService.findAll();
        List<NoteDTO> noteDTOs = foundNotes.stream().map(this.noteToNoteDTOConverter::convert).collect(Collectors.toList());

        return new Result(true, HttpStatus.OK.value(), "Find All Success", noteDTOs);
    }

    @PostMapping
    public Result addNote(@Valid @RequestBody NoteDTO noteDTO) {
        Note newNote = this.noteDTOToNoteConverter.convert(noteDTO);
        Note savedNote = this.notesDataService.save(newNote);
        NoteDTO savedNoteDTO = this.noteToNoteDTOConverter.convert(savedNote);
        return new Result(true, HttpStatus.OK.value(), "Add Success", savedNoteDTO);
    }

    @PutMapping(value = "/{noteId}")
    public Result updateNote(@PathVariable String noteId, @Valid @RequestBody NoteDTO noteDTO) {
        Note update = this.noteDTOToNoteConverter.convert(noteDTO);
        Note updatedNote = this.notesDataService.update(noteId, update);
        NoteDTO updatedNoteDTO = this.noteToNoteDTOConverter.convert(updatedNote);
        return new Result(true, HttpStatus.OK.value(), "Update Success", updatedNoteDTO);
    }

    @DeleteMapping(value = "/{noteId}")
    public Result deleteNote(@PathVariable String noteId) {
        this.notesDataService.delete(noteId);
        return new Result(true, HttpStatus.OK.value(), "Delete Success", null);
    }
}
