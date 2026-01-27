package com.lunanotes.mapper.tag;

import com.lunanotes.model.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagToLightTagDTOConverter implements Converter<Tag, LightTagDTO> {
    @Override
    public LightTagDTO convert(Tag source) {
        LightTagDTO lightTagDTO = new LightTagDTO(source.getId(),
                source.getName(),
                source.getHexColor()
        );
        return lightTagDTO;
    }
}
