package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
import com.mercadolibre.be_java_hisp_w31_g3.exception.BadRequestException;
import com.mercadolibre.be_java_hisp_w31_g3.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g3.model.Post;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.UserRepository;
import com.mercadolibre.be_java_hisp_w31_g3.util.CustomFactory;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    void addFollower_UserAlreadyFollowed_ThrowsBadRequestException() {
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
    void getFollowersCountTest_validId_returnUserDto() {
        // arrange
        Long userId = 2L;
        when(userRepository.isAnyMatch(any())).thenReturn(true);
        User user = CustomFactory.getFollowersCount(userId);
        Optional<User> optionalUser = Optional.of(user);
        when(userRepository.getById(Mockito.anyLong())).thenReturn(optionalUser);

        // act
        UserDto userDto = userService.getFollowersCount(userId);
        // assert
        assertEquals(user.getUserName(), userDto.getUserName());
        assertEquals(user.getFollowers().size(), userDto.getFollowersCount());
    }

    @Test
    public void getFollowersCountTest_invalid_throwsNotFoundException() {
        // arrange
        Long userId = 2000L;
        when(userRepository.isAnyMatch(any())).thenReturn(false);

        // act &assert
        assertThrows(NotFoundException.class, () -> userService.getFollowersCount(userId));
    }

    @Test
    public void getUserOrderAscTest_valid_returnListOrderName() {
        // Arrange
        User user = CustomFactory.getUserOrderAscTest();
        when(userRepository.isAnyMatch(any())).thenReturn(true);
        when(userRepository.getById(4L)).thenReturn(Optional.of(user));

        // Act
        UserDto result = userService.getFollowedList(4L, "name_asc");
        List<UserDto> followedList = result.getFollowed();

        // Assert
        assertEquals("Alice Johnson", followedList.get(0).getUserName());
        assertEquals("Jane Smith", followedList.get(1).getUserName());
    }

    @Test
    public void getUserOrderDescTest_valid_returnListOrderName() {
        // Arrange
        User user = CustomFactory.getUserOrderAscTest();
        when(userRepository.isAnyMatch(any())).thenReturn(true);
        when(userRepository.getById(4L)).thenReturn(Optional.of(user));

        // Act
        UserDto result = userService.getFollowedList(4L, "name_desc");
        List<UserDto> followedList = result.getFollowed();

        // Assert
        assertEquals("Jane Smith", followedList.get(0).getUserName());
        assertEquals("Alice Johnson", followedList.get(1).getUserName());
    }

    @Test
    public void getUserOrderDescTest_invalid_throwsException() {
        // Arrange
        User user = CustomFactory.getUserOrderAscTest();
        when(userRepository.isAnyMatch(any())).thenReturn(true);
        when(userRepository.getById(4L)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.getFollowedList(4L, "name_ascs"));
    }

}
