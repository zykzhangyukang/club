package com.coderman.club.config;

import com.coderman.club.constant.common.ResultConstant;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.vo.common.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatus status,
            @NonNull WebRequest request) {

        List<String> details = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));

        details.add(builder.toString());

        ResultVO<Object> err = ResultUtil.getResult(Object.class, ResultConstant.RESULT_CODE_400, "Invalid JSON", details);
        return ResponseEntity.status(status).body(err);

    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  @NonNull HttpHeaders headers, @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {
        ResultVO<Object> err = ResultUtil.getResult(Object.class, ResultConstant.RESULT_CODE_400, "Malformed JSON request", ex.getMessage());
        return ResponseEntity.status(status).body(err);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers, @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {

        List<String> details;
        details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getObjectName() + " : " + error.getDefaultMessage())
                .sorted(String::compareTo)
                .collect(Collectors.toList());

        ResultVO<Object> err = ResultUtil.getResult(Object.class, ResultConstant.RESULT_CODE_400, "Validation Errors", details);
        return ResponseEntity.status(status).body(err);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            @NonNull MissingServletRequestParameterException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatus status, @NonNull WebRequest request) {
        ResultVO<Object> err = ResultUtil.getResult(Object.class, ResultConstant.RESULT_CODE_400, "Missing Parameters", ex.getParameterName() + " parameter is missing");
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler({
            Exception.class,
    })
    public final ResponseEntity<Object> handleSysException(Exception ex, WebRequest request) throws Exception {
        ResultVO<Object> err = ResultUtil.getResult(Object.class, ResultConstant.RESULT_CODE_500, "Internal Server Error", ExceptionUtils.getRootCauseMessage(ex));
        log.error("系统异常：{}",ExceptionUtils.getRootCauseMessage(ex), ex);
        return ResponseEntity.status(ResultConstant.RESULT_CODE_500).body(err);
    }


}
