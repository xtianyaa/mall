package com.yuan.mall.exception;

import com.yuan.mall.common.Result;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldError() == null
                ? "请求参数错误"
                : exception.getBindingResult().getFieldError().getDefaultMessage();
        return Result.<String>builder()
                .code(400)
                .message(message)
                .data(null)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<String> handleConstraintViolationException(ConstraintViolationException exception) {
        return Result.<String>builder()
                .code(400)
                .message(exception.getMessage())
                .data(null)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<String> handleIllegalArgumentException(IllegalArgumentException exception) {
        return Result.<String>builder()
                .code(400)
                .message(exception.getMessage())
                .data(null)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
