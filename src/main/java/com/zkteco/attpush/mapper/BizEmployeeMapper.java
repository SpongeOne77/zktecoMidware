package com.zkteco.attpush.mapper;

import com.zkteco.attpush.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BizEmployeeMapper {
    public List<Employee> getByArea(String area);
}
