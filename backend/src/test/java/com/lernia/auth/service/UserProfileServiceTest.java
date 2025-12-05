package com.lernia.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lernia.auth.dto.UserProfileRequest;
import com.lernia.auth.dto.UserProfileResponse;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.entity.enums.Gender;
import com.lernia.auth.entity.enums.UserRole;
import com.lernia.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.Optional;

class UserProfileServiceTest {

    @InjectMocks
    private UserProfileService userProfileService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProfileByIdSuccess() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("u1");
        user.setName("User One");
        user.setEmail("u1@example.com");
        user.setAge(30);
        user.setGender(Gender.FEMALE);
        user.setLocation("City");
        user.setProfilePicture("pic.png");
        user.setJobTitle("Engineer");
        user.setCreationDate(LocalDate.of(2020, 1, 1));
        user.setUserRole(UserRole.REGULAR);
        user.setPremiumStartDate(LocalDate.of(2021, 6, 1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserProfileResponse res = userProfileService.getProfileById(1L);

        assertNotNull(res);
        assertEquals(1L, res.getId());
        assertEquals("u1", res.getUsername());
        assertEquals("User One", res.getName());
        assertEquals("u1@example.com", res.getEmail());
        assertEquals(30, res.getAge());
        assertEquals("FEMALE", res.getGender());
        assertEquals("City", res.getLocation());
        assertEquals("pic.png", res.getProfilePicture());
        assertEquals("Engineer", res.getJobTitle());
        assertEquals(LocalDate.of(2020, 1, 1), res.getCreationDate());
        assertEquals("REGULAR", res.getUserRole());
        assertEquals(LocalDate.of(2021, 6, 1), res.getPremiumStartDate());
    }

    @Test
    void testGetProfileByIdNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userProfileService.getProfileById(99L));
    }

    /*@Test
    void testGetProfileByUsernameSuccess() {
        UserEntity user = new UserEntity();
        user.setId(2L);
        user.setUsername("alice");
        user.setName("Alice");
        user.setEmail("alice@example.com");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        UserProfileResponse res = userProfileService.getProfileByUsername("alice");

        assertNotNull(res);
        assertEquals(2L, res.getId());
        assertEquals("alice", res.getUsername());
        assertEquals("Alice", res.getName());
        assertEquals("alice@example.com", res.getEmail());
    }

    @Test
    void testUpdateProfileById() {
        UserEntity user = new UserEntity();
        user.setId(3L);
        user.setUsername("bob");
        user.setName("Bob");
        user.setLocation("OldTown");
        user.setJobTitle("OldJob");
        user.setProfilePicture("old.png");
        user.setGender(Gender.OTHER);

        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UserProfileRequest req = new UserProfileRequest();
        req.setName("Bobby");
        req.setLocation("NewCity");
        req.setJobTitle("Developer");
        req.setProfilePicture("new.png");
        req.setGender(Gender.MALE);

        UserProfileResponse res = userProfileService.updateProfile(3L, req);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository, times(1)).save(captor.capture());
        UserEntity saved = captor.getValue();

        assertEquals("Bobby", saved.getName());
        assertEquals("NewCity", saved.getLocation());
        assertEquals("Developer", saved.getJobTitle());
        assertEquals("new.png", saved.getProfilePicture());
        assertEquals(Gender.MALE, saved.getGender());

        assertNotNull(res);
        assertEquals(3L, res.getId());
        assertEquals("Bobby", res.getName());
        assertEquals("MALE", res.getGender());
    }

    @Test
    void testUpdateProfileByUsername() {
        UserEntity user = new UserEntity();
        user.setId(4L);
        user.setUsername("carol");
        user.setName("Carol");

        when(userRepository.findByUsername("carol")).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UserProfileRequest req = new UserProfileRequest();
        req.setName("Carolyn");

        UserProfileResponse res = userProfileService.updateProfileByUsername("carol", req);

        verify(userRepository, times(1)).save(any(UserEntity.class));
        assertEquals("Carolyn", res.getName());
        assertEquals(4L, res.getId());
        assertEquals("carol", res.getUsername());
    }

    @Test
    void testUpdateProfileNotFoundById() {
        when(userRepository.findById(123L)).thenReturn(Optional.empty());
        UserProfileRequest req = new UserProfileRequest();
        assertThrows(UsernameNotFoundException.class, () -> userProfileService.updateProfile(123L, req));
    }

    @Test
    void testUpdateProfileNotFoundByUsername() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());
        UserProfileRequest req = new UserProfileRequest();
        assertThrows(UsernameNotFoundException.class, () -> userProfileService.updateProfileByUsername("missing", req));
    }*/

    @Test
    void testGetProfileById_GenderAndRoleNull() {
        UserEntity user = new UserEntity();
        user.setId(10L);
        user.setUsername("noRoleUser");
        user.setName("No Role User");
        user.setEmail("norole@example.com");
        user.setAge(25);
        user.setGender(null);
        user.setUserRole(null);
        user.setLocation("SomeCity");

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        UserProfileResponse res = userProfileService.getProfileById(10L);

        assertNotNull(res);
        assertEquals(10L, res.getId());
        assertEquals("noRoleUser", res.getUsername());
        assertEquals("No Role User", res.getName());
        assertEquals("norole@example.com", res.getEmail());
        assertEquals(25, res.getAge());
        assertEquals("SomeCity", res.getLocation());

        assertNull(res.getGender());
        assertNull(res.getUserRole());
    }

    @Test
    void testUpdateProfile_DoesNotOverrideWithNullValues() {
        UserEntity user = new UserEntity();
        user.setId(20L);
        user.setUsername("keepFields");
        user.setName("Original Name");
        user.setLocation("OriginalCity");
        user.setJobTitle("OriginalJob");
        user.setProfilePicture("original.png");
        user.setGender(Gender.FEMALE);

        when(userRepository.findById(20L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UserProfileRequest req = new UserProfileRequest();
        req.setLocation("NewCity");

        UserProfileResponse res = userProfileService.updateProfile(20L, req);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        UserEntity saved = captor.getValue();

        assertEquals("Original Name", saved.getName());
        assertEquals("OriginalJob", saved.getJobTitle());
        assertEquals("original.png", saved.getProfilePicture());
        assertEquals(Gender.FEMALE, saved.getGender());
        assertEquals("NewCity", saved.getLocation());

        assertNotNull(res);
        assertEquals(20L, res.getId());
        assertEquals("Original Name", res.getName());
        assertEquals("NewCity", res.getLocation());
        assertEquals("FEMALE", res.getGender());
    }

    @Test
    void testUpdateProfileByUsername_WithEmptyRequestKeepsExistingValues() {
        UserEntity user = new UserEntity();
        user.setId(30L);
        user.setUsername("emptyUpdateUser");
        user.setName("Existing Name");
        user.setLocation("ExistingCity");
        user.setJobTitle("ExistingJob");
        user.setProfilePicture("existing.png");
        user.setGender(Gender.OTHER);

        when(userRepository.findByUsername("emptyUpdateUser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UserProfileRequest req = new UserProfileRequest();

        UserProfileResponse res = userProfileService.updateProfileByUsername("emptyUpdateUser", req);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        UserEntity saved = captor.getValue();

        assertEquals("Existing Name", saved.getName());
        assertEquals("ExistingCity", saved.getLocation());
        assertEquals("ExistingJob", saved.getJobTitle());
        assertEquals("existing.png", saved.getProfilePicture());
        assertEquals(Gender.OTHER, saved.getGender());

        assertNotNull(res);
        assertEquals(30L, res.getId());
        assertEquals("emptyUpdateUser", res.getUsername());
        assertEquals("Existing Name", res.getName());
        assertEquals("ExistingCity", res.getLocation());
        assertEquals("OTHER", res.getGender());
    }

    @Test
    void testGetProfileByUsernameNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> userProfileService.getProfileByUsername("unknown")
        );
    }

    }
}
