package com.coderman.club.limiter.resolver;

/**
 * @author ：zhangyukang
 * @date ：2023/10/17 14:57
 */
import com.coderman.club.utils.HttpContextUtil;
import com.coderman.club.utils.IpUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * IP 维度的限流控制
 * @author ：zhangyukang
 * @date ：2023/09/27 12:07
 */
@Component
public class IpKeyResolver implements KeyResolver
{
    @Override
    public String resolve(HttpServletRequest request, ProceedingJoinPoint pjp) {
        return IpUtil.getIp(HttpContextUtil.getHttpServletRequest());
    }
}

