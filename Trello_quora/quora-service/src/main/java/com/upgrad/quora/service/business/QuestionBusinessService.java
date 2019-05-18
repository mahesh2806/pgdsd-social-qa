package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserBusinessService userBusinessService;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final String authorizationToken, QuestionEntity questionEntity) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userBusinessService.getUserAuthToken(authorizationToken);

        if (userAuthTokenEntity != null) {
            if (userBusinessService.isUserSignedIn(userAuthTokenEntity)) {
                questionEntity.setUser(userAuthTokenEntity.getUser());
                questionEntity = questionDao.createQuestion(questionEntity);
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
            }
        } else {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        return questionEntity;
    }

}
