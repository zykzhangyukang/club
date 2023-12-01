package com.coderman.club.controller.post;

import com.coderman.club.dto.post.PostCreateDTO;
import com.coderman.club.service.post.PostService;
import com.coderman.club.vo.common.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    @PutMapping(value = "/create")
    public ResultVO<Void> postCreate(@RequestBody PostCreateDTO postCreateDTO) {

        return this.postService.postCreate(postCreateDTO);
    }

    @ApiOperation(value = "获取防止重复token")
    @PutMapping(value = "/token")
    public ResultVO<String> getRepeatToken() {

        return this.postService.getRepeatToken();
    }
}
