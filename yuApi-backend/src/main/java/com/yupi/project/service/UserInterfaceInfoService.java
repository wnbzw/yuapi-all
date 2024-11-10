package com.yupi.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.common.model.entity.UserInterfaceInfo;

/**
* @author 16247
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2024-11-03 22:50:09
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean b);

    /**
     * 调用次数加一
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}
