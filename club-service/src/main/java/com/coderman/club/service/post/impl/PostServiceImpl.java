package com.coderman.club.service.post.impl;

import com.coderman.club.constant.common.CommonConstant;
import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.dao.post.PostDAO;
import com.coderman.club.dao.post.PostTagDAO;
import com.coderman.club.dto.post.PostPageDTO;
import com.coderman.club.dto.post.PostPublishDTO;
import com.coderman.club.enums.FileModuleEnum;
import com.coderman.club.model.post.PostModel;
import com.coderman.club.model.post.PostTagExample;
import com.coderman.club.model.post.PostTagModel;
import com.coderman.club.service.post.PostService;
import com.coderman.club.service.redis.RedisLockService;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.service.section.SectionService;
import com.coderman.club.utils.AliYunOssUtil;
import com.coderman.club.utils.AuthUtil;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.vo.common.PageVO;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.post.PostDetailVO;
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
    private PostTagDAO postTagDAO;

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
            if(StringUtils.length(title) < 5){
                return ResultUtil.getWarn("标题不能少于5个字符！");
            }
            if (StringUtils.length(title) > CommonConstant.LENGTH_128) {
                return ResultUtil.getWarn("标题字符最多128个字符！");
            }
            if (StringUtils.isBlank(content)) {
                return ResultUtil.getWarn("帖子内容不能为空！");
            }
            if(CollectionUtils.isNotEmpty(tags)){

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
        long pageSize = postPageDTO.getPageSize() != null && postPageDTO.getPageSize() > 0 ? postPageDTO.getPageSize() : 30L;

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("limit", pageSize);
        conditionMap.put("offset", (currentPage - 1) * pageSize);

        if (postPageDTO.getFirstSectionId() != null && postPageDTO.getFirstSectionId() > 0) {
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

        if(id == null || id < 0){
            return ResultUtil.getWarn("帖子不存在请刷新重试！");
        }

        PostDetailVO postDetailVO = this.postDAO.selectPostDetailVoById(id);
        if(postDetailVO == null){

            return ResultUtil.getWarn("帖子不存在请刷新重试！");
        }

        return ResultUtil.getSuccess(PostDetailVO.class, postDetailVO);
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
