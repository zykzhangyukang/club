package com.coderman.club.service.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coderman.club.mapper.post.PostMapper;
import com.coderman.club.mapper.user.UserMapper;
import com.coderman.club.model.post.PostModel;
import com.coderman.club.model.user.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
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

    private CloseableHttpClient httpClient;

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
            return false;
        } else {
            // 设置位图的对应位为1
            stringRedisTemplate.opsForValue().setBit(BITMAP_KEY, offset, true);
            return true;
        }
    }


    @PostConstruct
    @SuppressWarnings("all")
    public void fetch() {

        // 创建连接池管理器
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);
        cm.setDefaultMaxPerRoute(20);

        // 创建 HttpClient 实例，并绑定连接池管理器
        httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();

        userIdList = this.userMapper.selectList(null).stream().map(UserModel::getUserId).collect(Collectors.toList());

        // 爬虫线程1
//        String lastBeforeId = this.stringRedisTemplate.opsForValue().get(BEFORE_POST_ID);
//        Thread thread1 = new Thread(() -> {
//
//            String initialBeforeId = lastBeforeId == null ? "1830209586" : lastBeforeId;
//            try {
//
//                while (StringUtils.isNotBlank(initialBeforeId)) {
//                    initialBeforeId = makeRequest1(initialBeforeId);
//                    this.stringRedisTemplate.opsForValue().set(BEFORE_POST_ID, initialBeforeId);
//                    TimeUnit.SECONDS.sleep(1);
//                }
//
//            } catch (InterruptedException e) {
//                log.error("脉脉帖子爬虫出错了 :{}", e.getMessage(), e);
//            }
//        });
//        thread1.setDaemon(true);
//        thread1.setName("脉脉帖子爬虫线程");
//        thread1.setUncaughtExceptionHandler((t, e) -> {
//            if (StringUtils.equals(t.getName(), "脉脉帖子爬虫线程")) {
//                fetch();
//            }
//        });
//        thread1.start();

        // 爬虫线程2
        Thread thread2 = new Thread(() -> {
            try {
                mackRequest2();
            } catch (Exception e) {
                log.error("Segment帖子出错了 :{}", e.getMessage(), e);
            }
        });

        thread2.setDaemon(true);
        thread2.setName("Segment帖子爬虫线程");
        thread2.setUncaughtExceptionHandler((t, e) -> {
            if (StringUtils.equals(t.getName(), "Segment帖子爬虫线程")) {
                fetch();
            }
        });
        thread2.start();
    }

    public void mackRequest2() throws Exception {

        // API端点的URL
        String baseUrl = "https://segmentfault.com/gateway/questions?query=newest&page=";

        // 循环遍历每一页
        int page = 1;
        while (true) {

            // 构建当前页的URL
            String url = baseUrl + page + "&size=30";

            // 创建GET请求
            HttpGet httpGet = new HttpGet(url);

            // 设置请求头
            httpGet.setHeader("Accept", "application/json, text/plain, */*");
            httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            httpGet.setHeader("Connection", "keep-alive");
            httpGet.setHeader("Cookie", "_ga=GA1.1.775689204.1685506044; _ga_D9Y9QQWNVD=GS1.1.1687918469.1.0.1687918480.0.0.0; __gads=ID=ae2470900d942630-22876d3277e20059:T=1688346433:RT=1696755632:S=ALNI_MZWCt7L0IbT3r5GpxMzsfBezfvfTw; __gpi=UID=00000c906e78f890:T=1688346433:RT=1696755632:S=ALNI_Mb8HJ4FyYMLfU11dm22-xPHt5IL9Q; PHPSESSID=4377f955c72735f6d501e0869f46f58b; Hm_lvt_e23800c454aa573c0ccb16b52665ac26=1715933784,1716354244; Hm_lpvt_e23800c454aa573c0ccb16b52665ac26=1716354244; acw_tc=2760775517163587180512686e64f3a76ed962458342d78055735692bcc0d2; _ga_MJYFRXB3ZX=GS1.1.1716358718.50.0.1716358718.0.0.0; ssxmod_itna=Yq+ODK7KGKzhkDz=DUCjrRwDjoxfOOAEMY4DvT+QD/Y/+DnqD=GFDK40EASpTTeq/CBu5FqTmxitYC1BvdI572WoLEDwDB3DEx06eq+jY4GGRxBYDQxAYDGDDPDogPD1D3qDkD72=1lgx5Dbxi3UxDbDimkAxGCDeKD0=qHDQKDugI55eyxDGyxhbmbK77+CcB1aFH4obDxUxG1H40HsidvrFM65nH+6/3hjGKDXrQDvpy1/gapWeysSrBed0RYrDnor7exoi0o36G4CiKxQcZxP0ie3pUbXVD5OerbLCDDir0xLlSlper2iIR7IuirKfF7bDdthdURe8yq85eFGDD==; ssxmod_itna2=Yq+ODK7KGKzhkDz=DUCjrRwDjoxfOOAEMY4DvT4G9F5BYDBwMOD7phvPGCjHGb0gnv7D8hG6+ybQ07+mYO8m05kvjoaE=4eFjZD2k1S6wY5CNpFZGXq2iFlx9IvNS=r+7RdKeLQX31F4RWyDaoc=31PzKacrdcekPo7TNan+=pACbogDoD9CRl+N/ZFtkxEEZC7TeC0aypHxBGct2Cqks0rQ1tvrot0oN6qeN7ntM6TWU1galcT4KHn4tM8E9pn=lA5WkKoWj/IpCgkNlguy5/v=zC10k2xkOg8aeCR1=antZ7D");

            // 执行请求并获取响应
            HttpResponse response = httpClient.execute(httpGet);
            TimeUnit.SECONDS.sleep(2);

            // 获取响应代码
            int statusCode = response.getStatusLine().getStatusCode();

            // 如果响应代码为200（HTTP.OK），读取响应
            if (statusCode == 200) {
                // 读取响应内容
                String responseBody = EntityUtils.toString(response.getEntity());

                JSONObject jsonObject = JSON.parseObject(responseBody);
                for (Object item : jsonObject.getJSONArray("rows")) {

                    JSONObject itemObj = (JSONObject) item;
                    String postId = itemObj.getString("id");
                    String title = itemObj.getString("title");

                    if (isPostUnique("SEGMENT"+postId)) {


                        String content = getDetail(postId);
                        if(StringUtils.isNotBlank(content)){
                            log.info("爬取文章 postId:{}, title:{}", postId, title);
                            this.insertToDb(20L, title, content);

                        }
                    } else {
                        log.warn("帖子已经爬取过：{}", postId);
                    }
                }

            } else {
                System.err.println("GET请求失败，响应代码：" + statusCode);
            }
            page++;
        }
    }

    private String getDetail(String postId) throws IOException {

        String content = StringUtils.EMPTY;

        // 请求的URL
        String url = "https://segmentfault.com/q/"+postId;

        // 创建GET请求
        HttpGet httpGet = new HttpGet(url);


        // new
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Cookie", "_ga=GA1.1.775689204.1685506044; _ga_D9Y9QQWNVD=GS1.1.1687918469.1.0.1687918480.0.0.0; __gads=ID=ae2470900d942630-22876d3277e20059:T=1688346433:RT=1696755632:S=ALNI_MZWCt7L0IbT3r5GpxMzsfBezfvfTw; __gpi=UID=00000c906e78f890:T=1688346433:RT=1696755632:S=ALNI_Mb8HJ4FyYMLfU11dm22-xPHt5IL9Q; PHPSESSID=4377f955c72735f6d501e0869f46f58b; Hm_lvt_e23800c454aa573c0ccb16b52665ac26=1715933784,1716354244; acw_tc=2760775817163633424474838e0df4ab6a75070440b51fb77a6dca15f2d33c; acw_sc__v2=664da04ed076a72e418fd71f45a03593cb6d8376; Hm_lpvt_e23800c454aa573c0ccb16b52665ac26=1716363356; _ga_MJYFRXB3ZX=GS1.1.1716361378.51.1.1716363356.0.0.0; ssxmod_itna=iqmh7KDKAIeRgDUxBPxBb4Nx2eyDkt4NF/btKIKCBDBkkO4iNDnD8x7YDvIj5D0orpee8j7rLK5BBGi+Ho4/fguIt8ZWBbDHxY=DU1GGfoD4b5GwD0eG+DD4DWDmnHDnxAQDjxGPyn2v5CDYPDEBKDYxDrUhKDRxi7DD5=1x07DQvkhmiCoDDzoe3qtjxxKI7H=zHpc1i+kjhkD7ypDlc4cROm9+k4ZpAL0b7dAx0kPq0Og1vssaEP2ZvzeF2D6ii34r0NTGnK5j2NtQ0osmRK=Z=rsv+qegDlrlx5TYnKcqDWqQV7yq+iKdOdLONk4CLYX0oXB5XDda01r01qY4qiDD; ssxmod_itna2=iqmh7KDKAIeRgDUxBPxBb4Nx2eyDkt4NF/btKIKCD8wPDvqD/9bY+DFh7MDNFKidk0oCDtNx54QyLYxojGB5Cq/en2WGe0ZO2T4o0=WbRtB4s7K0av2ptBhhA5eQylvw1LtbXBSq/2FPl5iT6rWAiTWkZIOgQ0KooIGf=7tXxWNhZha5BikoUxWeOF+Gpa8V2wIfb0dGbHq+ZyDhOnkelo+nrGrdrdrKlH78=K1//gkteFXsFqsN=itLxI7sl+H8PhrsmC4NZ+rdMKxD/OUFV4CTHZxpXKMHfKS8cHm4rQSUq70w1NNm83MTYkrYYS1TdOXddX8GZDY=lDjDDw2q4nE0WwHQP0ePd9EhArtRD20DDjKDedx4D==");
        httpGet.setHeader("Referer", "https://segmentfault.com/q/1010000044901048");
        httpGet.setHeader("Sec-Fetch-Dest", "document");
        httpGet.setHeader("Sec-Fetch-Mode", "navigate");
        httpGet.setHeader("Sec-Fetch-Site", "same-origin");
        httpGet.setHeader("Sec-Fetch-User", "?1");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");
        httpGet.setHeader("sec-ch-ua", "\"Chromium\";v=\"124\", \"Google Chrome\";v=\"124\", \"Not-A.Brand\";v=\"99\"");
        httpGet.setHeader("sec-ch-ua-mobile", "?0");
        httpGet.setHeader("sec-ch-ua-platform", "\"Windows\"");

        // 执行请求并获取响应
        HttpResponse response = httpClient.execute(httpGet);

        // 获取响应代码
        int statusCode = response.getStatusLine().getStatusCode();

        // 如果响应代码为200（HTTP.OK），读取响应
        if (statusCode == 200) {
            // 读取响应内容
            String responseBody = EntityUtils.toString(response.getEntity());
            Document doc = Jsoup.parse(responseBody);

            // 获取 article-content 元素
            Elements elementsByClass = doc.getElementsByClass("article-content");
            if(CollectionUtils.isNotEmpty(elementsByClass)){

                // 取第一个元素
                Element firstElement = elementsByClass.first();
                content = firstElement == null ? "" : firstElement.html();
            }

        } else {
           log.error("GET请求失败，响应代码：" + statusCode);
        }

        return content;
    }

    public String makeRequest1(String beforeId) {
        String postId = "";

        try {

            // API端点的URL
            String url = "https://maimai.cn/sdk/web/content/get_list?api=feed%2Fv5%2Fnd1feed&u=237534056&page=1&before_id=" + beforeId;

            // 创建GET请求
            HttpGet httpGet = new HttpGet(url);

            // 设置请求头
            setRequestHeaders(httpGet);

            // 执行请求并获取响应
            HttpResponse response = httpClient.execute(httpGet);

            // 获取响应代码
            int responseCode = response.getStatusLine().getStatusCode();

            // 如果响应代码为200（HTTP.OK），读取响应
            if (responseCode == 200) {

                postId = handleResponse(response);
            } else {
                log.error("GET请求失败，响应代码：" + responseCode);
            }

        } catch (Exception e) {
            log.error("爬虫请求错误了：{}", e.getMessage(), e);
        }

        return postId;
    }

    private void setRequestHeaders(HttpGet httpGet) {

        httpGet.setHeader("Accept", "*/*");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpGet.setHeader("Cookie", "_buuid=66sWqHzmIQEsDe_3Ejx-G; _buuid.sig=n72jzffqYgwz-hOkY6li5vAmYm4; guid=GxsZBBsbEgQYGB4EGxoaVgcYGxkdEhMcGR4cVhwZBB0ZHwVDWEtMS3kKGgQaBBoEHhgbBU9HRVhCaQoDRUFJT20KT0FDRgoGZmd+YmECChwZBB0ZHwVeQ2FIT31PRlpaawoDHhxSChEeHERDfQoRGgQaGwp+ZApZXUVOREN9AgoaBB8FS0ZGQ1BFZw==; AGL_USER_ID=07914d23-0f6d-4ce7-be6c-5f80ba617553; browser_fingerprint=DA3A476C; u=237534056; u.sig=0u0uq7XCHFfiqtQRc1Bb2pBimEc; access_token=1.982898c17c17f3da4715817f915ff5c8; access_token.sig=Cu3ov9rvum7w2UEJnHs6TTozoSA; u=237534056; u.sig=0u0uq7XCHFfiqtQRc1Bb2pBimEc; access_token=1.982898c17c17f3da4715817f915ff5c8; access_token.sig=Cu3ov9rvum7w2UEJnHs6TTozoSA; channel=www; channel.sig=tNJvAmArXf-qy3NgrB7afdGlanM; maimai_version=4.0.0; maimai_version.sig=kbniK4IntVXmJq6Vmvk3iHsSv-Y; csrftoken=HcQ3tFYg-chR92mXsUTwVxUWhW06frspvIXY; session=eyJzZWNyZXQiOiJMNURKZ1ZteV9saGpvZEJneWI0bWM1YjMiLCJ1IjoiMjM3NTM0MDU2IiwiX2V4cGlyZSI6MTcxNjM4ODM2OTc2MywiX21heEFnZSI6ODY0MDAwMDB9; session.sig=-i0cfTRVG_bzax2UyuTgaDBUcZE");
        httpGet.setHeader("Priority", "u=1, i");
        httpGet.setHeader("Referer", "https://maimai.cn/web/feed_explore");
        httpGet.setHeader("sec-ch-ua", "\"Chromium\";v=\"124\", \"Google Chrome\";v=\"124\", \"Not-A.Brand\";v=\"99\"");
        httpGet.setHeader("sec-ch-ua-mobile", "?0");
        httpGet.setHeader("sec-ch-ua-platform", "\"Windows\"");
        httpGet.setHeader("sec-fetch-dest", "empty");
        httpGet.setHeader("sec-fetch-mode", "cors");
        httpGet.setHeader("sec-fetch-site", "same-origin");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");
        httpGet.setHeader("x-csrf-token", "HcQ3tFYg-chR92mXsUTwVxUWhW06frspvIXY");
    }

    private String handleResponse(HttpResponse response) throws Exception {
        String postId = "";
        String responseBody = EntityUtils.toString(response.getEntity());

        JSONObject jsonObject = JSON.parseObject(responseBody);
        JSONArray list = jsonObject.getJSONArray("list");

        if (list != null) {
            for (Object o : list) {
                JSONObject item = (JSONObject) o;
                postId = item.getString("id");
                if (item.containsKey("style44")) {

                    if (isPostUnique("MAIMAI:"+ postId)) {
                        log.info("爬取帖子===============>>>>>" + postId);

                        this.insertToDb(80L, StringUtils.substring(item.getJSONObject("style44").getString("text").replaceAll("\\s+", ""), 0, 80),
                                item.getJSONObject("style44").getString("text"));

                    } else {
                        log.warn("帖子已经爬取过：{}", postId);
                    }

                }
            }
        }
        return postId;
    }

    private void insertToDb(Long sectionId, String title, String content) {

        PostModel model = new PostModel();
        model.setLastUpdatedAt(new Date());
        model.setCreatedAt(new Date());
        model.setTitle(title);
        model.setUserId(userIdList.get(new Random().nextInt(userIdList.size())));
        model.setIsActive(true);
        model.setIsDraft(false);
        model.setSectionId(sectionId);
        model.setContent(content);
        this.postMapper.insert(model);
    }

    @PreDestroy
    public void close() {
        try {
            if (httpClient != null) {
                httpClient.close();
            }
        } catch (IOException e) {
            log.error("关闭 HttpClient 错误：{}", e.getMessage(), e);
        }
    }
}
