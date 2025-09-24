package com.wenji.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    // 处理认证相关异常
    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "认证失败");
        errorResponse.put("message", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    // 处理密码错误异常
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "密码错误");
        errorResponse.put("message", "账号或密码错误，请重新输入");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    // 处理验证参数异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "参数验证失败");
        errorResponse.put("details", errors);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    // 处理请求体解析异常
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        System.err.println("===== 请求体解析异常 =====");
        System.err.println("请求URL: " + request.getDescription(false));
        ex.printStackTrace();
        System.err.println("========================");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", 400);
        errorResponse.put("message", "访问令牌不能为空");
        errorResponse.put("error", "请求体解析失败");
        errorResponse.put("details", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    // 处理业务逻辑异常
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "业务逻辑错误");
        errorResponse.put("message", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    // 处理其他所有异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<?> handleAllExceptions(Exception ex, WebRequest request) {
        // 1. 打印异常堆栈到控制台（关键：手动强制输出异常）
        System.err.println("===== 接口异常详情 =====");
        System.err.println("请求URL: " + request.getDescription(false));
        ex.printStackTrace(); // 这行代码会在控制台打印完整异常堆栈
        System.err.println("======================");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "系统错误");
        errorResponse.put("message", "服务器内部错误，请稍后再试");
        // 添加详细异常信息用于调试（开发环境）
        errorResponse.put("exceptionType", ex.getClass().getName()); // 异常类型（如NullPointerException）
        errorResponse.put("exceptionMsg", ex.getMessage()); // 异常原因（如"xxx为null"）
        errorResponse.put("requestUrl", request.getDescription(false)); // 请求路径
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}