package com.lunanotes.controller.note.docs;

import com.lunanotes.mapper.note.NoteDTO;
import com.lunanotes.util.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface AdminNoteAPI {

    Result findNoteById(@PathVariable String noteId);

    Result findAllNotes();

    Result findAllNotesOfUser(@PathVariable String userId);

    Result updateNote(@PathVariable String noteId, @Valid @RequestBody NoteDTO noteDTO);

    Result deleteNote(@PathVariable String noteId);

    Result removeTag(@PathVariable String noteId, @PathVariable String tagId);
}
