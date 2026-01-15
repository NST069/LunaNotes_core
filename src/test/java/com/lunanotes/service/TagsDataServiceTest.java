package com.lunanotes.service;

import com.lunanotes.exception.ObjectNotFoundException;
import com.lunanotes.model.Note;
import com.lunanotes.model.Tag;
import com.lunanotes.model.User;
import com.lunanotes.repository.TagsJPARepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class TagsDataServiceTest {

    @Mock
    TagsJPARepository tagsJPARepository;

    @InjectMocks
    TagsDataService tagsDataService;

    List<Note> notes;
    List<User> users;
    List<Tag> tags;

    @BeforeEach
    void setUp() {
        this.notes=new ArrayList<>();
        this.users = new ArrayList<>();
        this.tags = new ArrayList<>();

        User user1 = User.builder()
                .id(1L)
                .userName("John Doe")
                .password("password")
                .roles("USER")
                .build();
        User user2 = User.builder()
                .id(2L)
                .userName("James Doe")
                .password("password")
                .roles("USER")
                .build();

        users.add(user1);
        users.add(user2);

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
                .owner(user2)
                .title("The test note")
                .content("Lorem ipsum dolor sit amet")
                .build();

        this.notes.add(note1);
        this.notes.add(note2);
        this.notes.add(note3);

        Tag tag1 = Tag.builder()
                .id(1L)
                .name("cats")
                .owner(user1)
                .build();
        Tag tag2 = Tag.builder()
                .id(2L)
                .name("dogs")
                .owner(user2)
                .build();
        Tag tag3 = Tag.builder()
                .id(3L)
                .name("important")
                .owner(user1)
                .build();
        Tag tag4 = Tag.builder()
                .id(4L)
                .name("trash")
                .owner(user1)
                .build();

        this.tags.add(tag1);
        this.tags.add(tag2);
        this.tags.add(tag3);
        this.tags.add(tag4);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findById_ExistingTag_ShouldReturnTag() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        tag.setOwner(users.get(0));

        given(tagsJPARepository.findById("1")).willReturn(Optional.of(tag));

        Tag result = tagsDataService.findById("1");

        assertThat(result.getId()).isEqualTo(tag.getId());
        assertThat(result.getName()).isEqualTo(tag.getName());
        assertThat(result.getOwner()).isEqualTo(tag.getOwner());

        verify(tagsJPARepository, times(1)).findById("1");
    }

    @Test
    void findById_NonExistingTag_ShouldThrowException() {
        given(tagsJPARepository.findById(Mockito.any(String.class))).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            tagsDataService.findById("1");
        });

        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find tag with Id 1");

        verify(tagsJPARepository, times(1)).findById("1");
    }

    @Test
    void findAllTags_ShouldReturnList(){
        given(tagsJPARepository.findAll()).willReturn(this.tags);

        List<Tag> result = tagsDataService.findAll();

        assertThat(result.size()).isEqualTo(this.tags.size());
        verify(tagsJPARepository, times(1)).findAll();
    }

    @Test
    void findAllTagsByUserId_ShouldReturnList() {
        List<Tag> filteredTags = this.tags.stream()
                .filter(tag -> ((tag.getOwner() != null) ? tag.getOwner().getId() : 0) == 1).toList();

        given(tagsJPARepository.findByOwnerId(1L)).willReturn(filteredTags);

        List<Tag> result = tagsDataService.findByOwnerId("1");

        assertThat(result.size()).isEqualTo(filteredTags.size());
        verify(tagsJPARepository, times(1)).findByOwnerId(1L);
    }

    @Test
    void saveTag_shouldSave() {
        Tag newTag = new Tag();
        newTag.setId(123456L);
        newTag.setName("test");
        newTag.setOwner(users.get(0));

        given(tagsJPARepository.save(newTag)).willReturn(newTag);

        Tag result = tagsJPARepository.save(newTag);

        assertThat(result.getId()).isEqualTo(123456L);
        assertThat(result.getName()).isEqualTo(newTag.getName());
        assertThat(result.getOwner()).isEqualTo(newTag.getOwner());

        verify(tagsJPARepository, times(1)).save(newTag);
    }

    @Test
    void updateTag_ExistingTag_ShouldUpdate(){
        Tag oldTag = new Tag();
        oldTag.setId(1L);
        oldTag.setName("test");
        oldTag.setOwner(users.get(0));

        Tag update = new Tag();
        update.setId(oldTag.getId());
        update.setName("test2");
        update.setOwner(oldTag.getOwner());

        given(tagsJPARepository.findById("1")).willReturn(Optional.of(oldTag));
        given(tagsJPARepository.save(oldTag)).willReturn(oldTag);

        Tag result = tagsDataService.update("1", update);

        assertThat(result.getId()).isEqualTo(update.getId());
        assertThat(result.getName()).isEqualTo(update.getName());

        verify(tagsJPARepository, times(1)).findById("1");
        verify(tagsJPARepository, times(1)).save(oldTag);
    }

    @Test
    void updateTag_NonExistingTag_ShouldThrowException(){
        Tag update = new Tag();
        update.setId(1L);
        update.setName("test2");
        update.setOwner(users.get(0));

        given(tagsJPARepository.findById("1")).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            tagsDataService.update("1", update);
        });

        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find tag with Id 1");

        verify(tagsJPARepository, times(1)).findById("1");
    }

    @Test
    void deleteTag_ExistingTag_ShouldDelete(){
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        tag.setOwner(users.get(0));

        given(tagsJPARepository.findById("1")).willReturn(Optional.of(tag));
        doNothing().when(tagsJPARepository).deleteById("1");

        tagsDataService.delete("1");

        verify(tagsJPARepository, times(1)).deleteById("1");
    }

    @Test
    void deleteTag_NonExistingTag_ShouldThrowException(){
        given(tagsJPARepository.findById("1")).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            tagsDataService.delete("1");
        });

        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find tag with Id 1");

        verify(tagsJPARepository, times(1)).findById("1");
    }
}
