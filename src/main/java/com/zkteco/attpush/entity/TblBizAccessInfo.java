package com.zkteco.attpush.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@TableName(value = "TblBizAccessInfo", autoResultMap = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TblBizAccessInfo implements Serializable {

    @TableId
    private long id;

    private long employeeId;

    private String employeeNumber;

    private String employeeName;

    @TableField
    private String area;

    private String time;

    private Date createTime;
}
