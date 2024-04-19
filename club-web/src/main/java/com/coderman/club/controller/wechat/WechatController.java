package com.coderman.club.controller.wechat;

import com.alibaba.fastjson.JSON;
import com.coderman.club.dto.wechat.WxBaseMessageDTO;
import com.coderman.club.dto.wechat.WxEventMessageDTO;
import com.coderman.club.exception.BusinessException;
import com.coderman.club.utils.SignUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.stream.Collectors;

/**
 * @author ：zhangyukang
 * @date ：2024/04/19 15:21
 */
@Api(value = "微信公众号模块", tags = {"微信公众号模块"})
@RestController
@RequestMapping(value = "/api/wechat")
@Slf4j
public class WechatController {


    @ApiOperation(value = "校验微信公众令牌")
    @GetMapping(value = "/message")
    public String message(HttpServletRequest request) {

        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");

        log.info("收到来自微信的认证消息, signature:{},timestamp:{}, nonce:{},echostr:{}  ", signature, timestamp, nonce, echostr);
        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new BusinessException("请求参数非法！");
        }
        if (SignUtil.checkSignature(signature, timestamp, nonce)) {
            return echostr;
        }
        return StringUtils.EMPTY;
    }

    @ApiOperation(value = "微信公众消息事件")
    @PostMapping(value = "/message")
    public void messageEvent(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        try {

            String fromXml = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            log.debug("收到来自微信的消息事件\n:{}", fromXml);

            // 解析微信消息
            this.messageParse(fromXml);

        } catch (Exception e) {
            log.error("解析微信消息事件错误:{}", e.getMessage(), e);
        }
    }

    public void messageParse(String message) throws JsonProcessingException {
        XmlMapper xmlMapper = new XmlMapper();
        WxBaseMessageDTO baseMessage = xmlMapper.readValue(message, WxBaseMessageDTO.class);
        //判断是否是事件类型
        if ("event".equals(baseMessage.getMsgType())) {
            WxEventMessageDTO event = xmlMapper.readValue(message, WxEventMessageDTO.class);
            //订阅
            if ("subscribe".equals(event.getEvent())) {
            }
            //取消订阅
            if ("unsubscribe".equals(event.getEvent())) {
            }
        } else if ("text".equals(baseMessage.getMsgType())) {
            //判断为文本消息
        }
        log.info("messageParse {}", JSON.toJSONString(baseMessage));
    }


}
