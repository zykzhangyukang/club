package com.coderman.club.service.wechat.imp;

import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.dto.wechat.WxBaseMessageDTO;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.service.user.UserService;
import com.coderman.club.service.wechat.WechatService;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.user.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;

@Service
@Slf4j
public class WechatServiceImpl implements WechatService {

    @Resource
    private RedisService redisService;

    @Resource
    private UserService userService;

    @Override
    public ResultVO<String> getEventCode(String deviceId) {

        if (StringUtils.isBlank(deviceId)) {
            return ResultUtil.getFail("参数错误！");
        }

        // 生成设备id绑定验证码
        String randomNumber = RandomStringUtils.randomNumeric(4);
        this.redisService.setString(RedisKeyConstant.USER_LOGIN_DEVICE_PREFIX + deviceId, randomNumber, 60, RedisDbConstant.REDIS_DB_DEFAULT);
        this.redisService.setString(RedisKeyConstant.USER_LOGIN_DEVICE_WECHAT__PREFIX + randomNumber, deviceId, 60, RedisDbConstant.REDIS_DB_DEFAULT);

        return ResultUtil.getSuccess(String.class, randomNumber);
    }

    @Override
    public String replyMessage(WxBaseMessageDTO wxBaseMessageDTO) {

        String msgType = wxBaseMessageDTO.getMsgType();
        String result = StringUtils.EMPTY;

        switch (msgType) {

            case "text":
                result = this.textMessage(wxBaseMessageDTO);
                break;

            case "event":

                if (StringUtils.equals("unsubscribe", wxBaseMessageDTO.getEvent())) {

                    log.warn("用户取消关注了。");
                } else if (StringUtils.equals("subscribe", wxBaseMessageDTO.getEvent())) {
                    result = getTextMessage(wxBaseMessageDTO, "欢迎您关注公众号！");
                }
                break;
            default:
                result = StringUtils.EMPTY;
        }
        return result;
    }


    private String textMessage(WxBaseMessageDTO wxBaseMessageDTO) {

        String xmlString = getTextMessage(wxBaseMessageDTO, "您好，感谢您的留言，我们会尽快回复。如果您有任何问题或疑虑，也请随时在后台提出，我们会竭诚为您服务。");

        if (!StringUtils.equals(wxBaseMessageDTO.getMsgType(), "text")) {
            return xmlString;
        }

        String number = wxBaseMessageDTO.getContent();
        if (StringUtils.isBlank(number) || StringUtils.length(number.trim()) > 4) {
            return xmlString;
        }

        // 这里只处理事件码登录的逻辑
        boolean isFourDigits = NumberUtils.isDigits(number);
        if (!isFourDigits) {
            return xmlString;
        }

        // 判断是否存在事件码绑定了设备号
        String deviceId = this.redisService.getString(RedisKeyConstant.USER_LOGIN_DEVICE_WECHAT__PREFIX + number, RedisDbConstant.REDIS_DB_DEFAULT);
        if (StringUtils.isBlank(deviceId)) {
            return getTextMessage(wxBaseMessageDTO, "验证码错误或已过期！");
        }

        // 事件码与用户openId绑定
        String openId = wxBaseMessageDTO.getFromUserName();
        this.redisService.setString(RedisKeyConstant.USER_LOGIN_WECHAT_PREFIX + number, openId, 60, RedisDbConstant.REDIS_DB_DEFAULT);

        return getTextMessage(wxBaseMessageDTO, "登录成功！");
    }

    private String getTextMessage(WxBaseMessageDTO wxBaseMessageDTO, String content) {
        return "<xml>\n" +
                "  <ToUserName><![CDATA[" + wxBaseMessageDTO.getFromUserName() + "]]></ToUserName>\n" +
                "  <FromUserName><![CDATA[" + wxBaseMessageDTO.getToUserName() + "]]></FromUserName>\n" +
                "  <CreateTime>" + Instant.now().getEpochSecond() + "</CreateTime>\n" +
                "  <MsgType><![CDATA[text]]></MsgType>\n" +
                "  <Content><![CDATA[" + content + "]]></Content>\n" +
                "</xml>";
    }

    @Override
    public ResultVO<UserLoginVO> subscribe(String deviceId) {

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

            return ResultUtil.getWarn(UserLoginVO.class, null, "");
        }

        ResultVO<UserLoginVO> resultVO = this.userService.loginByMp(openId);
        // 删除
        this.redisService.del(RedisKeyConstant.USER_LOGIN_WECHAT_PREFIX + eventCode, RedisDbConstant.REDIS_DB_DEFAULT);
        this.redisService.del(RedisKeyConstant.USER_LOGIN_DEVICE_PREFIX + deviceId, RedisDbConstant.REDIS_DB_DEFAULT);
        this.redisService.del(RedisKeyConstant.USER_LOGIN_DEVICE_WECHAT__PREFIX + eventCode, RedisDbConstant.REDIS_DB_DEFAULT);
        return resultVO;
    }


}
