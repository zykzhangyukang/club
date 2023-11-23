package com.coderman.club.limiter.resolver;

import org.aspectj.lang.ProceedingJoinPoint;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhangyukang
 * @since 1.0.0
 */
public interface KeyResolver {
    /**
     * 具体限流规则
     *
     * @param request request
     * @param pjp
     * @return request
     */
    String resolve(HttpServletRequest request, ProceedingJoinPoint pjp);
}
