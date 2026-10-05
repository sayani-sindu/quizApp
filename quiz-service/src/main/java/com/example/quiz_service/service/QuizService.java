package com.example.quiz_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

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

    @Autowired
    CircuitBreakerFactory circuitBreakerFactory;

    @Autowired
    MeterRegistry meterRegistry;

    private CircuitBreaker breaker(String id) {
        return circuitBreakerFactory.create(id);
    }

    public ResponseEntity<String> createQuiz(String category, Integer numQ, String title) {

        if (category == null || category.trim().isEmpty()) {
            return new ResponseEntity<>("Category must not be empty", HttpStatus.BAD_REQUEST);
        }

        if (numQ == null || numQ <= 0) {
            return new ResponseEntity<>("numQ must be a positive number", HttpStatus.BAD_REQUEST);
        }

        Timer.Sample sample = Timer.start(meterRegistry);

        List<Integer> questions = breaker("generateQuiz")
                .run(() -> quizInterface.generateQuiz(category.trim(), numQ).getBody(), t -> null);

        sample.stop(meterRegistry.timer("quiz.generate.duration"));

        if (questions == null) {
            return new ResponseEntity<>("Question service unavailable, please try again later", HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (questions.isEmpty()) {
            return new ResponseEntity<>(
                    "No questions found for category: " + category,
                    HttpStatus.NOT_FOUND
            );
        }
        
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionIds(questions);

        quizRepo.save(quiz);

        meterRegistry.counter("quiz.creations", "category", category).increment();

        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuestions(Integer id) {
        Optional<Quiz> quiz = quizRepo.findById(id);

        if (quiz.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<QuestionWrapper> questions = breaker("getQuestions")
                .run(() -> quizInterface.getQuestions(quiz.get().getQuestionIds()).getBody(), t -> null);

        if (questions == null) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {

        if (!quizRepo.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Integer score = breaker("getScore")
                .run(() -> quizInterface.getScore(responses).getBody(), t -> null);

        if (score == null) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }

        meterRegistry.counter("quiz.submissions").increment();

        return new ResponseEntity<>(score, HttpStatus.OK);
    }
    
}