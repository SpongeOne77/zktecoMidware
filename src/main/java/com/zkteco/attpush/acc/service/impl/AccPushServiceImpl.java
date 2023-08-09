package com.zkteco.attpush.acc.service.impl;

import com.alibaba.fastjson.JSON;
import com.zkteco.attpush.acc.service.AccPushService;
import com.zkteco.attpush.entity.Employee;
import com.zkteco.attpush.entity.EmployeeSignInOffEntity;
import com.zkteco.attpush.entity.config.Device;
import com.zkteco.attpush.entity.config.DeviceConfig;
import com.zkteco.attpush.mapper.BizAccessInfoMapper;
import com.zkteco.attpush.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class AccPushServiceImpl implements AccPushService {
    @Autowired
    public BizAccessInfoMapper bizAccessInfoMapper;

    public final static List<Employee> cachedEmployeesServer = new ArrayList<>();

    public final static List<Employee> cachedEmployeesDevice = new ArrayList<>();

    @Autowired
    public DeviceConfig deviceConfig;

    @Value("${uploadUrl}")
    private String uploadUrl;


    /**
     * @param rawRecord
     */
    @Override
    public void processNewPhoto(Map<String, String> rawRecord) {
        String employeeNo = rawRecord.get("pin");
        String content = rawRecord.get("content");
        for (Employee employee : cachedEmployeesServer) {
            if (employee.getEmployeeNumber().equals(employeeNo)) {
                employee.setEmployeePicture("data:image/jpeg;base64," + content);
                // upload employee info to server
                HttpClientUtil.post(uploadUrl + "/employee", JSON.toJSONString(employee));
                // setting up for employee info sent to device
                String SN = employee.getDevice();
                List<Device> devicesInSameArea = getDeviceInfoFromSameRegionBySN(SN);
                devicesInSameArea.forEach(device -> {
                    Employee tempEmployee = employee;
                    tempEmployee.setEmployeePicture(content);
                    tempEmployee.setDevice(device.getSN());
                    cachedEmployeesDevice.add(tempEmployee);
                    System.out.println("cached employees for device" + cachedEmployeesDevice);
//                String updateEmployeeInfoCommand = "C:296:DATA UPDATE userauthorize Pin=" + "1 AuthorizeTimezoneId=1 AuthorizeDoorId=1 DevID=1";
                });
                employee.setIsRecorded(true);
            }
        }
        // update cached employees for server, remove if the employee has been recorded
        cachedEmployeesServer.removeIf(employee -> employee.getIsRecorded().equals(true));
    }


    @Override
    public void processNewRecord(Map<String, String> rawRecord) {
        //grab info from rawRecord
        String cardNo = rawRecord.get("cardno");
        String employeeName = "";
        String employeeNumber = "";
        if ("0".equals(cardNo)) {
            employeeName = rawRecord.get("name");
            employeeNumber = rawRecord.get("pin");
        } else {
            employeeName = cardNo;
            employeeNumber = "V" + cardNo;
        }
        String SN = rawRecord.get("SN");
        //setup employee entity
        Employee newEmployee = new Employee();
        newEmployee.setEmployeeName(employeeName);
        newEmployee.setArea(getDeviceInfoFromSameRegionBySN(SN).get(0).getArea());
        newEmployee.setEmployeeNumber(employeeNumber);
        newEmployee.setDevice(SN);
        cachedEmployeesServer.add(newEmployee);
        System.out.println("cached employees for server" + cachedEmployeesServer);
    }

    public List<Device> getDeviceInfoFromSameRegionBySN(String SN) {
        String region = deviceConfig.getDeviceList().stream().filter(device -> device.getSN().equals(SN)).collect(Collectors.toList()).get(0).getArea();
        return deviceConfig.getDeviceList().stream().filter(device -> device.getArea().equals(region)).collect(Collectors.toList());
    }



    /**
     * when enrolled employee sign in/out
     *
     * @param rawRecord
     */
    @Override
    public boolean processSignInOut(Map<String, String> rawRecord) {
        EmployeeSignInOffEntity tempEmployee = new EmployeeSignInOffEntity();
        tempEmployee.setTime(rawRecord.get("time"));
        tempEmployee.setEmployeeNumber(rawRecord.get("pin"));
        Device device = getDeviceInfoFromSameRegionBySN(rawRecord.get("SN")).get(0);
        tempEmployee.setInoutStatus(device.getDirection());
        tempEmployee.setArea(device.getArea());
        System.out.println("this person is sign " + ("0".equals(tempEmployee.getInoutStatus()) ? "in" : "off"));
        System.out.println(tempEmployee);
        if ("1".equals(tempEmployee.getInoutStatus())) {
            //if the person is not in site(no signing in), then do not upload
            if (!bizAccessInfoMapper.getByAreaAndPin(tempEmployee).isEmpty()) {
                CompletableFuture.runAsync(() -> {
                    verifyAndRecordLogs(tempEmployee);
                });
            } else {
                System.out.println(tempEmployee.getEmployeeNumber() + " not in " + tempEmployee.getArea());
            }
            return true;
        } else {
            return "true".equals(verifyAndRecordLogs(tempEmployee));
        }
    }

    private String verifyAndRecordLogs(EmployeeSignInOffEntity record) {
        String res = HttpClientUtil.post(uploadUrl + "/real/event", JSON.toJSONString(record));
        Map resObj = (Map) JSON.parse(res);
        System.out.println("============================= access " + resObj.get("data").toString() + "=============================");
        return resObj.get("data").toString();
    }


    public void test() {
        System.out.println(getDeviceInfoFromSameRegionBySN("CJDE231960054"));
    }

}
