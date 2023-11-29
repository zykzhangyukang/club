package com.coderman.club.controller.message;

import com.coderman.club.dto.message.MessageSendDTO;
import com.coderman.club.service.message.MessageService;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.message.MessageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ：zhangyukang
 * @date ：2023/11/28 18:16
 */
@Api(value = "社区私信模块", tags = {"社区私信模块"})
@RestController
@RequestMapping(value = "/api/message")
public class MessageController {

    @Resource
    private MessageService messageService;

    @ApiOperation(value = "发送私信")
    @PostMapping(value = "/send")
    public ResultVO<Void> send(@RequestBody MessageSendDTO messageSendDTO){

        return this.messageService.send(messageSendDTO);
    }


    @ApiOperation(value = "获取会话消息")
    @PostMapping(value = "/list")
    public ResultVO<List<MessageVO>> getSessionMessages(Long sessionId){

        return this.messageService.getSessionMessages(sessionId);
    }
}
