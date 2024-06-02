package com.coderman.club.service.notification.impl;

import com.coderman.club.constant.common.CommonConstant;
import com.coderman.club.constant.common.ResultConstant;
import com.coderman.club.constant.user.UserConstant;
import com.coderman.club.dto.notification.NotificationDTO;
import com.coderman.club.dto.notification.NotifyMsgDTO;
import com.coderman.club.enums.NotificationTypeEnum;
import com.coderman.club.mapper.notification.NotificationMapper;
import com.coderman.club.model.notification.NotificationModel;
import com.coderman.club.service.notification.NotificationService;
import com.coderman.club.utils.AuthUtil;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.utils.WebsocketUtil;
import com.coderman.club.vo.common.PageVO;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.notification.NotificationCountVO;
import com.coderman.club.vo.notification.NotificationVO;
import com.coderman.club.vo.user.AuthUserVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

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
    private WebsocketUtil websocketUtil;


    @Override
    public void send(NotifyMsgDTO notifyMsgDTO) {

        Long senderId = notifyMsgDTO.getSenderId();
        List<Long> userIdList = notifyMsgDTO.getUserIdList();
        NotificationTypeEnum typeEnum = notifyMsgDTO.getTypeEnum();
        String content = notifyMsgDTO.getContent();
        String link = notifyMsgDTO.getLink();

        if (senderId == null || CollectionUtils.isEmpty(userIdList) || typeEnum == null) {

            throw new IllegalArgumentException("参数错误！");
        }
        if (StringUtils.isBlank(content) || StringUtils.length(content) > CommonConstant.LENGTH_256) {
            throw new IllegalArgumentException("发送的内容不能为空，且不超过256个字符！");
        }
        if (StringUtils.length(link) > CommonConstant.LENGTH_256) {
            throw new IllegalArgumentException("跳转链接不超过256个字符！");
        }

        for (Long userId : userIdList) {

            NotificationModel notificationModel = new NotificationModel();
            notificationModel.setSenderId(senderId);
            notificationModel.setContent(content);
            notificationModel.setCreateTime(new Date());
            notificationModel.setIsRead(Boolean.FALSE);
            notificationModel.setLink(link);
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

        for (NotificationVO notificationVo : pageInfo.getList()) {
            if (Objects.equals(notificationVo.getSenderId(), 0L)) {
                notificationVo.setSenderName("系统");
                notificationVo.setSenderAvatar(UserConstant.USER_DEFAULT_AVATAR);
            }
        }

        long total = pageInfo.getTotal();
        return ResultUtil.getSuccessPage(NotificationVO.class, new PageVO<>(total, notificationVos, currentPage, pageSize));
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

        NotificationModel notificationModel = this.notificationMapper.selectById(notificationId);
        if (notificationModel == null) {
            return ResultUtil.getWarn("消息不存在！");
        }

        if (BooleanUtils.isFalse(notificationModel.getIsRead())) {
            this.notificationMapper.updateReadStatus(BooleanUtils.toIntegerObject(true), current.getUserId(), notificationId);
        }

        return ResultUtil.getSuccess();
    }

}
