package com.lunanotes.mapper;

import jakarta.validation.constraints.NotEmpty;

public record TagDTO(long id,
                     @NotEmpty(message = "Name is required")
                     String name,
                     String hexColor,
                     UserDTO owner) {
}
