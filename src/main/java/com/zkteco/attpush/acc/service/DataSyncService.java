package com.zkteco.attpush.acc.service;

import com.zkteco.attpush.entity.Employee;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DataSyncService {
    void restoreRecords(List<Employee> records, String SN);
}
