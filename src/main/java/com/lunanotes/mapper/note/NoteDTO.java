package com.lunanotes.mapper.note;

import com.lunanotes.mapper.tag.LightTagDTO;
import com.lunanotes.mapper.user.UserDTO;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record NoteDTO(long id,
                      String title,
                      @NotEmpty(message = "Content is required")
                      String content,
                      int numberOfTags,
                      List<LightTagDTO> tags,
                      UserDTO owner) {
}
