package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.UserAuth;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class UserAuthRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public UserAuth createUserAuth(final UserAuth userAuth) {
        entityManager.persist(userAuth);
        return userAuth;
    }

}
