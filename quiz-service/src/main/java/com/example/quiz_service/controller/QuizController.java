package com.example.quiz_service.controller;

import org.springframework.web.bind.annotation.RestController;



import com.example.quiz_service.model.QuestionWrapper;
import com.example.quiz_service.model.QuizDto;
import com.example.quiz_service.model.Response;
import com.example.quiz_service.service.QuizService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("quiz")
public class QuizController {
    @Autowired 
    QuizService quizService;


    @PostMapping("create")
    public ResponseEntity<String> createQuiz(@RequestBody QuizDto quiz){

        return quizService.createQuiz(quiz.getCategoryName(), quiz.getNumOfQuestions(), quiz.getTitle());

    }

    @GetMapping("get/{id}")
    public ResponseEntity<List<QuestionWrapper>> getMethodName(@PathVariable Integer id) {
        return quizService.getQuestions(id);
    }

    @PostMapping("submit/{id}")
    public ResponseEntity<Integer> submitQuiz( @PathVariable Integer id, @RequestBody List<Response> responses) {

        return quizService.calculateResult(id, responses);
        
    }
    
    
    
}
