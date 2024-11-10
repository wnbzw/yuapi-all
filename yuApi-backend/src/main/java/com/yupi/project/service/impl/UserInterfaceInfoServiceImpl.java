package com.yupi.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.common.model.entity.UserInterfaceInfo;
import com.yupi.project.common.ErrorCode;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.service.UserInterfaceInfoService;
import com.yupi.project.mapper.UserInterfaceInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author 16247
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
* @createDate 2024-11-03 22:50:09
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService{

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean b) {
        if(userInterfaceInfo==null||userInterfaceInfo.getId()<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if(b){
            if(userInterfaceInfo.getInterfaceInfoId()<=0||userInterfaceInfo.getUserId()<=0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口或者用户不存在");
            }
        }
        if(userInterfaceInfo.getLeftNum()<0||userInterfaceInfo.getTotalNum()<0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"剩余调用次数不能小于0");
        }
    }

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        if(interfaceInfoId<=0||userId<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //使用UpdateWrapper对象
        return this.update()
                .setSql("leftNum=leftNum-1 ,totalNum=totalNum+1")
                .eq("interfaceInfoId",interfaceInfoId)
                .eq("userId",userId)
                .gt("leftNum",0)
                .update();
    }
}




