package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.*;

@RestController
@RequestMapping("/question/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    private static String QUESTION_CREATED = "QUESTION CREATED";
    private static String QUESTION_EDITED = "QUESTION EDITED";
    private static String QUESTION_DELETED = "QUESTION DELETED";

    @RequestMapping(method = RequestMethod.POST, path = "/create",  consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest createQuestionRequest, @RequestHeader("authorization") final String authorizationToken) throws AuthorizationFailedException {

        final QuestionEntity question = new QuestionEntity();
        question.setUuid(UUID.randomUUID().toString());
        question.setContent(createQuestionRequest.getContent());
        question.setDate(ZonedDateTime.now());

        final QuestionEntity createdQuestion = questionBusinessService.createQuestion(authorizationToken, question);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid()).status(QUESTION_CREATED);

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorizationToken) throws AuthorizationFailedException {
        List<QuestionDetailsResponse> questionDetailsResponseList = new ArrayList<QuestionDetailsResponse>();
        List<QuestionEntity> questionEntityList = questionBusinessService.getAllQuestions(authorizationToken);
        if (questionEntityList != null && !questionEntityList.isEmpty()) {
            for (QuestionEntity qEntity : questionEntityList) {
                questionDetailsResponseList.add(new QuestionDetailsResponse().id(qEntity.getUuid()).content(qEntity.getContent()));
            }
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponseList, HttpStatus.OK);
    }

@RequestMapping(method = RequestMethod.PUT, path = "/edit/{questionId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(@RequestHeader("authorization") final String authorizationToken,
                                                                    @PathVariable("questionId") final String questionIdUuid, QuestionEditRequest questionEditRequest)
            throws AuthorizationFailedException, InvalidQuestionException {
        QuestionEditResponse questionEditResponse = new QuestionEditResponse();
        QuestionEntity questionEntity = questionBusinessService.performEditQuestionContent(authorizationToken, questionIdUuid, questionEditRequest.getContent());
        questionEditResponse.setId(questionEntity.getUuid());
        questionEditResponse.setStatus(QUESTION_EDITED);
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/delete/{questionId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@RequestHeader("authorization") final String authorizationToken,
                                                                 @PathVariable("questionId") final String questionIdUuid)
            throws AuthorizationFailedException, InvalidQuestionException {
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse();
        questionBusinessService.performDeleteQuestion(authorizationToken, questionIdUuid);
        questionDeleteResponse.id(questionIdUuid).status(QUESTION_DELETED);
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/all/{userId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@RequestHeader("authorization") final String authorizationToken,
                                                                               @PathVariable("userId") final String userUuId) throws AuthorizationFailedException,
            UserNotFoundException {
        List<QuestionDetailsResponse> questionDetailsResponseList = new ArrayList<QuestionDetailsResponse>();
        List<QuestionEntity> questionEntityList = questionBusinessService.performGetAllQuestionsByUser(authorizationToken, userUuId);
        if (questionEntityList != null && !questionEntityList.isEmpty()) {
            for (QuestionEntity questionEntity : questionEntityList) {
                questionDetailsResponseList.add(new QuestionDetailsResponse().id(questionEntity.getUuid())
                        .content(questionEntity.getContent()));
            }
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponseList, HttpStatus.OK);

    }
}
