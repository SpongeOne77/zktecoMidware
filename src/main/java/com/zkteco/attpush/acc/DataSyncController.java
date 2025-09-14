package com.zkteco.attpush.acc;

import com.alibaba.fastjson.JSON;
import com.zkteco.attpush.acc.service.AccPushService;
import com.zkteco.attpush.acc.service.DataSyncService;
import com.zkteco.attpush.entity.Command;
import com.zkteco.attpush.entity.Employee;
import com.zkteco.attpush.entity.NewPersonnelRecord;
import com.zkteco.attpush.entity.config.Device;
import com.zkteco.attpush.entity.config.DeviceConfig;
import com.zkteco.attpush.mapper.BizEmployeeMapper;
import com.zkteco.attpush.utils.HttpClientUtil;
import com.zkteco.attpush.utils.excelUtil;
import com.zkteco.attpush.utils.photoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dataSync")
public class DataSyncController {
    @Autowired
    private AccPushService accPushService;

    @Autowired
    private DataSyncService dataSyncService;

    @Value("${uploadUrl}")
    private String uploadUrl;

    @Autowired
    private BizEmployeeMapper bizEmployeeMapper;
    private DeviceConfig deviceConfig;

    @RequestMapping(value = "/personnel", method = RequestMethod.POST)
    public String personnel(String address, String area, Boolean cards) {
        System.out.println("[Attpush]: starting personnel sync");
        if (area == null || area.isEmpty()) {
            System.out.println("[Attpush]: area is null");
            return "[AttPush]: area is null";
        } else {
            System.out.println("[Attpush]: area is " + area);
        }
        Map<String, String> personnelCahedMap = excelUtil.readExcel(address + "employee.xlsx");
        System.out.println(personnelCahedMap);
        for (String key : personnelCahedMap.keySet()) {
            System.out.println(key + " " + personnelCahedMap.get(key));
            NewPersonnelRecord tempEmployee = new NewPersonnelRecord();
            if (cards) {
                tempEmployee.setEmployeeNumber("V" + personnelCahedMap.get(key));
                tempEmployee.setEmployeeName(personnelCahedMap.get(key));
            } else {
                tempEmployee.setEmployeeNumber(key);
                tempEmployee.setEmployeeName(personnelCahedMap.get(key));
                String photoBase64 = photoUtil.getImgFileToBase64(address + "photos/" + key + ".jpg");
                tempEmployee.setEmployeePicture("data:image/jpeg;base64," + photoBase64);
            }
            tempEmployee.setArea(area);
            HttpClientUtil.post(uploadUrl + "/employee", JSON.toJSONString(tempEmployee));
        }
        return "dataSync";
    }

    @RequestMapping(value = "/registerOnePerson", method = RequestMethod.POST)
    public String registerOnePerson(String employeeNumber, String employeeName, String photoFolder, String SN) {
        System.out.println("[Attpush]: starting registering one person");
        System.out.println(employeeName + " " + employeeNumber);
        Map<String, String> rawData = new HashMap<>();
        rawData.put("pin", employeeNumber);
        rawData.put("name", employeeName);
        rawData.put("SN", SN);
        rawData.put("cardno", "0");
        accPushService.processNewRecord(rawData);
        String photoBase64 = photoUtil.getImgFileToBase64(photoFolder + employeeNumber + ".jpg");
        rawData.put("content", photoBase64);
        accPushService.processNewPhoto(rawData);
        return "OK";
    }

    @RequestMapping(value="/clearData", method = RequestMethod.POST)
    public String clearData(@RequestParam String SN,
                            @RequestParam(required = false) String Pin,
                            @RequestParam(required = false) String Pins) {
        if (SN == null || SN.trim().isEmpty()) {
            return "[Attpush]: SN is null or SN is empty";
        }
        if (Pin != null && Pins != null) {
            return "[Attpush]: can not pass pin and pins at the same time";
        }
        if (Pins != null) {
            dataSyncService.batchDeleteUsers(SN, Pins.split(","));
            return "OK";
        }
        if (!"".equals(Pin) && Pin != null) {
            dataSyncService.deleteUser(SN, Pin.trim());

        } else {
            dataSyncService.clearAllData(SN);
        }
        return "OK";
    }

    @RequestMapping(value="/clearByArea", method = RequestMethod.POST)
    public String clearByArea(@RequestParam String area,
                              @RequestParam List<String> pins,
                              @RequestParam(required = false) Boolean deleteAll) {
        if (area == null || area.trim().isEmpty()) {
            return "[Attpush]: area can not be empty";
        }
        if ((pins == null || pins.isEmpty()) && (deleteAll == null || !deleteAll)) {
            return "[Attpush]: pins/deleteAll can not be empty";
        }
        dataSyncService.deleteBatchUsersByArea(area, pins, deleteAll);
        return "OK";
    }

    @RequestMapping(value="/initConfig", method = RequestMethod.POST)
    public String initConfig(String SN) {
        Command command = new Command();
        command.setSN(SN);
//        command.setCmd("C:53328:DATA DELETE user Pin=*");
        command.setCmd("C:405:SET OPTIONS AutoServerFunOn=1,AutoServerMode=1,Door1SensorType=1,Door1MultiCardOpenDoor=0\n");
        accPushService.addCommand(command);
        return "OK";
    }

    @RequestMapping(value="/operation", method = RequestMethod.POST)
    public String restart(String SN, String operation) {
        Command command = new Command();
        command.setSN(SN);
        switch (operation) {
            case "restart":
                command.setCmd(("C:223:CONTROL DEVICE 03000000"));
                break;
                case "unlock":
                    command.setCmd(("C:221:CONTROL DEVICE 1 1 1 9"));
                    break;
            default:
                System.out.println("[info]: no operation was made");
                break;
        }
        accPushService.addCommand(command);
        return "OK";
    }

    @RequestMapping(value = "/restoreData", method = RequestMethod.POST)
    public String restoreData(String SN) {
        System.out.println("[Attpush]: starting restoring data for device" + SN);
        List<Employee> employeeList = bizEmployeeMapper.getByArea(deviceConfig.getAreaBySn(SN));
        dataSyncService.restoreRecords(employeeList, SN);
        return "OK";
    }
}
