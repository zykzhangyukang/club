package com.coderman.club.dao.post;

import com.coderman.club.dao.common.BaseDAO;
import com.coderman.club.model.post.PostTagExample;
import com.coderman.club.model.post.PostTagModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Administrator
 */
public interface PostTagDAO extends BaseDAO<PostTagModel, PostTagExample> {

    /**
     * 新增帖子
     * @param postId
     * @param tagList
     */
    void insertBatch(@Param(value = "postId") Long postId, @Param(value = "tagList") List<String> tagList);
}