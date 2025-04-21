package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.dto.PostDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.ProductDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
import com.mercadolibre.be_java_hisp_w31_g3.exception.BadRequestException;
import com.mercadolibre.be_java_hisp_w31_g3.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.IUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .map(user ->
                        UserDto
                                .builder()
                                .userId(user.getUserId())
                                .userName(user.getUserName())
                                .followers(user.getFollowers().stream()
                                        .map(followers ->
                                                UserDto
                                                        .builder()
                                                        .userName(followers.getUserName())
                                                        .userId(followers.getUserId())
                                                        .build())
                                        .collect(Collectors.toList())
                                )
                                .followed(
                                        user.getFollowed().stream()
                                                .map(followed ->
                                                        UserDto
                                                                .builder()
                                                                .userName(followed.getUserName())
                                                                .userId(followed.getUserId())
                                                                .build())
                                                .collect(Collectors.toList())
                                )
                                .posts(
                                        user.getPosts().stream()
                                                .map(post ->
                                                        PostDto
                                                                .builder()
                                                                .postId(post.getPostId())
                                                                .userId(post.getUserId())
                                                                .date(post.getDate().toString())
                                                                .product(ProductDto
                                                                        .builder()
                                                                        .productName(post.getProduct().getProductName())
                                                                        .build())
                                                                .build()
                                                )
                                                .collect(Collectors.toList())
                                )
                                .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    public void addFollower(Long userId, Long userToFollow) {
        if (userId == null || userToFollow == null) {
            throw new IllegalArgumentException("Los ids de los usuarios no deben ser nulos.");
        }
        if (Objects.equals(userId, userToFollow)) {
            throw new IllegalArgumentException("Un usuario no puede seguirse a sí mismo.");
        }

        Optional<User> userFollower = userRepository.getById(userId);
        Optional<User> userFollowed = userRepository.getById(userToFollow);

        if (userFollower.isEmpty() || userFollowed.isEmpty()) {
            throw new NotFoundException("Alguno de los usuarios no existe.");
        }

        userRepository.addFollower(userId, userToFollow);
    }

    @Override
    public UserDto getFollowersCount(Long userId) {
        Optional<User> optionalUser = userRepository.getById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("No se encontró el usuario con el id ingresado");
        }
        User user = optionalUser.get();
        Long followersCount = (long) user.getFollowers().size();
        return UserDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .followersCount(followersCount)
                .build();
    }

    @Override
    public UserDto getFollowersById(Long id, String order) {
        Optional<User> optionalUser = userRepository.getById(id);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("No existe el usuario con el id ingresado");
        }

        User user = optionalUser.get();

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
        Optional<User> user = userRepository.getById(id);
        if (user.isEmpty())
            throw new NotFoundException("Error, no se encontró un usuario con el Id enviado.");

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
        if(!userRepository.isAnyMatch(user -> user.getUserId().equals(userId))){
            throw new BadRequestException("El usuario no se encontró");
        }
        if(!userRepository.isAnyMatch(user -> user.getUserId().equals(userIdToUnfollow))){
            throw new BadRequestException("El usuario a dejar de seguir no se encontró");
        }
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
        }
        return userList;
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
