package com.coderman.club.service.message.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coderman.club.constant.common.CommonConstant;
import com.coderman.club.dto.message.MessageSendDTO;
import com.coderman.club.mapper.message.MessageMapper;
import com.coderman.club.mapper.message.MessageRelationMapper;
import com.coderman.club.mapper.message.MessageSessionMapper;
import com.coderman.club.mapper.message.MessageSessionRelationMapper;
import com.coderman.club.model.message.MessageModel;
import com.coderman.club.model.message.MessageRelationModel;
import com.coderman.club.model.message.MessageSessionModel;
import com.coderman.club.model.message.MessageSessionRelationModel;
import com.coderman.club.service.message.MessageService;
import com.coderman.club.service.user.UserInfoService;
import com.coderman.club.utils.AuthUtil;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.message.MessageSessionVO;
import com.coderman.club.vo.message.MessageVO;
import com.coderman.club.vo.user.AuthUserVO;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ：zhangyukang
 * @date ：2023/11/29 12:27
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageSessionRelationMapper messageSessionRelationMapper;

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private MessageService messageService;

    @Resource
    private MessageRelationMapper messageRelationMapper;

    @Resource
    private MessageSessionMapper messageSessionMapper;

    @Resource
    private UserInfoService userInfoService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultVO<Void> send(MessageSendDTO messageSendDTO) {

        String content = messageSendDTO.getContent();
        Long receiverId = messageSendDTO.getReceiverId();
        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {
            return ResultUtil.getWarn("用户未登录！");
        }
        if (receiverId == null || receiverId < 0) {
            return ResultUtil.getWarn("参数错误！");
        }
        if (StringUtils.isBlank(content)) {
            return ResultUtil.getWarn("发送消息内容不能为空！");
        }

        if (StringUtils.length(content) > CommonConstant.LENGTH_512) {
            return ResultUtil.getWarn("发送消息内容最多512个字符！");
        }

        // 创建会话
        Long sessionId = this.createSession(current.getUserId(), receiverId, content);
        // 设置会话关联
        this.createSessionRelation(current.getUserId(), sessionId, true);
        this.createSessionRelation(receiverId, sessionId, false);
        // 创建消息
        MessageModel messageModel = this.createMessage(sessionId, current.getUserId(), receiverId, content);
        // 设置消息关联
        this.createMessageRelation(current.getUserId(), sessionId, messageModel.getMessageId());
        this.createMessageRelation(receiverId,  sessionId, messageModel.getMessageId());

        return ResultUtil.getSuccess();
    }

    private void createMessageRelation(Long userId, Long sessionId, Long messageId) {
        MessageRelationModel messageRelationModel = new MessageRelationModel();
        messageRelationModel.setIsDelete(Boolean.FALSE);
        messageRelationModel.setMessageId(messageId);
        messageRelationModel.setUserId(userId);
        messageRelationModel.setSessionId(sessionId);
        messageRelationModel.setIsRead(Boolean.FALSE);
        this.messageRelationMapper.insert(messageRelationModel);
    }

    private void createSessionRelation(Long userId, Long sessionId, Boolean isRead) {
        MessageSessionRelationModel sessionRelationModel = this.messageSessionRelationMapper.selectUserRelation(userId, sessionId);
        if (sessionRelationModel == null) {

            MessageSessionRelationModel insertModel = new MessageSessionRelationModel();
            insertModel.setIsDelete(Boolean.FALSE);
            insertModel.setSessionId(sessionId);
            insertModel.setUserId(userId);
            if (BooleanUtils.isTrue(isRead)) {
                insertModel.setUnReadCount(0);
            } else {
                insertModel.setUnReadCount(1);
            }
            this.messageSessionRelationMapper.insert(insertModel);

        } else if (BooleanUtils.isFalse(isRead)) {

            this.messageSessionRelationMapper.update(null ,  Wrappers.<MessageSessionRelationModel>
                    lambdaUpdate().set(MessageSessionRelationModel::getUnReadCount, sessionRelationModel.getUnReadCount() + 1)
            .eq(MessageSessionRelationModel::getRelationId, sessionRelationModel.getRelationId()));
        }
    }

    @Override
    public ResultVO<List<MessageVO>> getSessionMessages(Long sessionId) {

        AuthUserVO current = AuthUtil.getCurrent();
        if(current == null){
            return ResultUtil.getWarn("用户未登录！");
        }
        if(sessionId == null || sessionId < 0){
            return ResultUtil.getWarn("参数错误！");
        }

        MessageSessionModel sessionModel = this.messageSessionMapper.selectById(sessionId);
        if(sessionModel == null){
            return ResultUtil.getWarn("会话不存在，请刷新页面重试！");
        }

        List<MessageVO> messageVos = this.messageMapper.selectUserMessages(current.getUserId(), sessionId);

        // 将该会话的所有消息都置为已读
        this.messageService.updateReadStatus(Objects.equals(current.getUserId(), sessionModel.getUserOne()) ? sessionModel.getUserTwo(): sessionModel.getUserOne() , sessionId);
        return ResultUtil.getSuccessList(MessageVO.class, messageVos);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateReadStatus(Long targetUserId, Long sessionId) {

        // 将当前用户的当前会话未读数变成0
        this.messageSessionRelationMapper.updateReadCountZero(Objects.requireNonNull(AuthUtil.getCurrent()).getUserId(), sessionId);

        // 将对方发给我的消息标记未已读 TODO
        this.messageRelationMapper.updateReadStatus(targetUserId, sessionId);
    }

    @Override
    public ResultVO<List<MessageSessionVO>> getSessions() {

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {

            return ResultUtil.getWarn("用户未登录！");
        }
        List<MessageSessionVO> sessionVos = this.messageSessionMapper.selectSessionList(current.getUserId());

        List<Long> userIds = sessionVos.stream()
                .map(e -> {
                    Long userOne = e.getUserOne();
                    Long userTwo = e.getUserTwo();
                    return Objects.equals(current.getUserId(), userOne) ? userTwo : userOne;
                }).distinct().collect(Collectors.toList());

        return ResultUtil.getSuccessList(MessageSessionVO.class, sessionVos);
    }

    @Override
    public ResultVO<Void> closeSession(Long sessionId) {

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {
            return ResultUtil.getWarn("用户未登录！");
        }

        if(sessionId == null || sessionId < 0){
            return ResultUtil.getWarn("参数错误！");
        }

        this.messageSessionRelationMapper.updateDeleteFlag(current.getUserId(), sessionId);
        return ResultUtil.getSuccess();
    }


    private MessageModel createMessage(Long sessionId, Long userId, Long receiverId, String content) {

        MessageModel messageModel = new MessageModel();
        messageModel.setContent(content);
        messageModel.setCreateTime(new Date());
        messageModel.setSenderId(userId);
        messageModel.setUserId(receiverId);
        messageModel.setSessionId(sessionId);
        this.messageMapper.insertSelectiveReturnKey(messageModel);
        return messageModel;
    }

    private Long createSession(Long userId, Long receiverId, String content) {

        //发送者id 是否 小于 接受者 id
        boolean isSmall = userId.compareTo(receiverId) < 0;

        //会话是否存在，否创建会话
        MessageSessionModel sessionModel;
        if (isSmall) {
            sessionModel = this.messageSessionMapper.selectSessionByUser(userId, receiverId);
        } else {
            sessionModel = this.messageSessionMapper.selectSessionByUser(receiverId, userId);
        }

        // 新增
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
            messageSessionModel.setLastMessage(content);
            messageSessionModel.setLastUserId(userId);
            this.messageSessionMapper.insertSelectiveReturnKey(messageSessionModel);
            return messageSessionModel.getSessionId();
        } else {

            // 更新
            MessageSessionModel update = new MessageSessionModel();
            update.setSessionId(sessionModel.getSessionId());
            update.setLastUserId(userId);
            update.setLastMessageTime(new Date());
            update.setLastMessage(content);
            this.messageSessionMapper.updateById(update);
            return sessionModel.getSessionId();
        }
    }
}
