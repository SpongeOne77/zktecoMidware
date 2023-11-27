package com.zkteco.attpush.acc.service;

import com.zkteco.attpush.entity.Command;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface AccPushService {
    /**
     * when new employee is signing up
     * @param record
     */
    public void processNewPhoto(Map<String, String> record);

    /**
     * when new employee is signing up
     * @param record
     */
    public void processNewRecord(Map<String, String> record);


    /**
     * when enrolled employee sign in
     * @param rawRecord
     */
    public boolean processSignInOut(Map<String, String> rawRecord);

    /**
     * calculate commands by SN code
     * @param SN
     * @return
     */
    public List<Command> getCommandListBySN(String SN);

    /**
     * turn list of commands into a string
     * @param commands
     * @return
     */
    public String combineCommands(List<Command> commands);

    public String heartbeatCheck(String SN);

    public void test();

}
