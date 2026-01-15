package com.lunanotes.util;

import com.lunanotes.model.Note;
import com.lunanotes.model.User;
import com.lunanotes.repository.NotesJPARepository;
import com.lunanotes.service.UsersDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DBDataInitializer implements CommandLineRunner {

    private final NotesJPARepository notesJPARepository;

    private final UsersDataService usersDataService;

    @Override
    public void run(String... args){
        User user1 = new User();
        user1.setUserName("User1");
        user1.setPassword("password1");
        user1.setRoles("USER ADMIN");
        User user2 = new User();
        user2.setUserName("User2");
        user2.setPassword("password2");
        user2.setRoles("USER");
        User user3 = new User();
        user3.setUserName("User3");
        user3.setPassword("password3");
        user3.setRoles("USER");

        Note note1 = new Note();
        note1.setContent("lorem ipsum1");
        note1.setTitle("test1");
        Note note2 = new Note();
        note2.setContent("lorem ipsum2");
        note2.setTitle("test2");
        Note note3 = new Note();
        note3.setContent("lorem ipsum3");
        note3.setTitle("test3");
        Note note4 = new Note();
        note4.setContent("lorem ipsum4");
        note4.setTitle("test4");
        Note note5 = new Note();
        note5.setContent("lorem ipsum5");
        note5.setTitle("test5");

        user1.addNote(note1);
        user1.addNote(note2);
        user2.addNote(note3);
        user2.addNote(note4);

        usersDataService.save(user1);
        usersDataService.save(user2);
        usersDataService.save(user3);

        notesJPARepository.save(note5);
    }
}
