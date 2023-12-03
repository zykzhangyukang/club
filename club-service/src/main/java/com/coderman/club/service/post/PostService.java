package com.coderman.club.service.post;

import com.coderman.club.dto.post.PostPublishDTO;
import com.coderman.club.vo.common.ResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    /**
     *
     * @param file
     * @return
     */
    ResultVO<String> uploadImage(MultipartFile file) throws IOException;
}
