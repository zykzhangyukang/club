package com.coderman.club.service.notification.impl;

import com.coderman.club.constant.common.CommonConstant;
import com.coderman.club.constant.common.ResultConstant;
import com.coderman.club.constant.user.UserConstant;
import com.coderman.club.dao.notification.NotificationDAO;
import com.coderman.club.dto.notification.NotifyMsgDTO;
import com.coderman.club.enums.NotificationTypeEnum;
import com.coderman.club.model.notification.NotificationModel;
import com.coderman.club.service.notification.NotificationService;
import com.coderman.club.utils.AuthUtil;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.utils.WebsocketUtil;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.notification.NotificationCountVO;
import com.coderman.club.vo.notification.NotificationVO;
import com.coderman.club.vo.user.AuthUserVO;
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
    private NotificationDAO notificationDAO;

    @Resource
    private WebsocketUtil websocketUtil;


    @Override
    public void saveAndNotify(NotifyMsgDTO notifyMsgDTO) {

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
            this.notificationDAO.insertSelective(notificationModel);

            // websocket推送
            this.websocketUtil.sendToUser(senderId, userId, notificationModel);
        }

    }

    @Override
    public ResultVO<Map<String, Object>> getUnReadCount() {

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {
            return ResultUtil.getWarn("用户未登录！");
        }

        List<NotificationCountVO> notificationCountVos = this.notificationDAO.getUnReadCount(current.getUserId());
        long totalCount = 0L;
        long sysCount = 0L;
        long replyCount = 0L;
        long zanCount = 0L;
        long atCount = 0L;
        long chatCount = 0L;
        long followCount = 0L;
        for (NotificationCountVO notificationCountVo : notificationCountVos) {
            Long count = Optional.ofNullable(notificationCountVo.getUnReadCount()).orElse(0L);
            totalCount += count;

            // 系统通知
            if (NotificationTypeEnum.FOLLOWING_USER.equals(NotificationTypeEnum.getByMsgType(notificationCountVo.getType())) ||
                    NotificationTypeEnum.REGISTER_WELCOME.equals(NotificationTypeEnum.getByMsgType(notificationCountVo.getType()))
            ) {
                sysCount += notificationCountVo.getUnReadCount();
            }

            // 点赞我的
            if(NotificationTypeEnum.LIKE_POST.equals(NotificationTypeEnum.getByMsgType(notificationCountVo.getType()))){
                zanCount +=notificationCountVo.getUnReadCount();
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        // 全部未读数
        resultMap.put("totalCount", totalCount);
        // @我的
        resultMap.put("atCount", atCount);
        // 回复我的
        resultMap.put("replyCount", replyCount);
        // 收到的赞
        resultMap.put("zanCount", zanCount);
        // 系统消息
        resultMap.put("sysCount", sysCount);
        // 关注信息
        resultMap.put("followCount", followCount);
        // 我的消息
        resultMap.put("chatCount", chatCount);

        ResultVO<Map<String, Object>> resultVO = new ResultVO<>();
        resultVO.setCode(ResultConstant.RESULT_CODE_200);
        resultVO.setResult(resultMap);
        return resultVO;
    }

    @Override
    public ResultVO<List<NotificationVO>> getList(Boolean isRead, String type) {

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {
            return ResultUtil.getWarn("用户未登录！");
        }
        if (StringUtils.isBlank(type)) {
            return ResultUtil.getWarn("消息类型不能为空！");
        }
        NotificationTypeEnum byMsgType = NotificationTypeEnum.getByMsgType(type);
        if (byMsgType == null) {
            return ResultUtil.getWarn("参数错误！");
        }

        List<NotificationVO> notificationVos = this.notificationDAO.getList(current.getUserId(), isRead, type);
        for (NotificationVO notificationVo : notificationVos) {
            if (Objects.equals(notificationVo.getSenderId(), 0L)) {
                notificationVo.setSenderName("系统");
                notificationVo.setSenderAvatar(UserConstant.USER_DEFAULT_AVATAR);
            }
        }

        return ResultUtil.getSuccessList(NotificationVO.class, notificationVos);
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

        NotificationModel notificationModel = this.notificationDAO.selectByPrimaryKey(notificationId);
        if (notificationModel == null) {
            return ResultUtil.getWarn("消息不存在！");
        }

        if (BooleanUtils.isFalse(notificationModel.getIsRead())) {
            this.notificationDAO.updateReadStatus(BooleanUtils.toIntegerObject(true), current.getUserId(), notificationId);
        }

        return ResultUtil.getSuccess();
    }

}
