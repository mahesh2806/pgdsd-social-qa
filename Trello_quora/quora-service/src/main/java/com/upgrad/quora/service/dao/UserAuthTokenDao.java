package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class UserAuthTokenDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserAuthTokenEntity createUserAuth(final UserAuthTokenEntity userAuthToken) {
        entityManager.persist(userAuthToken);
        return userAuthToken;
    }

}
