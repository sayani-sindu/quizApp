package com.example.question_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.question_service.model.Question;
import com.example.question_service.model.QuestionWrapper;
import com.example.question_service.model.Response;
import com.example.question_service.service.QuestionService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController 
@RequestMapping("question")
public class QuestionController {

    @Autowired 
    QuestionService questionService;
    
    @GetMapping("allQuestions")
    public ResponseEntity<List<Question>> getAllQuestions(){

        return questionService.getAllQuestions();

    }

    @GetMapping("category/{topic}")
    public ResponseEntity<List<Question>> getAllQuestionsByCategory( @PathVariable String topic){

        return questionService.getQuestionsByCategory(topic);

    }

    @PostMapping("add")
    public ResponseEntity<String> addQuestion(@RequestBody Question question){
        return questionService.addQuestion(question);
        

    }

    @PostMapping("addAll")
    public ResponseEntity<String> addQuestions(
            @RequestBody List<Question> questions) {

        return questionService.addQuestions(questions);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<String> updateQuestion(
            @PathVariable Integer id,
            @RequestBody Question question) {

        return questionService.updateQuestion(id, question);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteQuestion(
            @PathVariable Integer id) {

        return questionService.deleteQuestion(id);
    }

    @PostMapping("generate")
    public ResponseEntity<List<Integer>> generateQuiz(@RequestParam String category, @RequestParam Integer numQ) {

        return questionService.generateQuiz(category, numQ);
    }

    // 2. Get questions for quiz
    @PostMapping("getQuestions")
    public ResponseEntity<List<QuestionWrapper>> getQuestions(@RequestBody List<Integer> questionIds) {

        return questionService.getQuestionsFromId(questionIds);
    }

    // 3. Get score
    @PostMapping("getScore")
    public ResponseEntity<Integer> getScore(@RequestBody List<Response> responses) {

        return questionService.getScore(responses);
    }
}
