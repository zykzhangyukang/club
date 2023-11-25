package com.coderman.club.config;

import com.coderman.club.constant.common.ResultConstant;
import com.coderman.club.exception.RateLimitException;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.vo.common.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author Administrator
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler({
            Exception.class,
    })
    public final ResponseEntity<Object> handleSysException(Exception ex, WebRequest request) throws Exception {
        ResultVO<Object> err = ResultUtil.getResult(Object.class, ResultConstant.RESULT_CODE_500, "Internal Server Error", ExceptionUtils.getRootCauseMessage(ex));
        log.error("系统异常：{}", ExceptionUtils.getRootCauseMessage(ex), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }


    @ExceptionHandler({
            RateLimitException.class,
    })
    public final ResponseEntity<Object> handleRateLimitException(Exception ex, WebRequest request) throws Exception {
        ResultVO<Object> err = ResultUtil.getResult(Object.class, ResultConstant.RESULT_CODE_429, "Too Many Requests", ExceptionUtils.getRootCauseMessage(ex));
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(err);
    }


}
