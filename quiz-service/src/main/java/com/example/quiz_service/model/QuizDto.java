package com.example.quiz_service.model;

import lombok.Data;

/**
 * QuizDto
 */

@Data 
public class QuizDto {

    private String categoryName;
    private String title;
    private Integer numOfQuestions;


}
