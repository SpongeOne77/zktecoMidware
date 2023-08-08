package com.zkteco.attpush.utils.command;

public abstract class Command {
    protected String commandNo;

    protected String commandType;

    protected String tableName;

    protected String deviceSN;

    protected Boolean deliverStatus;

    public void Constructor(String commandNo, String commandType, String tableName) {
        System.out.println("Constructing a new command");
        this.commandNo = commandNo;
        this.commandType = commandType;
        this.tableName = tableName;
    }

    public String generate() {
        return "";
    }

}
