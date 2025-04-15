package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.mercadolibre.be_java_hisp_w31_g3.repository.IProductRepository;
import com.mercadolibre.be_java_hisp_w31_g3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ProductService implements IProductService{
    //private final IProductRepository productRepository;

    UserRepository userRepository;

    public ProductService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public void addFollower(Long userId, Long userToFollow) {
        if(userId== null && userToFollow == null){
            throw new IllegalArgumentException("Los identificadores de usuario no deben ser nulos.");
        }
        if (Objects.equals(userId, userToFollow)) {
            throw new IllegalArgumentException("Un usuario no puede seguirse a sí mismo.");
        }

        userRepository.addFollower(userId,userToFollow);
    }
}
