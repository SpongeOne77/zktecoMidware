package com.zkteco.attpush.entity;

import lombok.Data;

@Data
public class NewPersonnelRecord {
    private String employeeName;//姓名
    private String employeeNumber;//工号
    private String area;//区域
    private String employeePicture;//登记图片
}
