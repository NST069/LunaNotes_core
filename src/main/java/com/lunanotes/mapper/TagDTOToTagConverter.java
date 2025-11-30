package com.lunanotes.mapper;

import com.lunanotes.model.Tag;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TagDTOToTagConverter implements Converter<TagDTO, Tag>{
    @Override
    public Tag convert(TagDTO source) {
        Tag tag = new Tag();
        if (source.id() > 0) tag.setId(source.id());
        tag.setName(source.name());
        tag.setHexColor(source.hexColor());

        return tag;
    }
}
