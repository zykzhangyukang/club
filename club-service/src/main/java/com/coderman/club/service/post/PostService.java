package com.coderman.club.service.post;

import com.coderman.club.dto.post.PostPublishDTO;
import com.coderman.club.vo.common.ResultVO;

/**
 * @author Administrator
 */
public interface PostService {

    /**
     * 创建帖子
     * @param postPublishDTO
     * @return
     */
    ResultVO<Void> postPublish(PostPublishDTO postPublishDTO);

    /**
     * 获取防重token
     *
     * @return
     */
    ResultVO<String> postToken();


}
