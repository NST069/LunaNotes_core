package com.lunanotes.util;

import com.lunanotes.model.Note;
import com.lunanotes.model.Tag;
import com.lunanotes.model.User;
import com.lunanotes.repository.NotesJPARepository;
import com.lunanotes.repository.TagsJPARepository;
import com.lunanotes.repository.UsersJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Profile("test")
public class DBDataInitializer implements CommandLineRunner {

    private final NotesJPARepository notesJPARepository;

    private final UsersJPARepository usersJPARepository;

    private final TagsJPARepository tagsJPARepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        User user1 = new User();
        user1.setUsername("User1");
        user1.setPassword(this.passwordEncoder.encode("password1"));
        user1.setRoles(String.join(" ", UserRole.ADMIN.name(), UserRole.USER.name()));
        User user2 = new User();
        user2.setUsername("User2");
        user2.setPassword(this.passwordEncoder.encode("password2"));
        user2.setRoles(UserRole.USER.name());
        User user3 = new User();
        user3.setUsername("User3");
        user3.setPassword(this.passwordEncoder.encode("password3"));
        user3.setRoles(UserRole.USER.name());

        usersJPARepository.save(user1);
        usersJPARepository.save(user2);
        usersJPARepository.save(user3);

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

        notesJPARepository.save(note1);
        notesJPARepository.save(note2);
        notesJPARepository.save(note3);
        notesJPARepository.save(note4);
        notesJPARepository.save(note5);

        Tag tag1 = new Tag();
        tag1.setName("tag1");
        tag1.setHexColor("#FF0000");
        tag1.setOwner(user1);
        Tag tag2 = new Tag();
        tag2.setName("tag2");
        tag2.setHexColor("#FFFF00");
        tag2.setOwner(user1);
        Tag tag3 = new Tag();
        tag3.setName("tag3");
        tag3.setHexColor("#FF00FF");
        tag3.setOwner(user2);

        tagsJPARepository.save(tag1);
        tagsJPARepository.save(tag2);
        tagsJPARepository.save(tag3);

        user1.addNote(note1);
        user1.addNote(note2);
        user2.addNote(note3);
        user2.addNote(note4);
        user3.addNote(note5);

        note1.addTag(tag1);
        note2.addTag(tag2);
        note3.addTag(tag3);
    }
}
