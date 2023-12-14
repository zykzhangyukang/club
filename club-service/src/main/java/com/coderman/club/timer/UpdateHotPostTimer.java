package com.coderman.club.timer;

import com.alibaba.fastjson.JSON;
import com.coderman.club.service.post.PostHotService;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.vo.post.PostHotTaskVO;
import com.coderman.club.vo.post.PostHotVO;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * 计算帖子热度
 *
 * @author ：zhangyukang
 * @date ：2023/12/06 12:17
 */
@Component
@Slf4j
public class UpdateHotPostTimer {

    @Resource
    private PostHotService postHotService;

    private static final double TIME_DECAY_FACTOR = 0.8;
    private static final double VIEWS_WEIGHT = 3;
    private static final double LIKES_WEIGHT = 5;
    private static final double COMMENTS_WEIGHT = 2;

    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(2, 2, 0, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024),
            new ThreadFactoryBuilder().setNameFormat("post_thread_%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    @Resource
    private RedisService redisService;

    @Scheduled(cron = "0 */30 * * * ?")
    public void refreshHotPosts() {

        List<PostHotTaskVO> taskVoList = this.postHotService.getPostTaskList(100);
        log.info(JSON.toJSONString(taskVoList));

        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (PostHotTaskVO postHotTaskVO : taskVoList) {

            CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
                @Override
                public String get() {
                    try {

                        return deal(postHotTaskVO);
                    } catch (Exception e) {

                        log.error("refreshHotPosts error:{}", ExceptionUtils.getRootCauseMessage(e));
                        return String.format("刷新帖子热度失败: [beginId: %s, endId: %s] ", postHotTaskVO.getBeginId(), postHotTaskVO.getEndId());
                    }
                }
            }, THREAD_POOL);
            futures.add(future);
        }

        // 等待所有 CompletableFuture 完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.thenRun(() -> {
            log.info("刷新帖子热度完成");
        });
    }

    private String deal(PostHotTaskVO postHotTaskVO) {

        Long beginId = postHotTaskVO.getBeginId();
        Long endId = postHotTaskVO.getEndId();

        List<PostHotVO> postHotVoList = this.postHotService.getPostFormIndex(beginId, endId);
        for (PostHotVO postHotVO : postHotVoList) {

            // 计算热度
            BigDecimal score = this.getHotScore(postHotVO);

            // 保存到热贴redis
            if (score.compareTo(BigDecimal.ZERO) > 0) {

                log.info("id:{} , 帖子：{}， 热度: {}", postHotVO.getPostId(), postHotVO.getTitle(), score);
            }

        }

        return String.format("刷新帖子热度成功: [beginId: %s, endId: %s] ", beginId, endId);
    }

    private BigDecimal getHotScore(PostHotVO post) {

        // 基础热度值计算（根据相关指标计算）
        double viewsScore = post.getViewsCount() * VIEWS_WEIGHT;
        double likesScore = post.getLikesCount() * LIKES_WEIGHT;
        double commentsScore = post.getCommentsCount() * COMMENTS_WEIGHT;

        // 将指标分数相加得到基础热度值
        double basicHotness = viewsScore + likesScore + commentsScore;

        BigDecimal value = BigDecimal.valueOf(basicHotness);
        return this.applyTimeDecay(post, value);
    }

    private BigDecimal applyTimeDecay(PostHotVO post, BigDecimal hotness) {

        long postAgeInDays = (System.currentTimeMillis() - post.getCreatedAt().getTime()) / (1000 * 60 * 60 * 24);
        double timeDecay = Math.exp(-TIME_DECAY_FACTOR * postAgeInDays);
        // 应用时间衰减
        return hotness.multiply(BigDecimal.valueOf(timeDecay)).setScale(6, RoundingMode.HALF_UP);
    }


}
