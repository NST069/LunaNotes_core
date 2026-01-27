package com.lunanotes.controller.note;

import com.lunanotes.mapper.note.*;
import com.lunanotes.mapper.tag.*;
import com.lunanotes.model.Note;
import com.lunanotes.model.Tag;
import com.lunanotes.service.NoteService;
import com.lunanotes.util.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.base-url}/notes")
public class NoteController {

    private final NoteService noteService;

    private final NoteToNoteDTOConverter noteToNoteDTOConverter;

    private final NoteDTOToNoteConverter noteDTOToNoteConverter;

    private final TagToTagDTOConverter tagToTagDTOConverter;

    @GetMapping(value = {"/{noteId}", "/note-{noteId}"})
    public Result findNoteById(@PathVariable String noteId) {
        Note foundNote = this.noteService.findById(noteId);
        NoteDTO noteDTO = this.noteToNoteDTOConverter.convert(foundNote);
        return new Result(true, HttpStatus.OK.value(), "Find One Success", noteDTO);
    }

    @GetMapping
    public Result findAllNotes() {
        List<Note> foundNotes = this.noteService.findAll();
        List<NoteDTO> noteDTOs = foundNotes.stream().map(this.noteToNoteDTOConverter::convert).collect(Collectors.toList());

        return new Result(true, HttpStatus.OK.value(), "Find All Success", noteDTOs);
    }

    @GetMapping(value = "/user-{userId}")
    public Result findAllNotesOfUser(@PathVariable String userId) {
        List<Note> foundNotes = this.noteService.findByOwnerId(userId);
        List<NoteDTO> noteDTOs = foundNotes.stream().map(this.noteToNoteDTOConverter::convert).collect(Collectors.toList());

        return new Result(true, HttpStatus.OK.value(), "Find All For User Success", noteDTOs);
    }

    @PostMapping
    public Result addNote(@Valid @RequestBody NoteDTO noteDTO) {
        Note newNote = this.noteDTOToNoteConverter.convert(noteDTO);
        Note savedNote = this.noteService.save(newNote);
        NoteDTO savedNoteDTO = this.noteToNoteDTOConverter.convert(savedNote);
        return new Result(true, HttpStatus.OK.value(), "Add Success", savedNoteDTO);
    }

    @PutMapping(value = "/{noteId}")
    public Result updateNote(@PathVariable String noteId, @Valid @RequestBody NoteDTO noteDTO) {
        Note update = this.noteDTOToNoteConverter.convert(noteDTO);
        Note updatedNote = this.noteService.update(noteId, update);
        NoteDTO updatedNoteDTO = this.noteToNoteDTOConverter.convert(updatedNote);
        return new Result(true, HttpStatus.OK.value(), "Update Success", updatedNoteDTO);
    }

    @DeleteMapping(value = "/{noteId}")
    public Result deleteNote(@PathVariable String noteId) {
        this.noteService.delete(noteId);
        return new Result(true, HttpStatus.OK.value(), "Delete Success", null);
    }

    @GetMapping({"/{noteId}/tags", "/note-{noteId}/tags"})
    public Result getTags(@PathVariable String noteId){
        Set<Tag> tags = noteService.getTags(noteId);
        Set<TagDTO> tagsDTO = tags.stream().map(this.tagToTagDTOConverter::convert).collect(Collectors.toSet());
        return new Result(true, HttpStatus.OK.value(), "Get Tags Success", tagsDTO);
    }

    @PostMapping("/{noteId}/tags")
    public Result addTag(@PathVariable String noteId, @RequestBody AddTagRequest request) {
        Note note = noteService.addTag(noteId, request.tagName());
        NoteDTO noteDTO = this.noteToNoteDTOConverter.convert(note);
        return new Result(true, HttpStatus.OK.value(), "Add Tag Success", noteDTO);
    }

    @PostMapping("/{noteId}/tags/batch")
    public Result addMultipleTags(@PathVariable String noteId, @RequestBody AddMultipleTagsRequest request) {
        Note note = noteService.addMultipleTags(noteId, request.tagNames());
        NoteDTO noteDTO = this.noteToNoteDTOConverter.convert(note);
        return new Result(true, HttpStatus.OK.value(), "Add Multiple Tags Success", noteDTO);
    }

    @DeleteMapping("/{noteId}/tags/{tagId}")
    public Result removeTag(@PathVariable String noteId, @PathVariable String tagId) {
        Note note = noteService.removeTag(noteId, tagId);
        NoteDTO noteDTO = this.noteToNoteDTOConverter.convert(note);
        return new Result(true, HttpStatus.OK.value(), "Remove Tag Success", noteDTO);
    }
}
