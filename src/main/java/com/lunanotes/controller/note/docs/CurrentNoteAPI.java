package com.lunanotes.controller.note.docs;

import com.lunanotes.mapper.note.AddMultipleTagsRequest;
import com.lunanotes.mapper.note.AddTagRequest;
import com.lunanotes.mapper.note.NoteDTO;
import com.lunanotes.util.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface CurrentNoteAPI {

    Result findNoteById(@PathVariable String noteId);

    Result findAllNotes();

    Result addNote(@Valid @RequestBody NoteDTO noteDTO);

    Result updateNote(@PathVariable String noteId, @Valid @RequestBody NoteDTO noteDTO);

    Result deleteNote(@PathVariable String noteId);

    Result addTag(@PathVariable String noteId, @RequestBody AddTagRequest request);

    Result addMultipleTags(@PathVariable String noteId, @RequestBody AddMultipleTagsRequest request);

    Result removeTag(@PathVariable String noteId, @PathVariable String tagId);

}
