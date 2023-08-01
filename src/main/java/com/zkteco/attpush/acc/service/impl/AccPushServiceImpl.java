package com.zkteco.attpush.acc.service.impl;

import com.alibaba.fastjson.JSON;
import com.zkteco.attpush.acc.service.AccPushService;
import com.zkteco.attpush.entity.EmployeeSignInOffEntity;
import com.zkteco.attpush.entity.NewPersonnelRecord;
import com.zkteco.attpush.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class AccPushServiceImpl implements AccPushService {

    public final static Map<String, NewPersonnelRecord> registerMembers = new HashMap();

    @Value("#{${area}}")
    private Map<String, Map<String, String>> machineMap;

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
        if ("0".equals(rawRecord.get("cardno"))) {
            String recordNo = rawRecord.get("pin");
            System.out.println("entering processNewRecord");
            if (!registerMembers.containsKey(recordNo) && !"0".equals(recordNo)) {
                NewPersonnelRecord tempRecord = new NewPersonnelRecord();
                Map<String, String> trendInfo = calcTrendInfo(rawRecord.get("SN"));
                tempRecord.setEmployeeName(rawRecord.get("name"));
                tempRecord.setEmployeeNumber(recordNo);
                tempRecord.setArea(trendInfo.get("area"));
                registerMembers.put(recordNo, tempRecord);
                System.out.println("cached employee" + registerMembers);
            }
        } else {
            System.out.println("Activating card no " + rawRecord.get("cardno"));
            System.out.println("this record do not upload");
        }
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
            //TODO calc if the person is in site
            //if not insite return true
            CompletableFuture.runAsync(() -> {
                verifyAndRecordLogs(tempEmployee);
            });
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

}
