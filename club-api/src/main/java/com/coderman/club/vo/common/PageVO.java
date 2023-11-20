package com.coderman.club.vo.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author coderman
 * @Title: 分页对象
 * @Description: TOD
 * @date 2022/1/1521:32
 */
@Data
public class PageVO<T> {

    /**
     * 当前页码
     */
    private Integer currPage;

    /**
     * 页码显示条数
     */
    private Integer pageRow = 30;

    /**
     * 总页数
     */
    @JsonIgnore
    private Integer totalPage;

    /**
     * 总条数
     */
    private Long totalRow;

    /**
     * 数据集合
     */
    private T dataList;

    public PageVO() {
    }

    public PageVO(long totalRow, T dataList) {
        this.totalRow = totalRow;
        this.dataList = dataList;
    }


    public PageVO(long totalRow, T dataList, int currPage, int pageRow) {
        this.currPage = currPage;
        this.pageRow = pageRow;
        this.totalRow = totalRow;
        this.dataList = dataList;
    }
}
