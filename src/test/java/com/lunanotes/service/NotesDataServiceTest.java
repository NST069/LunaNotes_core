package com.lunanotes.service;

import com.lunanotes.exception.ObjectNotFoundException;
import com.lunanotes.mapper.CreateTagRequest;
import com.lunanotes.model.Note;
import com.lunanotes.model.Tag;
import com.lunanotes.model.User;
import com.lunanotes.repository.NotesJPARepository;
import com.lunanotes.repository.TagsJPARepository;
import com.lunanotes.util.IdWorker;
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
class NotesDataServiceTest {

    @Mock
    NotesJPARepository notesJPARepository;

    @Mock
    TagsJPARepository tagsJPARepository;

    @Mock
    IdWorker idWorker;

    @InjectMocks
    NotesDataService notesDataService;

    List<Note> notes;

    List<User> users;

    @BeforeEach
    void setUp() {
        User user1 = User.builder()
                .id(1L)
                .userName("John Doe")
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

    @AfterEach
    void tearDown() {
    }

    @Test
    void findById_ExistingNote_ShouldReturnNote() {
        User user = User.builder()
                .id(1L)
                .userName("JohnDoe")
                .password("password")
                .roles("USER")
                .build();
        Note note = Note.builder()
                .id(1L)
                .owner(user)
                .title("The test note")
                .content("Lorem ipsum dolor sit amet")
                .build();

        given(notesJPARepository.findById("1")).willReturn(Optional.of(note));

        Note result = notesDataService.findById("1");

        assertThat(result.getId()).isEqualTo(note.getId());
        assertThat(result.getOwner().getId()).isEqualTo(user.getId());
        assertThat(result.getTitle()).isEqualTo(note.getTitle());
        assertThat(result.getContent()).isEqualTo(note.getContent());

        verify(notesJPARepository, times(1)).findById("1");
    }

    @Test
    void findById_NonExistingNote_ShouldThrowException() {
        given(notesJPARepository.findById(Mockito.any(String.class))).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            notesDataService.findById("1");
        });

        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find note with Id 1");

        verify(notesJPARepository, times(1)).findById("1");
    }

    @Test
    void findAllNotes_ShouldReturnList(){
        given(notesJPARepository.findAll()).willReturn(this.notes);

        List<Note> result = notesDataService.findAll();

        assertThat(result.size()).isEqualTo(this.notes.size());
        verify(notesJPARepository, times(1)).findAll();
    }

    @Test
    void findAllNotesByUserId_ShouldReturnList() {
        List<Note> filteredNotes = this.notes.stream()
                .filter(note -> ((note.getOwner() != null) ? note.getOwner().getId() : 0) == 1).toList();

        given(notesJPARepository.findByOwnerId(1L)).willReturn(filteredNotes);

        List<Note> result = notesDataService.findByOwnerId("1");

        assertThat(result.size()).isEqualTo(filteredNotes.size());
        verify(notesJPARepository, times(1)).findByOwnerId(1L);
    }

    @Test
    void saveNote_ShouldSave(){
        Note newNote = new Note();
        newNote.setId(123456L);
        newNote.setTitle("test");
        newNote.setContent("lorem ipsum");

        //given(idWorker.nextId()).willReturn(123456L);
        given(notesJPARepository.save(newNote)).willReturn(newNote);

        Note result = notesDataService.save(newNote);

        assertThat(result.getId()).isEqualTo(123456L);
        assertThat(result.getTitle()).isEqualTo(newNote.getTitle());
        assertThat(result.getContent()).isEqualTo(newNote.getContent());

        verify(notesJPARepository, times(1)).save(newNote);
    }

    @Test
    void updateNote_ExistingNote_ShouldUpdate(){
        Note oldNote = new Note();
        oldNote.setId(1L);
        oldNote.setTitle("test");
        oldNote.setContent("lorem ipsum");

        Note update = new Note();
        update.setId(oldNote.getId());
        update.setTitle("test");
        update.setContent("lorem ipsum2");

        given(notesJPARepository.findById("1")).willReturn(Optional.of(oldNote));
        given(notesJPARepository.save(oldNote)).willReturn(oldNote);

        Note result = notesDataService.update("1", update);

        assertThat(result.getId()).isEqualTo(update.getId());
        assertThat(result.getContent()).isEqualTo(update.getContent());

        verify(notesJPARepository, times(1)).findById("1");
        verify(notesJPARepository, times(1)).save(oldNote);
    }

    @Test
    void updateNote_NonExistingNote_ShouldThrowException(){
        Note update = new Note();
        update.setTitle("test");
        update.setContent("lorem ipsum2");

        given(notesJPARepository.findById("1")).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            notesDataService.update("1", update);
        });

        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find note with Id 1");

        verify(notesJPARepository, times(1)).findById("1");
    }

    @Test
    void deleteNote_ExistingNote_ShouldDelete(){
        Note note = new Note();
        note.setId(1L);
        note.setTitle("test");
        note.setContent("lorem ipsum");

        given(notesJPARepository.findById("1")).willReturn(Optional.of(note));
        doNothing().when(notesJPARepository).deleteById("1");

        notesDataService.delete("1");

        verify(notesJPARepository, times(1)).deleteById("1");
    }

    @Test
    void deleteNote_NonExistingNote_ShouldThrowException(){
        given(notesJPARepository.findById("1")).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            notesDataService.delete("1");
        });

        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find note with Id 1");

        verify(notesJPARepository, times(1)).findById("1");
    }

    @Test
    void getTags_ExistingNote_ShouldReturnTagsList(){
        User user = User.builder()
                .id(1L)
                .userName("JohnDoe")
                .password("password")
                .roles("USER")
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

        given(notesJPARepository.findById("1")).willReturn(Optional.of(note));

        Set<Tag> result = notesDataService.getTags("1");

        assertThat(result.size()).isEqualTo(2);

        verify(notesJPARepository, times(1)).findById("1");
    }

    @Test
    void getTags_NonExistingNote_ShouldThrowExcepton(){
        given(notesJPARepository.findById(Mockito.any(String.class))).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            notesDataService.getTags("1");
        });

        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find note with Id 1");

        verify(notesJPARepository, times(1)).findById("1");
    }

    @Test
    void addTag_ExistingTag_ShouldAddTag() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        tag.setOwner(users.get(0));

        given(notesJPARepository.findById("1")).willReturn(Optional.of(notes.get(0)));
        given(tagsJPARepository.findById("1")).willReturn(Optional.of(tag));

        notesDataService.addTag("1", "1");

        assertThat(notes.get(0).getTags()).contains(tag);

        verify(tagsJPARepository, times(1)).findById("1");
        verify(notesJPARepository, times(1)).findById("1");
    }

    @Test
    void addTag_NonExistingTag_ShouldThrowException() {
        given(notesJPARepository.findById("1")).willReturn(Optional.of(notes.get(0)));
        given(tagsJPARepository.findById("1")).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            notesDataService.addTag("1", "1");
        });

        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find tag with Id 1");

        verify(tagsJPARepository, times(1)).findById("1");
        verify(notesJPARepository, times(1)).findById("1");
    }

    @Test
    void removeTag_ExistingTag_ShouldDeleteTag() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        tag.setOwner(users.get(0));

        given(notesJPARepository.findById("1")).willReturn(Optional.of(notes.get(0)));
        given(tagsJPARepository.findById("1")).willReturn(Optional.of(tag));

        notesDataService.removeTag("1", "1");

        assertThat(notes.get(0).getTags()).doesNotContain(tag);

        verify(tagsJPARepository, times(1)).findById("1");
        verify(notesJPARepository, times(1)).findById("1");
    }

    @Test
    void removeTag_NonExistingTag_ShouldThrowException() {
        given(notesJPARepository.findById("1")).willReturn(Optional.of(notes.get(0)));
        given(tagsJPARepository.findById("1")).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            notesDataService.removeTag("1", "1");
        });

        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find tag with Id 1");

        verify(tagsJPARepository, times(1)).findById("1");
        verify(notesJPARepository, times(1)).findById("1");
    }

    @Test
    void createAndAddTag_ExistingTag_ShouldAddTag() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        tag.setOwner(users.get(0));

        given(notesJPARepository.findById("1")).willReturn(Optional.of(notes.get(0)));
        given(tagsJPARepository.findByNameAndOwnerId("test", 1L)).willReturn(Optional.of(tag));

        notesDataService.createAndAddTag("1", new CreateTagRequest("test", "#00FFFF"));

        assertThat(notes.get(0).getTags()).contains(tag);

        verify(tagsJPARepository, times(1)).findByNameAndOwnerId("test", 1L);
        verify(notesJPARepository, times(1)).findById("1");
    }

    @Test
    void createAndAddTag_NonExistingTag_ShouldCreateAndAddTag() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        tag.setOwner(users.get(0));

        given(notesJPARepository.findById("1")).willReturn(Optional.of(notes.get(0)));
        given(tagsJPARepository.findByNameAndOwnerId("test", 1L)).willReturn(Optional.empty());
        given(tagsJPARepository.save(any(Tag.class))).willReturn(tag);

        notesDataService.createAndAddTag("1", new CreateTagRequest("test", "#00FFFF"));

        assertThat(notes.get(0).getTags()).contains(tag);

        verify(tagsJPARepository, times(1)).findByNameAndOwnerId("test", 1L);
        verify(notesJPARepository, times(1)).findById("1");
    }

    @Test
    void addMultipleTags_ShouldAddExistingTags() {
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

        given(notesJPARepository.findById("1")).willReturn(Optional.of(notes.get(0)));
        given(tagsJPARepository.findAllById(List.of("1", "2", "3"))).willReturn(tags);

        notesDataService.addMultipleTags("1", List.of("1", "2", "3"));

        assertThat(notes.get(0).getTags()).containsAll(tags);

        verify(notesJPARepository, times(1)).findById("1");
    }
}
