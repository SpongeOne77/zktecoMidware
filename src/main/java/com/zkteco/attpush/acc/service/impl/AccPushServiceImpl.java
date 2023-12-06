package com.zkteco.attpush.acc.service.impl;

import com.alibaba.fastjson.JSON;
import com.zkteco.attpush.acc.service.AccPushService;
import com.zkteco.attpush.entity.Command;
import com.zkteco.attpush.entity.Employee;
import com.zkteco.attpush.entity.EmployeeSignInOffEntity;
import com.zkteco.attpush.entity.config.Device;
import com.zkteco.attpush.entity.config.DeviceConfig;
import com.zkteco.attpush.mapper.BizAccessInfoMapper;
import com.zkteco.attpush.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.smartcardio.Card;
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

    public final static List<Command> cachedCommands = new ArrayList<>();

    @Autowired
    public DeviceConfig deviceConfig;

    @Value("${uploadUrl}")
    private String uploadUrl;


    /**
     * @param rawRecord
     */
    @Override
    public void processNewPhoto(Map<String, String> rawRecord) {
        //get employeeNumber and photo from raw data
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
                    Command tempCommand = new Command();
                    tempCommand.setSN(device.getSN());
                    tempCommand.setCmd("C:525:DATA UPDATE biophoto Pin=" + employee.getEmployeeNumber() + "/tType=9/tSize=" + content.length() + "/tContent=" + content + "/tFormat=0");
                    cachedCommands.add(tempCommand);
                });
                employee.setIsRecorded(true);
            }
        }
        // update cached employees for server, remove if the employee has been recorded
        cachedEmployeesServer.removeIf(employee -> employee.getIsRecorded().equals(true));
        printCommandInfo();
    }


    @Override
    public void processNewRecord(Map<String, String> rawRecord) {
        //grab info from rawRecord
        String cardNo = rawRecord.get("cardno");
        String SN = rawRecord.get("SN");
        String employeeName = "";
        String employeeNumber = "";
        List<Device> devicesInSameArea = getDeviceInfoFromSameRegionBySN(SN);
        String newUserCmd = "";
        String userAuthCmd = "";
        if ("0".equals(cardNo)) {
            //registration for person
            employeeName = rawRecord.get("name");
            employeeNumber = rawRecord.get("pin");
            newUserCmd = "C:295:DATA UPDATE user CardNo=/tPin=" + employeeNumber + "/tPassword=/tGroup=0/tStartTime=0/tEndTime=0/tName=" + employeeName + "/tPrivilege=0";
            userAuthCmd = "C:296:DATA UPDATE userauthorize Pin=" + employeeNumber + "/tAuthorizeTimezoneId=1/tAuthorizeDoorId=1/tDevID=1";
            //cache employee info with SN
            Employee newEmployee = new Employee();
            newEmployee.setEmployeeName(employeeName);
            newEmployee.setArea(devicesInSameArea.get(0).getArea());
            newEmployee.setEmployeeNumber(employeeNumber);
            newEmployee.setDevice(SN);
            cachedEmployeesServer.add(newEmployee);
            System.out.println("cached employees for server" + cachedEmployeesServer);
        } else {
            //registration for card
            employeeName = cardNo;
            employeeNumber = "V" + cardNo;
            newUserCmd = "C:295:DATA UPDATE user CardNo=" + cardNo + " Pin=/tPassword=/tGroup=0/tStartTime=0/tEndTime=0/tName=" + cardNo + "/tPrivilege=0";
            userAuthCmd = "C:296:DATA UPDATE userauthorize Pin=" + cardNo + "/tAuthorizeTimezoneId=1/tAuthorizeDoorId=1/tDevID=1";
            Employee tempEmployee = new Employee();
            tempEmployee.setEmployeeName(employeeName);
            tempEmployee.setArea(devicesInSameArea.get(0).getArea());
            tempEmployee.setEmployeeNumber(employeeNumber);
            HttpClientUtil.post(uploadUrl + "/employee", JSON.toJSONString(tempEmployee));
        }

        String finalNewUserCmd = newUserCmd;
        String finalUserAuthCmd = userAuthCmd;
        devicesInSameArea.forEach(device -> {
            //register for card
            Command newUserCommand = new Command();
            Command userAuthCommand = new Command();
            newUserCommand.setSN(device.getSN());
            userAuthCommand.setSN(device.getSN());
            //set register cmd
            newUserCommand.setCmd(finalNewUserCmd);
            //set access-granting cmd
            userAuthCommand.setCmd(finalUserAuthCmd);
            //cache them
            cachedCommands.add(newUserCommand);
            cachedCommands.add(userAuthCommand);
        });
        System.out.println(cachedCommands);

//        System.out.println("Cached cmd list");
//        cachedCommands.forEach(cmd -> {
//            System.out.println(cmd.getSN());
//            System.out.println(cmd.getCmd());
//        });
    }

    public List<Device> getDeviceInfoFromSameRegionBySN(String SN) {
        String region = deviceConfig.getDeviceList().stream().filter(device -> device.getSN().equals(SN)).collect(Collectors.toList()).get(0).getArea();
        return deviceConfig.getDeviceList().stream().filter(device -> device.getArea().equals(region)).collect(Collectors.toList());
    }

    public Device getDeviceInfoBySN(String SN) {
        return deviceConfig.getDeviceList().stream().filter(device -> device.getSN().equals(SN)).collect(Collectors.toList()).get(0);
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
        Device device = getDeviceInfoBySN(rawRecord.get("SN"));
        System.out.println("[info]this device is " + device);
        tempEmployee.setInoutStatus(device.getDirection());
        tempEmployee.setArea(device.getArea());
        System.out.println("[info]this person is signing " + ("0".equals(tempEmployee.getInoutStatus()) ? "in" : "off"));
        System.out.println(tempEmployee);
        if ("0".equals(tempEmployee.getInoutStatus())) {
            return "true".equals(verifyAndRecordLogs(tempEmployee));
        }
        //if the person is not in site(no signing in), then do not upload
        if (!bizAccessInfoMapper.getByAreaAndPin(tempEmployee).isEmpty()) {
            verifyAndRecordLogs(tempEmployee);
        } else {
            System.out.println(tempEmployee.getEmployeeNumber() + " not in " + tempEmployee.getArea());
        }
        return true;
    }

    private String verifyAndRecordLogs(EmployeeSignInOffEntity record) {
        String res = HttpClientUtil.post(uploadUrl + "/real/event", JSON.toJSONString(record));
        Map resObj = (Map) JSON.parse(res);
        return resObj.get("data").toString();
    }

    public List<Command> getCommandListBySN(String SN) {
        return cachedCommands.stream().filter((cmd) -> cmd.getSN().equals(SN)).collect(Collectors.toList());
    }

    public String combineCommands(List<Command> commands) {
        StringBuilder finalCommand = new StringBuilder();
        for(Command command: commands) {
            finalCommand.append(command.getCmd()).append("\\r\\n\\r\\n");
        }
        return finalCommand.toString();
    }

    public String heartbeatCheck(String SN) {
        List<Command> commandList = getCommandListBySN(SN);
        cachedCommands.removeIf(device -> device.getSN().equals(SN));
        if (commandList.isEmpty()) {
            return "OK";
        }
        String commondString = combineCommands(commandList);
        System.out.println(SN + " has cached commands: " + commondString);
        return commondString;
    }

    public void printCommandInfo() {
        List<String> relatedDevices = cachedCommands.stream().map(Command::getSN).distinct().collect(Collectors.toList());
        System.out.println("[Attpush] total " + relatedDevices + " devices left to sync");
    }

    @Override
    public boolean addCommand(Command command) {
        cachedCommands.add(command);
        return true;
    }

    public void test() {
        System.out.println(getDeviceInfoFromSameRegionBySN("CJDE231960055"));
    }

}
