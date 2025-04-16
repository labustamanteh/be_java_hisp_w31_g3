package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.dto.FollowersCountDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
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
        if(userList.isEmpty()){
            throw new NotFoundException("No existen los usuarios");
        }
        return userList.stream()
                .map(v -> mapper.convertValue(v,UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void addFollower(Long userId, Long userToFollow) {
        if (userId == null || userToFollow == null) {
            throw new IllegalArgumentException("Los identificadores de usuario no deben ser nulos.");
        }
        if (Objects.equals(userId, userToFollow)) {
            throw new IllegalArgumentException("Un usuario no puede seguirse a sí mismo.");
        }

        User userFollower = userRepository.getById(userId);
        User userFollowed = userRepository.getById(userToFollow);

        if (userFollower == null || userFollowed == null) {
            throw new NotFoundException("Alguno de los usuarios no existe.");
        }
        userFollowed.getFollowers().add(userFollower);
    }

    @Override
    public FollowersCountDto getfollowersCount(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("No se encontró el usuario con el id ingresado");
        }
        User userFind = userRepository.getById(userId);
        Long followersCount = Long.valueOf(userFind.getFollowers().size());
        return new FollowersCountDto(userId, userFind.getUserName(), followersCount);
    }

    private void loadDataBase() {
        try {
            File file = ResourceUtils.getFile("classpath:users.json");
            this.userRepository.addAll(mapper.readValue(file, new TypeReference<List<User>>() {}));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final UserDto.UserDtoBuilder userDtoBuilder = UserDto.getUserDtoBuilder();

    @Override
    public UserDto getFollowersById(Long id, String order) {
        if (!userRepository.existsById(id)) {
            // TODO: throw not found
        }

        User user = userRepository.getById(id);
        List<User> orderedFollowers = getUserListOrderedByName(order, user.getFollowers());
        orderedFollowers.forEach(u -> {
            u.setFollowed(null);
            u.setFollowers(null);
        });

        return userDtoBuilder.withUserId(user.getUserId())
                .withUserName(user.getUserName())
                .withFollowers(orderedFollowers)
                .build();
    }

    private static List<User> getUserListOrderedByName(String order, List<User> userList) {
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
}
