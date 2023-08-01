package com.zkteco.attpush.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@TableName("tbl_biz_access_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TblBizAccessInfo implements Serializable {

    private long id;

    private long employeeId;

    private String employeeNumber;

    private String employeeName;

    private String area;

    private String time;

    private Date createTime;
}
