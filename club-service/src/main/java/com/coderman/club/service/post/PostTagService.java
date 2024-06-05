package com.coderman.club.service.post;

import com.coderman.club.model.post.PostTagModel;

import java.util.List;

/**
 * @author zhangyukang
 */
public interface PostTagService {

    /**
     * 获取热标签（指定排名）
     *
     * @param top
     * @return
     */
    List<PostTagModel> getTopHotTags(int top);
}
