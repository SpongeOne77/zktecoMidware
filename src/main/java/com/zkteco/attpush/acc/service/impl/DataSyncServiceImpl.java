package com.zkteco.attpush.acc.service.impl;

import com.zkteco.attpush.acc.service.DataSyncService;
import com.zkteco.attpush.entity.Command;
import com.zkteco.attpush.entity.Employee;
import com.zkteco.attpush.entity.config.DeviceConfig;
import com.zkteco.attpush.utils.photoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.zkteco.attpush.acc.service.impl.AccPushServiceImpl.cachedCommands;

@Service
public class DataSyncServiceImpl implements DataSyncService {

    @Value("${photoFolder}")
    private String photoFolder;

    @Autowired
    public DeviceConfig deviceConfig;

    /**
     * @param records
     */
    @Override
    public void restoreRecords(List<Employee> records, String SN) {

        for (Employee record : records) {
            String UserCommand = generateUserCommand(record);
            String AuthCommand = "C:296:DATA UPDATE userauthorize Pin=" + record.getEmployeeNumber() + "\tAuthorizeTimezoneId=1\tAuthorizeDoorId=1\tDevID=1";
            Command userCommand = new Command();
            Command authCommand = new Command();
            userCommand.setSN(SN);
            userCommand.setCmd(UserCommand);
            authCommand.setSN(SN);
            authCommand.setCmd(AuthCommand);
            cachedCommands.add(userCommand);
            cachedCommands.add(authCommand);

            if (record.getEmployeePicture() != null) {
                Command userPicCommand = getPicCommand(SN, record);
                cachedCommands.add(userPicCommand);

            }
        }
    }

    private Command getPicCommand(String SN, Employee record) {
        Command userPicCommand = new Command();
//        String rawPicBase64 = record.getEmployeePicture();
//        String picture = rawPicBase64.substring(rawPicBase64.indexOf("base64,/") + 7);
        String picture = photoUtil.getImgFileToBase64(photoFolder + record.getEmployeeNumber() + ".jpg");
        userPicCommand.setSN(SN);
//                C:4:DATA UPDATE biophoto PIN=456123	Type=9	Format=0	Url=	Size=41304	Content=
        if (picture != null) {
        String pictureCmdString = "C:" +
                record.getEmployeeNumber() +
                ":DATA UPDATE biophoto PIN=" +
                record.getEmployeeNumber() +
                "\tType=9\tSize=" +
                picture.length() +
                "\tContent=" +
                picture +
                "\tFormat=0\tUrl=\tPostBackTmpFlag=0";
//        userPicCommand.setCmd("C:" + record.getEmployeeNumber() + ":DATA UPDATE biophoto PIN=" + record.getEmployeeNumber() + "\tType=9\tFormat=0\tUrl=\tSize=" + picture.length() + "\tContent=" + picture + "\r\n\r\n");
        userPicCommand.setCmd(pictureCmdString);
        } else {
            userPicCommand.setCmd("OK");
        }
        return userPicCommand;
    }

    private String generateUserCommand(Employee record) {
        Employee employee;
        employee = record;
        //if visitor
        if (record.getEmployeeNumber().startsWith("V")) {
            employee.setCardno(record.getEmployeeNumber().substring(1));
            employee.setEmployeeNumber(record.getEmployeeNumber().substring(1));
        } else {// if employee
            employee.setCardno("");
        }
        String password = "";
        String Privilege = "0";
        if (deviceConfig.getAdminList().contains(employee.getEmployeeNumber())) {
            Privilege = "14";
            password = employee.getEmployeeNumber();
        }
        return "C:295:DATA UPDATE user CardNo=" + employee.getCardno() + "\tPin=" + record.getEmployeeNumber() + "\tPassword=" + password + "\tGroup=0\tStartTime=0\tEndTime=0\tName=" + record.getEmployeeName() + "\tPrivilege=" + Privilege;
    }
}
