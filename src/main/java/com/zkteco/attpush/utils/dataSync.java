package com.zkteco.attpush.utils;

import com.alibaba.fastjson.JSON;
import com.zkteco.attpush.entity.NewPersonnelRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dataSync")
public class dataSync {

    @Value("${uploadUrl}")
    private String uploadUrl;

    private static Map<String, String> personnelCahedMap = new HashMap<>();

    @RequestMapping(value = "/personnel", method = RequestMethod.POST)
    public String personnel(String address) {
        System.out.println("[Attpush]: starting personnel sync");
        personnelCahedMap = excelUtil.readExcel(address + "employee.xlsx");
        System.out.println(personnelCahedMap);
        for (String key : personnelCahedMap.keySet()) {
            System.out.println(key + " " + personnelCahedMap.get(key));
            NewPersonnelRecord tempEmployee = new NewPersonnelRecord();
            tempEmployee.setEmployeeNumber(key);
            tempEmployee.setEmployeeName(personnelCahedMap.get(key));
            tempEmployee.setArea("熔解");
            String photoBase64 = photoUtil.getImgFileToBase64(address + "photos/" + key + ".jpg");
            tempEmployee.setEmployeePicture("data:image/jpeg;base64," + photoBase64);
            HttpClientUtil.post(uploadUrl + "/employee", JSON.toJSONString(tempEmployee));

        }
        return "dataSync";
    }
}
