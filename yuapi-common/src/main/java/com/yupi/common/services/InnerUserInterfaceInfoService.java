package com.yupi.common.services;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.common.model.entity.UserInterfaceInfo;

public interface InnerUserInterfaceInfoService  {

    /**
     * 调用次数加一
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);




}