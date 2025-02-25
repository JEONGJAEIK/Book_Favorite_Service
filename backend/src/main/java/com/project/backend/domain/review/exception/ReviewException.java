package com.project.backend.domain.review.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;


/**
 * 리뷰와 리뷰 코멘트에서 발생할 수 있는 예외를 처리할 커스컴 클래스
 */
@Getter
public class ReviewException extends RuntimeException{
    private final HttpStatus status;
    private final String errorCode;
    private final String message;

    public ReviewException(HttpStatus status,String errorCode,String message){
        super(message);
        this.status=status;
        this.errorCode = errorCode;
        this.message=message;
    }
}
