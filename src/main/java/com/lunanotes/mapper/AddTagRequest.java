package com.lunanotes.mapper;

import jakarta.validation.constraints.NotBlank;

public record AddTagRequest(@NotBlank
                            String tagId) {

}
