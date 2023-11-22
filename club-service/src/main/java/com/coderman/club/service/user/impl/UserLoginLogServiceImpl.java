package com.coderman.club.service.user.impl;

import com.coderman.club.dao.user.UserLoginLogDAO;
import com.coderman.club.model.user.UserLoginLogModel;
import com.coderman.club.service.user.UserLoginLogService;
import com.coderman.club.utils.HttpContextUtil;
import com.coderman.club.utils.IpUtil;
import com.coderman.club.vo.user.AuthUserVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author ：zhangyukang
 * @date ：2023/11/22 17:31
 */
@Service
public class UserLoginLogServiceImpl implements UserLoginLogService {

    @Resource
    private UserLoginLogDAO userLoginLogDAO;

    @Override
    public void insertLoginLog(AuthUserVO authUserVO, Date loginTime) {

        if(authUserVO == null){
            return;
        }

        Long userId = authUserVO.getUserId();
        UserLoginLogModel insertModel = new UserLoginLogModel();
        insertModel.setUserId(userId);
        insertModel.setLoginTime(loginTime);
        insertModel.setIpAddress(IpUtil.getIp(HttpContextUtil.getHttpServletRequest()));
        insertModel.setDeviceInfo(IpUtil.getClientDeviceInfo(HttpContextUtil.getHttpServletRequest()));
        insertModel.setLocation(IpUtil.getCityInfo(insertModel.getIpAddress()));
        this.userLoginLogDAO.insertSelective(insertModel);
    }



}
