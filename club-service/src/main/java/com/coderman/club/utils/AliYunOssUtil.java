package com.coderman.club.utils;


import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.ServiceException;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.*;
import com.coderman.club.enums.FileModuleEnum;
import com.coderman.club.properties.AliYunOssProperties;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author ：zhangyukang
 * @date ：2023/09/25 10:40
 */
@Component
public class AliYunOssUtil {

    private final AliYunOssProperties aliYunOssProperties;

    /**
     * 获取阿里云OSS客户端对象
     */
    private OSSClient ossClient;

    @Value("${spring.application.name:app}")
    private String applicationName;

    /**
     * 允许文件覆盖写
     */
    public static String FORBID_OVERWRITE_HEADER = "x-oss-forbid-overwrite";

    public AliYunOssUtil(AliYunOssProperties aliYunOssProperties) {
        this.aliYunOssProperties = aliYunOssProperties;
    }


    /**
     * 获取阿里云OSS客户端对象
     */
    private OSSClient getOssClient(){
        if(ossClient == null){
            ClientConfiguration conf = new ClientConfiguration();
            //设置OSSClient允许打开的最大HTTP连接数，默认为1024个。
            conf.setMaxConnections(1024);
            //OssApiUtil设置Socket层传输数据的超时时间，默认为50000毫秒。
            conf.setSocketTimeout(50000);
            //OssApiUtil设置建立连接的超时时间，默认为50000毫秒。
            conf.setConnectionTimeout(50000);
            ossClient = new OSSClient(aliYunOssProperties.getEndPoint(), new DefaultCredentialProvider(aliYunOssProperties.getAccessKeyId() ,aliYunOssProperties.getAccessKeySecret()), conf);
        }
        return ossClient;
    }

    /**
     * 上传文件 允许覆盖同名文件 多级目录可以直接生成
     *
     * @param inputStream 文件流
     * @param path        路径
     */
    public void uploadStream(InputStream inputStream, String path) {
        this.uploadFile(inputStream, path, true);
    }


    /**
     * 上传文件 不允许覆盖同名文件 多级目录可以直接生成
     *
     * @param inputStream 文件流
     * @param path        路径
     */
    public void uploadStreamIfNotExist(InputStream inputStream, String path) {
        this.uploadFile(inputStream, path, false);
    }

    /**
     * 获取文件上传的路径
     *
     * @param originalFilename 原文件
     * @param fileModuleEnum   上传模块
     * @return
     */
    @SneakyThrows
    public String genFilePath(String originalFilename, FileModuleEnum fileModuleEnum) {

        fileModuleEnum = Optional.ofNullable(fileModuleEnum).orElse(FileModuleEnum.COMMON_MODULE);
        Assert.isTrue(StringUtils.isNotBlank(originalFilename), "文件原始名称不能为空!!");

        int index = originalFilename.lastIndexOf(".");
        Assert.isTrue(index > 0, String.format("根据文件名%s获取文件类型失败!!", originalFilename));

        // 文件类型
        String fileType = originalFilename.substring(index + 1);
        Assert.isTrue(StringUtils.isNotBlank(fileType), String.format("根据文件名%s获取文件类型失败!!", originalFilename));

        Integer second = (int) Instant.now().getEpochSecond();
        String day = DateFormatUtils.format(new Date(), "yyyy-MM-dd");

        String newFileName;
        if (isImage(fileType)) {
            //图片文件名格式 = 当前日期+ 文件格式 + 文件名称
            newFileName = second + "_" + RandomStringUtils.randomAlphanumeric(4) + "." + fileType;
        } else {
            newFileName = second + "_" + URLEncoder.encode(originalFilename, StandardCharsets.UTF_8.name());
        }
        return String.format("%s/%s/%s/%s/%s", applicationName, fileModuleEnum.getCode(), day, fileType, newFileName);
    }


    /**
     * 判断是否是图片
     *
     * @param fileType 文件类型
     */
    protected static boolean isImage(String fileType) {
        if (StringUtils.isNotBlank(fileType)) {
            List<String> allImages = Arrays.asList(
                    "jpg",
                    "jpeg",
                    "png",
                    "gif",
                    "tiff",
                    "psd",
                    "pdf",
                    "eps",
                    "ai",
                    "indd",
                    "raw"
            );
            return allImages.contains(fileType.toLowerCase());
        }
        return false;
    }


    /**
     * 简单上传文件 不允许覆盖同名文件 多级目录可以直接生成
     *
     * @param inputStream 文件流
     * @param path        路径
     * @param overWrite   是否支持覆盖
     */
    protected PutObjectResult uploadFile(InputStream inputStream, String path, boolean overWrite) {

        // 创建OSSClient的实例
        OSSClient ossClient = getOssClient();
        if (!overWrite) {
            boolean exist = this.existFile(aliYunOssProperties.getBucketName(), path);
            if (exist) {
                return null;
            }
        }
        PutObjectRequest putObjectRequest = new PutObjectRequest(aliYunOssProperties.getBucketName(), path, inputStream);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setHeader(FORBID_OVERWRITE_HEADER, !overWrite);
        putObjectRequest.setMetadata(metadata);
        PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
        this.ossConClose(putObjectResult);
        return putObjectResult;
    }

    /**
     * 判断文件对象是否存在
     *
     * @param bucketName 桶名称
     * @param fullPath 路径
     * @return
     */
    public boolean existFile(String bucketName, String fullPath) {
        OSSClient ossClient = getOssClient();
        return ossClient.doesObjectExist(bucketName, fullPath);
    }

    /**
     * 创建存储空间
     *
     * @param bucketName 存储空间
     */
    public String createBucketName(String bucketName) {
        // 存储空间
        OSSClient ossClient = getOssClient();
        GenericResult result = null;
        try {
            // 创建CreateBucketRequest对象。
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
            // 如果创建存储空间的同时需要指定存储类型和数据容灾类型, 请参考如下代码。
            // 设置存储空间的权限为公共读，默认为私有。
            createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
            // 创建存储空间。
            result = ossClient.createBucket(createBucketRequest);
        } catch (Exception oe) {
            throw new ServiceException("创建bucket失败:" + oe.getMessage());
        } finally {
            this.ossConClose(result);
        }
        return bucketName;
    }

    /**
     * 调用完毕后进行关闭
     */
    private void ossConClose(GenericResult result) {
        if (result != null) {
            try {
                result.getResponse().close();
            } catch (Exception ignored) {
            }
        }
    }

}
