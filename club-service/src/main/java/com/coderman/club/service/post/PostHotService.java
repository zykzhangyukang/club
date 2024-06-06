package com.coderman.club.service.post;

import com.coderman.club.vo.post.PostHotTaskVO;
import com.coderman.club.vo.post.PostHotVO;

import java.util.List;

/**
 * 热门帖子
 * @author zhangyukang
 */
public interface PostHotService {

    /**
     * 获取刷新热度任务
     * @param batchSize
     * @return
     */
    List<PostHotTaskVO> getPostTaskList(Integer batchSize);

    /**
     * 获取指定区间的帖子
     *
     * @param beginId
     * @param endId
     * @return
     */
    List<PostHotVO> getPostFormIndex(Long beginId, Long endId);
}
