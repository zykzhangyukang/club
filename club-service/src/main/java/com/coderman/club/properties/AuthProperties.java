package com.coderman.club.properties;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author ：zhangyukang
 * @date ：2024/06/06 14:41
 */
@ConfigurationProperties(prefix = "auth")
@Configuration
public class AuthProperties {

    @ApiModelProperty(value = "接口白名单")
    private List<String> whiteList;

    @ApiModelProperty(value = "token有效期（单位秒）")
    private Integer tokenExpiration;

    @ApiModelProperty(value = "刷新令牌有效期（单位秒）")
    private Integer refreshTokenExpiration;

    public List<String> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

    public Integer getTokenExpiration() {
        return tokenExpiration;
    }

    public void setTokenExpiration(Integer tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }

    public Integer getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public void setRefreshTokenExpiration(Integer refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }
}
