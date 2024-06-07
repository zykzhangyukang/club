package com.coderman.club.utils;

import com.coderman.club.aop.AuthAspect;
import com.coderman.club.constant.common.CommonConstant;
import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.vo.user.AuthUserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ：zhangyukang
 * @date ：2023/11/23 16:09
 */
@Slf4j
public class AuthUtil {

    /**
     * 获取当前用户会话信息
     *
     * @return
     */
    public static AuthUserVO getCurrent() {

        // 先判断请求里面是否存在
        HttpServletRequest httpServletRequest = HttpContextUtil.getHttpServletRequest();
        Object obj = httpServletRequest.getAttribute(CommonConstant.USER_SESSION_KEY);

        AuthUserVO authUserVO = null;

        if (obj instanceof AuthUserVO) {

            return (AuthUserVO) obj;

        } else {

            String token = getToken();

            // 如果用户的token存在，则尝试从redis中获取
            if (StringUtils.isNotBlank(token)) {

                try {

                    authUserVO = AuthAspect.USER_TOKEN_CACHE_MAP.get(token, () -> {
                        RedisService redisService = SpringContextUtil.getBean(RedisService.class);
                        log.warn("尝试从redis中获取用户信息结果.token:{}", token);
                        return redisService.getObject(RedisKeyConstant.USER_ACCESS_TOKEN_PREFIX + token, AuthUserVO.class, RedisDbConstant.REDIS_DB_DEFAULT);
                    });
                } catch (Exception e) {
                    log.error("尝试从redis中获取用户信息结果失败:{}", e.getMessage());
                }
            }
        }

        return authUserVO;
    }


    public static String getToken(){
        HttpServletRequest httpServletRequest = HttpContextUtil.getHttpServletRequest();
        return StringUtils.substringAfter(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION), "Bearer ");
    }


    /**
     * 设置当前会话
     *
     * @param authUserVO 用户信息
     */
    public static void setCurrent(AuthUserVO authUserVO) {
        HttpServletRequest httpServletRequest = HttpContextUtil.getHttpServletRequest();
        httpServletRequest.setAttribute(CommonConstant.USER_SESSION_KEY, authUserVO);
    }
}
