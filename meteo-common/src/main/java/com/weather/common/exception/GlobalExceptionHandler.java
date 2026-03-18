package com.weather.common.exception;

import com.weather.common.response.DataResult;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DataResult handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<ObjectError> errors = exception.getBindingResult().getAllErrors();
        StringBuilder errorMessage = new StringBuilder();
        for (ObjectError error : errors) {
            errorMessage.append(error.getDefaultMessage()).append(';');
        }
        return DataResult.fail(errorMessage.toString());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public DataResult handleConstraintViolationException(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        StringBuilder errorMessage = new StringBuilder();
        for (ConstraintViolation<?> violation : violations) {
            errorMessage.append(violation.getMessage()).append(';');
        }
        return DataResult.fail(errorMessage.toString());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public DataResult handleBadRequest(Exception exception) {
        return DataResult.fail("请求参数错误或类型不匹配");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public DataResult handleIllegalArgument(IllegalArgumentException exception) {
        return DataResult.fail(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public DataResult handleInternalServerError(Exception exception) {
        return DataResult.fail("内部服务错误，请稍后重试");
    }
}
