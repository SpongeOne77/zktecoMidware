package com.zkteco.attpush.entity.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class Device implements Serializable {
    private String SN;
    private String area;
    private String direction;
}
