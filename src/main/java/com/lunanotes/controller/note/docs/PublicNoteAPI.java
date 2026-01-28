package com.lunanotes.controller.note.docs;

import com.lunanotes.util.Result;
import org.springframework.web.bind.annotation.PathVariable;

public interface PublicNoteAPI {

    Result findPublicNoteById(@PathVariable String noteId);

    Result findAllPublicNotesOfUser(@PathVariable String userId);
}
