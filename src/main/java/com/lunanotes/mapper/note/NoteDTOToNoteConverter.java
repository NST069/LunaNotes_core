package com.lunanotes.mapper.note;

import com.lunanotes.model.Note;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class NoteDTOToNoteConverter implements Converter<NoteDTO, Note> {
    @Override
    public Note convert(NoteDTO source) {
        Note note = new Note();
        if (source.id() > 0) note.setId(source.id());
        note.setTitle(source.title());
        note.setContent(source.content());

        return note;
    }
}
