package com.example.question_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.question_service.model.Question;



@Repository
public interface QuestionRepo extends JpaRepository<Question, Integer>{

    List<Question> findByCategory(String category);

    @Query(value = "SELECT * FROM question q WHERE q.category=:category ORDER BY RANDOM() LIMIT :numQ",nativeQuery = true )
    List<Question> findRandomQuestionsByCategory(String category, Integer numQ);
    
}
