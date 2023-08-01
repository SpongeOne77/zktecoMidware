package com.zkteco.attpush.acc.service;

import com.zkteco.attpush.entity.TblBizAccessInfo;
import org.springframework.stereotype.Service;

public interface BizAccessInfoService {
    TblBizAccessInfo getByArea(String area);
}
