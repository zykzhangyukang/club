package com.coderman.club.timer;

import com.alibaba.fastjson.JSON;
import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.service.post.PostHotService;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.vo.post.PostHotTaskVO;
import com.coderman.club.vo.post.PostHotVO;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.DefaultTuple;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * 计算帖子热度
 *
 * @author ：zhangyukang
 * @date ：2023/12/06 12:17
 */
@Component
@Slf4j
public class UpdateHotPostTimer implements CommandLineRunner {

    @Resource
    private PostHotService postHotService;

    @Resource
    private RedisService redisService;

    private final DelayQueue<PostHotTaskVO> delayQueue = new DelayQueue<>();

    private static final double TIME_DECAY_FACTOR = 0.8;
    private static final double VIEWS_WEIGHT = 3;
    private static final double LIKES_WEIGHT = 5;
    private static final double COMMENTS_WEIGHT = 2;
    private static final double COLLECTS_WEIGHT = 8;

    private static final int MAX_HOT_POSTS = 10;

    private static final int THREAD_POOL_SIZE = 1;
    private static final int TASK_QUEUE_CAPACITY = 1024;

    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(
            THREAD_POOL_SIZE, THREAD_POOL_SIZE,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(TASK_QUEUE_CAPACITY),
            new ThreadFactoryBuilder().setNameFormat("post_thread_%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    private static final ExecutorService RETRY_THREAD_POOL = new ThreadPoolExecutor(
            THREAD_POOL_SIZE, THREAD_POOL_SIZE,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(TASK_QUEUE_CAPACITY),
            new ThreadFactoryBuilder().setNameFormat("retry_thread_%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @PostConstruct
    public void initRetryThread() {
        RETRY_THREAD_POOL.execute(() -> {

            while (true) {

                try {
                    PostHotTaskVO postHotTaskVO = delayQueue.take();
                    log.warn("重试线程开始处理任务:{}", JSON.toJSONString(postHotTaskVO));
                    thread0(postHotTaskVO);
                } catch (InterruptedException e) {

                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Scheduled(cron = "0 */30 * * * ?")
    public void refreshHotPosts() {
        List<PostHotTaskVO> taskVoList = postHotService.getPostTaskList(500);
        log.info("Retrieved post tasks: {}", JSON.toJSONString(taskVoList));

        long currentTimeMillis = System.currentTimeMillis();
        String oldKey = RedisKeyConstant.REDIS_HOT_POST_CACHE + ":" + currentTimeMillis;

        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (PostHotTaskVO postHotTaskVO : taskVoList) {
            postHotTaskVO.setRedisKeyName(oldKey);
            CompletableFuture<String> future = thread0(postHotTaskVO);
            futures.add(future);
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.thenRun(() -> {
            this.redisService.rename(oldKey, RedisKeyConstant.REDIS_HOT_POST_CACHE,RedisDbConstant.REDIS_BIZ_CACHE);
            log.info("刷新帖子热度完成，耗时：{} ms", System.currentTimeMillis() - currentTimeMillis);
        });

    }

    private CompletableFuture<String> thread0(PostHotTaskVO postHotTaskVO) {
        return CompletableFuture.supplyAsync(() -> {
            try {

                return deal(postHotTaskVO);
            } catch (Exception e) {

                log.error("处理帖子热度计算失败ERROR:{}",e.getMessage());
                // 重试队列
                addTaskToDelayQueue(postHotTaskVO);

                return String.format("刷新帖子热度失败: [beginId: %s, endId: %s]", postHotTaskVO.getBeginId(), postHotTaskVO.getEndId());
            }
        }, THREAD_POOL);
    }

    private void addTaskToDelayQueue(PostHotTaskVO postHotTaskVO) {

        log.warn("刷新帖子热度任务，加入重试队列：{}", JSON.toJSONString(postHotTaskVO));

        int retryTimes = postHotTaskVO.getRetry().getAndIncrement();

        if (retryTimes == 0) {

            postHotTaskVO.setDelayTime(10);

        } else if (retryTimes == 1) {

            postHotTaskVO.setDelayTime(30);

        } else if (retryTimes == 2) {

            postHotTaskVO.setDelayTime(60);

        } else {

            log.error("超过重试次数 3 次,禁止重试，callbackTask:{}", JSON.toJSONString(postHotTaskVO));
            return;
        }
        delayQueue.add(postHotTaskVO);
    }

    private String deal(PostHotTaskVO postHotTaskVO) {

        Long beginId = postHotTaskVO.getBeginId();
        Long endId = postHotTaskVO.getEndId();
        List<PostHotVO> postHotVoList = postHotService.getPostFormIndex(beginId, endId);

        Set<RedisZSetCommands.Tuple> tuples = new HashSet<>();
        for (PostHotVO postHotVO : postHotVoList) {
            BigDecimal score = calculateHotScore(postHotVO);
            if (score.compareTo(BigDecimal.ZERO) > 0) {

                RedisZSetCommands.Tuple t = new DefaultTuple(String.valueOf(postHotVO.getPostId()).getBytes(), score.doubleValue());
                tuples.add(t);
            }
        }

        if (!tuples.isEmpty()) {
            this.redisService.addZSetWithMaxSize(postHotTaskVO.getRedisKeyName(), tuples, RedisDbConstant.REDIS_BIZ_CACHE, MAX_HOT_POSTS);
        }

        String msg = String.format("刷新帖子热度成功: [beginId: %s, endId: %s]", beginId, endId);
        log.info(msg);
        return msg;
    }

    private BigDecimal calculateHotScore(PostHotVO post) {
        double viewsScore = post.getViewsCount() * VIEWS_WEIGHT;
        double likesScore = post.getLikesCount() * LIKES_WEIGHT;
        double commentsScore = post.getCommentsCount() * COMMENTS_WEIGHT;
        double collectsScore = post.getCollectsCount() * COLLECTS_WEIGHT;
        double basicHotness = viewsScore + likesScore + commentsScore + collectsScore;

        BigDecimal hotness = BigDecimal.valueOf(basicHotness);
        return applyTimeDecay(post, hotness);
    }

    private BigDecimal applyTimeDecay(PostHotVO post, BigDecimal hotness) {
        long postAgeInDays = (System.currentTimeMillis() - post.getCreatedAt().getTime()) / (1000 * 60 * 60 * 24);
        double timeDecay = Math.exp(-TIME_DECAY_FACTOR * postAgeInDays);
        return hotness.multiply(BigDecimal.valueOf(timeDecay)).setScale(6, RoundingMode.HALF_UP);
    }

    @Override
    public void run(String... args) {
        this.refreshHotPosts();
    }
}
