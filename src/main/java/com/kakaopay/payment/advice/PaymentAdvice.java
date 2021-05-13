package com.kakaopay.payment.advice;

import java.util.StringJoiner;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kakaopay.payment.advice.exceptions.EncryptException;
import com.kakaopay.payment.advice.exceptions.PaymentCancelException;
import com.kakaopay.payment.advice.exceptions.PaymentResourceNotFoundException;
import com.kakaopay.payment.entity.ApiExceptionMessage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestControllerAdvice
public class PaymentAdvice {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiExceptionMessage defaultException(HttpServletRequest request, Exception e) {
        return ApiExceptionMessage.builder()
        		.code(-1000)
        		.message("Request Failed")
        		.build();
    }
    
    @ExceptionHandler(EncryptException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiExceptionMessage encryptException(HttpServletRequest request, EncryptException e) {
        return ApiExceptionMessage.builder()
        		.code(-1001)
        		.message("Error occured while encrypting data")
        		.build();
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiExceptionMessage invalidArgumentException(HttpServletRequest request, MethodArgumentNotValidException e) {
    	BindingResult bindingResult = e.getBindingResult();
    	StringJoiner joiner = new StringJoiner(",");
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
        	joiner.add(fieldError.getField());
        }
    	
        return ApiExceptionMessage.builder()
        		.code(-1002)
        		.message("Invalid input")
        		.detailMessage(joiner.toString())
        		.build();
    }
    
    @ExceptionHandler(PaymentResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ApiExceptionMessage resourceNotFoundException(HttpServletRequest request, PaymentResourceNotFoundException e) {
        return ApiExceptionMessage.builder()
        		.code(-2000)
        		.message("Cannot found payment")
        		.detailMessage(e.getMessage())
        		.build();
    }
    
    @ExceptionHandler(PaymentCancelException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiExceptionMessage cancelException(HttpServletRequest request, PaymentCancelException e) {
        return ApiExceptionMessage.builder()
        		.code(-3000)
        		.message("Error occured while canceling payment")
        		.detailMessage(e.getMessage())
        		.build();
    }
	
}
