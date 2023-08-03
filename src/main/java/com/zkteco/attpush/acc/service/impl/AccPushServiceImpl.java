package com.zkteco.attpush.acc.service.impl;

import com.alibaba.fastjson.JSON;
import com.zkteco.attpush.acc.service.AccPushService;
import com.zkteco.attpush.entity.Employee;
import com.zkteco.attpush.entity.EmployeeSignInOffEntity;
import com.zkteco.attpush.entity.NewPersonnelRecord;
import com.zkteco.attpush.entity.config.Device;
import com.zkteco.attpush.entity.config.DeviceConfig;
import com.zkteco.attpush.mapper.BizAccessInfoMapper;
import com.zkteco.attpush.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class AccPushServiceImpl implements AccPushService {
    @Autowired
    public BizAccessInfoMapper bizAccessInfoMapper;

    public final static Map<String, NewPersonnelRecord> registerMembers = new HashMap();

    public final static List<Employee> cachedEmployees = new ArrayList<>();

    @Value("#{${area}}")
    private Map<String, Map<String, String>> machineMap;

    @Autowired
    public DeviceConfig deviceConfig;

    @Value("${uploadUrl}")
    private String uploadUrl;


    /**
     * @param rawRecord
     */
    @Override
    public void processNewPhoto(Map<String, String> rawRecord) {
        String recordNo = rawRecord.get("pin");
        if (registerMembers.containsKey(recordNo)) {
            NewPersonnelRecord personnelRecord = registerMembers.get(recordNo);
            personnelRecord.setEmployeePicture("data:image/jpeg;base64," + rawRecord.get("content"));
            HttpClientUtil.post(uploadUrl + "/employee", JSON.toJSONString(personnelRecord));
            registerMembers.remove(recordNo);
        }
    }

    @Override
    public void processNewRecord(Map<String, String> rawRecord) {
        //TODO cache personnel info
        //TODO cache devices' SN in the same region together with region name and cached personnel info
        //TODO upload person's info to server with different region names
        String cardNo = rawRecord.get("cardno");
        if ("0".equals(cardNo)) {
            String SN = rawRecord.get("SN");
            List<Device> devicesInSameArea = getSameRegionDevices(SN);
            devicesInSameArea.forEach(device -> {
                Employee newEmployee = new Employee();
                newEmployee.setEmployeeName(rawRecord.get("name"));
                newEmployee.setArea(rawRecord.get("area"));
                newEmployee.setEmployeeNumber(rawRecord.get("pin"));
                newEmployee.setDevice(SN);
                //TODO

            });
            String employeeNumber = rawRecord.get("pin");
            NewPersonnelRecord tempRecord = new NewPersonnelRecord();
            Map<String, String> trendInfo = calcTrendInfo(rawRecord.get("SN"));
            registerMembers.put(employeeNumber, tempRecord);
            System.out.println("cached employee" + registerMembers);
        } else {

        }
    }

    public List<Device> getSameRegionDevices(String SN) {
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
        Map<String, String> trendInfo = calcTrendInfo(rawRecord.get("SN"));
        tempEmployee.setInoutStatus(trendInfo.get("direction"));
        tempEmployee.setArea(trendInfo.get("area"));
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

    private Map<String, String> calcTrendInfo(String SN) {
        return machineMap.get(SN);
    }

    public void test() {
        System.out.println(getSameRegionDevices("CJDE231960054"));
    }

}
