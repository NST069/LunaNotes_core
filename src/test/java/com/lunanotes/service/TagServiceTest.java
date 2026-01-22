package com.lunanotes.service;

import com.lunanotes.exception.ObjectNotFoundException;
import com.lunanotes.model.Note;
import com.lunanotes.model.Tag;
import com.lunanotes.model.User;
import com.lunanotes.repository.TagJPARepository;
import com.lunanotes.security.CurrentUserService;
import com.lunanotes.util.UserRole;
import org.assertj.core.api.AssertionsForClassTypes;
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
class TagServiceTest {

    @Mock
    TagJPARepository tagJPARepository;

    @Mock
    CurrentUserService currentUserService;

    @InjectMocks
    TagService tagService;

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
                .username("John Doe")
                .password("password")
                .roles(UserRole.USER.name())
                .build();
        User user2 = User.builder()
                .id(2L)
                .username("James Doe")
                .password("password")
                .roles(UserRole.USER.name())
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

    @Test
    void findById_ExistingTag_ShouldReturnTag() {
        //given(currentUserService.getCurrentUserId()).willReturn(1L);
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        tag.setOwner(users.get(0));

        given(tagJPARepository.findById("1")).willReturn(Optional.of(tag));

        Tag result = tagService.findById("1");

        assertThat(result.getId()).isEqualTo(tag.getId());
        assertThat(result.getName()).isEqualTo(tag.getName());
        assertThat(result.getOwner()).isEqualTo(tag.getOwner());

        verify(tagJPARepository, times(1)).findById("1");
    }

    @Test
    void findById_NonExistingTag_ShouldThrowException() {
        given(tagJPARepository.findById(Mockito.any(String.class))).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            tagService.findById("1");
        });

        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find tag with Id 1");

        verify(tagJPARepository, times(1)).findById("1");
    }

    @Test
    void findAllTags_ShouldReturnList() {
        given(currentUserService.getCurrentUserId()).willReturn(1L);
        List<Tag> currentUserTags = this.tags.stream()
                .filter(tag -> ((tag.getOwner() != null) ? tag.getOwner().getId() : 0) == 1).toList();
        given(tagJPARepository.findByOwnerId(1L)).willReturn(currentUserTags);

        List<Tag> result = tagService.findAll();

        assertThat(result.size()).isEqualTo(currentUserTags.size());
        verify(tagJPARepository, times(1)).findByOwnerId(1L);
    }

    @Test
    void findAllTagsByUserId_ShouldReturnList() {
        List<Tag> filteredTags = this.tags.stream()
                .filter(tag -> ((tag.getOwner() != null) ? tag.getOwner().getId() : 0) == 1).toList();

        given(tagJPARepository.findByOwnerId(1L)).willReturn(filteredTags);

        List<Tag> result = tagService.findByOwnerId("1");

        assertThat(result.size()).isEqualTo(filteredTags.size());
        verify(tagJPARepository, times(1)).findByOwnerId(1L);
    }

    @Test
    void saveTag_shouldSave() {
        Tag newTag = new Tag();
        newTag.setName("test");
        newTag.setOwner(users.get(0));

        given(tagJPARepository.save(newTag)).willReturn(newTag);

        Tag result = tagJPARepository.save(newTag);

        assertThat(result.getId()).isEqualTo(newTag.getId());
        assertThat(result.getName()).isEqualTo(newTag.getName());
        assertThat(result.getOwner()).isEqualTo(newTag.getOwner());

        verify(tagJPARepository, times(1)).save(newTag);
    }

    @Test
    void updateTag_ExistingTag_ShouldUpdate(){
        given(currentUserService.getCurrentUserId()).willReturn(1L);
        Tag oldTag = new Tag();
        oldTag.setId(1L);
        oldTag.setName("test");
        oldTag.setOwner(users.get(0));

        Tag update = new Tag();
        update.setId(oldTag.getId());
        update.setName("test2");
        update.setOwner(oldTag.getOwner());

        given(tagJPARepository.findByIdAndOwnerId(1L, 1L)).willReturn(Optional.of(oldTag));
        given(tagJPARepository.save(oldTag)).willReturn(oldTag);

        Tag result = tagService.update("1", update);

        assertThat(result.getId()).isEqualTo(update.getId());
        assertThat(result.getName()).isEqualTo(update.getName());

        verify(tagJPARepository, times(1)).findByIdAndOwnerId(1L, 1L);
        verify(tagJPARepository, times(1)).save(oldTag);
    }

    @Test
    void updateTag_NonExistingTag_ShouldThrowException(){
        given(currentUserService.getCurrentUserId()).willReturn(1L);
        Tag update = new Tag();
        update.setId(1L);
        update.setName("test2");
        update.setOwner(users.get(0));

        given(tagJPARepository.findByIdAndOwnerId(1L, 1L)).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            tagService.update("1", update);
        });

        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find tag with Id 1");

        verify(tagJPARepository, times(1)).findByIdAndOwnerId(1L, 1L);
    }

    @Test
    void deleteTag_ExistingTag_ShouldDelete(){
        given(currentUserService.getCurrentUserId()).willReturn(1L);
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        tag.setOwner(users.get(0));

        given(tagJPARepository.findByIdAndOwnerId(1L, 1L)).willReturn(Optional.of(tag));
        doNothing().when(tagJPARepository).deleteById("1");

        tagService.delete("1");

        verify(tagJPARepository, times(1)).deleteById("1");
    }

    @Test
    void deleteTag_NonExistingTag_ShouldThrowException(){
        given(currentUserService.getCurrentUserId()).willReturn(1L);
        given(tagJPARepository.findByIdAndOwnerId(1L, 1L)).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            tagService.delete("1");
        });

        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find tag with Id 1");

        verify(tagJPARepository, times(1)).findByIdAndOwnerId(1L, 1L);
    }
}
