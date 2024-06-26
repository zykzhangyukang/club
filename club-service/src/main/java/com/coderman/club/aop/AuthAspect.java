package com.coderman.club.aop;

import com.alibaba.fastjson.JSON;
import com.coderman.club.constant.common.ResultConstant;
import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.properties.AuthProperties;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.utils.AuthUtil;
import com.coderman.club.utils.HttpContextUtil;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.user.AuthUserVO;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @author ：zhangyukang
 * @date ：2023/11/23 14:09
 */
@Aspect
@Component
@Order(value = 100)
@Lazy(value = false)
@Slf4j
public class AuthAspect {

    /**
     * 保存token与用户的关系 (缓存5分钟防止频繁请求redis)
     */
    public static final Cache<String, AuthUserVO> USER_TOKEN_CACHE_MAP = CacheBuilder.newBuilder()
            //设置缓存初始大小
            .initialCapacity(10)
            //最大值
            .maximumSize(500)
            //多线程并发数
            .concurrencyLevel(5)
            //过期时间，写入后15s过期
            .expireAfterWrite(15, TimeUnit.SECONDS)
            // 过期监听
            .removalListener((RemovalListener<String, AuthUserVO>) removalNotification -> {
                log.debug("过期会话缓存清除 token:{} is removed cause:{}", removalNotification.getKey(), removalNotification.getCause());
            })
            .recordStats()
            .build();

    @Resource
    private RedisService redisService;

    @Resource
    private AuthProperties authProperties;

    @Pointcut("(execution(* com.coderman..controller..*(..)))")
    public void pointcut() {
    }


    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
        HttpServletResponse response = HttpContextUtil.getHttpServletResponse();
        String path = request.getServletPath();

        if (authProperties.getWhiteList().contains(path)) {
            return point.proceed();
        }

        String tokenVal = AuthUtil.getToken();

        // 用户信息
        AuthUserVO authUserVO = null;
        if (StringUtils.isNotBlank(tokenVal)) {

            try {
                authUserVO = USER_TOKEN_CACHE_MAP.get(tokenVal, () -> {

                    log.warn("尝试从redis中获取用户信息结果.token:{}", tokenVal);
                    return this.redisService.getObject(RedisKeyConstant.USER_ACCESS_TOKEN_PREFIX + tokenVal, AuthUserVO.class, RedisDbConstant.REDIS_DB_DEFAULT);
                });
            } catch (Exception e) {
                log.error("尝试从redis中获取用户信息结果失败:{}", e.getMessage());
            }
        }

        if (authUserVO == null) {

            ResultVO<String> resultVO = ResultUtil.getResult(String.class, ResultConstant.RESULT_CODE_401, "用户未登录", null);

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader("Content-type", "application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(JSON.toJSONString(resultVO));
            return null;
        }

        // 将h会话保存到请求中
        AuthUtil.setCurrent(authUserVO);

        return point.proceed();
    }


}
