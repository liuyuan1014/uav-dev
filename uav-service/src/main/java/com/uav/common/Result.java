package com.uav.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果类
 * 
 * @param <T> 响应数据类型
 */
@Data
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int code;
    private String message;
    private T data;
    
    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 500;
    
    public Result() {}
    
    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    /**
     * 成功响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS_CODE, "success", data);
    }
    
    /**
     * 成功响应（带消息）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(SUCCESS_CODE, message, data);
    }
    
    /**
     * 错误响应
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(ERROR_CODE, message, null);
    }
    
    /**
     * 错误响应（带错误码）
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }
}