package com.zkteco.attpush.utils;

import com.alibaba.fastjson.JSON;
import com.zkteco.attpush.entity.EmployeeSignInOffEntity;
import com.zkteco.attpush.entity.TblBizAccessInfo;
import com.zkteco.attpush.entity.config.Jobs;
import com.zkteco.attpush.mapper.BizAccessInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class scheduledTask {

    @Autowired
    private BizAccessInfoMapper bizAccessInfoMapper;

    @Autowired
    private Jobs jobs;

    @Value("${uploadUrl}")
    private String uploadUrl;

    @Scheduled(cron = "${jobs.cron}")
    public void removeAbsentPersonnel() {
        // grab all access info
        List<TblBizAccessInfo> allPersonnelInfo = bizAccessInfoMapper.getAllPersonnel();
        System.out.println("select all access info" + allPersonnelInfo);
        // truncate table
//        bizAccessInfoMapper.clearAll();
        // emulate personnel exiting area
        System.out.println("emulating personnel exit area");
        System.out.println("time: " + new Date());
        allPersonnelInfo.forEach(personnel -> {
            EmployeeSignInOffEntity employeeSignInOffEntity = new EmployeeSignInOffEntity();
            employeeSignInOffEntity.setArea(personnel.getArea());
            employeeSignInOffEntity.setEmployeeNumber(personnel.getEmployeeNumber());
            employeeSignInOffEntity.setTime("time");
            employeeSignInOffEntity.setInoutStatus("1");
            HttpClientUtil.post(uploadUrl + "/real/event", JSON.toJSONString(employeeSignInOffEntity));
            System.out.println(employeeSignInOffEntity.getEmployeeNumber() + "  " + employeeSignInOffEntity.getArea());
        });
        System.out.println("emulation finished");
    }
}
