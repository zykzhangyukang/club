package com.coderman.club.model.notification;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This is the base record class for table: club_notification
 * Generated by MyBatis Generator.
 * @author MyBatis Generator
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value="NotificationModel", description = "club_notification 实体类")
public class NotificationModel extends BaseModel {
    

    @ApiModelProperty(value = "主键")
    private Long notificationId;

    @ApiModelProperty(value = "接收通知的用户的唯一标识符")
    private Long userId;

    @ApiModelProperty(value = "发送通知的用户或系统的唯一标识符")
    private Long senderId;

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