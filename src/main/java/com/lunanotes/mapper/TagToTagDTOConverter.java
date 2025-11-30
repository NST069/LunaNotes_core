package com.lunanotes.mapper;

import com.lunanotes.model.Tag;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TagToTagDTOConverter implements Converter<Tag, TagDTO> {
    private final UserToUserDTOConverter userToUserDTOConverter;

    public TagToTagDTOConverter(UserToUserDTOConverter userToUserDTOConverter) {
        this.userToUserDTOConverter = userToUserDTOConverter;
    }

    @Override
    public TagDTO convert(Tag source) {
        TagDTO tagDTO = new TagDTO(source.getId(),
                source.getName(),
                source.getHexColor(),
                source.getOwner() != null ? this.userToUserDTOConverter.convert(source.getOwner()) : null);
        return tagDTO;
    }
}
