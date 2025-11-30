package com.lunanotes.mapper;

import com.lunanotes.model.Note;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class NoteToNoteDTOConverter implements Converter<Note,NoteDTO> {

    private final UserToUserDTOConverter userToUserDTOConverter;

    public NoteToNoteDTOConverter(UserToUserDTOConverter userToUserDTOConverter) {
        this.userToUserDTOConverter = userToUserDTOConverter;
    }

    @Override
    public NoteDTO convert(Note source) {
        NoteDTO noteDTO = new NoteDTO(source.getId(),
                source.getTitle(),
                source.getContent(),
                source.getNumberOfTags(),
                source.getOwner() != null ? this.userToUserDTOConverter.convert(source.getOwner()) : null);
        return noteDTO;
    }
}
