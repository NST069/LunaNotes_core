package com.lunanotes.controller.note;

import com.lunanotes.controller.note.docs.PublicNoteAPI;
import com.lunanotes.mapper.note.NoteDTO;
import com.lunanotes.mapper.note.NoteToNoteDTOConverter;
import com.lunanotes.model.Note;
import com.lunanotes.service.NoteService;
import com.lunanotes.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.base-url}/notes")
public class PublicNoteController implements PublicNoteAPI {

    private final NoteService noteService;

    private final NoteToNoteDTOConverter noteToNoteDTOConverter;

    @GetMapping(value = {"/{noteId}", "/note-{noteId}"})
    public Result findPublicNoteById(@PathVariable String noteId) {
        Note foundNote = this.noteService.findPublicById(noteId);
        NoteDTO noteDTO = this.noteToNoteDTOConverter.convert(foundNote);
        return new Result(true, HttpStatus.OK.value(), "Find One Success", noteDTO);
    }

    @GetMapping(value = "/user-{userId}")
    public Result findAllPublicNotesOfUser(@PathVariable String userId) {
        List<Note> foundNotes = this.noteService.findAllPublic(userId);
        List<NoteDTO> noteDTOs = foundNotes.stream().map(this.noteToNoteDTOConverter::convert).collect(Collectors.toList());

        return new Result(true, HttpStatus.OK.value(), "Find All For User Success", noteDTOs);
    }
}
