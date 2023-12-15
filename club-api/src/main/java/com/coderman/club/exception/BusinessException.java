package com.coderman.club.exception;

/**
 * @author ：zhangyukang
 * @date ：2023/12/15 15:09
 */
public class BusinessException extends RuntimeException{

    public BusinessException(String message) {
        super(message);
    }
}
