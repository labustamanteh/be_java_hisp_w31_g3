package com.mercadolibre.be_java_hisp_w31_g3.repository;

import com.mercadolibre.be_java_hisp_w31_g3.model.User;

public interface IUserRepository {
    User getUserById(Long userId);
    Boolean isExistsById(Long userId);
}
