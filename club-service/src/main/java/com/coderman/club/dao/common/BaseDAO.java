package com.coderman.club.dao.common;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author coderman
 * @Title: baseDAO
 * @Description: TOD
 * @date 2022/5/2022:00
 */
public interface BaseDAO<T, E> {

    /**
     * 查询对应条件数据的行数
     *
     * @param example
     * @return
     */
    Long countByExample(E example);

    /**
     * 根据条件删除数据
     *
     * @param example
     * @return
     */
    int deleteByExample(E example);

    /**
     * 根据主键删除id
     *
     * @param primaryKey
     * @return
     */
    int deleteByPrimaryKey(Integer primaryKey);

    /**
     * 根据主键删除id
     *
     * @param primaryKey
     * @return
     */
    int deleteByPrimaryKey(String primaryKey);


    /**
     * 插入
     *
     * @param record
     * @return
     */
    int insert(T record);

    /**
     * 动态插入
     *
     * @param record
     * @return
     */
    int insertSelective(T record);

    /**
     * 根据条件查询
     *
     * @param example
     * @return
     */
    List<T> selectByExample(E example);


    /**
     * 根据主键查询
     *
     * @param primaryKey
     * @return
     */
    T selectByPrimaryKey(Integer primaryKey);

    /**
     * 根据主键查询
     *
     * @param primaryKey
     * @return
     */
    T selectByPrimaryKey(String primaryKey);

    /**
     * 动态更新
     *
     * @param record
     * @param example
     * @return
     */
    int updateByExampleSelective(@Param("record") T record, @Param("example") E example);

    /**
     * 更新
     *
     * @param record
     * @param example
     * @return
     */
    int updateByExample(@Param("record") T record, @Param("example") E example);

    /**
     * 根据主键动态更新
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(T record);

    /**
     * 根据主键更新
     *
     * @param record
     * @return
     */
    int updateByPrimaryKey(T record);

}
