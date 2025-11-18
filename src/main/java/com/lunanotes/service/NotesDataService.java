package com.lunanotes.service;

import com.lunanotes.repository.NotesCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotesDataService {

    @Autowired
    private NotesCrudRepository notesCrudRepository;


}
