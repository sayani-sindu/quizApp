package com.example.question_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.question_service.model.Question;
import com.example.question_service.model.QuestionWrapper;
import com.example.question_service.model.Response;
import com.example.question_service.repository.QuestionRepo;



@Service
public class QuestionService {

    @Autowired
    QuestionRepo questionRepo;

  

    public ResponseEntity<List<Question>> getAllQuestions() {

        try {
            return new ResponseEntity<>(
                    questionRepo.findAll(),
                    HttpStatus.OK
            );

        } catch (Exception e) {
            e.printStackTrace();

            return new ResponseEntity<>(
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<List<Question>> getQuestionsByCategory(String topic) {

        try {
            return new ResponseEntity<>(
                    questionRepo.findByCategory(topic),
                    HttpStatus.OK
            );

        } catch (Exception e) {
            e.printStackTrace();

            return new ResponseEntity<>(
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<String> addQuestion(Question question) {

        try {
            questionRepo.save(question);

            return new ResponseEntity<>(
                    "Question added successfully",
                    HttpStatus.CREATED
            );

        } catch (Exception e) {
            e.printStackTrace();

            return new ResponseEntity<>(
                    "Failed to add question",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<String> addQuestions(List<Question> questions) {

        try {
            questionRepo.saveAll(questions);

            return new ResponseEntity<>(
                    "Questions added successfully",
                    HttpStatus.CREATED
            );

        } catch (Exception e) {
            e.printStackTrace();

            return new ResponseEntity<>(
                    "Failed to add questions",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<String> updateQuestion(Integer id, Question question) {

        try {
            Question existingQuestion =
                    questionRepo.findById(id).orElse(null);

            if (existingQuestion == null) {
                return new ResponseEntity<>(
                        "Question not found",
                        HttpStatus.NOT_FOUND
                );
            }

            existingQuestion.setQuestionTitle(question.getQuestionTitle());
            existingQuestion.setOption1(question.getOption1());
            existingQuestion.setOption2(question.getOption2());
            existingQuestion.setOption3(question.getOption3());
            existingQuestion.setOption4(question.getOption4());
            existingQuestion.setRightAnswer(question.getRightAnswer());
            existingQuestion.setDifficultyLevel(question.getDifficultyLevel());
            existingQuestion.setCategory(question.getCategory());

            questionRepo.save(existingQuestion);

            return new ResponseEntity<>(
                    "Question updated successfully",
                    HttpStatus.OK
            );

        } catch (Exception e) {
            e.printStackTrace();

            return new ResponseEntity<>(
                    "Failed to update question",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<String> deleteQuestion(Integer id) {

        try {

            if (!questionRepo.existsById(id)) {
                return new ResponseEntity<>(
                        "Question not found",
                        HttpStatus.NOT_FOUND
                );
            }

            questionRepo.deleteById(id);

            return new ResponseEntity<>(
                    "Question deleted successfully",
                    HttpStatus.OK
            );

        } catch (Exception e) {
            e.printStackTrace();

            return new ResponseEntity<>(
                    "Failed to delete question",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<List<Integer>> generateQuiz(String category, Integer numQ) {
        try {
            List<Question> questions = questionRepo.findRandomQuestionsByCategory(category, numQ);
            List<Integer> questionIds = new ArrayList<>();

            for (Question question : questions) {
                questionIds.add(question.getId());
            }

            return new ResponseEntity<>(questionIds, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

     public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(List<Integer> questionIds) {

        try {

            List<QuestionWrapper> questionWrappers = new ArrayList<>();
            List<Question> questions = new ArrayList<>();

            for (Integer id : questionIds) {

                java.util.Optional<Question> question = questionRepo.findById(id);

                if (question.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }

                questions.add(question.get());
            }

            for(Question question: questions){

                QuestionWrapper questionWrapper = new QuestionWrapper(
                    question.getId(),
                    question.getQuestionTitle(),
                    question.getOption1(),
                    question.getOption2(),
                    question.getOption3(),
                    question.getOption4()
                );

                questionWrappers.add(questionWrapper);

            }

            return new ResponseEntity<>(questionWrappers, HttpStatus.OK);

        } catch (Exception e) {

            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    public ResponseEntity<Integer> getScore(List<Response> responses) {
        try {
         
            int score = 0;

            for (Response response : responses) {
                if (response.getResponse() == null) {
                    continue;
                }

                java.util.Optional<Question> question = questionRepo.findById(response.getId());

                if (question.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }

                if(response.getResponse().equals(question.get().getRightAnswer())){
                    score += 1;
                }
            }

            return new ResponseEntity<>(score, HttpStatus.OK);

        } catch (Exception e) {
            
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}