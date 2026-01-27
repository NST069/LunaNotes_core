package com.lunanotes.mapper.note;

import jakarta.validation.constraints.NotBlank;

public record AddTagRequest(@NotBlank
                            String tagName) {

}
