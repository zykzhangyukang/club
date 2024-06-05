package com.coderman.club.service.notification.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coderman.club.constant.common.CommonConstant;
import com.coderman.club.dto.notification.NotificationDTO;
import com.coderman.club.dto.notification.NotifyMsgDTO;
import com.coderman.club.enums.NotificationTypeEnum;
import com.coderman.club.mapper.notification.NotificationMapper;
import com.coderman.club.mapper.post.PostCommentMapper;
import com.coderman.club.mapper.post.PostMapper;
import com.coderman.club.model.notification.NotificationModel;
import com.coderman.club.service.notification.NotificationService;
import com.coderman.club.utils.AuthUtil;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.utils.WebsocketUtil;
import com.coderman.club.vo.common.PageVO;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.notification.*;
import com.coderman.club.vo.user.AuthUserVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：zhangyukang
 * @date ：2023/11/24 15:30
 */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Resource
    private NotificationMapper notificationMapper;
    @Resource
    private PostMapper postMapper;
    @Resource
    private WebsocketUtil websocketUtil;
    @Resource
    private PostCommentMapper postCommentMapper;


    @Override
    public void send(NotifyMsgDTO notifyMsgDTO) {

        Long senderId = notifyMsgDTO.getSenderId();
        List<Long> userIdList = notifyMsgDTO.getUserIdList();
        NotificationTypeEnum typeEnum = notifyMsgDTO.getTypeEnum();
        String content = notifyMsgDTO.getContent();
        Long relationId = notifyMsgDTO.getRelationId();

        if (senderId == null || CollectionUtils.isEmpty(userIdList) || typeEnum == null) {

            throw new IllegalArgumentException("参数错误！");
        }
        if (StringUtils.isBlank(content) || StringUtils.length(content) > CommonConstant.LENGTH_512) {
            throw new IllegalArgumentException("发送的内容不能为空，且不超过512个字符！");
        }

        for (Long userId : userIdList) {

            NotificationModel notificationModel = new NotificationModel();
            notificationModel.setSenderId(senderId);
            notificationModel.setContent(content);
            notificationModel.setCreateTime(new Date());
            notificationModel.setIsRead(Boolean.FALSE);
            notificationModel.setRelationId(relationId);
            notificationModel.setType(typeEnum.getMsgType());
            notificationModel.setUserId(userId);
            this.notificationMapper.insert(notificationModel);

            // websocket推送
            this.websocketUtil.sendToUser(senderId, userId, notificationModel);
        }

    }

    @Override
    public ResultVO<NotificationCountVO> getUnReadCount() {

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {
            return ResultUtil.getWarn("用户未登录！");
        }

        NotificationCountVO notificationCountVO = this.notificationMapper.getUnReadCount(current.getUserId());
        notificationCountVO.setChatCount(0);
        return ResultUtil.getSuccess(NotificationCountVO.class, notificationCountVO);
    }

    @Override
    public ResultVO<PageVO<List<NotificationVO>>> getPage(NotificationDTO notificationDTO) {

        String type = notificationDTO.getType();
        Boolean isRead = notificationDTO.getIsRead();
        int currentPage = Optional.ofNullable(notificationDTO.getCurrentPage()).orElse(1);
        int pageSize = Optional.ofNullable(notificationDTO.getPageSize()).orElse(10);

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {
            return ResultUtil.getWarn("用户未登录！");
        }
        if (StringUtils.isBlank(type)) {
            return ResultUtil.getWarn("消息类型不能为空！");
        }

        PageHelper.startPage(currentPage, pageSize);
        List<NotificationVO> notificationVos = this.notificationMapper.getPage(current.getUserId(), isRead, type);
        PageInfo<NotificationVO> pageInfo = new PageInfo<>(notificationVos);

        List<NotificationVO> voList = pageInfo.getList();

        // 帖子信息
        Map<Long, NotificationPostVO> postModelMap = Maps.newHashMap();
        List<Long> postIdList = voList.stream().filter(e -> this.isPostInfo(e.getType())).map(NotificationVO::getRelationId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(postIdList)) {
            postModelMap = this.postMapper.selectNotificationPostVOs(postIdList).stream()
                    .collect(Collectors.toMap(NotificationPostVO::getPostId, e -> e, (k1, k2) -> k2));
        }

        // 评论信息
        Map<Long,NotificationCommentVO> commentVoMap= Maps.newHashMap();
        List<Long> commentIdList = voList.stream().filter(e -> this.isCommentInfo(e.getType())).map(NotificationVO::getRelationId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(commentIdList)) {
            commentVoMap = this.postCommentMapper.selectNotificationCommentVos(commentIdList)
                    .stream()
                    .collect(Collectors.toMap(NotificationCommentVO::getCommentId, e -> e, (k1, k2) -> k2));
        }

        for (NotificationVO notificationVO : voList) {

            // 帖子信息封装
            if (this.isPostInfo(notificationVO.getType())) {
                notificationVO.setPost(postModelMap.get(notificationVO.getRelationId()));
            }

            // 评论信息
            if (this.isCommentInfo(notificationVO.getType())) {
                notificationVO.setComment(commentVoMap.get(notificationVO.getRelationId()));
            }
        }
        long total = pageInfo.getTotal();
        return ResultUtil.getSuccessPage(NotificationVO.class, new PageVO<>(total, notificationVos, currentPage, pageSize));
    }

    private boolean isPostInfo(String type) {
        return StringUtils.equals(type, NotificationTypeEnum.LIKE_POST.getMsgType())
                || StringUtils.equals(type, NotificationTypeEnum.COLLECT_POST.getMsgType());
    }
    private boolean isCommentInfo(String type) {
        return StringUtils.equals(type, NotificationTypeEnum.COMMENT.getMsgType())
                || StringUtils.equals(type, NotificationTypeEnum.REPLY.getMsgType())
                || StringUtils.equals(type, NotificationTypeEnum.REPLY_AT.getMsgType());
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultVO<Void> read(Long notificationId) {

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {
            return ResultUtil.getWarn("用户未登录！");
        }
        if (notificationId == null || notificationId < 0) {
            return ResultUtil.getWarn("参数错误！");
        }

        NotificationModel notificationModel = this.selectNotification(notificationId);
        if (notificationModel == null) {
            return ResultUtil.getWarn("消息不存在！");
        }

        if (BooleanUtils.isFalse(notificationModel.getIsRead())) {
            this.notificationMapper.updateReadStatus(BooleanUtils.toIntegerObject(true), current.getUserId(), notificationId);
        }

        return ResultUtil.getSuccess();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<Void> delete(Long notificationId) {

        NotificationModel notificationModel = this.selectNotification(notificationId);
        if (notificationModel == null) {
            return ResultUtil.getWarn("消息不存在！");
        }

        this.notificationMapper.deleteById(notificationId);
        return ResultUtil.getSuccess();
    }

    private NotificationModel selectNotification(Long id) {
        return this.notificationMapper.selectOne(Wrappers.<NotificationModel>lambdaQuery()
                .eq(NotificationModel::getNotificationId, id)
                .eq(NotificationModel::getUserId, AuthUtil.getCurrent().getUserId())
                .last("limit 1")
        );
    }

}
