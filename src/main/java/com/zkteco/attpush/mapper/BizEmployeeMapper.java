package com.zkteco.attpush.mapper;

import com.zkteco.attpush.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BizEmployeeMapper {
    public List<Employee> getByArea(String area);

    public void deleteByPin(String pin, String area);

    public void batchDeleteByPins(@Param("pins") List<String> pins,@Param("area") String area);
}
