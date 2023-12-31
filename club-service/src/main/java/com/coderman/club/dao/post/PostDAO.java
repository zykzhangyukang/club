package com.coderman.club.dao.post;

import com.coderman.club.dao.common.BaseDAO;
import com.coderman.club.model.post.PostExample;
import com.coderman.club.model.post.PostModel;
import com.coderman.club.vo.post.PostDetailVO;
import com.coderman.club.vo.post.PostHotTaskVO;
import com.coderman.club.vo.post.PostHotVO;
import com.coderman.club.vo.post.PostListItemVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface PostDAO extends BaseDAO<PostModel, PostExample> {

    /**
     * 分页总条数
     * @param conditionMap
     * @return
     */
    Long countPage(Map<String, Object> conditionMap);

    /**
     * 分页查询
     * @param conditionMap
     * @return
     */
    List<PostListItemVO> pageList(Map<String, Object> conditionMap);

    /**
     * 新增帖子
     * @param postModel
     * @return
     */
    int insertSelectiveReturnKey(PostModel postModel);

    /**
     * 查询帖子详情
     * @param id
     * @return
     */
    PostDetailVO selectPostDetailVoById(@Param(value = "postId") Long id);

    /**
     * 增加帖子浏览量
     *
     * @param postId
     */
    void addViewsCount(@Param(value = "postId") Long postId,@Param(value = "count") Integer count);

    /**
     * 查询用户帖子
     * @param userId
     * @param postId
     * @return
     */
    PostModel selectUserPostById(@Param(value = "userId") Long userId,@Param(value = "postId") Long postId);

    /**
     * 更新帖子
     *
     * @param updateModel
     */
    void updatePostWithContentById(@Param(value = "updateModel") PostModel updateModel);

    /**
     * 增加帖子点赞数
     *
     * @param postId
     */
    void addLikesCount(@Param(value = "postId") Long postId, @Param(value = "count") Integer count);

    /**
     * 帖子热度刷新任务
     * @param batchSize
     * @return
     */
    List<PostHotTaskVO> getPostTaskList(@Param(value = "batchSize") Integer batchSize);

    /**
     * 获取帖子指定索引区间
     * @param beginId
     * @param endId
     * @return
     */
    List<PostHotVO> getPostFormIndex(@Param(value = "beginId") Long beginId,@Param(value = "endId") Long endId);
}