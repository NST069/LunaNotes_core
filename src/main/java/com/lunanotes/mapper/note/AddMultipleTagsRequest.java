package com.lunanotes.mapper.note;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AddMultipleTagsRequest(@NotEmpty
                                     List<String> tagNames) {
}
