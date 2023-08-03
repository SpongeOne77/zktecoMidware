package com.zkteco.attpush.entity;

import lombok.Data;

@Data
public class Employee {

    private String employeeName;

    private String employeeNumber;

    /**
     * area which the employee belongs to
     */
    private String area;

    /**
     * SN of the device which the employee belongs to
     */
    private String device;


    private String employeePicture;
}
