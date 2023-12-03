package com.coderman.club.service.post.impl;

import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.dao.post.PostDAO;
import com.coderman.club.dto.post.PostPublishDTO;
import com.coderman.club.model.post.PostModel;
import com.coderman.club.service.post.PostService;
import com.coderman.club.service.redis.RedisLockService;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.service.section.SectionService;
import com.coderman.club.utils.AuthUtil;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.section.SectionVO;
import com.coderman.club.vo.user.AuthUserVO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author ：zhangyukang
 * @date ：2023/12/01 15:45
 */
@Service
public class PostServiceImpl implements PostService {

    @Resource
    private SectionService sectionService;

    @Resource
    private PostDAO postDAO;

    @Resource
    private RedisLockService redisLockService;

    @Resource
    private RedisService redisService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultVO<Void> postPublish(PostPublishDTO postPublishDTO) {

        String title = postPublishDTO.getTitle();
        Long sectionId = postPublishDTO.getSectionId();
        String token = postPublishDTO.getToken();
        String content = postPublishDTO.getContent();

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {
            return ResultUtil.getWarn("用户未登录！");
        }

        final String lockName = RedisKeyConstant.REDIS_POST_CREATE_LOCK_PREFIX + token;
        boolean tryLock = this.redisLockService.tryLock(lockName, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(3));
        if (!tryLock) {
            return ResultUtil.getWarn("发布帖子失败！");
        }
        try {

            // 防重校验
            boolean exists = this.redisService.exists(RedisKeyConstant.REDIS_POST_REPEAT + token, RedisDbConstant.REDIS_BIZ_CACHE);
            if (!exists) {
                return ResultUtil.getWarn("数据已过期，请重新进行提交！");
            }

            if (StringUtils.isBlank(title)) {
                return ResultUtil.getWarn("标题不能为空！");
            }
            if (sectionId == null || sectionId < 0) {
                return ResultUtil.getWarn("板块不能为空！");
            }
            if (StringUtils.length(title) > 32) {
                return ResultUtil.getWarn("标题字符最多32个字符！");
            }
            if (StringUtils.isBlank(content)) {
                return ResultUtil.getWarn("帖子内容不能为空！");
            }

            SectionVO sectionVO = this.sectionService.getSectionVoById(sectionId);
            if (sectionVO == null) {
                throw new IllegalArgumentException("栏目信息不存在！");
            }
            // 保存帖子
            PostModel postModel = new PostModel();
            postModel.setTitle(title);
            postModel.setContent(content);
            postModel.setIsActive(Boolean.TRUE);
            postModel.setUserId(current.getUserId());
            postModel.setSectionId(sectionId);
            postModel.setCreatedAt(new Date());
            postModel.setLastUpdatedAt(new Date());
            int rowCount = this.postDAO.insertSelective(postModel);

            // 删除防重令牌
            if (rowCount > 0) {
                this.redisService.del(RedisKeyConstant.REDIS_POST_REPEAT + token, RedisDbConstant.REDIS_BIZ_CACHE);
            }
        } finally {
            this.redisLockService.unlock(lockName);
        }

        return ResultUtil.getSuccess();
    }

    @Override
    public ResultVO<String> postToken() {

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {

            return ResultUtil.getWarn("用户未登录！");
        } else {

            String token = RandomStringUtils.randomAlphabetic(32);
            this.redisService.setString(RedisKeyConstant.REDIS_POST_REPEAT + token,
                    DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"), 60 * 30, RedisDbConstant.REDIS_BIZ_CACHE);
            return ResultUtil.getSuccess(String.class, token);
        }
    }
}
