package com.coderman.club.limiter.resolver;

import com.coderman.club.utils.AuthUtil;
import com.coderman.club.vo.user.AuthUserVO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * ip和用户作为key
 *
 * @author ：zhangyukang
 * @date ：2024/06/04 16:21
 */
@Component
public class UserKeyResolver implements KeyResolver {

    @Resource
    private IpKeyResolver ipKeyResolver;

    @Resource
    private UriKeyResolver uriKeyResolver;

    @Override
    public String resolve(HttpServletRequest request, ProceedingJoinPoint pjp) {
        AuthUserVO current = AuthUtil.getCurrent();

        String url = uriKeyResolver.resolve(request, pjp);
        String ip = ipKeyResolver.resolve(request, pjp);

        String key = ip + "@" + url;

        if (current != null) {
            key = key + "@" + current.getUserId();
        }

        return key;
    }
}
