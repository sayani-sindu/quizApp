package com.example.quiz_service.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.quiz_service.model.QuestionWrapper;
import com.example.quiz_service.model.Response;

@FeignClient("QUESTION-SERVICE")
public interface QuizInterface {

    @PostMapping("/question/generate")
    ResponseEntity<List<Integer>> generateQuiz(
            @RequestParam("category") String category,
            @RequestParam("numQ") Integer numQ);

    @PostMapping("/question/getQuestions")
    ResponseEntity<List<QuestionWrapper>> getQuestions(
            @RequestBody List<Integer> questionIds);

    @PostMapping("/question/getScore")
    ResponseEntity<Integer> getScore(
            @RequestBody List<Response> responses);
}