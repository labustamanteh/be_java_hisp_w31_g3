package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.exception.BadRequestException;
import com.mercadolibre.be_java_hisp_w31_g3.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.UserRepository;
import com.mercadolibre.be_java_hisp_w31_g3.util.CustomFactory;
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

    private User user1;
    private User user2;
    private final long userId1 = 1L;
    private final long userId2 = 2L;

    @BeforeEach
    void setUp() {
        // Usamos la CustomFactory para simplificar la creación de usuarios
        user1 = CustomFactory.getUserWithIdAndName(userId1, "User1");
        user2 = CustomFactory.getUserWithIdAndName(userId2, "User2");
    }

    @Test
    void addFollower_ValidUsers_FollowersListUpdated() {
        // Arrange
        when(userRepository.getById(userId1)).thenReturn(Optional.of(user1));
        when(userRepository.getById(userId2)).thenReturn(Optional.of(user2));

        // Act
        userService.addFollower(userId1, userId2);

        // Assert
        verify(userRepository).addFollower(userId1, userId2);
    }

    @Test
    void addFollower_NullUserId_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> userService.addFollower(null, userId2));
    }

    @Test
    void addFollower_NullUserToFollow_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> userService.addFollower(userId1, null));
    }

    @Test
    void addFollower_UserFollowItself_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> userService.addFollower(userId1, userId1));
    }

    @Test
    void addFollower_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.getById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.addFollower(userId1, userId2));
    }

    @Test
    void addFollower_UserAlreadyFollowed_ThrowsBadRequestException() {
        user1.getFollowed().add(user2);
        when(userRepository.getById(userId1)).thenReturn(Optional.of(user1));
        when(userRepository.getById(userId2)).thenReturn(Optional.of(user2));

        assertThrows(BadRequestException.class, () -> userService.addFollower(userId1, userId2));
    }

    @Test
    void unfollowUser_ValidUsers_FollowerRemoved() {
        when(userRepository.isAnyMatch(any())).thenReturn(true);

        userService.unfollowUser(userId1, userId2);

        verify(userRepository).unfollowUser(userId1, userId2);
    }

    @Test
    void unfollowUser_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.isAnyMatch(any())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.unfollowUser(userId1, userId2));
    }

    @Test
    void unfollowUser_UserNotInFollowersList_NoActionNeeded() {
        when(userRepository.isAnyMatch(any())).thenReturn(true);

        userService.unfollowUser(userId1, userId2);

        assertFalse(user1.getFollowed().contains(user2));
        assertFalse(user2.getFollowers().contains(user1));
    }

    @Test
    void unfollowUser_UserUnfollowItself_NoOperation() {
        when(userRepository.isAnyMatch(any())).thenReturn(true);

        userService.unfollowUser(userId1, userId1);

        verify(userRepository).unfollowUser(userId1, userId1);

        assertTrue(user1.getFollowed().isEmpty());
        assertTrue(user1.getFollowers().isEmpty());
    }
}