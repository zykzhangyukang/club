package com.coderman.club.service.post.impl;

import com.coderman.club.constant.common.CommonConstant;
import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.constant.user.PostConst;
import com.coderman.club.dao.post.PostDAO;
import com.coderman.club.dao.post.PostLikeDAO;
import com.coderman.club.dao.post.PostTagDAO;
import com.coderman.club.dto.notification.NotifyMsgDTO;
import com.coderman.club.dto.post.PostPageDTO;
import com.coderman.club.dto.post.PostPublishDTO;
import com.coderman.club.dto.post.PostUpdateDTO;
import com.coderman.club.enums.FileModuleEnum;
import com.coderman.club.enums.NotificationTypeEnum;
import com.coderman.club.model.post.*;
import com.coderman.club.service.notification.NotificationService;
import com.coderman.club.service.post.PostService;
import com.coderman.club.service.redis.RedisLockService;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.service.section.SectionService;
import com.coderman.club.service.user.UserFollowingService;
import com.coderman.club.service.user.UserService;
import com.coderman.club.utils.*;
import com.coderman.club.vo.common.PageVO;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.post.PostDetailVO;
import com.coderman.club.vo.post.PostListItemVO;
import com.coderman.club.vo.section.SectionVO;
import com.coderman.club.vo.user.AuthUserVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
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
    private PostLikeDAO postLikeDAO;

    @Resource
    private PostTagDAO postTagDAO;

    @Resource
    private RedisLockService redisLockService;

    @Resource
    private UserFollowingService userFollowingService;

    @Resource
    private AliYunOssUtil aliYunOssUtil;

    @Resource
    private RedisService redisService;

    @Resource
    private NotificationService notificationService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultVO<Void> postPublish(PostPublishDTO postPublishDTO) {

        String title = postPublishDTO.getTitle();
        Long sectionId = postPublishDTO.getSectionId();
        String token = postPublishDTO.getToken();
        String content = postPublishDTO.getContent();
        List<String> tags = postPublishDTO.getTags();

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
            if (StringUtils.length(title) < 5) {
                return ResultUtil.getWarn("标题不能少于5个字符！");
            }
            if (StringUtils.length(title) > CommonConstant.LENGTH_128) {
                return ResultUtil.getWarn("标题字符最多128个字符！");
            }
            if (StringUtils.isBlank(content)) {
                return ResultUtil.getWarn("帖子内容不能为空！");
            }
            if (CollectionUtils.isNotEmpty(tags)) {

                // 标签去重
                tags = tags.stream().filter(StringUtils::isNotBlank).map(StringUtils::trim).distinct().collect(Collectors.toList());
                if (CollectionUtils.size(tags) > 5) {
                    return ResultUtil.getWarn("最多添加5个标签！");
                }

                for (String tag : tags) {
                    if (StringUtils.length(tag) > 20) {
                        return ResultUtil.getWarn(tag + "-标签不能超过20个字符！");
                    }
                }
            }

            SectionVO sectionVO = this.sectionService.getSectionVoById(sectionId);
            if (sectionVO == null) {
                throw new IllegalArgumentException("栏目信息不存在！");
            }

            // 保存帖子
            PostModel postModel = this.savePost(postPublishDTO);
            // 保存标签关联关系
            this.savePostTag(postModel, tags);

            // 删除防重令牌
            this.redisService.del(RedisKeyConstant.REDIS_POST_REPEAT + token, RedisDbConstant.REDIS_BIZ_CACHE);

        } finally {
            this.redisLockService.unlock(lockName);
        }

        return ResultUtil.getSuccess();
    }

    private PostModel savePost(PostPublishDTO dto) {

        AuthUserVO current = AuthUtil.getCurrent();
        assert current != null;
        Date currentTime = new Date();
        // 保存帖子
        PostModel postModel = new PostModel();
        postModel.setTitle(dto.getTitle());
        postModel.setContent(dto.getContent());
        postModel.setIsActive(Boolean.TRUE);
        postModel.setUserId(current.getUserId());
        postModel.setSectionId(dto.getSectionId());
        postModel.setCreatedAt(currentTime);
        postModel.setLastUpdatedAt(currentTime);
        postModel.setIsDraft(dto.getIsDraft() == null ? Boolean.FALSE : dto.getIsDraft());
        this.postDAO.insertSelectiveReturnKey(postModel);
        return postModel;
    }

    private void savePostTag(PostModel postModel, List<String> tagList) {
        if (postModel == null || CollectionUtils.isEmpty(tagList)) {
            return;
        }
        this.postTagDAO.insertBatch(postModel.getPostId(), tagList);
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
        long fileSizeInKb = fileSizeInBytes / 1024;
        long fileSizeInMb = fileSizeInKb / 1024;
        if (fileSizeInMb > 1) {
            throw new IllegalArgumentException("文件大小超过限制（最大限制为1MB）");
        }

        String url = this.aliYunOssUtil.genFilePath(file.getOriginalFilename(), FileModuleEnum.POST_MODULE);
        this.aliYunOssUtil.uploadStream(file.getInputStream(), url);

        return ResultUtil.getSuccess(String.class, CommonConstant.OSS_DOMAIN + url);
    }

    @Override
    public ResultVO<PageVO<List<PostListItemVO>>> postPage(PostPageDTO postPageDTO) {
        long currentPage = postPageDTO.getCurrentPage() != null && postPageDTO.getCurrentPage() > 0 ? postPageDTO.getCurrentPage() : 1L;
        long pageSize = postPageDTO.getPageSize() != null && postPageDTO.getPageSize() > 0 ? postPageDTO.getPageSize() : 20L;

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("limit", pageSize);
        conditionMap.put("offset", (currentPage - 1) * pageSize);

        if ((postPageDTO.getSecondSectionId() == null || postPageDTO.getSecondSectionId() < 0)
                && postPageDTO.getFirstSectionId() != null
                && postPageDTO.getFirstSectionId() > 0) {

            List<Long> sectionIdList = this.sectionService.getSectionVoByPid(postPageDTO.getFirstSectionId())
                    .stream()
                    .map(SectionVO::getSectionId)
                    .distinct()
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(sectionIdList)) {
                conditionMap.put("sectionIdList", sectionIdList);
            }
        } else if (postPageDTO.getSecondSectionId() != null && postPageDTO.getSecondSectionId() > 0) {
            conditionMap.put("sectionIdList", Collections.singletonList(postPageDTO.getSecondSectionId()));
        }

        List<PostListItemVO> postListItemVos = new ArrayList<>();
        Long count = this.postDAO.countPage(conditionMap);
        if (count > 0) {
            postListItemVos = this.postDAO.pageList(conditionMap);

            this.buildPostItems(postListItemVos);
        }

        return ResultUtil.getSuccessPage(PostListItemVO.class, new PageVO<>(count, postListItemVos, currentPage, pageSize));
    }

    @Override
    public ResultVO<PostDetailVO> postDetail(Long id) {

        if (id == null || id < 0) {
            return ResultUtil.getWarn("帖子不存在，请刷新重试！");
        }

        PostDetailVO postDetailVO = this.postDAO.selectPostDetailVoById(id);
        if (postDetailVO == null) {

            return ResultUtil.getWarn("帖子不存在，请刷新重试！");
        }

        //是否已关注当前发帖用户
        Boolean isFollowed = false;
        AuthUserVO current = AuthUtil.getCurrent();
        if (current != null) {
            isFollowed = this.userFollowingService.isFollowedUser(current.getUserId(), postDetailVO.getUserId());
        }
        postDetailVO.setIsFollowed(isFollowed);

        // 查询一级栏目数据
        Long pSectionId = postDetailVO.getParentSectionId();
        if (pSectionId != null) {
            SectionVO firstSection = this.sectionService.getSectionVoById(pSectionId);
            postDetailVO.setParentSectionId(firstSection.getSectionId());
            postDetailVO.setParentSectionName(firstSection.getSectionName());
        }

        // 设置标签
        PostTagExample example = new PostTagExample();
        example.createCriteria().andPostIdEqualTo(postDetailVO.getPostId());
        List<String> tags = this.postTagDAO.selectByExample(example).stream().map(PostTagModel::getTagName).distinct().collect(Collectors.toList());
        postDetailVO.setTags(tags);

        // 增加浏览量
        boolean result = this.addViewsCount(postDetailVO);
        if (BooleanUtils.isTrue(result)) {
            postDetailVO.setViewsCount(postDetailVO.getViewsCount() + 1);
        }

        return ResultUtil.getSuccess(PostDetailVO.class, postDetailVO);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultVO<Void> postUpdate(PostUpdateDTO postUpdateDTO) {

        Long postId = postUpdateDTO.getPostId();
        if (postId == null || postId < 0) {

            return ResultUtil.getWarn("参数错误！");
        }

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {
            return ResultUtil.getWarn("用户未登录！");
        }

        PostModel postModel = this.postDAO.selectUserPostById(current.getUserId(), postId);
        if (postModel == null) {
            return ResultUtil.getWarn("帖子不存在！");
        }

        if (StringUtils.isBlank(postUpdateDTO.getTitle())) {
            return ResultUtil.getWarn("标题不能为空！");
        }
        if (postUpdateDTO.getSectionId() == null || postUpdateDTO.getSectionId() < 0) {
            return ResultUtil.getWarn("板块不能为空！");
        }
        if (StringUtils.length(postUpdateDTO.getTitle()) < 5) {
            return ResultUtil.getWarn("标题不能少于5个字符！");
        }
        if (StringUtils.length(postUpdateDTO.getTitle()) > CommonConstant.LENGTH_128) {
            return ResultUtil.getWarn("标题字符最多128个字符！");
        }
        if (StringUtils.isBlank(postUpdateDTO.getContent())) {
            return ResultUtil.getWarn("帖子内容不能为空！");
        }
        List<String> tags = postUpdateDTO.getTags();
        if (CollectionUtils.isNotEmpty(tags)) {

            // 标签去重
            tags = tags.stream().filter(StringUtils::isNotBlank).map(StringUtils::trim).distinct().collect(Collectors.toList());
            if (CollectionUtils.size(postUpdateDTO.getTags()) > 5) {
                return ResultUtil.getWarn("最多添加5个标签！");
            }

            for (String tag : postUpdateDTO.getTags()) {
                if (StringUtils.length(tag) > 20) {
                    return ResultUtil.getWarn(tag + "-标签不能超过20个字符！");
                }
            }
        }

        SectionVO sectionVO = this.sectionService.getSectionVoById(postUpdateDTO.getSectionId());
        if (sectionVO == null) {
            throw new IllegalArgumentException("栏目信息不存在！");
        }

        // 更新字段
        PostModel updateModel = new PostModel();
        updateModel.setPostId(postId);
        updateModel.setTitle(postUpdateDTO.getTitle());
        updateModel.setContent(postUpdateDTO.getContent());
        updateModel.setLastUpdatedAt(new Date());
        updateModel.setSectionId(postUpdateDTO.getSectionId());
        updateModel.setIsDraft(postUpdateDTO.getIsDraft() == null ? Boolean.FALSE : postUpdateDTO.getIsDraft());
        this.postDAO.updatePostWithContentById(updateModel);

        // 贴子标签更新
        this.updatePostTag(postUpdateDTO, tags);

        return ResultUtil.getSuccess();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultVO<Void> postLike(Long postId) {

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {
            return ResultUtil.getWarn("用户未登录！");
        }

        if (postId == null || postId < 0) {
            return ResultUtil.getWarn("帖子不存在，请刷新重试！");
        }

        PostDetailVO postDetailVO = this.postDAO.selectPostDetailVoById(postId);
        if (postDetailVO == null) {
            return ResultUtil.getWarn("帖子不存在，请刷新重试！");
        }

        int rowCount;
        boolean isFirstLike = false;

        final String lockName = RedisKeyConstant.REDIS_POST_LIKE_LOCK_PREFIX + current.getUserId() + ":" + postId;
        boolean tryLock = this.redisLockService.tryLock(lockName, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(3));
        if (!tryLock) {
            return ResultUtil.getWarn("请勿重复操作！");
        }

        try {

            // 判断之前是否有点赞过
            PostLikeExample example = new PostLikeExample();
            example.createCriteria().andPostIdEqualTo(postId).andUserIdEqualTo(current.getUserId());
            List<PostLikeModel> postLikeModels = this.postLikeDAO.selectByExample(example);

            if (CollectionUtils.isEmpty(postLikeModels)) {

                PostLikeModel insertModel = new PostLikeModel();
                insertModel.setUserId(current.getUserId());
                insertModel.setPostId(postId);
                insertModel.setCreateTime(new Date());
                insertModel.setStatus(PostConst.LIKE_STATUS_NORMAL);
                rowCount = this.postLikeDAO.insertSelective(insertModel);
                isFirstLike = true;

            } else {

                PostLikeModel postLikeModel = postLikeModels.get(0);
                if (StringUtils.equals(postLikeModel.getStatus(), PostConst.LIKE_STATUS_NORMAL)) {

                    return ResultUtil.getWarn("请勿重复点赞！");
                }

                PostLikeModel updateModel = new PostLikeModel();
                updateModel.setPostLikeId(postLikeModel.getPostId());
                updateModel.setStatus(PostConst.LIKE_STATUS_NORMAL);
                rowCount = this.postLikeDAO.updateByPrimaryKeySelective(updateModel);
            }

            // 首次点赞消息通知 (点赞自己的帖子不通知)
            if (isFirstLike && rowCount > 0 && !Objects.equals(current.getUserId(), postDetailVO.getUserId())) {

                NotifyMsgDTO notifyMsgDTO = NotifyMsgDTO.builder()
                        .senderId(0L)
                        .userIdList(Collections.singletonList(postDetailVO.getUserId()))
                        .typeEnum(NotificationTypeEnum.LIKE_POST)
                        .content(String.format(NotificationTypeEnum.LIKE_POST.getTemplate(), current.getNickname(), postDetailVO.getTitle()))
                        .build();
                this.notificationService.saveAndNotify(notifyMsgDTO);
            }

            // 维护帖子点赞数
            if (rowCount > 0) {
                this.postDAO.addLikesCount(postId, 1);
            }

        } finally {

            this.redisLockService.unlock(lockName);
        }

        return ResultUtil.getSuccess();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultVO<Void> postUnLike(Long postId) {

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {

            return ResultUtil.getWarn("用户未登录~");
        }

        if (postId == null || postId < 0) {
            return ResultUtil.getWarn("帖子不存在，请刷新重试！");
        }

        PostDetailVO postDetailVO = this.postDAO.selectPostDetailVoById(postId);
        if (postDetailVO == null) {
            return ResultUtil.getWarn("帖子不存在，请刷新重试！");
        }

        final String lockName = RedisKeyConstant.REDIS_POST_UNLIKE_LOCK_PREFIX + current.getUserId() + ":" + postId;
        boolean tryLock = this.redisLockService.tryLock(lockName, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(3));
        if (!tryLock) {
            return ResultUtil.getWarn("请勿重复操作！");
        }

        try {

            // 在club_post_like一定有数据
            PostLikeExample example = new PostLikeExample();
            example.createCriteria().andPostIdEqualTo(postId).andUserIdEqualTo(current.getUserId());
            List<PostLikeModel> postLikeModels = this.postLikeDAO.selectByExample(example);

            if (CollectionUtils.isNotEmpty(postLikeModels)) {
                PostLikeModel postLikeModel = postLikeModels.get(0);
                if (StringUtils.equals(postLikeModel.getStatus(), PostConst.LIKE_STATUS_CANCEL)) {

                    return ResultUtil.getWarn("请勿重复取消点赞！");
                }

                PostLikeModel updateModel = new PostLikeModel();
                updateModel.setPostLikeId(postLikeModel.getPostLikeId());
                updateModel.setStatus(PostConst.LIKE_STATUS_CANCEL);
                this.postLikeDAO.updateByPrimaryKeySelective(updateModel);

                // 点赞数量-1
                this.postDAO.addLikesCount(postId, -1);
            }

        } finally {

            this.redisLockService.unlock(lockName);
        }

        return ResultUtil.getSuccess();
    }

    @Override
    public ResultVO<Void> postDelete(Long postId) {

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {

            return ResultUtil.getWarn("用户未登录！");
        }

        if (postId == null || postId < 0) {
            return ResultUtil.getWarn("参数错误！");
        }

        PostModel postModel = this.postDAO.selectUserPostById(current.getUserId(), postId);
        if (postModel == null) {

            return ResultUtil.getWarn("帖子不存在或已被删除！");
        }

        PostModel updateModel = new PostModel();
        updateModel.setPostId(postId);
        updateModel.setIsActive(Boolean.FALSE);
        updateModel.setLastUpdatedAt(new Date());
        this.postDAO.updateByPrimaryKeySelective(updateModel);

        return ResultUtil.getSuccess();
    }


    private void updatePostTag(PostUpdateDTO postUpdateDTO, List<String> tags) {

        if (postUpdateDTO == null) {
            return;
        }

        // 先把之前的标签删掉
        PostTagExample example = new PostTagExample();
        example.createCriteria().andPostIdEqualTo(postUpdateDTO.getPostId());
        this.postTagDAO.deleteByExample(example);

        if (CollectionUtils.isNotEmpty(tags)) {

            this.postTagDAO.insertBatch(postUpdateDTO.getPostId(), tags);
        }
    }

    private boolean addViewsCount(PostDetailVO postDetailVO) {

        String redisLimitKey;

        AuthUserVO current = AuthUtil.getCurrent();
        if (current != null) {
            redisLimitKey = RedisKeyConstant.POST_VIEWS_LIMIT_PREFIX + postDetailVO.getPostId() + ":" + current.getUserId();
        } else {

            String ip = IpUtil.getIp(HttpContextUtil.getHttpServletRequest());
            redisLimitKey = RedisKeyConstant.POST_VIEWS_LIMIT_PREFIX + postDetailVO.getPostId() + ":" + ip;
        }

        // 判断redis中是否存在值，存在说明近1分钟有浏览过, 不存在则需要增加浏览量
        boolean exists = this.redisService.exists(redisLimitKey, RedisDbConstant.REDIS_BIZ_CACHE);
        if (!exists) {
            this.postDAO.addViewsCount(postDetailVO.getPostId(), 1);
            this.redisService.setString(redisLimitKey, "1", 60, RedisDbConstant.REDIS_BIZ_CACHE);
            return true;
        }

        return false;
    }

    private void buildPostItems(List<PostListItemVO> postListItemVos) {
        if (CollectionUtils.isEmpty(postListItemVos)) {
            return;
        }
        List<Long> postIdList = postListItemVos.stream().map(PostListItemVO::getPostId).distinct().collect(Collectors.toList());
        PostTagExample example = new PostTagExample();
        example.createCriteria().andPostIdIn(postIdList);
        final Map<Long, List<PostTagModel>> tagMap = this.postTagDAO.selectByExample(example).stream()
                .collect(Collectors.groupingBy(PostTagModel::getPostId));

        for (PostListItemVO postListItemVo : postListItemVos) {

            List<PostTagModel> postTagModels = tagMap.get(postListItemVo.getPostId());

            if (CollectionUtils.isNotEmpty(postTagModels)) {
                // 帖子标签
                List<String> tagNames = postTagModels.stream().distinct().map(PostTagModel::getTagName).collect(Collectors.toList());
                postListItemVo.setTags(tagNames);
            }
        }
    }
}
