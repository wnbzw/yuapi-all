package com.yupi.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.common.model.entity.UserInterfaceInfo;

import java.util.List;

/**
* @author 16247
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Mapper
* @createDate 2024-11-03 22:50:09
* @Entity com.yupi.project.model.entity.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    // 获取最热门的接口信息
    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}




