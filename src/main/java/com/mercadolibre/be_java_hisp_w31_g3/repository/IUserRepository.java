package com.mercadolibre.be_java_hisp_w31_g3.repository;

import com.mercadolibre.be_java_hisp_w31_g3.model.User;

import java.util.List;

public interface IUserRepository {
    void addFollower(Long userId, Long userToFollow);
}
