package com.coderman.club.websocket;

import lombok.Data;

import java.security.Principal;

/**
 * @author ：zhangyukang
 * @date ：2023/10/19 14:45
 */
@Data
public class MyPrincipal implements Principal {

    private final Long userId;

    public MyPrincipal(Long userId) {
        this.userId = userId;
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }
}