package com.lunanotes.service;

import com.lunanotes.exception.ObjectNotFoundException;
import com.lunanotes.model.Note;
import com.lunanotes.model.Tag;
import com.lunanotes.model.User;
import com.lunanotes.repository.NoteJPARepository;
import com.lunanotes.repository.TagJPARepository;
import com.lunanotes.security.CurrentUserService;
import com.lunanotes.util.IdWorker;
import com.lunanotes.util.UserRole;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class NoteServiceTest {

    @Mock
    NoteJPARepository noteJPARepository;

    @Mock
    TagJPARepository tagJPARepository;

    @Mock
    CurrentUserService currentUserService;

    @Mock
    IdWorker idWorker;

    @InjectMocks
    NoteService noteService;

    List<Note> notes;

    List<User> users;

    @BeforeEach
    void setUp() {
        User user1 = User.builder()
                .id(1L)
                .username("John Doe")
                .password("password")
                .roles("USER")
                .build();

        this.users = List.of(user1);

        Note note1 = Note.builder()
                .id(1L)
                .owner(user1)
                .title("The test note")
                .content("Lorem ipsum dolor sit amet")
                .build();
        Note note2 = Note.builder()
                .id(2L)
                .owner(user1)
                .title("The test note")
                .content("Lorem ipsum dolor sit amet")
                .build();
        Note note3 = Note.builder()
                .id(2L)
                .title("The test note")
                .content("Lorem ipsum dolor sit amet")
                .build();

        this.notes = List.of(note1, note2, note3);
    }

    @Test
    void findById_ExistingNote_ShouldReturnNote() {
        User user = User.builder()
                .id(1L)
                .username("JohnDoe")
                .password("password")
                .roles("USER")
                .build();
        Note note = Note.builder()
                .id(1L)
                .owner(user)
                .title("The test note")
                .content("Lorem ipsum dolor sit amet")
                .build();

        given(noteJPARepository.findById("1")).willReturn(Optional.of(note));

        Note result = noteService.findById("1");

        assertThat(result.getId()).isEqualTo(note.getId());
        assertThat(result.getOwner().getId()).isEqualTo(user.getId());
        assertThat(result.getTitle()).isEqualTo(note.getTitle());
        assertThat(result.getContent()).isEqualTo(note.getContent());

        verify(noteJPARepository, times(1)).findById("1");
    }

    @Test
    void findById_NonExistingNote_ShouldThrowException() {
        given(noteJPARepository.findById(Mockito.any(String.class))).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            noteService.findById("1");
        });

        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find note with Id 1");

        verify(noteJPARepository, times(1)).findById("1");
    }

    @Test
    void findAllNotes_ShouldReturnList() {
        given(currentUserService.getCurrentUserId()).willReturn(1L);
        List<Note> currentUserNotes = this.notes.stream()
                .filter(note -> ((note.getOwner() != null) ? note.getOwner().getId() : 0) == 1).toList();
        given(noteJPARepository.findByOwnerId(1L)).willReturn(currentUserNotes);

        List<Note> result = noteService.findAll();

        assertThat(result.size()).isEqualTo(currentUserNotes.size());
        verify(noteJPARepository, times(1)).findByOwnerId(1L);
    }

    @Test
    void findAllNotesByUserId_ShouldReturnList() {
        List<Note> filteredNotes = this.notes.stream()
                .filter(note -> ((note.getOwner() != null) ? note.getOwner().getId() : 0) == 1).toList();

        given(noteJPARepository.findByOwnerId(1L)).willReturn(filteredNotes);

        List<Note> result = noteService.findByOwnerId("1");

        assertThat(result.size()).isEqualTo(filteredNotes.size());
        verify(noteJPARepository, times(1)).findByOwnerId(1L);
    }

    @Test
    void saveNote_ShouldSave() {
        Note newNote = new Note();
        newNote.setTitle("test");
        newNote.setContent("lorem ipsum");

        given(noteJPARepository.save(newNote)).willReturn(newNote);

        Note result = noteService.save(newNote);

        assertThat(result.getId()).isEqualTo(newNote.getId());
        assertThat(result.getTitle()).isEqualTo(newNote.getTitle());
        assertThat(result.getContent()).isEqualTo(newNote.getContent());

        verify(noteJPARepository, times(1)).save(newNote);
    }

    @Test
    void updateNote_ExistingNote_ShouldUpdate() {
        given(currentUserService.getCurrentUserId()).willReturn(1L);
        Note oldNote = new Note();
        oldNote.setTitle("test");
        oldNote.setContent("lorem ipsum");

        Note update = new Note();
        update.setId(oldNote.getId());
        update.setTitle("test");
        update.setContent("lorem ipsum2");

        given(noteJPARepository.findByIdAndOwnerId(1L, 1L)).willReturn(Optional.of(oldNote));
        given(noteJPARepository.save(oldNote)).willReturn(oldNote);

        Note result = noteService.update("1", update);

        assertThat(result.getId()).isEqualTo(update.getId());
        assertThat(result.getContent()).isEqualTo(update.getContent());

        verify(noteJPARepository, times(1)).findByIdAndOwnerId(1L, 1L);
        verify(noteJPARepository, times(1)).save(oldNote);
    }

    @Test
    void updateNote_NonExistingNote_ShouldThrowException() {
        given(currentUserService.getCurrentUserId()).willReturn(1L);
        Note update = new Note();
        update.setTitle("test");
        update.setContent("lorem ipsum2");

        given(noteJPARepository.findByIdAndOwnerId(1L, 1L)).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            noteService.update("1", update);
        });

        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find note with Id 1");

        verify(noteJPARepository, times(1)).findByIdAndOwnerId(1L, 1L);
    }

    @Test
    void deleteNote_ExistingNote_ShouldDelete() {
        given(currentUserService.getCurrentUserId()).willReturn(1L);
        Note note = new Note();
        note.setTitle("test");
        note.setContent("lorem ipsum");

        given(noteJPARepository.findByIdAndOwnerId(1L, 1L)).willReturn(Optional.of(note));
        doNothing().when(noteJPARepository).deleteById("1");

        noteService.delete("1");

        verify(noteJPARepository, times(1)).deleteById("1");
    }

    @Test
    void deleteNote_NonExistingNote_ShouldThrowException() {
        given(currentUserService.getCurrentUserId()).willReturn(1L);
        given(noteJPARepository.findByIdAndOwnerId(1L, 1L)).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            noteService.delete("1");
        });

        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find note with Id 1");

        verify(noteJPARepository, times(1)).findByIdAndOwnerId(1L, 1L);
    }

    @Test
    void getTags_ExistingNote_ShouldReturnTagsList() {
        given(currentUserService.getCurrentUserId()).willReturn(1L);
        User user = User.builder()
                .id(1L)
                .username("JohnDoe")
                .password("password")
                .roles(UserRole.USER.name())
                .build();
        Note note = Note.builder()
                .id(1L)
                .owner(user)
                .title("The test note")
                .content("Lorem ipsum dolor sit amet")
                .build();
        Tag tag1 = Tag.builder()
                .id(1L)
                .owner(user)
                .name("test1")
                .hexColor("#FF0000")
                .build();
        Tag tag2 = Tag.builder()
                .id(2L)
                .owner(user)
                .name("test2")
                .hexColor("#FF0000")
                .build();
        note.addTag(tag1);
        note.addTag(tag2);

        given(noteJPARepository.findByIdAndOwnerId(1L, 1L)).willReturn(Optional.of(note));

        Set<Tag> result = noteService.getTags("1");

        assertThat(result.size()).isEqualTo(2);

        verify(noteJPARepository, times(1)).findByIdAndOwnerId(1L, 1L);
    }

    @Test
    void getTags_NonExistingNote_ShouldThrowException() {
        given(currentUserService.getCurrentUserId()).willReturn(1L);
        given(noteJPARepository.findByIdAndOwnerId(Mockito.any(Long.class), eq(1L))).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            noteService.getTags("1");
        });

        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find note with Id 1");

        verify(noteJPARepository, times(1)).findByIdAndOwnerId(1L, 1L);
    }

    @Test
    void addTag_ExistingTag_ShouldAddTag() {
        given(currentUserService.getCurrentUserId()).willReturn(1L);
        given(currentUserService.getCurrentUser()).willReturn(this.users.get(0));
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        tag.setOwner(users.get(0));

        given(noteJPARepository.findByIdAndOwnerId(1L, 1L)).willReturn(Optional.of(notes.get(0)));
        given(tagJPARepository.findByNameAndOwnerId("test", 1L)).willReturn(Optional.of(tag));

        noteService.addTag("1", tag.getName());

        assertThat(notes.get(0).getTags()).contains(tag);

        verify(tagJPARepository, times(1)).findByNameAndOwnerId("test", 1L);
        verify(noteJPARepository, times(1)).findByIdAndOwnerId(1L, 1L);
    }

    @Test
    void addTag_NonExistingTag_ShouldCreateAndAddTag() {
        given(currentUserService.getCurrentUserId()).willReturn(1L);
        given(currentUserService.getCurrentUser()).willReturn(this.users.get(0));
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        tag.setOwner(users.get(0));

        given(noteJPARepository.findByIdAndOwnerId(1L, 1L)).willReturn(Optional.of(notes.get(0)));
        given(tagJPARepository.findByNameAndOwnerId("test", 1L)).willReturn(Optional.empty());
        given(tagJPARepository.save(any(Tag.class))).willReturn(tag);

        noteService.addTag("1", "test");

        assertThat(notes.get(0).getTags()).contains(tag);

        verify(tagJPARepository, times(1)).findByNameAndOwnerId("test", 1L);
        verify(noteJPARepository, times(1)).findByIdAndOwnerId(1L, 1L);
    }

    @Test
    void removeTag_ExistingTag_ShouldDeleteTag() {
        given(currentUserService.getCurrentUserId()).willReturn(1L);
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        tag.setOwner(users.get(0));

        given(noteJPARepository.findByIdAndOwnerId(1L, 1L)).willReturn(Optional.of(notes.get(0)));
        given(tagJPARepository.findByIdAndOwnerId(1L, 1L)).willReturn(Optional.of(tag));

        noteService.removeTag("1", "1");

        assertThat(notes.get(0).getTags()).doesNotContain(tag);

        verify(tagJPARepository, times(1)).findByIdAndOwnerId(1L, 1L);
        verify(noteJPARepository, times(1)).findByIdAndOwnerId(1L, 1L);
    }

    @Test
    void removeTag_NonExistingTag_ShouldThrowException() {
        given(currentUserService.getCurrentUserId()).willReturn(1L);
        given(noteJPARepository.findByIdAndOwnerId(1L, 1L)).willReturn(Optional.of(notes.get(0)));
        given(tagJPARepository.findByIdAndOwnerId(1L, 1L)).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            noteService.removeTag("1", "1");
        });

        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find tag with Id 1");

        verify(tagJPARepository, times(1)).findByIdAndOwnerId(1L, 1L);
        verify(noteJPARepository, times(1)).findByIdAndOwnerId(1L, 1L);
    }

    @Test
    void addMultipleTags_ShouldAddExistingTags() {
        given(currentUserService.getCurrentUserId()).willReturn(1L);
        given(currentUserService.getCurrentUser()).willReturn(this.users.get(0));
        Tag tag1 = Tag.builder()
                .id(1L)
                .name("cats")
                .owner(users.getFirst())
                .build();
        Tag tag2 = Tag.builder()
                .id(2L)
                .name("dogs")
                .owner(users.getFirst())
                .build();
        Tag tag3 = Tag.builder()
                .id(3L)
                .name("important")
                .owner(users.getFirst())
                .build();

        List<Tag> tags = List.of(tag1, tag2, tag3);

        given(noteJPARepository.findByIdAndOwnerId(1L, 1L)).willReturn(Optional.of(notes.get(0)));
        given(tagJPARepository.findByNameAndOwnerId("cats", 1L)).willReturn(Optional.of(tag1));
        given(tagJPARepository.findByNameAndOwnerId("dogs", 1L)).willReturn(Optional.of(tag2));
        given(tagJPARepository.findByNameAndOwnerId("important", 1L)).willReturn(Optional.of(tag3));

        noteService.addMultipleTags("1", tags.stream().map(Tag::getName).toList());

        assertThat(notes.get(0).getTags()).containsAll(tags);

        verify(noteJPARepository, times(1)).findByIdAndOwnerId(1L, 1L);
    }
}
