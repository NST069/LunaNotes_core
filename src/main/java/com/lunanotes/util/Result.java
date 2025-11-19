package com.lunanotes.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class Result {

    @Getter @Setter
    private boolean flag;

    @Getter @Setter
    private int code;

    @Getter @Setter
    private String message;

    @Getter @Setter
    private Object data;
}
