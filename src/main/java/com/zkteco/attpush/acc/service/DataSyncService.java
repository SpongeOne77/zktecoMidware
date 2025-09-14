package com.zkteco.attpush.acc.service;

import com.zkteco.attpush.entity.Employee;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DataSyncService {
    void restoreRecords(List<Employee> records, String SN);

    void deleteUser(String SN, String Pin);

    void batchDeleteUsers(String SN, String[] Pins);

    void deleteBatchUsersByArea(String area, List<String> pins, Boolean deleteAll);

    void clearAllData(String SN);
}