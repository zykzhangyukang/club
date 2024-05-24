package com.coderman.club.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * @author ：zhangyukang
 * @date ：2023/11/22 18:03
 */
@Slf4j
public class IpUtil {

    private final static String LOCAL_IP = "127.0.0.1";
    public final static String DEFAULT_ADDRESS = "未知";

    public static String getCityInfo(String ip) {
        DbSearcher searcher = null;
        try {
            String dbPath = IpUtil.class.getResource("/ip2region/ip2region.db").getPath();
            File file = new File(dbPath);
            if (!file.exists()) {
                String tmpDir = System.getProperties().getProperty("java.io.tmpdir");
                dbPath = tmpDir + "ip.db";
                file = new File(dbPath);
                FileUtils.copyInputStreamToFile(Objects.requireNonNull(IpUtil.class.getClassLoader().getResourceAsStream("classpath:ip2region/ip2region.db")), file);
            }
            DbConfig config = new DbConfig();
            searcher = new DbSearcher(config, file.getPath());
            Method method = searcher.getClass().getMethod("btreeSearch", String.class);
            if (!Util.isIpAddress(ip)) {
                log.error("Error: Invalid ip address:{}", ip);
                return DEFAULT_ADDRESS;
            }
            // 格式为：国家|区域|省份|城市|运营商
            DataBlock dataBlock = (DataBlock) method.invoke(searcher, ip);
            return parseCityInfo(dataBlock.getRegion());
        } catch (Exception e) {
            log.error("获取地址信息异常:{}", e.getMessage());
        } finally {
            if (searcher != null) {
                try {
                    searcher.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return DEFAULT_ADDRESS;
    }


    private static String parseCityInfo(String region) {
        String result = DEFAULT_ADDRESS;
        if (StringUtils.isNotBlank(region)) {
            String[] parts = region.split("\\|");
            if (parts.length >= 4) {
                String city = parts[3];
                String province = parts[2];
                String country = parts[1];

                if (StringUtils.isNotBlank(province) && !StringUtils.equals("0", province)) {
                    result =  removeKeywords(province);
                } else if (StringUtils.isNotBlank(country) && !StringUtils.equals("0", country)) {
                    result =  removeKeywords(country);
                } else if (StringUtils.isNotBlank(city) && !StringUtils.equals("0", city)) {
                    result =  removeKeywords(city);
                }
            }
        }
        log.info("解析IP地址开始:{}, 解析结果:{}", region, result);
        return result;
    }

    private static String removeKeywords(String input) {
        return input.replaceAll("自治州|地区|行政单位|盟|市辖区|市|县|区|旗|海域|岛|自治区|特别行政区|省|壮族自治区|回族自治区|维吾尔自治区", StringUtils.EMPTY);
    }

    /**
     * @param request
     * @return
     */
    @SuppressWarnings("all")
    public static String getIp(HttpServletRequest request) {
        String ipAddress;
        try {
            // 以下两个获取在k8s中，将真实的客户端IP，放到了x-Original-Forwarded-For。而将WAF的回源地址放到了 x-Forwarded-For了。
            ipAddress = request.getHeader("X-Original-Forwarded-For");
            if (ipAddress == null || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("X-Forwarded-For");
            }
            //获取nginx等代理的ip
            if (ipAddress == null || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("x-forwarded-for");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ipAddress == null || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            // 2.如果没有转发的ip，则取当前通信的请求端的ip(兼容k8s集群获取ip)
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                // 如果是127.0.0.1，则取本地真实ip
                if (LOCAL_IP.equals(ipAddress)) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                        ipAddress = inet.getHostAddress();
                    } catch (UnknownHostException e) {
                        log.error("error:{}",e.getMessage(), e);
                    }
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) {
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            log.error("解析请求IP失败", e);
            ipAddress = "";
        }
        return "0:0:0:0:0:0:0:1".equals(ipAddress) ? LOCAL_IP : ipAddress;
    }


    @SuppressWarnings("all")
    public static String getClientDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String os = "Unknown";
        if (userAgent.toLowerCase().contains("windows")) {
            os = "Windows";
        } else if (userAgent.toLowerCase().contains("mac")) {
            os = "Mac OS";
        } else if (userAgent.toLowerCase().contains("linux")) {
            os = "Linux";
        } else if (userAgent.toLowerCase().contains("android")) {
            os = "Android";
        } else if (userAgent.toLowerCase().contains("iphone") || userAgent.toLowerCase().contains("ipad")) {
            os = "iOS";
        }
        return os;
    }


}
