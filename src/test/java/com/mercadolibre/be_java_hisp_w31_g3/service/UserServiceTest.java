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

import static org.junit.jupiter.api.Assertions.*;
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

        // Act
        userService.addFollower(userId1, userId2);

        // Assert
        verify(userRepository).addFollower(userId1, userId2);
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
    void addFollower_UserFollowItself_ThrowsBadRequestException() {
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
    void addFollower_UserAlreadyFollowed_ThrowsBadRequestException()  {
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

    @Test
    void unfollowUser_ValidUsers_FollowerRemoved() {
        // Arrange
        User user1 = new User();
        User user2 = new User();

        long userId1 = user1.getUserId();
        long userId2 = user2.getUserId();

        when(userRepository.isAnyMatch(any())).thenReturn(true);

        // Act
        userService.unfollowUser(userId1, userId2);

        // Assert
        verify(userRepository).unfollowUser(userId1, userId2);
    }

    @Test
    void unfollowUser_UserNotFound_ThrowsNotFoundException() {
        // Arrange
        when(userRepository.isAnyMatch(any())).thenReturn(false);// Ningún usuario encontrado

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.unfollowUser(1L, 2L));
    }

    @Test
    void unfollowUser_UserNotInFollowersList_NoActionNeeded() {
        // Arrange
        User user1 = new User();
        User user2 = new User();
        long userId1 = user1.getUserId();
        long userId2 = user2.getUserId();

        when(userRepository.isAnyMatch(any())).thenReturn(true);
        // Act
        userService.unfollowUser(userId1, userId2);

        // Assert
        assertFalse(user1.getFollowed().contains(user2));
        assertFalse(user2.getFollowers().contains(user1));
    }

    @Test
    void unfollowUser_UserUnfollowItself_NoOperation() {
        // Arrange
        User user = new User();
        long userId = user.getUserId();

        when(userRepository.isAnyMatch(any())).thenReturn(true);
        // Act
        userService.unfollowUser(userId, userId);

        // Assert
        verify(userRepository).unfollowUser(userId, userId);

        assertTrue(user.getFollowed().isEmpty());
        assertTrue(user.getFollowers().isEmpty());
    }
}
