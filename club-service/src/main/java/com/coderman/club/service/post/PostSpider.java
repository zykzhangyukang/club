package com.coderman.club.service.post;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coderman.club.mapper.post.PostMapper;
import com.coderman.club.mapper.user.UserMapper;
import com.coderman.club.model.post.PostModel;
import com.coderman.club.model.user.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PostSpider {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final String BEFORE_POST_ID = "postBeforeId";
    private static final String BITMAP_KEY = "postBitmap";

    @Resource
    private PostMapper postMapper;

    @Resource
    private UserMapper userMapper;

    private static List<Long> userIdList;

    /**
     * 判断帖子是否唯一，并标记为已存在
     *
     * @param postId 帖子ID
     * @return 如果帖子是唯一的，返回true；否则返回false
     */
    public boolean isPostUnique(String postId) {
        // 使用简单的Hash函数将postId转换为位图偏移量
        int offset = Math.abs(postId.hashCode() % Integer.MAX_VALUE);

        // 检查位图的对应位是否为1
        Boolean bit = stringRedisTemplate.opsForValue().getBit(BITMAP_KEY, offset);

        if (bit != null && bit) {
            return false; // 位已设置，帖子不是唯一的
        } else {
            // 设置位图的对应位为1
            stringRedisTemplate.opsForValue().setBit(BITMAP_KEY, offset, true);
            return true; // 位未设置，帖子是唯一的
        }
    }

    @PostConstruct
    public void fetchMaiMai() {

        userIdList = this.userMapper.selectList(null).stream().map(UserModel::getUserId).collect(Collectors.toList());

        String lastBeforeId = this.stringRedisTemplate.opsForValue().get(BEFORE_POST_ID);

        Thread thread = new Thread(() -> {
            String initialBeforeId = lastBeforeId == null ? "1830209586" : lastBeforeId;
            try {
                request(initialBeforeId);
            } catch (InterruptedException e) {
                log.error("爬虫出错了:{}", e.getMessage(), e);
            }

        });

        thread.setDaemon(true);
        thread.setName("帖子爬虫线程");
        thread.setUncaughtExceptionHandler((t, e) -> {
            if (StringUtils.equals(t.getName(), "帖子爬虫线程")) {
                fetchMaiMai();
            }
        });
        thread.start();
    }


    public void request(String beforeId) throws InterruptedException {
        while (StringUtils.isNotBlank(beforeId)) {
            beforeId = makeRequest(beforeId);
            TimeUnit.SECONDS.sleep(1);
            this.stringRedisTemplate.opsForValue().set(BEFORE_POST_ID, beforeId);
        }
    }

    public String makeRequest(String beforeId) {
        String postId = "";
        try {
            // API端点的URL
            String url = "https://maimai.cn/sdk/web/content/get_list?api=feed%2Fv5%2Fnd1feed&u=237534056&page=1&before_id=" + beforeId;

            // 创建URL对象
            URL obj = new URL(url);

            // 打开到URL的连接
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // 设置请求方法为GET
            con.setRequestMethod("GET");

            // 设置请求头
            setRequestHeaders(con);

            // 获取响应代码
            int responseCode = con.getResponseCode();

            // 如果响应代码为200（HTTP.OK），读取响应
            if (responseCode == HttpURLConnection.HTTP_OK) {
                postId = handleResponse(con);
            } else {
                log.error("GET请求失败，响应代码：" + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return postId;
    }

    private static void setRequestHeaders(HttpURLConnection con) {
        con.setRequestProperty("Accept", "*/*");
        con.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
        con.setRequestProperty("Cookie", "_buuid=66sWqHzmIQEsDe_3Ejx-G; _buuid.sig=n72jzffqYgwz-hOkY6li5vAmYm4; guid=GxsZBBsbEgQYGB4EGxoaVgcYGxkdEhMcGR4cVhwZBB0ZHwVDWEtMS3kKGgQaBBoEHhgbBU9HRVhCaQoDRUFJT20KT0FDRgoGZmd+YmECChwZBB0ZHwVeQ2FIT31PRlpaawoDHhxSChEeHERDfQoRGgQaGwp+ZApZXUVOREN9AgoaBB8FS0ZGQ1BFZw==; AGL_USER_ID=07914d23-0f6d-4ce7-be6c-5f80ba617553; browser_fingerprint=DA3A476C; u=237534056; u.sig=0u0uq7XCHFfiqtQRc1Bb2pBimEc; access_token=1.982898c17c17f3da4715817f915ff5c8; access_token.sig=Cu3ov9rvum7w2UEJnHs6TTozoSA; u=237534056; u.sig=0u0uq7XCHFfiqtQRc1Bb2pBimEc; access_token=1.982898c17c17f3da4715817f915ff5c8; access_token.sig=Cu3ov9rvum7w2UEJnHs6TTozoSA; channel=www; channel.sig=tNJvAmArXf-qy3NgrB7afdGlanM; maimai_version=4.0.0; maimai_version.sig=kbniK4IntVXmJq6Vmvk3iHsSv-Y; csrftoken=HcQ3tFYg-chR92mXsUTwVxUWhW06frspvIXY; session=eyJzZWNyZXQiOiJMNURKZ1ZteV9saGpvZEJneWI0bWM1YjMiLCJ1IjoiMjM3NTM0MDU2IiwiX2V4cGlyZSI6MTcxNjM4ODM2OTc2MywiX21heEFnZSI6ODY0MDAwMDB9; session.sig=-i0cfTRVG_bzax2UyuTgaDBUcZE");
        con.setRequestProperty("Priority", "u=1, i");
        con.setRequestProperty("Referer", "https://maimai.cn/web/feed_explore");
        con.setRequestProperty("sec-ch-ua", "\"Chromium\";v=\"124\", \"Google Chrome\";v=\"124\", \"Not-A.Brand\";v=\"99\"");
        con.setRequestProperty("sec-ch-ua-mobile", "?0");
        con.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
        con.setRequestProperty("sec-fetch-dest", "empty");
        con.setRequestProperty("sec-fetch-mode", "cors");
        con.setRequestProperty("sec-fetch-site", "same-origin");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");
        con.setRequestProperty("x-csrf-token", "HcQ3tFYg-chR92mXsUTwVxUWhW06frspvIXY");
    }

    private String handleResponse(HttpURLConnection con) throws Exception {
        String postId = "";
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // 解析响应
        JSONObject jsonObject = JSON.parseObject(response.toString());
        JSONArray list = jsonObject.getJSONArray("list");

        if (list != null) {
            for (Object o : list) {
                JSONObject item = (JSONObject) o;
                postId = item.getString("id");
                if (item.containsKey("style44")) {

                    if (isPostUnique(postId)) {
                        log.info("爬取帖子===============>>>>>" + postId);

                        PostModel model = new PostModel();
                        model.setLastUpdatedAt(new Date());
                        model.setCreatedAt(new Date());
                        model.setTitle(StringUtils.substring(item.getJSONObject("style44").getString("text").replaceAll("\\s+", ""), 0, 80));
                        model.setUserId(userIdList.get(new Random().nextInt(userIdList.size())));
                        model.setIsActive(true);
                        model.setIsDraft(false);
                        model.setSectionId(20L);
                        model.setContent(item.getJSONObject("style44").getString("text"));
                        this.postMapper.insert(model);

                    } else {
                        log.warn("帖子已经爬取过：{}", postId);
                    }

                }
            }
        }
        return postId;
    }
}
