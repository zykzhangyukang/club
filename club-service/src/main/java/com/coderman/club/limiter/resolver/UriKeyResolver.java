package com.coderman.club.limiter.resolver;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhangyukang
 * @since 1.0.0
 * 根据请求的uri进行限流
 */
@Component
public class UriKeyResolver implements KeyResolver {

    @Override
    public String resolve(HttpServletRequest request, ProceedingJoinPoint pjp) {
        return request.getRequestURI();
    }
}
