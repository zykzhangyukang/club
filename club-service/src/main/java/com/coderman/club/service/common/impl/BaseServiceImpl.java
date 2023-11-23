package com.coderman.club.service.common.impl;

import com.coderman.club.dao.common.BaseDAO;
import com.coderman.club.service.common.BaseService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Administrator
 */
public class BaseServiceImpl<T,E>  implements BaseService<T,E> {

    protected final  Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Resource
    private BaseDAO<T,E> baseDAO;

    /**
     * 查询对应条件数据的行数
     *
     * @param example
     * @return
     */
    @Override
    public Long countByExample(E example){

        return this.baseDAO.countByExample(example);
    }

    /**
     * 根据条件删除数据
     *
     * @param example
     * @return
     */
    @Override
    public int deleteByExample(E example){

        return this.baseDAO.deleteByExample(example);
    }

    /**
     * 根据主键删除id
     *
     * @param primaryKey
     * @return
     */
    @Override
    public int deleteByPrimaryKey(Integer primaryKey){

        return this.baseDAO.deleteByPrimaryKey(primaryKey);
    }

    /**
     * 根据主键删除id
     *
     * @param primaryKey
     * @return
     */
    @Override
    public int deleteByPrimaryKey(String primaryKey){

        return this.baseDAO.deleteByPrimaryKey(primaryKey);
    }


    /**
     * 插入
     *
     * @param record
     * @return
     */
    @Override
    public int insert(T record){

        return this.baseDAO.insert(record);
    }

    /**
     * 动态插入
     *
     * @param record
     * @return
     */
    @Override
    public int insertSelective(T record){

        return this.baseDAO.insertSelective(record);
    }

    /**
     * 根据条件查询
     *
     * @param example
     * @return
     */
    @Override
    public List<T> selectByExample(E example){

        return this.baseDAO.selectByExample(example);
    }


    /**
     * 根据主键查询
     *
     * @param primaryKey
     * @return
     */
    @Override
    public T selectByPrimaryKey(Integer primaryKey){

        return this.baseDAO.selectByPrimaryKey(primaryKey);
    }


    /**
     * 根据主键查询
     *
     * @param primaryKey
     * @return
     */
    @Override
    public T selectByPrimaryKey(Long primaryKey){

        return this.baseDAO.selectByPrimaryKey(primaryKey);
    }

    /**
     * 根据主键查询
     *
     * @param primaryKey
     * @return
     */
    @Override
    public T selectByPrimaryKey(String primaryKey){

        return this.baseDAO.selectByPrimaryKey(primaryKey);
    }

    /**
     * 动态更新
     *
     * @param record
     * @param example
     * @return
     */
    @Override
    public int updateByExampleSelective(@Param("record") T record, @Param("example") E example){

        return this.baseDAO.updateByExampleSelective(record,example);
    }

    /**
     * 更新
     *
     * @param record
     * @param example
     * @return
     */
    @Override
    public int updateByExample(@Param("record") T record, @Param("example") E example){

        return this.baseDAO.updateByExample(record,example);
    }

    /**
     * 根据主键动态更新
     *
     * @param record
     * @return
     */
    @Override
    public int updateByPrimaryKeySelective(T record){

        return this.baseDAO.updateByPrimaryKeySelective(record);
    }

    /**
     * 根据主键更新
     *
     * @param record
     * @return
     */
    @Override
    public int updateByPrimaryKey(T record){

        return this.baseDAO.updateByPrimaryKey(record);
    }


}
