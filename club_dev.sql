/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 50735
 Source Host           : localhost:3306
 Source Schema         : club_dev

 Target Server Type    : MySQL
 Target Server Version : 50735
 File Encoding         : 65001

 Date: 18/06/2024 10:26:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for club_carousel
-- ----------------------------
DROP TABLE IF EXISTS `club_carousel`;
CREATE TABLE `club_carousel`  (
  `carousel_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `title` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `description` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述信息',
  `image_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片url',
  `target_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '跳转url',
  `order_priority` int(11) NULL DEFAULT NULL COMMENT '优先级顺序',
  `is_active` bit(1) NULL DEFAULT NULL COMMENT '是否启用',
  PRIMARY KEY (`carousel_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of club_carousel
-- ----------------------------

-- ----------------------------
-- Table structure for club_message
-- ----------------------------
DROP TABLE IF EXISTS `club_message`;
CREATE TABLE `club_message`  (
  `message_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息id',
  `session_id` bigint(20) NOT NULL COMMENT '回话id',
  `sender_id` bigint(20) NOT NULL COMMENT '发送人id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `content` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '私信内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `ix_session_id`(`session_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of club_message
-- ----------------------------

-- ----------------------------
-- Table structure for club_message_relation
-- ----------------------------
DROP TABLE IF EXISTS `club_message_relation`;
CREATE TABLE `club_message_relation`  (
  `relation_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `message_id` bigint(20) NOT NULL COMMENT '消息id',
  `session_id` bigint(20) NOT NULL COMMENT '会话id',
  `is_delete` bit(1) NOT NULL COMMENT '是否删除',
  `is_read` bit(1) NOT NULL COMMENT '是否已读',
  PRIMARY KEY (`relation_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 47 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of club_message_relation
-- ----------------------------

-- ----------------------------
-- Table structure for club_message_session
-- ----------------------------
DROP TABLE IF EXISTS `club_message_session`;
CREATE TABLE `club_message_session`  (
  `session_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '会话id',
  `user_one` bigint(20) NOT NULL COMMENT '用户1 (小id)',
  `user_two` bigint(20) NOT NULL COMMENT '用户2 (大id)',
  `last_message` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '最后一条消息内容',
  `last_user_id` bigint(20) NOT NULL COMMENT '最后发送人id',
  `last_message_time` datetime NOT NULL COMMENT '最后一条私信时间',
  PRIMARY KEY (`session_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of club_message_session
-- ----------------------------

-- ----------------------------
-- Table structure for club_message_session_relation
-- ----------------------------
DROP TABLE IF EXISTS `club_message_session_relation`;
CREATE TABLE `club_message_session_relation`  (
  `relation_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `session_id` bigint(20) NOT NULL COMMENT '会话id',
  `is_delete` bit(1) NOT NULL COMMENT '是否删除',
  `un_read_count` int(11) NOT NULL COMMENT '未读数',
  PRIMARY KEY (`relation_id`) USING BTREE,
  UNIQUE INDEX `ix_uq_key`(`user_id`, `session_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of club_message_session_relation
-- ----------------------------

-- ----------------------------
-- Table structure for club_notification
-- ----------------------------
DROP TABLE IF EXISTS `club_notification`;
CREATE TABLE `club_notification`  (
  `notification_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(11) NOT NULL COMMENT '接收通知的用户的唯一标识符',
  `sender_id` bigint(11) NOT NULL COMMENT '发送通知的用户或系统的唯一标识符',
  `type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通知的类型（例如：点赞、评论、回复、系统通知等）',
  `content` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通知的具体内容',
  `is_read` bit(1) NULL DEFAULT NULL COMMENT '标记通知是否已被用户阅读',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `read_time` datetime NULL DEFAULT NULL COMMENT '读取时间',
  `relation_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`notification_id`) USING BTREE,
  INDEX `ix_type_read`(`type`, `is_read`) USING BTREE,
  INDEX `ix_user_id`(`user_id`) USING BTREE,
  INDEX `ix_sender_id`(`sender_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 589 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of club_notification
-- ----------------------------

-- ----------------------------
-- Table structure for club_point_account
-- ----------------------------
DROP TABLE IF EXISTS `club_point_account`;
CREATE TABLE `club_point_account`  (
  `account_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `user_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户编号',
  `points_balance` int(11) NOT NULL COMMENT '积分余额',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`account_id`) USING BTREE,
  UNIQUE INDEX `ix_ucode`(`user_code`) USING BTREE,
  UNIQUE INDEX `ix_use_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of club_point_account
-- ----------------------------
INSERT INTO `club_point_account` VALUES (39, 126, 'CU00000', 0, '2024-06-04 11:07:49');

-- ----------------------------
-- Table structure for club_point_transaction
-- ----------------------------
DROP TABLE IF EXISTS `club_point_transaction`;
CREATE TABLE `club_point_transaction`  (
  `transaction_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `user_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户编号',
  `point_change` int(11) NOT NULL COMMENT '积分变更',
  `relation_id` bigint(20) NULL DEFAULT NULL COMMENT '关联id',
  `transaction_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '变更类型',
  `transaction_date` datetime NOT NULL COMMENT '变更时间',
  PRIMARY KEY (`transaction_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of club_point_transaction
-- ----------------------------

-- ----------------------------
-- Table structure for club_post
-- ----------------------------
DROP TABLE IF EXISTS `club_post`;
CREATE TABLE `club_post`  (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL COMMENT '发帖用户id',
  `section_id` bigint(20) NOT NULL COMMENT '帖子所属板块',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '帖子标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容',
  `views_count` int(11) NOT NULL DEFAULT 0 COMMENT '浏览量',
  `likes_count` int(11) NOT NULL DEFAULT 0 COMMENT '点赞量',
  `comments_count` int(11) NOT NULL DEFAULT 0 COMMENT '评论量',
  `collects_count` int(11) NOT NULL DEFAULT 0 COMMENT '收藏量',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `last_updated_at` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `is_active` bit(1) NOT NULL COMMENT '是否启用',
  `is_draft` bit(1) NOT NULL COMMENT '是否为草稿',
  PRIMARY KEY (`post_id`) USING BTREE,
  INDEX `ix_user_id`(`user_id`) USING BTREE,
  INDEX `ix_section_id`(`section_id`) USING BTREE,
  INDEX `ix_active_draft`(`is_active`, `is_draft`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2424 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of club_post
-- ----------------------------
INSERT INTO `club_post` VALUES (2423, 126, 1, '这个是帖子的标题', '这个是帖子的内容', 0, 0, 127, 0, '2024-06-01 11:15:12', NULL, b'1', b'0');

-- ----------------------------
-- Table structure for club_post_collect
-- ----------------------------
DROP TABLE IF EXISTS `club_post_collect`;
CREATE TABLE `club_post_collect`  (
  `post_collect_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `post_id` bigint(20) NOT NULL COMMENT '帖子id',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`post_collect_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of club_post_collect
-- ----------------------------

-- ----------------------------
-- Table structure for club_post_comment
-- ----------------------------
DROP TABLE IF EXISTS `club_post_comment`;
CREATE TABLE `club_post_comment`  (
  `comment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `post_id` bigint(20) NOT NULL COMMENT '帖子id',
  `parent_id` bigint(20) NOT NULL COMMENT '父级评论id',
  `reply_id` bigint(20) NOT NULL COMMENT '被回复的评论id',
  `reply_count` int(11) NOT NULL COMMENT '子评论的数量',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `to_user_id` bigint(20) NOT NULL COMMENT '被评论人id',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容',
  `location` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '地址',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `is_hide` bit(1) NOT NULL COMMENT '是否隐藏',
  `type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型（评论：comment, 回复：reply_comment, 回复@ reply_at_comment）',
  PRIMARY KEY (`comment_id`) USING BTREE,
  INDEX `ix_reply_id`(`reply_id`) USING BTREE,
  INDEX `ix_parent_id`(`parent_id`) USING BTREE,
  INDEX `ix_user`(`user_id`) USING BTREE,
  INDEX `ix_to_user`(`to_user_id`) USING BTREE,
  INDEX `ix_type`(`type`) USING BTREE,
  INDEX `ix_post_id`(`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 479 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of club_post_comment
-- ----------------------------

-- ----------------------------
-- Table structure for club_post_like
-- ----------------------------
DROP TABLE IF EXISTS `club_post_like`;
CREATE TABLE `club_post_like`  (
  `post_like_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `post_id` bigint(20) NOT NULL COMMENT '帖子id',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '点赞状态',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`post_like_id`) USING BTREE,
  UNIQUE INDEX `ix_user_post_status`(`user_id`, `post_id`, `status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 52 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of club_post_like
-- ----------------------------

-- ----------------------------
-- Table structure for club_post_tag
-- ----------------------------
DROP TABLE IF EXISTS `club_post_tag`;
CREATE TABLE `club_post_tag`  (
  `post_tag_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `post_id` bigint(20) NOT NULL COMMENT '帖子id',
  `tag_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标签',
  PRIMARY KEY (`post_tag_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 334 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of club_post_tag
-- ----------------------------
INSERT INTO `club_post_tag` VALUES (333, 2423, 'java');

-- ----------------------------
-- Table structure for club_section
-- ----------------------------
DROP TABLE IF EXISTS `club_section`;
CREATE TABLE `club_section`  (
  `section_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `section_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '版块名称',
  `parent_section` bigint(20) NULL DEFAULT NULL COMMENT '父版块（可为空）',
  `description` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '版块描述',
  `is_active` bit(1) NULL DEFAULT NULL COMMENT '是否处于启用状态',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序字段',
  PRIMARY KEY (`section_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 133 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of club_section
-- ----------------------------
INSERT INTO `club_section` VALUES (1, 'Java分类', 0, '描述信息', b'1', 1);

-- ----------------------------
-- Table structure for club_user
-- ----------------------------
DROP TABLE IF EXISTS `club_user`;
CREATE TABLE `club_user`  (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `user_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户编号',
  `nickname` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱地址',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `mp_open_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信公众号用户openId',
  `salt` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '盐值',
  `user_status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号状态',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `ix_uq_username`(`username`) USING BTREE,
  UNIQUE INDEX `ix_uq_email`(`email`) USING BTREE,
  UNIQUE INDEX `ix_ucode`(`user_code`) USING BTREE,
  INDEX `ix_mq_open_id`(`mp_open_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 127 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of club_user
-- ----------------------------
INSERT INTO `club_user` VALUES (126, 'CU00000', '时尚的过客', '3053161401@qq.com', 'zhangyukang', '79595ab0a83334a8f1141e26c27416d0', '', 'mdRSugPqBGbZiZmMGKgvRANyjBZxraBi', 'enable', '2024-06-04 11:07:49', '2024-06-04 11:07:49');

-- ----------------------------
-- Table structure for club_user_following
-- ----------------------------
DROP TABLE IF EXISTS `club_user_following`;
CREATE TABLE `club_user_following`  (
  `follow_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `follower_id` bigint(20) NOT NULL COMMENT '关注者用户',
  `followed_id` bigint(20) NOT NULL COMMENT '被关注者用户',
  `follow_date` datetime NOT NULL COMMENT '关注日期',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关注状态',
  PRIMARY KEY (`follow_id`) USING BTREE,
  UNIQUE INDEX `ix_uq_follow`(`follower_id`, `followed_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 55 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of club_user_following
-- ----------------------------

-- ----------------------------
-- Table structure for club_user_info
-- ----------------------------
DROP TABLE IF EXISTS `club_user_info`;
CREATE TABLE `club_user_info`  (
  `user_info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户信息id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `user_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户编号',
  `user_tags` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户标签',
  `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像地址',
  `gender` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户性别',
  `bio` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个人简介',
  `website` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个人网站',
  `location` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地址位置',
  `followers_count` bigint(20) NULL DEFAULT NULL COMMENT '关注者数量',
  `following_count` bigint(20) NULL DEFAULT NULL COMMENT '关注的人数量',
  `register_time` datetime NULL DEFAULT NULL COMMENT '注册时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最新登录时间',
  PRIMARY KEY (`user_info_id`) USING BTREE,
  UNIQUE INDEX `ix_uq_userid`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 88 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of club_user_info
-- ----------------------------
INSERT INTO `club_user_info` VALUES (87, 126, 'CU00000', '', 'https://ioss-bucket.oss-cn-shenzhen.aliyuncs.com/club/user/2024-06-04/png/1717470468_OdcS.png', 'man', '', '', '', 0, 0, '2024-06-04 11:07:49', '2024-06-06 16:01:44');

-- ----------------------------
-- Table structure for club_user_login_log
-- ----------------------------
DROP TABLE IF EXISTS `club_user_login_log`;
CREATE TABLE `club_user_login_log`  (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `login_time` datetime NOT NULL COMMENT '登录时间',
  `ip_address` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登入ip',
  `device_info` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备信息',
  `location` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录地址',
  PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 416 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of club_user_login_log
-- ----------------------------
INSERT INTO `club_user_login_log` VALUES (413, 126, '2024-06-04 11:11:26', '192.168.2.71', 'Windows', '内网IP');
INSERT INTO `club_user_login_log` VALUES (414, 126, '2024-06-05 11:18:21', '192.168.2.71', 'Windows', '内网IP');
INSERT INTO `club_user_login_log` VALUES (415, 126, '2024-06-06 16:01:44', '192.168.2.71', 'Windows', '内网IP');

-- ----------------------------
-- Table structure for club_user_setting
-- ----------------------------
DROP TABLE IF EXISTS `club_user_setting`;
CREATE TABLE `club_user_setting`  (
  `setting_id` bigint(20) NOT NULL COMMENT '主键id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `setting_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设置项键名',
  `setting_value` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设置项值',
  PRIMARY KEY (`setting_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of club_user_setting
-- ----------------------------

-- ----------------------------
-- Table structure for pub_serial_number
-- ----------------------------
DROP TABLE IF EXISTS `pub_serial_number`;
CREATE TABLE `pub_serial_number`  (
  `serial_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `serial_prefix` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `is_ymd` bit(1) NOT NULL,
  `digit_with` int(11) NOT NULL,
  `next_seq` int(11) NOT NULL,
  `buffer_step` int(11) NOT NULL,
  `update_time` datetime NOT NULL,
  `c_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `u_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`serial_type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of pub_serial_number
-- ----------------------------
INSERT INTO `pub_serial_number` VALUES ('user_code', 'CU', b'0', 5, 100, 100, '2024-06-04 11:07:48', '2024-06-04 11:07:07', '2024-06-04 11:07:48');

-- ----------------------------
-- Procedure structure for usp_get_serial_number
-- ----------------------------
DROP PROCEDURE IF EXISTS `usp_get_serial_number`;
delimiter ;;
CREATE PROCEDURE `usp_get_serial_number`(IN in_serial_type   VARCHAR(32),
  IN in_serial_count  INT,
  OUT out_next_seq     INT,
  OUT out_update_time  DATETIME)
begin

SET autocommit =0;
START TRANSACTION;

set out_update_time = NOW();
UPDATE
    pub_serial_number
SET
next_seq =
(
  case when is_ymd = 1 and datediff(out_update_time,update_time)!=0 then 1+in_serial_count else next_seq+in_serial_count
  end
),update_time = out_update_time
where serial_type = in_serial_type;

select (next_seq - in_serial_count) into out_next_seq from pub_serial_number where serial_type = in_serial_type;
commit;
end
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
