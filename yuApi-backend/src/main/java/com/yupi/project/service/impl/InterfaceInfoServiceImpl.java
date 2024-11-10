package com.yupi.project.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.common.model.entity.InterfaceInfo;
import com.yupi.project.common.ErrorCode;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.model.enums.InterfaceInfoStatusEnum;
import com.yupi.project.service.InterfaceInfoService;
import com.yupi.project.mapper.InterfaceInfoMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author 16247
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2024-10-30 16:19:34
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService{

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean b) {
        if(interfaceInfo == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        String description = interfaceInfo.getDescription();
        String url = interfaceInfo.getUrl();
        String requestHeader = interfaceInfo.getRequestHeader();
        String responseHeader = interfaceInfo.getResponseHeader();
        Integer status = interfaceInfo.getStatus();
        String method = interfaceInfo.getMethod();

        if(b){
            if(StringUtils.isAnyBlank(name)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if(StringUtils.isNotBlank(name) && name.length() > 50){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"名称过长");
        }
        if(StringUtils.isNotBlank(description) && description.length() > 1000){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"描述过长");
        }
        if(StringUtils.isNotBlank(url) && url.length() > 500){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"url过长");
        }
        if(status!=null && !InterfaceInfoStatusEnum.getValues().contains(status)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"状态不符合要求");
        }
    }
}




