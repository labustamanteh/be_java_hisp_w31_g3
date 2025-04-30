package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.exception.BadRequestException;
import com.mercadolibre.be_java_hisp_w31_g3.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void addFollower_ValidUsers_FollowersListUpdated() {
        // Arrange
        User user1 = new User();
        User user2 = new User();

        long userId1 = user1.getUserId();
        long userId2 = user2.getUserId();

        when(userRepository.getById(userId1)).thenReturn(Optional.of(user1));
        when(userRepository.getById(userId2)).thenReturn(Optional.of(user2));

        doAnswer(invocation -> {
            user1.getFollowed().add(user2);
            user2.getFollowers().add(user1);
            return null;
        }).when(userRepository).addFollower(userId1, userId2);

        // Act
        userService.addFollower(userId1, userId2);

        // Assert
        assertTrue(user2.getFollowers().contains(user1), "User1 debería ser un seguidor de User2");
        assertTrue(user1.getFollowed().contains(user2), "User2 debería ser un seguidor de User1");
    }

    @Test
    void addFollower_NullUserId_ThrowsBadRequestException() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.addFollower(null, 2L));
    }

    @Test
    void addFollower_NullUserToFollow_ThrowsBadRequestException() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.addFollower(1L, null));
    }

    @Test
    void addFollower_SameUserId_ThrowsBadRequestException() { {
        // Arrange
        Long userId = 1L;

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.addFollower(userId, userId));
    }

    @Test
    void addFollower_UserNotFound_ThrowsNotFoundException() {
        // Arrange
        when(userRepository.getById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.addFollower(1L, 2L));
    }

    @Test
    void addFollower_UserAlreadyFollowed_ThrowsBadRequestException() { {
        // Arrange
        User user1 = new User();
        User user2 = new User();

        long userId1 = user1.getUserId();
        long userId2 = user2.getUserId();

        user1.getFollowed().add(user2);

        when(userRepository.getById(userId1)).thenReturn(Optional.of(user1));
        when(userRepository.getById(userId2)).thenReturn(Optional.of(user2));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.addFollower(userId1, userId2));
    }
}
