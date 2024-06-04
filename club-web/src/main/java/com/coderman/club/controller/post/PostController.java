package com.coderman.club.controller.post;

import com.coderman.club.annotation.RateLimit;
import com.coderman.club.dto.post.*;
import com.coderman.club.limiter.LimiterStrategy;
import com.coderman.club.service.post.PostService;
import com.coderman.club.vo.common.PageVO;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.post.PostCommentVO;
import com.coderman.club.vo.post.PostDetailVO;
import com.coderman.club.vo.post.PostListItemVO;
import com.coderman.club.vo.post.PostReplyVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author ：zhangyukang
 * @date ：2023/12/01 15:42
 */
@Api(value = "社区帖子模块", tags = {"社区帖子模块"})
@RestController
@RequestMapping(value = "/api/post")
public class PostController {

    @Resource
    private PostService postService;

    @ApiOperation(value = "创建帖子")
    @PostMapping(value = "/publish")
    @RateLimit(strategy = LimiterStrategy.FIXED_WINDOW, windowSize = 5 , windowRequests = 5)
    public ResultVO<Void> postPublish(@RequestBody PostPublishDTO postPublishDTO) {

        return this.postService.postPublish(postPublishDTO);
    }

    @ApiOperation(value = "评论帖子")
    @PostMapping(value = "/comment")
    @RateLimit(strategy = LimiterStrategy.FIXED_WINDOW, windowSize = 5 , windowRequests = 5)
    public ResultVO<PostCommentVO> postComment(@RequestBody PostCommentDTO postCommentDTO) {

        return this.postService.postComment(postCommentDTO);
    }

    @ApiOperation(value = "回复分页加载")
    @PostMapping(value = "/reply/page")
    @RateLimit(strategy = LimiterStrategy.FIXED_WINDOW, windowSize = 5 , windowRequests = 5)
    public ResultVO<List<PostReplyVO>> postReplyPage(@RequestBody PostReplyDTO postReplyDTO) {

        return this.postService.postReplyPage(postReplyDTO);
    }

    @ApiOperation(value = "删除帖子评论")
    @DeleteMapping(value = "/comment/delete/{commentId}")
    public ResultVO<Void> postCommentDel(@PathVariable(value = "commentId") Long commentId) {

        return this.postService.postCommentDel(commentId);
    }

    @ApiOperation(value = "点赞帖子")
    @PostMapping(value = "/like/{postId}")
    public ResultVO<Void> postLike(@PathVariable(value = "postId") Long postId) {

        return this.postService.postLike(postId);
    }

    @ApiOperation(value = "取消点赞")
    @PostMapping(value = "/unlike/{postId}")
    public ResultVO<Void> postUnLike(@PathVariable(value = "postId") Long postId) {

        return this.postService.postUnLike(postId);
    }


    @ApiOperation(value = "收藏帖子")
    @PostMapping(value = "/collect/{postId}")
    public ResultVO<Void> postCollect(@PathVariable(value = "postId") Long postId) {

        return this.postService.postCollect(postId);
    }

    @ApiOperation(value = "取消收藏")
    @PostMapping(value = "/uncollect/{postId}")
    public ResultVO<Void> postUnCollect(@PathVariable(value = "postId") Long postId) {

        return this.postService.postUnCollect(postId);
    }

    @ApiOperation(value = "修改帖子")
    @PutMapping(value = "/update")
    @RateLimit(strategy = LimiterStrategy.FIXED_WINDOW, windowSize = 5 , windowRequests = 5)
    public ResultVO<Void> postUpdate(@RequestBody PostUpdateDTO postUpdateDTO) {
        return this.postService.postUpdate(postUpdateDTO);
    }


    @ApiOperation(value = "帖子详情")
    @GetMapping(value = "/detail")
    public ResultVO<PostDetailVO> getPostDetail(String id) {

        return this.postService.getPostDetail(id);
    }


    @ApiOperation(value = "评论列表")
    @PostMapping(value = "/comment/page")
    public ResultVO<PageVO<List<PostCommentVO>>> getCommentPage(@RequestBody PostCommentPageDTO pageDTO) {

        return this.postService.getCommentPage(pageDTO);
    }



    @ApiOperation(value = "删除帖子")
    @DeleteMapping(value = "/detail/{id}")
    @RateLimit
    public ResultVO<Void> postDelete(@PathVariable(value = "id") Long id) {

        return this.postService.postDelete(id);
    }


    @ApiOperation(value = "帖子列表")
    @PostMapping(value = "/page")
    public ResultVO<PageVO<List<PostListItemVO>>> postPage(@RequestBody PostPageDTO postPageDTO) {

        return this.postService.postPage(postPageDTO);
    }


    @ApiOperation(value = "上传图片")
    @PostMapping(value = "/upload/image")
    @RateLimit(strategy = LimiterStrategy.FIXED_WINDOW, windowSize = 5 , windowRequests = 5)
    public ResultVO<String> uploadImage(MultipartFile file) throws IOException {

        return this.postService.uploadImage(file);
    }

    @ApiOperation(value = "防重token获取")
    @PostMapping(value = "/token")
    public ResultVO<String> postToken() {

        return this.postService.postToken();
    }
}
