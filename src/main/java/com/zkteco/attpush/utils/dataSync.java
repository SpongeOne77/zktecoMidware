package com.zkteco.attpush.utils;

import com.alibaba.fastjson.JSON;
import com.zkteco.attpush.entity.NewPersonnelRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dataSync")
public class dataSync {

    @Value("${uploadUrl}")
    private String uploadUrl;

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
            } else {
                tempEmployee.setEmployeeNumber(key);
                tempEmployee.setEmployeeName(personnelCahedMap.get(key));
                String photoBase64 = photoUtil.getImgFileToBase64(address + "photos/" + key + ".jpg");
                tempEmployee.setEmployeePicture("data:image/jpeg;base64," + photoBase64);
            }
            tempEmployee.setArea(area);
//            HttpClientUtil.post(uploadUrl + "/employee", JSON.toJSONString(tempEmployee));
        }
        return "dataSync";
    }

    @RequestMapping(value = "/loadOnePerson", method = RequestMethod.POST)
    public String loadOnePerson(String employeeNumber, String employeeName, String photoFolder, String area) {
        System.out.println("[Attpush]: starting loading one person");
        NewPersonnelRecord tempemployee = new NewPersonnelRecord();
        tempemployee.setEmployeeNumber(employeeNumber);
        tempemployee.setEmployeeName(employeeName);
        String photoBase64 = photoUtil.getImgFileToBase64(photoFolder + employeeNumber + ".jpg");
        tempemployee.setEmployeePicture("data:image/jpeg;base64," + photoBase64);
        tempemployee.setArea(area);
        HttpClientUtil.post(uploadUrl + "/employee", JSON.toJSONString(tempemployee));
        return "[Attpush]: loaded " + employeeNumber + " " + employeeName;
    }
}
