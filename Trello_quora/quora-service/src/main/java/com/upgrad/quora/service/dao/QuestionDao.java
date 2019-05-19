package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public QuestionEntity getUserForQuestionId(String uuid) {
        try {
            return entityManager.createNamedQuery("getOwnerForQuestionId", QuestionEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<QuestionEntity> getAllQuestions() {
        try {
            return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
/**
     * this methoed updates the question for passed in questionEntity object in DB
     *
     * @param questionEntity
     */
    public void updateQuestion(QuestionEntity questionEntity) {
        entityManager.merge(questionEntity);
    }

    /**
     * This method deletes the question for question entity
     *
     * @param questionEntity
     */
    public void deleteQuestion(QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
    }

    /**
     * This method gets the questions for a user id
     *
     * @param userId
     * @return
     */
    public List<QuestionEntity> getQuestionsForUserId(Integer userId) {
        try {
            return entityManager.createNamedQuery("questionsByUserId", QuestionEntity.class).setParameter("userId", userId).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }


    /**
     * retrieves question for question id
     * @param questionId
     * @return
     */
    public QuestionEntity getQuestionForQuestionId(String questionId) {
        try {
            return entityManager.createNamedQuery("getQuestionForQuestionId", QuestionEntity.class).setParameter("uuid", questionId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
