package com.zkteco.attpush.mapper;

import com.zkteco.attpush.entity.EmployeeSignInOffEntity;
import com.zkteco.attpush.entity.TblBizAccessInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BizAccessInfoMapper {
    public List<TblBizAccessInfo> getByArea(String area);

    public List<TblBizAccessInfo> getByAreaAndPin(EmployeeSignInOffEntity currentEmployee);

    public List<TblBizAccessInfo> getAllPersonnel();

}
