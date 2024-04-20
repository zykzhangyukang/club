package com.coderman.club.service.wechat.imp;

import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.dto.wechat.WxBaseMessageDTO;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.service.wechat.WechatService;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.vo.common.ResultVO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;

@Service
public class WechatServiceImpl implements WechatService {

    @Resource
    private RedisService redisService;

    @Override
    public ResultVO<String> getEventCode(String deviceId) {

        if (StringUtils.isBlank(deviceId)) {
            return ResultUtil.getFail("参数错误！");
        }

        // 生成设备id绑定验证码
        String randomNumber = RandomStringUtils.randomNumeric(4);
        this.redisService.setString(RedisKeyConstant.USER_LOGIN_DEVICE_PREFIX + deviceId, randomNumber, 60 * 5, RedisDbConstant.REDIS_DB_DEFAULT);
        return ResultUtil.getSuccess(String.class, randomNumber);
    }

    @Override
    public String replyMessage(WxBaseMessageDTO wxBaseMessageDTO) {

        String xmlString = "<xml>\n" +
                "  <ToUserName><![CDATA[" + wxBaseMessageDTO.getToUserName() + "]]></ToUserName>\n" +
                "  <FromUserName><![CDATA[" + wxBaseMessageDTO.getFromUserName() + "]]></FromUserName>\n" +
                "  <CreateTime>" + Instant.now().getEpochSecond() + "</CreateTime>\n" +
                "  <MsgType><![CDATA[text]]></MsgType>\n" +
                "  <Content><![CDATA[你好！]]></Content>\n" +
                "</xml>";

        if (!StringUtils.equals(wxBaseMessageDTO.getMsgType(), "text")) {
            return xmlString;
        }

        String content = wxBaseMessageDTO.getContent();
        if (StringUtils.isBlank(content) || StringUtils.length(content.trim()) > 4) {
            return xmlString;
        }

        // 这里只处理事件码登录的逻辑
        boolean isFourDigits = NumberUtils.isDigits(content);
        if (!isFourDigits) {
            return xmlString;
        }

        // 事件码与用户openId绑定
        String openId = wxBaseMessageDTO.getFromUserName();
        this.redisService.setString(RedisKeyConstant.USER_LOGIN_WECHAT_PREFIX + content, openId, 60 * 5, RedisDbConstant.REDIS_DB_DEFAULT);
        return null;
    }

    @Override
    public ResultVO<String> subscribe(String deviceId) {

        if (StringUtils.isBlank(deviceId)) {
            return ResultUtil.getFail("参数错误！");
        }
        String eventCode = this.redisService.getString(RedisKeyConstant.USER_LOGIN_DEVICE_PREFIX + deviceId, RedisDbConstant.REDIS_DB_DEFAULT);
        if (StringUtils.isBlank(eventCode)) {
            return ResultUtil.getFail("验证码过期！");
        }

        // 获取openId
        String openId = this.redisService.getString(RedisKeyConstant.USER_LOGIN_WECHAT_PREFIX + eventCode, RedisDbConstant.REDIS_DB_DEFAULT);
        if (StringUtils.isBlank(openId)) {

            return ResultUtil.getSuccess(String.class, null);
        }

        // 删除
        this.redisService.del(RedisKeyConstant.USER_LOGIN_WECHAT_PREFIX + eventCode, RedisDbConstant.REDIS_DB_DEFAULT);
        this.redisService.del(RedisKeyConstant.USER_LOGIN_DEVICE_PREFIX + deviceId, RedisDbConstant.REDIS_DB_DEFAULT);

        return ResultUtil.getSuccess(String.class, openId);
    }


}
