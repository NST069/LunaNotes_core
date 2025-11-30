package com.lunanotes.mapper;

import jakarta.validation.constraints.NotEmpty;

public record TagRequest(long id,
                         String title,
                         @NotEmpty(message = "Content is required")
                         String content,
                         int numberOfTags,
                         long userId) {
}
