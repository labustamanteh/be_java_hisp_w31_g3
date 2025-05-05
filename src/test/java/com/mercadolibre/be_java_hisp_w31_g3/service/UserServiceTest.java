package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
import com.mercadolibre.be_java_hisp_w31_g3.exception.BadRequestException;
import com.mercadolibre.be_java_hisp_w31_g3.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.UserRepository;
import com.mercadolibre.be_java_hisp_w31_g3.util.CustomFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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

    private final User user1 = new User();
    private final User user2 = new User();
    private final long userId1 = user1.getUserId();
    private final long userId2 = user2.getUserId();

    private void mockUsersExistInRepo() {
        when(userRepository.getById(userId1)).thenReturn(Optional.of(user1));
        when(userRepository.getById(userId2)).thenReturn(Optional.of(user2));
    }

    @Test
    void addFollower_ValidUsers_FollowersListUpdated() {
        // Arrange
        mockUsersExistInRepo();

        // Act
        userService.addFollower(userId1, userId2);

        // Assert
        verify(userRepository).addFollower(userId1, userId2);
    }

    @Test
    void addFollower_UserAlreadyInFollowersList_ThrowsBadRequestException() {
        // Arrange
        User follower = new User();
        follower.setUserId(userId1);
        User followed = new User();
        followed.setUserId(userId2);

        followed.getFollowers().add(follower);

        when(userRepository.getById(userId1)).thenReturn(Optional.of(follower));
        when(userRepository.getById(userId2)).thenReturn(Optional.of(followed));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.addFollower(userId1, userId2));
    }

    @Test
    void addFollower_NullUserId_ThrowsBadRequestException() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.addFollower(null, userId2));
    }

    @Test
    void addFollower_NullUserToFollow_ThrowsBadRequestException() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.addFollower(userId1, null));
    }

    @Test
    void addFollower_UserFollowItself_ThrowsBadRequestException() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.addFollower(userId1, userId1));
    }

    @Test
    void addFollower_UserNotFound_ThrowsNotFoundException() {
        // Arrange
        when(userRepository.getById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.addFollower(userId1, userId2));
    }

    @Test
    void addFollower_UserAlreadyFollowed_ThrowsBadRequestException() {
        // Arrange
        user1.getFollowed().add(user2);
        mockUsersExistInRepo();

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.addFollower(userId1, userId2));
    }

    @Test
    void unfollowUser_ValidUsers_FollowerRemoved() {
        // Arrange
        when(userRepository.isAnyMatch(any())).thenReturn(true);

        // Act
        userService.unfollowUser(userId1, userId2);

        // Assert
        verify(userRepository).unfollowUser(userId1, userId2);
    }

    @Test
    void unfollowUser_UserNotFound_ThrowsNotFoundException() {
        // Arrange
        when(userRepository.isAnyMatch(any())).thenReturn(false);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.unfollowUser(userId1, userId2));
    }

    @Test
    void unfollowUser_UserNotInFollowersList_NoActionNeeded() {
        // Arrange
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
        when(userRepository.isAnyMatch(any())).thenReturn(true);

        // Act
        userService.unfollowUser(userId1, userId1);

        // Assert
        verify(userRepository).unfollowUser(userId1, userId1);
        assertTrue(user1.getFollowed().isEmpty());
        assertTrue(user1.getFollowers().isEmpty());
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
    public void getFollowedList_ValidOrderAsc_returnListOrderName() {
        // Arrange
        User user = CustomFactory.getUserFollowedListAsc();
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
    public void getFollowedList_ValidOrderDesc_returnListOrderName() {
        // Arrange
        User user = CustomFactory.getUserFollowedListDesc();
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
    public void getFollowedList_InvalidTypeOrder_throwsException() {
        // Arrange
        User user = CustomFactory.getUserFollowedListDesc();
        when(userRepository.isAnyMatch(any())).thenReturn(true);
        when(userRepository.getById(4L)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.getFollowedList(4L, "name_ascs"));
    }

}
