package com.coderman.club.service.post.impl;

import com.coderman.club.constant.common.CommonConstant;
import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.dao.post.PostDAO;
import com.coderman.club.dto.post.PostPageDTO;
import com.coderman.club.dto.post.PostPublishDTO;
import com.coderman.club.enums.FileModuleEnum;
import com.coderman.club.model.post.PostModel;
import com.coderman.club.service.post.PostService;
import com.coderman.club.service.redis.RedisLockService;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.service.section.SectionService;
import com.coderman.club.utils.AliYunOssUtil;
import com.coderman.club.utils.AuthUtil;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.vo.common.PageVO;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.post.PostListItemVO;
import com.coderman.club.vo.section.SectionVO;
import com.coderman.club.vo.user.AuthUserVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private AliYunOssUtil aliYunOssUtil;

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
            if (StringUtils.length(title) > CommonConstant.LENGTH_128) {
                return ResultUtil.getWarn("标题字符最多128个字符！");
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

    @Override
    public ResultVO<String> uploadImage(MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ResultUtil.getWarn("文件不能为空");
        }
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new IllegalArgumentException("请选择图片文件上传");
        }

        long fileSizeInBytes = file.getSize();
        long fileSizeInKB = fileSizeInBytes / 1024;
        long fileSizeInMB = fileSizeInKB / 1024;
        if (fileSizeInMB > 1) {
            throw new IllegalArgumentException("文件大小超过限制（最大限制为1MB）");
        }

        String url = this.aliYunOssUtil.genFilePath(file.getOriginalFilename(), FileModuleEnum.POST_MODULE);
        this.aliYunOssUtil.uploadStream(file.getInputStream(), url);

        return ResultUtil.getSuccess(String.class, CommonConstant.OSS_DOMAIN + url);
    }

    @Override
    public ResultVO<PageVO<List<PostListItemVO>>> postPage(PostPageDTO postPageDTO) {

        Long currentPage = postPageDTO.getCurrentPage();
        Long pageSize = postPageDTO.getPageSize();
        Long firstSectionId = postPageDTO.getFirstSectionId();
        Long secondSectionId = postPageDTO.getSecondSectionId();

        if (currentPage == null || currentPage < 1) {
            currentPage = 1L;
        }
        if (pageSize == null || currentPage > 30) {
            pageSize = 30L;
        }

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("limit", pageSize);
        conditionMap.put("offset", (currentPage - 1) * pageSize);

        // 只带了筛选一级分类, 查询对应的所有二级分类id
        List<Long> sectionIdList = new ArrayList<>();
        if (firstSectionId != null && firstSectionId > 0) {
            sectionIdList = this.sectionService.getSectionVoByPid(firstSectionId)
                    .stream().map(SectionVO::getSectionId)
                    .distinct().collect(Collectors.toList());
        } else if ((firstSectionId == null || firstSectionId < 0) && (secondSectionId != null && secondSectionId > 0)) {
            sectionIdList = Collections.singletonList(secondSectionId);
        }
        if (CollectionUtils.isNotEmpty(sectionIdList)) {
            conditionMap.put("sectionIdList", sectionIdList);
        }

        List<PostListItemVO> postListItemVos = new ArrayList<>();
        Long count = this.postDAO.countPage(conditionMap);
        if (count > 0) {

            postListItemVos = this.postDAO.pageList(conditionMap);
        }

        return ResultUtil.getSuccessPage(PostListItemVO.class, new PageVO<>(count, postListItemVos, currentPage, pageSize));
    }
}
