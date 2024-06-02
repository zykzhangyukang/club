package com.coderman.club.vo.notification;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author ：zhangyukang
 * @date ：2023/11/28 9:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NotificationVO extends BaseModel {

    @ApiModelProperty(value = "主键")
    private Long notificationId;

    @ApiModelProperty(value = "接收通知的用户的唯一标识符")
    private Long userId;

    @ApiModelProperty(value = "关联业务id")
    private Long relationId;

    @ApiModelProperty(value = "帖子标题")
    private String postTitle;

    @ApiModelProperty(value = "接收人")
    private String username;

    @ApiModelProperty(value = "用户头像")
    private String userAvatar;

    @ApiModelProperty(value = "发送通知的用户或系统的唯一标识符")
    private Long senderId;

    @ApiModelProperty(value = "发送人")
    private String senderName;

    @ApiModelProperty(value = "发送人昵称")
    private String sendNickName;

    @ApiModelProperty(value = "发送人头像")
    private String senderAvatar;

    @ApiModelProperty(value = "通知的类型（例如：点赞、评论、回复、系统通知等）")
    private String type;

    @ApiModelProperty(value = "通知的具体内容")
    private String content;

    @ApiModelProperty(value = "通知相关页面的链接（可选）")
    private String link;

    @ApiModelProperty(value = "标记通知是否已被用户阅读")
    private Boolean isRead;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "读取时间")
    private Date readTime;

}
