package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
import com.mercadolibre.be_java_hisp_w31_g3.exception.BadRequestException;
import com.mercadolibre.be_java_hisp_w31_g3.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.IUserRepository;
import com.mercadolibre.be_java_hisp_w31_g3.util.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final ObjectMapper mapper;

    public UserService(IUserRepository userRepository, ObjectMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        loadDataBase();
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> userList = userRepository.getAll();
        if (userList.isEmpty()) {
            throw new NotFoundException("No hay usuarios para mostrar");
        }

        return userList.stream()
                .map(UserMapper::getUserDto)
                .toList();
    }

    @Override
    public void addFollower(Long userId, Long userToFollow) {
        if (userId == null || userToFollow == null) {
            throw new BadRequestException("Los ids de los usuarios no deben ser nulos.");
        }
        if (Objects.equals(userId, userToFollow)) {
            throw new BadRequestException("Un usuario no puede seguirse a sí mismo.");
        }

        Optional<User> userFollower = userRepository.getById(userId);
        Optional<User> userFollowed = userRepository.getById(userToFollow);

        if (userFollower.isEmpty() || userFollowed.isEmpty()) {
            throw new NotFoundException("Alguno de los usuarios no existe.");
        }

        if (userFollowed.get().getFollowers().stream().anyMatch(follower -> follower.getUserId().equals(userId))
        || userFollower.get().getFollowed().stream().anyMatch(followed -> followed.getUserId().equals(userToFollow))) {
            throw new BadRequestException("El usuario ya sigue a este usuario.");
        }

        userRepository.addFollower(userId, userToFollow);
    }

    @Override
    public UserDto getFollowersCount(Long userId) {
        checkUserExists(userId);

        User user = userRepository.getById(userId).get();
        Long followersCount = (long) user.getFollowers().size();
        return UserDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .followersCount(followersCount)
                .build();
    }

    @Override
    public UserDto getFollowersById(Long id, String order) {
        checkUserExists(id);

        User user = userRepository.getById(id).get();
        List<User> orderedFollowers = getUserListOrderedByName(order, user.getFollowers());
        List<UserDto> orderedFollowersDtos = orderedFollowers.stream().map(
                        u -> UserDto.builder()
                                .userId(u.getUserId())
                                .userName(u.getUserName())
                                .build())
                .toList();

        return UserDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .followers(orderedFollowersDtos)
                .build();
    }

    @Override
    public UserDto getFollowedList(Long id, String order) {
        checkUserExists(id);

        Optional<User> user = userRepository.getById(id);
        List<User> orderedFollowed = getUserListOrderedByName(order, user.get().getFollowed());
        List<UserDto> userList = orderedFollowed.stream().map(
                u -> UserDto.builder()
                        .userId(u.getUserId())
                        .userName(u.getUserName()).build()
        ).toList();

        return UserDto.builder()
                .userId(user.get().getUserId())
                .userName(user.get().getUserName())
                .followed(userList)
                .build();
    }

    @Override
    public void unfollowUser(Long userId, Long userIdToUnfollow) {
        checkUserExists(userId);
        checkUserExists(userIdToUnfollow);

        userRepository.unfollowUser(userId, userIdToUnfollow);
    }

    private List<User> getUserListOrderedByName(String order, List<User> userList) {
        switch (order) {
            case "name_asc":
                userList = userList.stream().sorted(Comparator.comparing(User::getUserName)).toList();
                break;
            case "name_desc":
                userList = userList.stream().sorted(Comparator.comparing(User::getUserName).reversed()).toList();
                break;
            default:
                throw new NotFoundException("Debe ingresar un tipo de orden correcto.");
        }
        return userList;
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.isAnyMatch(user -> user.getUserId().equals(userId))) {
            throw new NotFoundException("No se encontró el usuario con el id ingresado");
        }
    }

    private void loadDataBase() {
        try {
            File file = ResourceUtils.getFile("classpath:users.json");
            this.userRepository.addAll(mapper.readValue(file, new TypeReference<List<User>>() {
            }));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
