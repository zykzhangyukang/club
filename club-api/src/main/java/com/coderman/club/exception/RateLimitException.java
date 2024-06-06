package com.coderman.club.exception;

/**
 * 限流异常
 * @author zhangyukang
 */
public class RateLimitException extends RuntimeException {

    public RateLimitException(String message) {
        super(message);
    }

}