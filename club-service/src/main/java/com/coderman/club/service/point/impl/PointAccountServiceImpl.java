package com.coderman.club.service.point.impl;

import com.coderman.club.dao.point.PointAccountDAO;
import com.coderman.club.model.point.PointAccountModel;
import com.coderman.club.service.point.PointAccountService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author ：zhangyukang
 * @date ：2023/12/01 11:57
 */
@Service
public class PointAccountServiceImpl implements PointAccountService {

    @Resource
    private PointAccountDAO pointAccountDAO;

    @Override
    public void insertSelective(PointAccountModel pointAccountModel) {
        if(pointAccountModel == null){
            return;
        }
        this.pointAccountDAO.insertSelective(pointAccountModel);
    }
}
