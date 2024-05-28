package com.coderman.club.controller.wechat;

import com.alibaba.fastjson.JSON;
import com.coderman.club.dto.wechat.WxBaseMessageDTO;
import com.coderman.club.exception.BusinessException;
import com.coderman.club.service.wechat.WechatService;
import com.coderman.club.utils.WechatUtil;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.user.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringReader;
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

    @Resource
    private WechatService wechatService;

    @ApiOperation(value = "获取微信公众令牌")
    @GetMapping(value = "/code")
    public ResultVO<String> getEventCode(String deviceId) {

        return this.wechatService.getEventCode(deviceId);
    }

    @ApiOperation(value = "获取微信公众令牌")
    @GetMapping(value = "/subscribe",produces = "text/event-stream;charset=UTF-8")
    public SseEmitter subscribe(String deviceId) {

        return this.wechatService.subscribe(deviceId);
    }

    @ApiOperation(value = "校验微信公众令牌")
    @GetMapping(value = "/message")
    public String message(HttpServletRequest request) {

        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");

        log.debug("收到来自微信的认证消息, signature:{},timestamp:{}, nonce:{},echostr:{}  ", signature, timestamp, nonce, echostr);

        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new BusinessException("请求参数非法！");
        }
        if (WechatUtil.checkSignature(signature, timestamp, nonce)) {
            return echostr;
        }
        return StringUtils.EMPTY;
    }

    @ApiOperation(value = "微信公众消息事件")
    @PostMapping(value = "/message")
    public void messageEvent(HttpServletRequest req, HttpServletResponse resp) {

        PrintWriter out = null;
        try {

            // 创建 SAXBuilder 对象来构建 XML 文档
            String fromXml = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            Document document = new SAXBuilder().build(new StringReader(fromXml));
            Element root = document.getRootElement();

            WxBaseMessageDTO wxBaseMessageDTO = new WxBaseMessageDTO();
            wxBaseMessageDTO.setToUserName(root.getChildText("ToUserName"));
            wxBaseMessageDTO.setFromUserName(root.getChildText("FromUserName"));
            wxBaseMessageDTO.setContent(root.getChildText("Content"));
            wxBaseMessageDTO.setMsgId(root.getChildText("MsgId"));
            wxBaseMessageDTO.setMsgType(root.getChildText("MsgType"));
            wxBaseMessageDTO.setEvent(root.getChildText("Event"));
            wxBaseMessageDTO.setCreateTime(root.getChildText("CreateTime"));

            log.debug(fromXml);
            log.debug("收到来自微信的消息事件: wxBaseMessageDTO:{}", JSON.toJSONString(wxBaseMessageDTO));

            String result = this.wechatService.replyMessage(wxBaseMessageDTO);

            if (StringUtils.isBlank(result)) {
                return;
            }
            resp.setCharacterEncoding("UTF-8");
            out = resp.getWriter();
            out.write(result);
        } catch (Exception e) {
            log.error("解析微信消息事件错误:{}", e.getMessage(), e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
