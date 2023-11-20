package com.coderman.club.constant.common;

/**
 * @author coderman
 * @Title: 状态码code
 * @Description: TOD
 * @date 2022/5/19:14
 */
public interface ResultConstant {

    /**
     * 200 成功
     */
    Integer RESULT_CODE_200 = 200;


    /**
     * 400 请求错误
     */
    Integer RESULT_CODE_400 = 400;

    /**
     * 401 未登入
     */
    Integer RESULT_CODE_401 = 401;


    /**
     * 402 处理失败
     */
    Integer RESULT_CODE_402 =402;

    /**
     * 403 无权限
     */
    Integer RESULT_CODE_403 = 403;


    /**
     * 404 资源不存在
     */
    Integer RESULT_CODE_404 = 404;


    /**
     * 405 警告
     */
    Integer RESULT_CODE_405 = 405;


    /**
     * 429 限流
     */
    Integer RESULT_CODE_429 = 429;


    /**
     * 服务器异常
     */
    Integer RESULT_CODE_500 = 500;

}
