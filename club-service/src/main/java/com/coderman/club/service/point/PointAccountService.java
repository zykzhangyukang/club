package com.coderman.club.service.point;

import com.coderman.club.model.point.PointAccountModel;

/**
 * @author Administrator
 */
public interface PointAccountService {

    /**
     * 新增
     * @param pointAccountModel
     */
    void insertSelective(PointAccountModel pointAccountModel);
}
