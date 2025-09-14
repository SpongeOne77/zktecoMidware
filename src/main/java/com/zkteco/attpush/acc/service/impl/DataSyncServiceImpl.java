package com.zkteco.attpush.acc.service.impl;

import com.zkteco.attpush.acc.service.AccPushService;
import com.zkteco.attpush.acc.service.DataSyncService;
import com.zkteco.attpush.entity.Command;
import com.zkteco.attpush.entity.Employee;
import com.zkteco.attpush.entity.config.DeviceConfig;
import com.zkteco.attpush.mapper.BizEmployeeMapper;
import com.zkteco.attpush.utils.CommandGenerator;
import com.zkteco.attpush.utils.CommandManager;
import com.zkteco.attpush.utils.photoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static com.zkteco.attpush.acc.service.impl.AccPushServiceImpl.cachedCommands;

@Service
public class DataSyncServiceImpl implements DataSyncService {

    @Value("${photoFolder}")
    private String photoFolder;

    @Autowired
    public DeviceConfig deviceConfig;

    @Autowired
    private AccPushService accPushService;

    @Autowired
    private BizEmployeeMapper bizEmployeeMapper;

    private final CommandGenerator commandGenerator = new CommandGenerator();

    private final CommandManager commandManager = new CommandManager();

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

    /**
     * @param SN device sn number
     * @param pin employee id
     */
    @Override
    public void deleteUser(String SN, String pin) {
        Command deleteUserCommand = commandGenerator.generateSingleUserDeleteCommand(SN, pin);
        Command deleteAuthCommand = commandGenerator.generateSingleAuthDeleteCommand(SN, pin);
        accPushService.addCommand(deleteUserCommand);
        accPushService.addCommand(deleteAuthCommand);
        String area = deviceConfig.getAreaBySn(SN);
        bizEmployeeMapper.deleteByPin(pin, area);//delete from table
    }

    /**
     * @param SN
     * @param Pins
     */
    @Override
    @Transactional
    public void batchDeleteUsers(String SN, String[] Pins) {
        String area = deviceConfig.getAreaBySn(SN);
        List<String> pinArray = Arrays.asList(Pins);
        bizEmployeeMapper.batchDeleteByPins(pinArray, area);
        for (String pin : pinArray) {
            Command deleteUserCommand = commandGenerator.generateSingleUserDeleteCommand(SN, pin);
            Command deleteAuthCommand = commandGenerator.generateSingleAuthDeleteCommand(SN, pin);
            accPushService.addCommand(deleteUserCommand);
            accPushService.addCommand(deleteAuthCommand);
        }
    }

    /**
     * @param area
     * @param pins
     * @param deleteAll
     */
    @Override
    public void deleteBatchUsersByArea(String area, List<String> pins, Boolean deleteAll) {
        List<String> deviceSns = deviceConfig.getSnsByArea(area);
        if (deviceSns.isEmpty()) {
            throw new RuntimeException("[Attpush]: there is no device in area" + area);
        }

        for (String sn : deviceSns) {
            if (deleteAll) {
                List<Command> commands = Arrays.asList(commandGenerator.generateClearAllCommand(sn), commandGenerator.generateAllAuthDeleteCommand(sn));
                commandManager.addBatchCommands(sn, commands);
                //TODO db
            } else {
                for (String pin : pins) {
                    List<Command> commands = Arrays.asList(
                            commandGenerator.generateSingleUserDeleteCommand(sn, pin),
                            commandGenerator.generateSingleAuthDeleteCommand(sn, pin)
                    );

                    commandManager.addBatchCommands(sn, commands);
                    bizEmployeeMapper.batchDeleteByPins(pins, area);
                }
            }
        }
    }

    /**
     * @param SN device sn number
     */
    @Override
    public void clearAllData(String SN) {
        Command clearAllCommand = commandGenerator.generateClearAllCommand(SN);
        accPushService.addCommand(clearAllCommand);
    }


    private Command getPicCommand(String SN, Employee record) {
        Command userPicCommand = new Command();
        String picture = photoUtil.getImgFileToBase64(photoFolder + record.getEmployeeNumber() + ".jpg");
        userPicCommand.setSN(SN);
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
            System.out.println(employee.getEmployeeNumber() + " is admin \n");
            Privilege = "14";
            password = employee.getEmployeeNumber();
        }
        return "C:295:DATA UPDATE user CardNo=" + employee.getCardno() + "\tPin=" + record.getEmployeeNumber() + "\tPassword=" + password + "\tGroup=0\tStartTime=0\tEndTime=0\tName=" + record.getEmployeeName() + "\tPrivilege=" + Privilege;
    }
}
