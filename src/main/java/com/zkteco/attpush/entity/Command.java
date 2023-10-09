package com.zkteco.attpush.entity;

import lombok.Data;

@Data
public class Command {
    private String SN;
    private String cmd;
    private Boolean availability = true;
}
