package com.zkteco.attpush.entity;

import lombok.Data;

@Data
public class EmployeeSignInOffEntity {
    String time;//时间
    String employeeNumber;//工号
    String inoutStatus;// 0进 1出
    String area;//区域
}
