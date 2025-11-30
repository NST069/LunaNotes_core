package com.lunanotes.mapper;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AddMultipleTagsRequest(@NotEmpty
                                     List<String> tagIds) {
}
