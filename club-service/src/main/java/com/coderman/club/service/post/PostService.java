package com.coderman.club.service.post;

import com.coderman.club.dto.post.PostCreateDTO;
import com.coderman.club.vo.common.ResultVO;

/**
 * @author Administrator
 */
public interface PostService {

    /**
     * 创建帖子
     * @param postCreateDTO
     * @return
     */
    ResultVO<Void> postCreate(PostCreateDTO postCreateDTO);

    /**
     * 获取防重token
     *
     * @return
     */
    ResultVO<String> getRepeatToken();


}
