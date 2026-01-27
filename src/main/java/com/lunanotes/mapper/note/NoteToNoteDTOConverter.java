package com.lunanotes.mapper.note;

import com.lunanotes.mapper.tag.TagToLightTagDTOConverter;
import com.lunanotes.mapper.tag.TagToTagDTOConverter;
import com.lunanotes.mapper.user.UserToUserDTOConverter;
import com.lunanotes.model.Note;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoteToNoteDTOConverter implements Converter<Note,NoteDTO> {

    private final UserToUserDTOConverter userToUserDTOConverter;

    private final TagToLightTagDTOConverter tagToLightTagDTOConverter;

    @Override
    public NoteDTO convert(Note source) {
        NoteDTO noteDTO = new NoteDTO(source.getId(),
                source.getTitle(),
                source.getContent(),
                source.getNumberOfTags(),
                source.getNumberOfTags() > 0 ? source.getTags().stream().map(this.tagToLightTagDTOConverter::convert).toList(): null,
                source.getOwner() != null ? this.userToUserDTOConverter.convert(source.getOwner()) : null);
        return noteDTO;
    }
}
