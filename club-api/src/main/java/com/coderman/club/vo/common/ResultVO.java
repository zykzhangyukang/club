package com.coderman.club.vo.common;

import com.coderman.club.model.common.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author coderman
 * @Title: 公共返回对象
 * @Description: TOD
 * @date 2022/1/115:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultVO<T> extends BaseModel {

    /**
     * 状态码
     */
    private Integer code = -1;

    /**
     * 返回消息
     */
    private String msg;


    /**
     * 响应结果
     */
    private T result;
}
