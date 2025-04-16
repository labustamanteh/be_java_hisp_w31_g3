package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
import com.mercadolibre.be_java_hisp_w31_g3.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final ObjectMapper mapper;

    @Override
    public List<UserDto> getUsers() {
        List<User> userList = userRepository.getUsers();
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
        if (!userRepository.existsById(userId) || !userRepository.existsById(userToFollow)) {
            throw new NotFoundException("Alguno de los usuarios no existe.");
        }
        userRepository.addFollower(userId, userToFollow);
    }
}
