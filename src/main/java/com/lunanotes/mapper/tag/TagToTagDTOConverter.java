package com.lunanotes.mapper.tag;

import com.lunanotes.mapper.user.UserToUserDTOConverter;
import com.lunanotes.model.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagToTagDTOConverter implements Converter<Tag, TagDTO> {
    private final UserToUserDTOConverter userToUserDTOConverter;

    @Override
    public TagDTO convert(Tag source) {
        TagDTO tagDTO = new TagDTO(source.getId(),
                source.getName(),
                source.getHexColor(),
                source.getOwner() != null ? this.userToUserDTOConverter.convert(source.getOwner()) : null);
        return tagDTO;
    }
}
