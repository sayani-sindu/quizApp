package com.example.quiz_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.quiz_service.feign.QuizInterface;
import com.example.quiz_service.model.QuestionWrapper;
import com.example.quiz_service.model.Quiz;
import com.example.quiz_service.model.Response;

import com.example.quiz_service.repository.QuizRepo;

@Service 
public class QuizService {

    @Autowired
    QuizInterface quizInterface;

    @Autowired
    QuizRepo quizRepo;

    public ResponseEntity<String> createQuiz(String category, Integer numQ, String title) {

        if (category == null || category.trim().isEmpty()) {
            return new ResponseEntity<>("Category must not be empty", HttpStatus.BAD_REQUEST);
        }

        if (numQ == null || numQ <= 0) {
            return new ResponseEntity<>("numQ must be a positive number", HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<List<Integer>> response = quizInterface.generateQuiz(category.trim(), numQ);

        if (response.getBody() == null || response.getBody().isEmpty()) {
            return new ResponseEntity<>(
                    "No questions found for category: " + category,
                    HttpStatus.NOT_FOUND
            );
        }

        List<Integer> questions = response.getBody();
        
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionIds(questions);

        quizRepo.save(quiz);

        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuestions(Integer id) {
        Optional<Quiz> quiz = quizRepo.findById(id);

        if (quiz.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Integer> questionIds = quiz.get().getQuestionIds();
        List<QuestionWrapper> questions = quizInterface.getQuestions(questionIds).getBody();

        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {

        if (!quizRepo.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Integer score = quizInterface.getScore(responses).getBody();

        return new ResponseEntity<>(score, HttpStatus.OK);
    }
    
}
