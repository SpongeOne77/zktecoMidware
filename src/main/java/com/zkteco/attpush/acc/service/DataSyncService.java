package com.zkteco.attpush.acc.service;

import com.zkteco.attpush.entity.Command;
import com.zkteco.attpush.entity.config.Device;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface DataSyncService {
    public void processNewPhoto(Map<String, String> record);

}
