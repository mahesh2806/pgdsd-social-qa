package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.*;

@RestController
@RequestMapping("/question/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    private static String QUESTION_CREATED = "QUESTION CREATED";

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
}
