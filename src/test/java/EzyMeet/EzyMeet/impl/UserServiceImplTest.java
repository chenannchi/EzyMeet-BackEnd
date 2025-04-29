package EzyMeet.EzyMeet.impl;

import EzyMeet.EzyMeet.model.User;
import EzyMeet.EzyMeet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {


    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createUser() {
        User inputUser = new User(
                "google-123",
                "test@example.com",
                false,
                "google.com",
                null,
                null,
                null,
                null,
                "Test User",
                null,
                null
        );

        User savedUser = new User(
                "google-123",
                "test@example.com",
                false,
                "google.com",
                null,
                null,
                null,
                "user123",
                "Test User",
                null,
                null
        );

        when(userRepository.create(inputUser)).thenReturn(savedUser);

        User result = userService.create(inputUser);

        assertEquals(savedUser, result);
        assertEquals("user123", result.getId());
        verify(userRepository).create(inputUser);
    }

    @Test
    public void updateUser() {
        User userToUpdate = new User(
                "google-123",
                "test@example.com",
                false,
                "google.com",
                null,
                null,
                null,
                "existing-user-123",
                "Test User",
                null,
                null
        );

        User updatedUser = new User(
                "google-123",
                "test@example.com",
                false,
                "google.com",
                null,
                null,
                null,
                "existing-user-123",
                "Updated User Name", // Changed display name
                null,
                null
        );

        when(userRepository.update(userToUpdate)).thenReturn(updatedUser);

        User result = userService.update(userToUpdate);

        assertEquals(updatedUser, result);
        assertEquals("Updated User Name", result.getDisplayName());
        verify(userRepository).update(userToUpdate);
    }

    @Test
    public void getUserIdByGoogleId() {
        String googleId = "google-123";
        String expectedUserId = "user-123";

        User user1 = new User(
                googleId,
                "test@example.com",
                false,
                "google.com",
                null,
                null,
                null,
                expectedUserId,
                "Test User",
                null,
                null
        );

        User user2 = new User(
                "google-456",         // Different googleUid
                "other@example.com",
                false,
                "google.com",
                null,
                null,
                null,
                "user-456",
                "Other User",
                null,
                null
        );

        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        String result = userService.getUserIdByGoogleId(googleId);

        assertEquals(expectedUserId, result);
        verify(userRepository).findAll();
    }

    @Test
    public void syncGoogleUserWhenUserExistsShouldUpdateUser() {
        String existingGoogleId = "google-123";
        String existingUserId = "user-123";

        User googleUser = new User(
                existingGoogleId,
                "test@example.com",
                true,
                "google.com",
                "token123",
                "2023-01-01",
                "2023-05-01",
                null,
                "Google User",
                "photo-url.jpg",
                null
        );

        User existingUser = new User(
                existingGoogleId,
                "old-email@example.com",
                false,
                "google.com",
                "old-token",
                "2022-01-01",
                "2022-12-31",
                existingUserId,
                "Old Display Name",
                null,
                null
        );

        User updatedUser = new User(
                existingGoogleId,
                "test@example.com",
                true,
                "google.com",
                "token123",
                "2023-01-01",
                "2023-05-01",
                existingUserId,
                "Google User",
                "photo-url.jpg",
                null
        );

        when(userRepository.findAll()).thenReturn(List.of(existingUser));
        when(userRepository.update(any(User.class))).thenReturn(updatedUser);

        User result = userService.syncGoogleUser(googleUser);

        assertEquals(existingUserId, result.getId());
        assertEquals("Google User", result.getDisplayName());
        verify(userRepository).update(googleUser);
        verify(userRepository, never()).create(any(User.class));
    }


    @Test
    public void syncGoogleUserWhenUserDoesNotExistShouldCreateUser() {
        String newGoogleId = "google-new";
        String generatedUserId = "user-new";

        User googleUser = new User(
                newGoogleId,
                "new@example.com",
                true,
                "google.com",
                "token-new",
                "2023-01-01",
                "2023-05-01",
                null,
                "New User",
                "photo-url.jpg",
                null
        );

        User createdUser = new User(
                newGoogleId,
                "new@example.com",
                true,
                "google.com",
                "token-new",
                "2023-01-01",
                "2023-05-01",
                generatedUserId,
                "New User",
                "photo-url.jpg",
                null
        );

        User existingUser = new User(
                "google-different",
                "different@example.com",
                false,
                "google.com",
                null,
                null,
                null,
                "user-different",
                "Different User",
                null,
                null
        );

        when(userRepository.findAll()).thenReturn(List.of(existingUser));
        when(userRepository.create(any(User.class))).thenReturn(createdUser);

        User result = userService.syncGoogleUser(googleUser);

        assertEquals(generatedUserId, result.getId());
        verify(userRepository).create(googleUser);
        verify(userRepository, never()).update(any(User.class));
    }

    @Test
    public void getAllParticipantOptions() {
        User user1 = new User(
                "google-123",
                "user1@example.com",
                true,
                "google.com",
                null,
                null,
                null,
                "user-id-1",
                "User One",
                null,
                null
        );

        User user2 = new User(
                "google-456",
                "user2@example.com",
                true,
                "google.com",
                null,
                null,
                null,
                "user-id-2",
                "User Two",
                null,
                null
        );

        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<Map<String, String>> result = userService.getAllParticipantOptions();

        assertEquals(2, result.size());

        Map<String, String> firstUser = result.get(0);
        assertEquals("user-id-1", firstUser.get("id"));
        assertEquals("user1@example.com", firstUser.get("email"));
        assertEquals("User One", firstUser.get("displayName"));

        Map<String, String> secondUser = result.get(1);
        assertEquals("user-id-2", secondUser.get("id"));
        assertEquals("user2@example.com", secondUser.get("email"));
        assertEquals("User Two", secondUser.get("displayName"));

        verify(userRepository).findAll();
    }

    @Test
    public void getUserById() {
        User user1 = new User(
                "google-123",
                "user1@example.com",
                true,
                "google.com",
                null,
                null,
                null,
                "user-id-1",
                "User One",
                null,
                null
        );

        User user2 = new User(
                "google-456",
                "user2@example.com",
                true,
                "google.com",
                null,
                null,
                null,
                "user-id-2",
                "User Two",
                null,
                null
        );

        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        User result = userService.getUserById("user-id-1");

        assertEquals(user1, result);
        verify(userRepository).findAll();
    }

    @Test
    public void getUserByIdNotFound() {
        User user1 = new User(
                "google-123",
                "user1@example.com",
                true,
                "google.com",
                null,
                null,
                null,
                "user-id-1",
                "User One",
                null,
                null
        );

        User user2 = new User(
                "google-456",
                "user2@example.com",
                true,
                "google.com",
                null,
                null,
                null,
                "user-id-2",
                "User Two",
                null,
                null
        );

        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        User result = userService.getUserById("non-existent-id");

        assertNull(result);
        verify(userRepository).findAll();
    }
}
