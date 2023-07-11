package com.zkteco.attpush.acc.service;

import com.zkteco.attpush.entity.NewPersonnelRecord;
import org.springframework.stereotype.Service;

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

}
