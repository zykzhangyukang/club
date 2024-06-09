package com.coderman.club.service.post;

import com.coderman.club.dto.post.*;
import com.coderman.club.vo.common.PageVO;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.post.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author zhangyukang
 */
public interface PostService {

    /**
     * 创建帖子。
     *
     * @param postPublishDTO 包含待发布帖子信息的数据传输对象。
     * @return 表示帖子创建操作结果的响应结果对象。
     */
    ResultVO<Void> postPublish(PostPublishDTO postPublishDTO);

    /**
     * 获取防重复提交的 token。
     *
     * @return 表示获取防重复提交 token 操作结果的响应结果对象。
     */
    ResultVO<String> postToken();

    /**
     * 上传帖子富文本图片。
     *
     * @param file 待上传的图片文件。
     * @return 表示图片上传操作结果的响应结果对象。
     * @throws IOException 如果上传过程中发生 IO 错误。
     */
    ResultVO<String> uploadImage(MultipartFile file) throws IOException;

    /**
     * 获取帖子列表。
     *
     * @param postPageDTO 包含帖子列表相关参数的数据传输对象。
     * @return 表示获取帖子列表操作结果的响应结果对象。
     */
    ResultVO<PageVO<List<PostListItemVO>>> postPage(PostPageDTO postPageDTO);

    /**
     * 获取指定帖子的详情信息。
     *
     * @param id 帖子的唯一标识符。
     * @return 表示获取帖子详情操作结果的响应结果对象。
     */
    ResultVO<PostDetailVO> getPostDetail(String id);

    /**
     * 更新指定帖子的信息。
     *
     * @param postUpdateDTO 包含待更新帖子信息的数据传输对象。
     * @return 表示更新帖子操作结果的响应结果对象。
     */
    ResultVO<Void> postUpdate(PostUpdateDTO postUpdateDTO);

    /**
     * 点赞指定帖子。
     *
     * @param postId 帖子的唯一标识符。
     * @return 表示点赞操作结果的响应结果对象。
     */
    ResultVO<Void> postLike(Long postId);

    /**
     * 取消对指定帖子的点赞。
     *
     * @param postId 帖子的唯一标识符。
     * @return 表示取消点赞操作结果的响应结果对象。
     */
    ResultVO<Void> postUnLike(Long postId);

    /**
     * 删除指定帖子。
     *
     * @param postId 帖子的唯一标识符。
     * @return 表示删除帖子操作结果的响应结果对象。
     */
    ResultVO<Void> postDelete(Long postId);

    /**
     * 收藏指定帖子。
     *
     * @param postId 帖子的唯一标识符。
     * @return 表示收藏帖子操作结果的响应结果对象。
     */
    ResultVO<Void> postCollect(Long postId);

    /**
     * 取消收藏指定帖子。
     *
     * @param postId 帖子的唯一标识符。
     * @return 表示取消收藏帖子操作结果的响应结果对象。
     */
    ResultVO<Void> postUnCollect(Long postId);

    /**
     * 获取指定用户收藏的帖子数量。
     *
     * @param userId 用户的唯一标识符。
     * @return 用户收藏的帖子数量。
     */
    Integer getCollectCountByUserId(Long userId);

    /**
     * 对指定帖子进行评论。
     *
     * @param postCommentDTO 包含待评论帖子信息的数据传输对象。
     * @return 表示评论帖子操作结果的响应结果对象。
     */
    ResultVO<PostCommentResultVO> postComment(PostCommentDTO postCommentDTO);

    /**
     * 删除指定帖子评论。
     *
     * @param commentId 评论的唯一标识符。
     * @return 表示删除帖子评论操作结果的响应结果对象。
     */
    ResultVO<Void> postCommentDel(Long commentId);

    /**
     * 获取指定帖子评论的回复分页。
     *
     * @param postReplyDTO 包含获取帖子评论回复分页相关参数的数据传输对象。
     * @return 表示获取帖子评论回复分页操作结果的响应结果对象。
     */
    ResultVO<PostReplyPageVO> postReplyPage(PostReplyDTO postReplyDTO);

    /**
     * 获取帖子评论的分页列表。
     *
     * @param pageDTO 包含获取帖子评论分页列表相关参数的数据传输对象。
     * @return 表示获取帖子评论分页列表操作结果的响应结果对象。
     */
    ResultVO<PageVO<List<PostCommentVO>>> getCommentPage(PostCommentPageDTO pageDTO);

}
