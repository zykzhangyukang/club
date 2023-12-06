package com.coderman.club.service.post;

import com.coderman.club.dto.post.PostPageDTO;
import com.coderman.club.dto.post.PostPublishDTO;
import com.coderman.club.dto.post.PostUpdateDTO;
import com.coderman.club.vo.common.PageVO;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.post.PostDetailVO;
import com.coderman.club.vo.post.PostListItemVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
     * 帖子富文本图片上传
     * @param file
     * @return
     */
    ResultVO<String> uploadImage(MultipartFile file) throws IOException;

    /**
     * 帖子列表
     * @param postPageDTO
     * @return
     */
    ResultVO<PageVO<List<PostListItemVO>>> postPage(PostPageDTO postPageDTO);

    /**
     * 帖子详情
     * @param id
     * @return
     */
    ResultVO<PostDetailVO> postDetail(Long id);

    /**
     * 更新帖子
     * @param postUpdateDTO
     * @return
     */
    ResultVO<Void> postUpdate(PostUpdateDTO postUpdateDTO);

    /**
     * 点赞帖子
     *
     * @param postId
     * @return
     */
    ResultVO<Void> postLike(Long postId);

    /**
     * 取消点赞
     *
     * @param postId
     * @return
     */
    ResultVO<Void> postUnLike(Long postId);
}
