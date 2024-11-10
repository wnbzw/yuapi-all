package com.yupi.common.services;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.common.model.entity.User;


/**
 * 用户服务
 *
 * @author yupi
 */
public interface InnerUserService  {

    /**
     * 根据accessKey获取用户
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);
}
