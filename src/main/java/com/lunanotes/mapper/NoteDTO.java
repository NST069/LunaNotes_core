package com.lunanotes.mapper;

import jakarta.validation.constraints.NotEmpty;

public record NoteDTO(long id,
                      String title,
                      @NotEmpty(message = "Content is required")
                      String content,
                      UserDTO owner) {
}
