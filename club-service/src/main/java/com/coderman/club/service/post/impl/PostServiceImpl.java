package com.coderman.club.service.post.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coderman.club.constant.common.CommonConstant;
import com.coderman.club.constant.common.ResultConstant;
import com.coderman.club.constant.post.PostConstant;
import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.constant.user.CommonConst;
import com.coderman.club.dto.notification.NotifyMsgDTO;
import com.coderman.club.dto.post.*;
import com.coderman.club.enums.FileModuleEnum;
import com.coderman.club.enums.NotificationTypeEnum;
import com.coderman.club.exception.BusinessException;
import com.coderman.club.mapper.post.*;
import com.coderman.club.model.post.*;
import com.coderman.club.model.user.UserModel;
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
import com.coderman.club.vo.post.*;
import com.coderman.club.vo.section.SectionVO;
import com.coderman.club.vo.user.AuthUserVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
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
    private PostMapper postMapper;
    @Resource
    private PostCommentMapper postCommentMapper;
    @Resource
    private UserService userService;
    @Resource
    private PostLikeMapper postLikeMapper;
    @Resource
    private PostTagMapper postTagMapper;
    @Resource
    private PostCollectMapper postCollectMapper;
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

        // 防重校验
        boolean exists = this.redisService.exists(RedisKeyConstant.REDIS_POST_REPEAT + token, RedisDbConstant.REDIS_BIZ_CACHE);
        if (!exists) {
            return ResultUtil.getWarn("Token已过期，请重新提交！");
        }

        try {

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
            Long postId = this.saveOrUpdate(postPublishDTO);

            // 保存标签关联关系
            this.savePostTag(postId, tags);

            // 删除防重令牌
            this.redisService.del(RedisKeyConstant.REDIS_POST_REPEAT + token, RedisDbConstant.REDIS_BIZ_CACHE);

        } finally {
            this.redisLockService.unlock(lockName);
        }

        return ResultUtil.getSuccess();
    }

    private Long saveOrUpdate(PostPublishDTO dto) {

        AuthUserVO current = AuthUtil.getCurrent();
        assert current != null;
        Date currentTime = new Date();

        PostModel postModel = new PostModel();
        postModel.setTitle(dto.getTitle());
        postModel.setContent(dto.getContent());
        postModel.setIsActive(Boolean.TRUE);
        postModel.setUserId(current.getUserId());
        postModel.setSectionId(dto.getSectionId());
        postModel.setIsDraft(dto.getIsDraft() == null ? Boolean.FALSE : dto.getIsDraft());

        if (dto.getPostId() == null) {

            // 保存帖子
            postModel.setCreatedAt(currentTime);
            this.postMapper.insert(postModel);
        } else {

            // 更新的时候校验
            PostModel model = this.postMapper.selectById(dto.getPostId());
            if (!Objects.equals(current.getUserId(), model.getUserId())) {
                throw new BusinessException("禁止越权访问！");
            }

            postModel.setPostId(dto.getPostId());
            postModel.setLastUpdatedAt(currentTime);
            this.postMapper.updateById(postModel);
        }

        return postModel.getPostId();
    }

    private void savePostTag(Long postId, List<String> tagList) {

        // 如果是更新
        if (postId != null) {

            this.postTagMapper.delete(Wrappers.<PostTagModel>lambdaQuery().eq(PostTagModel::getPostId, postId));
        }

        if (CollectionUtils.isNotEmpty(tagList)) {
            this.postTagMapper.insertBatch(postId, tagList);
        }
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
        long pageSize = postPageDTO.getPageSize() != null && postPageDTO.getPageSize() > 0 ? postPageDTO.getPageSize() : 30L;

        if (pageSize > 50L) {
            pageSize = 50L;
        }

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
        Long count = this.postMapper.countPage(conditionMap);
        if (count > 0) {
            postListItemVos = this.postMapper.pageList(conditionMap);

            this.buildPostItems(postListItemVos);
        }

        return ResultUtil.getSuccessPage(PostListItemVO.class, new PageVO<>(count, postListItemVos, currentPage, pageSize));
    }

    @Override
    public ResultVO<PostDetailVO> getPostDetail(String idStr) {

        if (!NumberUtils.isDigits(idStr)) {
            return ResultUtil.getWarn("抱歉，该帖子不存在！");
        }

        long id = 0;
        try {
            id = Long.parseLong(idStr);
        }catch (Exception ignored){
        }

        if (id <= 0) {
            return ResultUtil.getWarn("抱歉，该帖子不存在！");
        }

        PostDetailVO postDetailVO = this.postMapper.selectPostDetailVoById(id);
        if (postDetailVO == null) {

            return ResultUtil.getWarn("抱歉，该帖子不存在！");
        }

        //是否关注用户
        boolean isFollowed = false;
        AuthUserVO current = AuthUtil.getCurrent();
        if (current != null) {
            isFollowed = this.userFollowingService.isFollowedUser(current.getUserId(), postDetailVO.getUserId());
        }
        postDetailVO.setIsFollowed(isFollowed);

        // 是否点赞帖子
        boolean isLiked = false;
        if (current != null) {
            isLiked = this.isLikedPost(current.getUserId(), id);
        }
        postDetailVO.setIsLiked(isLiked);

        // 是否收藏帖子
        boolean isCollected = false;
        if (current != null) {
            isCollected = this.isCollectedPost(current.getUserId(), id);
        }
        postDetailVO.setIsCollected(isCollected);

        // 设置标签
        List<String> tags = this.postTagMapper.selectList(Wrappers.<PostTagModel>lambdaQuery()
                .eq(PostTagModel::getPostId, postDetailVO.getPostId())).stream().map(PostTagModel::getTagName).distinct().collect(Collectors.toList());
        postDetailVO.setTags(tags);

        // 增加浏览量
        boolean result = this.addViewsCount(postDetailVO);
        if (BooleanUtils.isTrue(result)) {
            postDetailVO.setViewsCount(postDetailVO.getViewsCount() + 1);
        }

        return ResultUtil.getSuccess(PostDetailVO.class, postDetailVO);
    }

    /**
     * 获取帖子评论
     *
     * @return
     */
    @Override
    public ResultVO<PageVO<List<PostCommentVO>>> getCommentPage(PostCommentPageDTO pageDTO) {
        Integer pageSize = pageDTO.getPageSize();
        Integer currentPage = pageDTO.getCurrentPage();

        String seekCid = pageDTO.getSeekCid();

        List<Long> orderByComments = Lists.newArrayList();
        List<Long> orderByReplies = Lists.newArrayList();
        if (StringUtils.isNotBlank(seekCid) && NumberUtils.isDigits(seekCid)) {
            PostCommentModel seek = this.selectCommentById(Long.parseLong(seekCid));
            if (seek != null && StringUtils.equals(seek.getType(), PostConstant.COMMENT_TYPE)) {
                orderByComments.add(seek.getCommentId());
            } else if (seek != null && StringUtils.equals(seek.getType(), PostConstant.REPLY_TYPE)) {
                orderByComments.add(seek.getParentId());
                orderByReplies.add(seek.getCommentId());
            } else if (seek != null && StringUtils.equals(seek.getType(), PostConstant.REPLY_AT_TYPE)) {
                orderByComments.add(seek.getParentId());
                orderByReplies.add(seek.getCommentId());
            }
        }

        // 设置根评论排序
        pageDTO.setOrderByComments(orderByComments);

        // 获取帖子下所有的根评论信息
        PageHelper.startPage(currentPage, pageSize);
        List<PostCommentVO> list = this.postCommentMapper.selectRootCommentVos(pageDTO);
        PageInfo<PostCommentVO> pageInfo = new PageInfo<>(list);
        List<PostCommentVO> rootComments = pageInfo.getList().stream().peek(e -> e.setReplies(Lists.newArrayList())).collect(Collectors.toList());


        // 获取每个根评论的前三条子评论
        List<Long> parentIds = rootComments.stream().map(PostCommentVO::getCommentId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(parentIds)) {

            List<PostReplyVO> topReplies = this.postCommentMapper.getTopRepliesForComments(parentIds, orderByReplies);

            for (PostReplyVO reply : topReplies) {
                rootComments.stream()
                        .filter(root -> Objects.equals(reply.getParentId(), root.getCommentId()))
                        .findFirst()
                        .ifPresent(root -> root.getReplies().add(reply));
            }
        }

        return ResultUtil.getSuccessPage(PostCommentVO.class, new PageVO<>(pageInfo.getTotal(), rootComments, currentPage, pageSize));
    }




    private boolean isCollectedPost(Long userId, long postId) {
        return this.postCollectMapper.selectCount(Wrappers.<PostCollectModel>lambdaQuery()
                .eq(PostCollectModel::getUserId, userId)
                .eq(PostCollectModel::getStatus, CommonConst.STATUS_NORMAL)
                .eq(PostCollectModel::getPostId, postId)
        ) > 0;
    }

    private boolean isLikedPost(Long userId, long postId) {
        return this.postLikeMapper.selectCount(Wrappers.<PostLikeModel>lambdaQuery()
                .eq(PostLikeModel::getUserId, userId)
                .eq(PostLikeModel::getStatus, CommonConst.STATUS_NORMAL)
                .eq(PostLikeModel::getPostId, postId)
        ) > 0;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultVO<Void> postUpdate(PostUpdateDTO postUpdateDTO) {
        Long postId = postUpdateDTO.getPostId();
        if (postId == null || postId < 0) {
            return ResultUtil.getWarn("无效的帖子ID！");
        }

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {
            return ResultUtil.getWarn("请先登录！");
        }

        PostModel postModel = this.postMapper.selectUserPostById(current.getUserId(), postId);
        if (postModel == null) {
            return ResultUtil.getWarn("帖子不存在或无权编辑！");
        }

        String title = postUpdateDTO.getTitle();
        if (StringUtils.isBlank(title)) {
            return ResultUtil.getWarn("标题不能为空！");
        }
        if (postUpdateDTO.getSectionId() == null || postUpdateDTO.getSectionId() < 0) {
            return ResultUtil.getWarn("请选择有效的板块！");
        }
        if (StringUtils.length(title) < 5) {
            return ResultUtil.getWarn("标题不能少于5个字符！");
        }
        if (StringUtils.length(title) > CommonConstant.LENGTH_128) {
            return ResultUtil.getWarn("标题不能超过128个字符！");
        }
        if (StringUtils.isBlank(postUpdateDTO.getContent())) {
            return ResultUtil.getWarn("帖子内容不能为空！");
        }

        List<String> tags = postUpdateDTO.getTags();
        if (CollectionUtils.isNotEmpty(tags)) {
            // 去除空白和重复的标签
            tags = tags.stream()
                    .filter(StringUtils::isNotBlank)
                    .map(StringUtils::trim)
                    .distinct()
                    .collect(Collectors.toList());

            if (tags.size() > 5) {
                return ResultUtil.getWarn("最多添加5个标签！");
            }

            for (String tag : tags) {
                if (StringUtils.length(tag) > 20) {
                    return ResultUtil.getWarn("标签“" + tag + "”不能超过20个字符！");
                }
            }
        }

        SectionVO sectionVO = this.sectionService.getSectionVoById(postUpdateDTO.getSectionId());
        if (sectionVO == null) {
            throw new IllegalArgumentException("所选板块不存在！");
        }

        // 更新帖子内容
        PostModel updateModel = new PostModel();
        updateModel.setPostId(postId);
        updateModel.setTitle(title);
        updateModel.setContent(postUpdateDTO.getContent());
        updateModel.setLastUpdatedAt(new Date());
        updateModel.setSectionId(postUpdateDTO.getSectionId());
        updateModel.setIsDraft(postUpdateDTO.getIsDraft() != null ? postUpdateDTO.getIsDraft() : Boolean.FALSE);

        this.postMapper.updatePostWithContentById(updateModel);

        // 更新标签
        this.updatePostTag(postUpdateDTO, tags);

        return ResultUtil.getSuccess("帖子更新成功！");
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultVO<Void> postLike(Long postId) {

        ResultVO<PostDetailVO> check = this.commonCheck(postId);
        if (!ResultConstant.RESULT_CODE_200.equals(check.getCode())) {
            return ResultUtil.getFail(check.getMsg());
        }

        PostDetailVO postDetailVO = check.getResult();
        AuthUserVO current = AuthUtil.getCurrent();

        int rowCount;
        boolean isFirstLike = false;

        final String lockName = RedisKeyConstant.REDIS_POST_LIKE_LOCK_PREFIX + current.getUserId() + ":" + postId;
        boolean tryLock = this.redisLockService.tryLock(lockName, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(3));
        if (!tryLock) {
            return ResultUtil.getWarn("请勿重复操作！");
        }

        try {

            // 判断之前是否有点赞过
            PostLikeModel postLikeModel = this.postLikeMapper.selectOne(Wrappers.<PostLikeModel>lambdaQuery()
                    .eq(PostLikeModel::getPostId, postId)
                    .eq(PostLikeModel::getUserId, current.getUserId())
                    .last("limit 1")
            );

            if (postLikeModel == null) {

                PostLikeModel insertModel = new PostLikeModel();
                insertModel.setUserId(current.getUserId());
                insertModel.setPostId(postId);
                insertModel.setCreateTime(new Date());
                insertModel.setStatus(CommonConst.STATUS_NORMAL);
                rowCount = this.postLikeMapper.insert(insertModel);
                isFirstLike = true;

            } else {

                if (StringUtils.equals(postLikeModel.getStatus(), CommonConst.STATUS_NORMAL)) {
                    return ResultUtil.getWarn("操作失败。");
                }

                PostLikeModel updateModel = new PostLikeModel();
                updateModel.setPostLikeId(postLikeModel.getPostLikeId());
                updateModel.setStatus(CommonConst.STATUS_NORMAL);
                rowCount = this.postLikeMapper.updateById(updateModel);
            }

            // 首次点赞消息通知 (点赞自己的帖子不通知)
            if (isFirstLike && rowCount > 0 && !Objects.equals(current.getUserId(), postDetailVO.getUserId())) {

                NotifyMsgDTO notifyMsgDTO = NotifyMsgDTO.builder()
                        .senderId(current.getUserId())
                        .userIdList(Collections.singletonList(postDetailVO.getUserId()))
                        .typeEnum(NotificationTypeEnum.LIKE_POST)
                        .content(String.format(NotificationTypeEnum.LIKE_POST.getTemplate(), current.getNickname(), postDetailVO.getTitle()))
                        .relationId(postId)
                        .build();
                this.notificationService.send(notifyMsgDTO);
            }

            // 维护帖子点赞数
            if (rowCount > 0) {
                this.postMapper.addLikesCount(postId, 1);
            }

        } finally {

            this.redisLockService.unlock(lockName);
        }

        return ResultUtil.getSuccess();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultVO<Void> postUnLike(Long postId) {

        ResultVO<PostDetailVO> check = this.commonCheck(postId);
        if (!ResultConstant.RESULT_CODE_200.equals(check.getCode())) {
            return ResultUtil.getFail(check.getMsg());
        }

        AuthUserVO current = AuthUtil.getCurrent();

        final String lockName = RedisKeyConstant.REDIS_POST_UNLIKE_LOCK_PREFIX + current.getUserId() + ":" + postId;
        boolean tryLock = this.redisLockService.tryLock(lockName, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(3));
        if (!tryLock) {
            return ResultUtil.getWarn("操作频繁，请稍后重试。");
        }

        try {
            // 查询用户是否已点赞该帖子
            PostLikeModel postLikeModel = this.postLikeMapper.selectOne(Wrappers.<PostLikeModel>lambdaQuery()
                    .eq(PostLikeModel::getPostId, postId)
                    .eq(PostLikeModel::getUserId, current.getUserId())
                    .last("limit 1")
            );

            if (postLikeModel != null) {
                // 检查点赞状态
                if (StringUtils.equals(postLikeModel.getStatus(), CommonConst.STATUS_CANCEL)) {
                    return ResultUtil.getWarn("操作失败。");
                }

                // 更新点赞状态为取消
                PostLikeModel updateModel = new PostLikeModel();
                updateModel.setPostLikeId(postLikeModel.getPostLikeId());
                updateModel.setStatus(CommonConst.STATUS_CANCEL);
                this.postLikeMapper.updateById(updateModel);

                // 更新帖子的点赞数量
                this.postMapper.addLikesCount(postId, -1);
            }
        } finally {
            // 释放锁
            this.redisLockService.unlock(lockName);
        }

        return ResultUtil.getSuccess("取消点赞成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<Void> postDelete(Long postId) {

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {

            return ResultUtil.getWarn("用户未登录！");
        }

        if (postId == null || postId < 0) {
            return ResultUtil.getWarn("参数错误！");
        }

        PostModel postModel = this.postMapper.selectUserPostById(current.getUserId(), postId);
        if (postModel == null) {

            return ResultUtil.getWarn("帖子不存在或已被删除！");
        }

        PostModel updateModel = new PostModel();
        updateModel.setPostId(postId);
        updateModel.setIsActive(Boolean.FALSE);
        updateModel.setLastUpdatedAt(new Date());
        this.postMapper.updateById(updateModel);

        return ResultUtil.getSuccess();
    }

    private ResultVO<PostDetailVO> commonCheck(Long postId) {

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {
            return ResultUtil.getWarn("用户未登录！");
        }

        if (postId == null || postId < 0) {
            return ResultUtil.getWarn("帖子不存在，请刷新重试！");
        }
        PostDetailVO postDetailVO = this.postMapper.selectPostDetailVoById(postId);
        if (postDetailVO == null) {
            return ResultUtil.getWarn("帖子不存在，请刷新重试！");
        }
        return ResultUtil.getSuccess(PostDetailVO.class, postDetailVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<Void> postCollect(Long postId) {

        ResultVO<PostDetailVO> check = this.commonCheck(postId);
        if (!ResultConstant.RESULT_CODE_200.equals(check.getCode())) {
            return ResultUtil.getFail(check.getMsg());
        }

        AuthUserVO current = AuthUtil.getCurrent();
        PostDetailVO postDetailVO = check.getResult();

        int rowCount;
        boolean isFirstCollect = false;

        final String lockName = RedisKeyConstant.REDIS_POST_COLLECT_LOCK_PREFIX + current.getUserId() + ":" + postId;
        boolean tryLock = this.redisLockService.tryLock(lockName, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(3));
        if (!tryLock) {
            return ResultUtil.getWarn("请勿重复操作！");
        }

        try {

            // 判断之前是否有收藏过
            PostCollectModel postCollectModel = this.postCollectMapper.selectOne(Wrappers.<PostCollectModel>lambdaQuery()
                    .eq(PostCollectModel::getPostId, postId)
                    .eq(PostCollectModel::getUserId, current.getUserId())
                    .last("limit 1")
            );

            if (postCollectModel == null) {

                PostCollectModel insertModel = new PostCollectModel();
                insertModel.setUserId(current.getUserId());
                insertModel.setPostId(postId);
                insertModel.setCreateTime(new Date());
                insertModel.setStatus(CommonConst.STATUS_NORMAL);
                rowCount = this.postCollectMapper.insert(insertModel);
                isFirstCollect = true;

            } else {

                if (StringUtils.equals(postCollectModel.getStatus(), CommonConst.STATUS_NORMAL)) {
                    return ResultUtil.getWarn("操作失败。");
                }

                PostCollectModel updateModel = new PostCollectModel();
                updateModel.setPostCollectId(postCollectModel.getPostCollectId());
                updateModel.setStatus(CommonConst.STATUS_NORMAL);
                rowCount = this.postCollectMapper.updateById(updateModel);
            }

            // 首次收藏消息通知 (点赞自己的帖子不通知)
            if (isFirstCollect && rowCount > 0 && !Objects.equals(current.getUserId(), postDetailVO.getUserId())) {

                NotifyMsgDTO notifyMsgDTO = NotifyMsgDTO.builder()
                        .senderId(0L)
                        .userIdList(Collections.singletonList(postDetailVO.getUserId()))
                        .typeEnum(NotificationTypeEnum.COLLECT_POST)
                        .content(String.format(NotificationTypeEnum.COLLECT_POST.getTemplate(), current.getNickname(), postDetailVO.getTitle()))
                        .relationId(postId)
                        .build();
                this.notificationService.send(notifyMsgDTO);
            }

            // 维护帖子收藏数
            if (rowCount > 0) {
                this.postMapper.addCollectsCount(postId, 1);
            }

        } finally {

            this.redisLockService.unlock(lockName);
        }

        return ResultUtil.getSuccess();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<Void> postUnCollect(Long postId) {

        ResultVO<PostDetailVO> check = this.commonCheck(postId);
        if (!ResultConstant.RESULT_CODE_200.equals(check.getCode())) {

            return ResultUtil.getFail(check.getMsg());
        }

        AuthUserVO current = AuthUtil.getCurrent();

        final String lockName = RedisKeyConstant.REDIS_POST_UNCOLLECT_LOCK_PREFIX + current.getUserId() + ":" + postId;
        boolean tryLock = this.redisLockService.tryLock(lockName, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(4));
        if (!tryLock) {

            return ResultUtil.getWarn("操作频繁，请稍后重试。");
        }

        try {
            // 查询用户是否已收藏该帖子
            PostCollectModel postCollectModel = this.postCollectMapper.selectOne(Wrappers.<PostCollectModel>lambdaQuery()
                    .eq(PostCollectModel::getPostId, postId)
                    .eq(PostCollectModel::getUserId, current.getUserId())
                    .last("limit 1")
            );

            if (postCollectModel != null) {
                // 检查收藏状态
                if (StringUtils.equals(postCollectModel.getStatus(), CommonConst.STATUS_CANCEL)) {
                    return ResultUtil.getWarn("操作失败。");
                }

                // 更新点赞状态为取消
                PostCollectModel updateModel = new PostCollectModel();
                updateModel.setPostCollectId(postCollectModel.getPostCollectId());
                updateModel.setStatus(CommonConst.STATUS_CANCEL);
                this.postCollectMapper.updateById(updateModel);

                // 更新帖子的收藏数量
                this.postMapper.addCollectsCount(postId, -1);
            }
        } finally {
            // 释放锁
            this.redisLockService.unlock(lockName);
        }

        return ResultUtil.getSuccess("取消收藏成功");
    }

    @Override
    public Integer getCollectCountByUserId(Long userId) {

        if (userId == null) {
            return 0;
        }

        return this.postCollectMapper.selectCount(Wrappers.<PostCollectModel>lambdaQuery()
                .eq(PostCollectModel::getUserId, userId)
                .eq(PostCollectModel::getStatus, CommonConst.STATUS_NORMAL));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<PostCommentVO> postComment(PostCommentDTO postCommentDTO) {

        String content = postCommentDTO.getContent();
        Long postId = postCommentDTO.getPostId();
        Long parentId = Optional.ofNullable(postCommentDTO.getParentId()).orElse(0L);
        Long replyId = Optional.ofNullable(postCommentDTO.getReplyId()).orElse(0L);

        if (StringUtils.isBlank(content)) {
            return ResultUtil.getWarn("内容不能为空！");
        }
        if (StringUtils.length(content) > CommonConstant.LENGTH_512) {
            return ResultUtil.getWarn("内容不超过512个字符！");
        }

        PostModel postModel = this.selectPostById(postId);
        if (postModel == null) {
            throw new BusinessException("评论的帖子不存在！");
        }

        AuthUserVO current = AuthUtil.getCurrent();

        PostCommentModel insertModel = new PostCommentModel();
        insertModel.setCreateTime(new Date());
        insertModel.setPostId(postId);
        insertModel.setParentId(parentId);
        insertModel.setUserId(current.getUserId());
        insertModel.setIsHide(Boolean.FALSE);
        insertModel.setContent(content);
        insertModel.setLocation(IpUtil.getCityInfo());
        insertModel.setReplyId(replyId);
        insertModel.setReplyCount(0);

        PostCommentModel parentComment = null;
        PostCommentModel replyComment = null;

        if (parentId == 0) {

            // 根评论 (回复帖子)
            insertModel.setToUserId(postModel.getUserId());
            insertModel.setType(PostConstant.COMMENT_TYPE);

        } else {

            // 父级评论
            parentComment = this.selectCommentById(parentId);
            if (parentComment == null) {
                throw new BusinessException("回复的评论不存在！");
            }

            // @被回复的评论
            if (insertModel.getReplyId() > 0) {
                replyComment = this.selectCommentById(replyId);
                if (replyComment == null) {
                    throw new BusinessException("被@的评论不存在！");
                }

                // 更新目标评论的回复数
                this.postCommentMapper.addReplyCount(insertModel.getReplyId(), 1);

                insertModel.setToUserId(replyComment.getUserId());
                insertModel.setType(PostConstant.REPLY_AT_TYPE);
            } else {
                insertModel.setType(PostConstant.REPLY_TYPE);
                insertModel.setToUserId(parentComment.getUserId());
            }

            // 更新根评论的回复数
            this.postCommentMapper.addReplyCount(parentId, 1);
        }

        // 插入评论数据
        this.postCommentMapper.insert(insertModel);

        // 增加帖子评论数
        this.postMapper.addCommentsCount(postId, 1);

        // 发送消息通知
        this.sendCommentNotification(insertModel, postModel, parentComment, replyComment, content);

        UserModel userModel = this.userService.getById(current.getUserId());
        Assert.notNull(userModel,  "userModel is null");

        // 返回给前台
        PostCommentVO commentVO = new PostCommentVO();
        BeanUtils.copyProperties(insertModel, commentVO);
        commentVO.setReplies(Lists.newArrayList());
        commentVO.setNickname(userModel.getNickname());
        commentVO.setAvatar(userModel.getAvatar());
        return ResultUtil.getSuccess(PostCommentVO.class, commentVO);
    }

    private PostCommentModel selectCommentById(Long commentId) {
        return this.postCommentMapper.selectOne(Wrappers.<PostCommentModel>lambdaQuery().eq(PostCommentModel::getIsHide, 0)
                .eq(PostCommentModel::getCommentId, commentId)
                .last("limit 1")
        );
    }

    private PostModel selectPostById(Long postId) {
        return this.postMapper.selectOne(Wrappers.<PostModel>lambdaQuery().eq(PostModel::getIsActive, 1).eq(PostModel::getIsDraft, 0)
                .eq(PostModel::getPostId, postId)
                .last("limit 1")
        );
    }

    private void sendCommentNotification(PostCommentModel insertModel, PostModel postModel, PostCommentModel parentComment, PostCommentModel replyComment, String content) {

        AuthUserVO current = AuthUtil.getCurrent();
        if (Objects.equals(current.getUserId(), insertModel.getToUserId())) {
            return;
        }

        NotifyMsgDTO.NotifyMsgDTOBuilder notifyMsgBuilder = NotifyMsgDTO.builder()
                .senderId(current.getUserId())
                .content(content)
                .userIdList(Collections.singletonList(insertModel.getToUserId()))
                .relationId(insertModel.getCommentId());

        NotifyMsgDTO msgDTO = null;
        if (StringUtils.equals(insertModel.getType(), PostConstant.COMMENT_TYPE)) {

            // 评论帖子
            msgDTO = notifyMsgBuilder
                    .content(String.format(NotificationTypeEnum.COMMENT.getTemplate(), current.getNickname(), postModel.getTitle(), content))
                    .typeEnum(NotificationTypeEnum.COMMENT).build();

        } else if (StringUtils.equals(insertModel.getType(), PostConstant.REPLY_TYPE)) {

            // 发送消息通知 (回复评论)
            assert parentComment != null;
            msgDTO = notifyMsgBuilder
                    .content(String.format(NotificationTypeEnum.REPLY.getTemplate(), current.getNickname(), parentComment.getContent(), content))
                    .typeEnum(NotificationTypeEnum.REPLY).build();

        } else if (StringUtils.equals(insertModel.getType(), PostConstant.REPLY_AT_TYPE)) {

            // 发送消息通知 (回复@某人)
            assert replyComment != null;
            msgDTO = notifyMsgBuilder
                    .content(String.format(NotificationTypeEnum.REPLY_AT.getTemplate(), current.getNickname(), replyComment.getContent(), content))
                    .typeEnum(NotificationTypeEnum.REPLY_AT).build();

            // 如果@的人 != 父评论人，也需要发送消息通知给父评论人
            if (!Objects.equals(parentComment.getUserId(), insertModel.getToUserId()) && !Objects.equals(parentComment.getUserId(), current.getUserId())) {

                this.notificationService.send(
                        NotifyMsgDTO.builder()
                                .senderId(current.getUserId())
                                .content(content)
                                .userIdList(Collections.singletonList(parentComment.getUserId()))
                                .relationId(insertModel.getCommentId())
                                .content(String.format(NotificationTypeEnum.REPLY_AT.getTemplate(), current.getNickname(), replyComment.getContent(), content))
                                .typeEnum(NotificationTypeEnum.REPLY_AT)
                                .build()
                );
            }

        }

        this.notificationService.send(msgDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<Void> postCommentDel(Long commentId) {

        if (commentId == null || commentId <= 0) {
            return ResultUtil.getWarn("参数错误！");
        }

        PostCommentModel postCommentModel = this.postCommentMapper.selectById(commentId);
        if (!Objects.equals(postCommentModel.getUserId(), AuthUtil.getCurrent().getUserId())) {
            return ResultUtil.getWarn("非法越权访问！");
        }

        int count = 0;

        // 根评论 下面的子评论也要删除
        if (StringUtils.equals(postCommentModel.getType(), PostConstant.COMMENT_TYPE)) {
            count += this.postCommentMapper.update(null, Wrappers.<PostCommentModel>lambdaUpdate()
                    .eq(PostCommentModel::getParentId, postCommentModel.getCommentId())
                    .eq(PostCommentModel::getIsHide, Boolean.FALSE)
                    .set(PostCommentModel::getIsHide, Boolean.TRUE)
            );
        }
        count += this.postCommentMapper.update(null, Wrappers.<PostCommentModel>lambdaUpdate()
                .eq(PostCommentModel::getCommentId, commentId)
                .eq(PostCommentModel::getIsHide, Boolean.FALSE)
                .set(PostCommentModel::getIsHide, Boolean.TRUE)
        );

        // 维护评论表中的回复数
        if (StringUtils.equals(postCommentModel.getType(), PostConstant.REPLY_TYPE)) {
            // 更新根评论的回复数
            this.postCommentMapper.addReplyCount(postCommentModel.getParentId(), -1);
        }

        if (StringUtils.equals(postCommentModel.getType(), PostConstant.REPLY_AT_TYPE)) {
            // 更新根评论的回复数
            this.postCommentMapper.addReplyCount(postCommentModel.getParentId(), -1);
            // 更新目标评论的回复数
            this.postCommentMapper.addReplyCount(postCommentModel.getReplyId(), -1);
        }


        // 减少帖子评论数
        this.postMapper.addCommentsCount(postCommentModel.getPostId(), -count);

        return ResultUtil.getSuccess();
    }

    @Override
    public ResultVO<PostReplyPageVO> postReplyPage(PostReplyDTO postReplyDTO) {

        Integer pageSize = postReplyDTO.getPageSize();
        Integer currentPage = postReplyDTO.getCurrentPage();
        Long commentId = postReplyDTO.getCommentId();

        if (pageSize == null || pageSize <= 0 || pageSize >= 20) {
            pageSize = 20;
        }

        PageHelper.startPage(currentPage, pageSize);
        List<PostReplyVO> postReplyList = this.postCommentMapper.getPostReplyPage(commentId);
        PageInfo<PostReplyVO> pageInfo = new PageInfo<>(postReplyList);


        PostReplyPageVO pageVO =  new PostReplyPageVO();
        pageVO.setList(postReplyList);
        pageVO.setHasMore(pageInfo.isHasNextPage());
        pageVO.setTotal(pageInfo.getTotal());
        return ResultUtil.getSuccess(PostReplyPageVO.class, pageVO);
    }


    private void updatePostTag(PostUpdateDTO postUpdateDTO, List<String> tags) {

        if (postUpdateDTO == null) {
            return;
        }

        // 先把之前的标签删掉
        this.postTagMapper.delete(Wrappers.<PostTagModel>lambdaQuery()
                .eq(PostTagModel::getPostId, postUpdateDTO.getPostId()));

        if (CollectionUtils.isNotEmpty(tags)) {

            this.postTagMapper.insertBatch(postUpdateDTO.getPostId(), tags);
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
            this.postMapper.addViewsCount(postDetailVO.getPostId(), 1);
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
        final Map<Long, List<PostTagModel>> tagMap = this.postTagMapper.selectList(Wrappers.<PostTagModel>lambdaQuery()
                .in(PostTagModel::getPostId, postIdList)).stream()
                .collect(Collectors.groupingBy(PostTagModel::getPostId));

        // 获取热帖
        Set<RedisZSetCommands.Tuple> tuples = this.redisService.zRangeWithScores(RedisKeyConstant.REDIS_HOT_POST_CACHE, RedisDbConstant.REDIS_BIZ_CACHE, 0, -1);
        List<Long> hotPostIdList = tuples.stream().map(e -> new String(e.getValue())).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toList());

        for (PostListItemVO postListItemVo : postListItemVos) {

            List<PostTagModel> postTagModels = tagMap.get(postListItemVo.getPostId());

            if (CollectionUtils.isNotEmpty(postTagModels)) {
                // 帖子标签
                List<String> tagNames = postTagModels.stream().distinct().map(PostTagModel::getTagName).collect(Collectors.toList());
                postListItemVo.setTags(tagNames);
            }

            // 热帖标识
            postListItemVo.setIsHot(hotPostIdList.contains(postListItemVo.getPostId()));
        }
    }
}
