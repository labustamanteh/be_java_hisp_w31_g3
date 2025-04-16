package com.mercadolibre.be_java_hisp_w31_g3.service;

import org.springframework.stereotype.Service;

import com.mercadolibre.be_java_hisp_w31_g3.dto.FollowersCountDto;
import com.mercadolibre.be_java_hisp_w31_g3.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.IUserRepository;
import com.mercadolibre.be_java_hisp_w31_g3.repository.UserRepository;

@Service
public class UserService implements IUserService {
    private IUserRepository repository;

    public UserService(UserRepository userRepository) {
        this.repository = userRepository;
    }

    @Override
    public FollowersCountDto getfollowersCount(Long userId) {
        if (repository.isExistsById(userId) == false) {
            throw new NotFoundException("No se encontró el usuario con el id ingresado");
        }
        User userFind = repository.getUserById(userId);
        Long followersCount = Long.valueOf(userFind.getFollowers().size());
        return new FollowersCountDto(userId, userFind.getUserName(), followersCount);
    }
}
