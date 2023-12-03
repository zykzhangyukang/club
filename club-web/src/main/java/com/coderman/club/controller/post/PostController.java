package com.coderman.club.controller.post;

import com.coderman.club.annotation.RateLimit;
import com.coderman.club.dto.post.PostPublishDTO;
import com.coderman.club.service.post.PostService;
import com.coderman.club.vo.common.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

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
    public ResultVO<Void> postPublish(@RequestBody PostPublishDTO postPublishDTO) {

        return this.postService.postPublish(postPublishDTO);
    }


    @ApiOperation(value = "上传图片")
    @PostMapping(value = "/upload/image")
    @RateLimit
    public ResultVO<String> uploadImage(MultipartFile file) throws IOException {

        return this.postService.uploadImage(file);
    }

    @ApiOperation(value = "获取防止重复token")
    @PostMapping(value = "/token")
    public ResultVO<String> postToken() {

        return this.postService.postToken();
    }
}
