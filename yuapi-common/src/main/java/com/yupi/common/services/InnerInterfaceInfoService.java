package com.yupi.common.services;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.common.model.entity.InterfaceInfo;


/**
* @author 16247
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-10-30 16:19:34
*/
public interface InnerInterfaceInfoService  {

    /**
     * 获取接口信息
     * @param url
     * @param method
     * @return
     */
    InterfaceInfo getInterfaceInfo(String url, String method);
}
