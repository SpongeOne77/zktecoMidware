package com.zkteco.attpush.mapper;

import com.zkteco.attpush.entity.EmployeeSignInOffEntity;
import com.zkteco.attpush.entity.TblBizAccessInfo;

import java.util.List;

public interface BizAccessInfoMapper {
    public List<TblBizAccessInfo> getByArea(String area);

    public List<TblBizAccessInfo> getByAreaAndPin(EmployeeSignInOffEntity currentEmployee);
}
