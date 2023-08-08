package com.zkteco.attpush.utils.command;

public class EmployeeInfoCmd extends Command {
    private String cardNo;
    private String Pin;
    private String Password;
    private String Group;
    private String Name;
    private String Privilege;
    private String Picture;

    public EmployeeInfoCmd(String SN, String commandNo, String commandType, String tableName) {
        System.out.println("Constructing a new command for employee info");
        this.commandNo = commandNo;
        this.commandType = commandType;
        this.tableName = tableName;
        this.deviceSN = SN;
    }
}
