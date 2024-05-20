package com.coderman.club.service.post.impl;

import com.coderman.club.mapper.post.PostMapper;
import com.coderman.club.service.post.PostHotService;
import com.coderman.club.vo.post.PostHotTaskVO;
import com.coderman.club.vo.post.PostHotVO;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 热贴服务实现
 *
 * @author ：zhangyukang
 * @date ：2023/12/08 12:21
 */
@Service
public class PostHotServiceImpl implements PostHotService {

    @Resource
    private PostMapper postMapper;

    @Override
    public List<PostHotTaskVO> getPostTaskList(Integer batchSize) {

        if (batchSize == null || batchSize < 0) {
            batchSize = 100;
        }
        return this.postMapper.getPostTaskList(batchSize);
    }

    @Override
    public List<PostHotVO> getPostFormIndex(Long beginId, Long endId) {
        if(beginId == null || beginId < 0 || endId == null || endId < 0){
            return Lists.newArrayList();
        }
        return this.postMapper.getPostFormIndex(beginId, endId);
    }
}
