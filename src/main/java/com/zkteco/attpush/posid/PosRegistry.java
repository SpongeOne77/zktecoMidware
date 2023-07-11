package com.zkteco.attpush.posid;

import com.zkteco.attpush.posid.vo.PosIDFaceConsumeDeviceMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/posIDFaceConsumeDevice.do")
public class PosRegistry {


    @RequestMapping(params = "registry",method = RequestMethod.GET)
    @ResponseBody
    public PosIDFaceConsumeDeviceMessage registry(String SN,String ipAddress,String devType,String devName,String access_token){
                System.out.println("设备的序列号为。。。。。。。。"+SN);
                System.out.println("设备的IP为。。。。。。。。。。"+ipAddress);
                if (devType.equals("1")){
                    System.out.println("设备类型为。。。。。。。。。。。。。消费机");
                }else if (devType.equals("2")){
                    System.out.println("设备类型为。。。。。。。。。。。查询机");
                }

                System.out.println("设备的名字为。。。。。。。。。。。"+devName);
                System.out.println("请求需要携带的token是。。。。。。。。"+access_token);
        PosIDFaceConsumeDeviceMessage posIDFaceConsumeDeviceMessage = new PosIDFaceConsumeDeviceMessage();
        posIDFaceConsumeDeviceMessage.setRet("unauthorized");
        posIDFaceConsumeDeviceMessage.setMessage("unauthorized");

        return posIDFaceConsumeDeviceMessage;



    }
    @RequestMapping(params = "heartbeat",method = RequestMethod.GET)
    @ResponseBody
    public PosIDFaceConsumeDeviceMessage heartbeat(String SN,String access_token){


        System.out.println("设备序列号为。。。。。。。。。"+SN);
        System.out.println("设备请求token。。。。。。。。。。。。。"+access_token);
        PosIDFaceConsumeDeviceMessage posIDFaceConsumeDeviceMessage = new PosIDFaceConsumeDeviceMessage();
        posIDFaceConsumeDeviceMessage.setRet("OK");
        posIDFaceConsumeDeviceMessage.setMessage("success");
        BufferedReader cmd = null;
        File file = new File("d://pos.txt");
        if (file.exists()){
            try {
                List list = new ArrayList();
                cmd = new BufferedReader(new FileReader(file));
                String pos = "";
                while ((pos = cmd.readLine())!=null){
                   list.add(pos);
                   posIDFaceConsumeDeviceMessage.setData("addPersData:"+list);
                   System.out.println(list);
                }
                cmd.close();
                file.delete();
                Thread.sleep(1000);
                return posIDFaceConsumeDeviceMessage;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            return posIDFaceConsumeDeviceMessage;
        }
            return posIDFaceConsumeDeviceMessage;
    }
    @RequestMapping(params = "deviceUpdate",method = RequestMethod.GET)
    @ResponseBody
    public PosIDFaceConsumeDeviceMessage deviceUpdate(String deviceFaceVersion){
        PosIDFaceConsumeDeviceMessage posIDFaceConsumeDeviceMessage = new PosIDFaceConsumeDeviceMessage();
        posIDFaceConsumeDeviceMessage.setRet("OK");
        posIDFaceConsumeDeviceMessage.setMessage("success");
        return posIDFaceConsumeDeviceMessage;


    }

}
