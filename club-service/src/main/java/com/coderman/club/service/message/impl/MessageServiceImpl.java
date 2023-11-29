package com.coderman.club.service.message.impl;

import com.coderman.club.constant.common.CommonConstant;
import com.coderman.club.dao.message.MessageDAO;
import com.coderman.club.dao.message.MessageSessionDAO;
import com.coderman.club.dto.message.MessageSendDTO;
import com.coderman.club.model.message.MessageSessionModel;
import com.coderman.club.service.message.MessageService;
import com.coderman.club.utils.AuthUtil;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.user.AuthUserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author ：zhangyukang
 * @date ：2023/11/29 12:27
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageDAO messageDAO;

    @Resource
    private MessageSessionDAO messageSessionDAO;

    @Override
    public ResultVO<Void> send(MessageSendDTO messageSendDTO) {

        String content = messageSendDTO.getContent();
        Long receiverId = messageSendDTO.getReceiverId();
        AuthUserVO current = AuthUtil.getCurrent();
        if(current == null){
            return ResultUtil.getWarn("用户未登录！");
        }
        if(receiverId == null || receiverId <0){
            return ResultUtil.getWarn("参数错误！");
        }
        if(StringUtils.isBlank(content)){
            return ResultUtil.getWarn("发送消息内容不能为空！");
        }

        if(StringUtils.length(content) > CommonConstant.LENGTH_256){
            return ResultUtil.getWarn("发送消息内容最多256个字符！");
        }

        // 创建会话
        MessageSessionModel session = this.createSession(current.getUserId(), receiverId, content);


        return null;
    }

    private MessageSessionModel createSession(Long userId, Long receiverId, String content) {

        //发送者id 是否 小于 接受者 id
        boolean isSmall = userId.compareTo(receiverId) < 0;

        //会话是否存在，否创建会话
        MessageSessionModel sessionModel;
        if (isSmall) {
            sessionModel = this.messageSessionDAO.selectSessionByUser(userId, receiverId);
        } else {
            sessionModel = this.messageSessionDAO.selectSessionByUser(receiverId, userId);
        }

        if (sessionModel == null) {
            MessageSessionModel messageSessionModel = new MessageSessionModel();
            if (isSmall) {
                messageSessionModel.setUserOne(userId);
                messageSessionModel.setUserTwo(receiverId);
            } else {
                messageSessionModel.setUserOne(receiverId);
                messageSessionModel.setUserTwo(userId);
            }
            messageSessionModel.setLastMessageTime(new Date());
        }

    }
}
