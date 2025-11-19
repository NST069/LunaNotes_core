package com.lunanotes.service;

import com.lunanotes.exception.NoteNotFoundException;
import com.lunanotes.model.Note;
import com.lunanotes.model.User;
import com.lunanotes.repository.NotesJPARepository;
import com.lunanotes.util.IdWorker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotesDataServiceTest {

    @Mock
    NotesJPARepository notesJPARepository;

    @Mock
    IdWorker idWorker;

    @InjectMocks
    NotesDataService notesDataService;

    List<Note> notes;

    @BeforeEach
    void setUp() {
        this.notes=new ArrayList<>();

        Note note1 = Note.builder()
                .id(1L)
                .title("The test note")
                .content("Lorem ipsum dolor sit amet")
                .build();
        Note note2 = Note.builder()
                .id(2L)
                .title("The test note")
                .content("Lorem ipsum dolor sit amet")
                .build();

        this.notes.add(note1);
        this.notes.add(note2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findById_ExistingNote_ShouldReturnNote() {
        User user = User.builder()
                .id(1L)
                .userName("JohnDoe")
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

        Throwable thrown = catchThrowable(()->{
            Note result = notesDataService.findById("1");
        });

        assertThat(thrown).isInstanceOf(NoteNotFoundException.class)
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

        assertThrows(NoteNotFoundException.class, ()->{
            notesDataService.update("1", update);
        });

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

        assertThrows(NoteNotFoundException.class, ()->{
            notesDataService.delete("1");
        });

        verify(notesJPARepository, times(1)).findById("1");
    }

}
