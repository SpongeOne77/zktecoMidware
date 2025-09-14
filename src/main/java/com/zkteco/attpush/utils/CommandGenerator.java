package com.zkteco.attpush.utils;

import com.zkteco.attpush.entity.Command;

public class CommandGenerator {
    public Command generateSingleUserDeleteCommand(String sn, String pin) {
        Command command = new Command();
        command.setSN(sn);
        command.setCmd("53328:DATA DELETE user Pin=" + pin);
        return command;
    }

    public Command generateSingleAuthDeleteCommand(String sn, String pin) {
        Command command = new Command();
        command.setSN(sn);
        command.setCmd("C:53327:DATA DELETE userauthorize Pin=" + pin);
        return command;
    }

    public Command generateAllAuthDeleteCommand(String sn) {
        Command command = new Command();
        command.setSN(sn);
        command.setCmd("53328:DATA DELETE userauthorize Pin=*");
        return command;
    }

    public Command generateClearAllCommand(String sn) {
        Command command = new Command();
        command.setSN(sn);
        command.setCmd("C:53328:DATA DELETE user Pin=*");
        return command;
    }
}
