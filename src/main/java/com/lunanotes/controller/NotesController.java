package com.lunanotes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotesController {

    @GetMapping("/ping")
    public String pibg(@RequestParam(name="name", required = false, defaultValue = "username") String name){

        return "pong "+name;
    }
}
