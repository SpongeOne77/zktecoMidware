package com.zkteco.attpush.acc;

import com.zkteco.attpush.acc.service.AccPushService;
import com.zkteco.attpush.mapper.BizAccessInfoMapper;
import com.zkteco.attpush.entity.TblBizAccessInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/iclock")
/**
 * 1.初始化
 * 2.注册
 * 3.push
 * 4.心跳
 * 5.心跳处理结果
 * 6.实时记录
 */
public class AccPushProccesor {

    @Autowired
    private AccPushService accPushService;
    @Autowired
    private BizAccessInfoMapper bizAccessInfoMapper;
    private static boolean test = true;

    private static List<String> cmds = null;

    static {
        cmds = GenerateCmd.cmd();
    }

    public final static Map checkinMembers = new HashMap();
    private static Map<String, List<String>> cmdMap = new HashMap<>();


    /**
     * 处理初始化请求的
     *
     * @return
     */
    @RequestMapping(value = "/cdata", method = RequestMethod.GET)
    public String init(String SN, String pushver, String options, HttpServletRequest req) {
//        System.out.println("#######请求的URL:"+req.getServletPath());
        Map<String, String> param = convertMap(req);
//        System.out.println("######请求的参数"+param.toString());
//        System.out.println("######请求完整URL:"+req.getServletPath()+"?"+param.toString().trim().replace(", ", "&").replace("{", "").replace("}", ""));
        System.out.println("SN..." + SN + "...pushver..." + pushver + "...options..." + options + "..." + new SimpleDateFormat("HH:mm:ss").format(new Date()));

        return "OK";
    }

    /**
     * 处理注册请求的
     *
     * @return
     */
    @RequestMapping("/registry")
    public String registry(@RequestBody String deviceData, HttpServletRequest req) {

//        System.out.println("#######请求的URL:"+req.getServletPath());
        Map<String, String> param = convertMap(req);
//        System.out.println("######请求的参数"+param.toString());
//        System.out.println("######请求完整URL:"+req.getServletPath()+"?"+param.toString().trim().replace(", ", "&").replace("{", "").replace("}", ""));
        System.out.println(deviceData);

        return "RegistryCode=Uy47fxftP3";
    }

    /**
     * 处理push请求
     *
     * @return
     */
    @RequestMapping(value = "/push")
    public String push(HttpServletRequest req) {
        System.out.println("进入到push请求.....");
//        System.out.println("#######请求的URL:"+req.getServletPath());
        Map<String, String> param = convertMap(req);
//        System.out.println("######请求的参数"+param.toString());
//        System.out.println("######请求完整URL:"+req.getServletPath()+"?"+param.toString().trim().replace(", ", "&").replace("{", "").replace("}", ""));
        StringBuffer sb = new StringBuffer();
        sb.append("ServerVersion=3.0.1\n");
        sb.append("ServerName=ADMS\n");
        sb.append("PushVersion=3.0.1\n");
        sb.append("ErrorDelay=30\n");
        sb.append("RequestDelay=2\n");
        sb.append("TransTimes=00:0014:00\n");
        sb.append("TransInterval=1\n");
        sb.append("TransTables=User Transaction\n");
        sb.append("Realtime=1\n");
        sb.append("SessionID=30BFB04B2C8AECC72C01C03BFD549D15\n");
        sb.append("TimeoutSec=10\n");
        return sb.toString();
    }

    /*
     * 处理心跳请求
     *
     */
    @RequestMapping("/getrequest")
    public String heartbeat(String SN) {
//        System.out.println("进入到心跳请求...." + SN + new Date());
        //cmd.txt 放在d盘
        BufferedReader br = null;
        File file = new File("D://cmd.txt");//elegent

        StringBuffer sb = new StringBuffer();

        if (file.exists()) {
            try {
                br = new BufferedReader(new FileReader(file));
                String cmd = "";
                while ((cmd = br.readLine()) != null) {
                    if (cmd.startsWith("C")) {
                        //正常的命令
                        sb.append(cmd + "\r\n\r\n");
                        //return sb.toString();
                    } else {
                        //进到这里目前看只能是E开头，则表示人员头像
                    }
                }
                br.close();
//                file.delete();
                Thread.sleep(1000);
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        //TODO iterate cached signup personnel info
        //TODO compare current SN code with SN code from cached personnel info
        //TODO send personnel info to device
        //TODO grant the personnel access to the device
        //TODO remove the personnel info from cached personnel info
        return "OK";

    }

    public Map<String, String> convertMap(HttpServletRequest request) {
        Map<String, String> returnMap = new HashMap<>();
        // 转换为Entry
        Set<Map.Entry<String, String[]>> entries = request.getParameterMap().entrySet();
        for (Map.Entry<String, String[]> entry : entries) {
            String key = entry.getKey();
            StringBuffer value = new StringBuffer("");
            String[] val = entry.getValue();
            if (null != val && val.length > 0) {
                for (String v : val) {
                    value.append(v);
                }
            }
            returnMap.put(key, value.toString());
        }
        return returnMap;
    }

    @RequestMapping("/ping")
    public String ping(HttpServletRequest req) {
        System.out.println("设备上传时通过Ping请求维持心跳");
//        System.out.println("#######请求的URL:" + req.getServletPath());
        Map<String,String> param = convertMap(req);
        System.out.println("######请求的参数"+param.toString());
//        System.out.println("######请求完整URL:"+req.getServletPath()+"?"+param.toString().trim().replace(", ", "&").replace("{", "").replace("}", ""));
        return "OK";
    }

    @RequestMapping(value = "/cdata", method = RequestMethod.POST)
    public String handleForm(@RequestBody String data, String SN, HttpServletRequest req, String table, String AuthType) {
//        if (new ArrayList<String>() {{
//            add("options");
//            add("rtlog");
//        }}.contains(table)) {
//            return "OK";
//        }
        System.out.println("上传的表名为：....。。。。。。。。" + table);
        System.out.println("设备上传的实时记录为..." + data + "序列号。。。。。。。" + SN);
        Map<String, String> param = convertMap(req);
        System.out.println("######请求的参数" + param);
        //sign up for the first time
        if ("tabledata".equals(table) && Objects.equals(param.get("tablename"), "user")) {
            String[] tempParam = ("SN=" + SN + "\t").concat(data).replaceAll("\r\n", "").replaceAll("user ", "").split("\t");
            accPushService.processNewRecord(ArrayToRawRecord(tempParam));
            return "OK";
        }
        if ("tabledata".equals(table) && Objects.equals(param.get("tablename"), "biophoto")) {
            String[] tempParam = data.replaceAll("\r\n", "").replaceAll("biophoto ", "").split("\t");
            accPushService.processNewPhoto(ArrayToRawRecord(tempParam));
            return "OK";
        }
        //sign in/out with face
        if (table == null) {
            String[] tempParamArray = paramStringToArray(("SN=" + SN + "\t").concat(data));
            Map<String, String> tempParamMap = ArrayToRawRecord(tempParamArray);
            // if uses white card, then set pin to "V" + cardno
            if (!"0".equals(tempParamMap.get("cardno"))) {
                tempParamMap.put("pin", "V" + tempParamMap.get("cardno"));
            }
            boolean access = accPushService.processSignInOut(tempParamMap);
            return calcReturnMsg(access, data);
        }

        return "OK";
    }

    public String[] paramStringToArray(String rawParam) {
        return rawParam.replaceAll("\r\n", "").split("\t");
    }

    public String calcReturnMsg(boolean access, String originalRequestString) {
        String verification = "";
        String cmd = "";
        if (!access) {
            verification = "AUTH=FAIL\r\n";
            System.out.println("Warning: Shall not pass");
        } else {
            System.out.println("Waining: Access granted");
            verification = "AUTH=SUCCESS\n";
            cmd = "C:221:CONTROL DEVICE 1 1 1 9\r\n\r\n";
        }
        System.out.println(verification + originalRequestString + "\r\n" + cmd);
        return verification + originalRequestString + "\r\n" + cmd;
    }

    /**
     * @param target
     * @return
     * @apiNote transform 'a=1 b=2' patterns into Map
     */
    public Map<String, String> ArrayToRawRecord(String[] target) {
        return Arrays.stream(target).collect(Collectors.toMap(a -> a.split("=")[0], a -> {
            return a.split("=").length < 2 ? "0" : a.split("=")[1];
        }));
    }


    @RequestMapping(value = "/querydata", method = RequestMethod.POST)
    public String query(@RequestBody String querrydata) {
        String[] dataArray = querrydata.split("\r\n");

        int count = dataArray.length;
        String tableName = dataArray[0].split(" ")[0];
        System.out.println("查询信息是。。。。。" + querrydata);
        String returnValue = tableName + "=" + count;
        System.out.println(returnValue);
        return returnValue;

    }

    @RequestMapping(value = "/rtdata", method = RequestMethod.GET)
    public String rtdata(String type) {
        System.out.println(type + "11111111111");

        String time = new DateUtils().Time();
        String ServerTZ = "ServerTZ=+0800";
        return "DateTime=667723680" + "," + ServerTZ;//格林威治时间减去八小时return

    }


    @RequestMapping(value = "/devicecmd")
    public String deviceCmd(@RequestBody String cmdResult, HttpServletRequest req) {
        System.out.println("命令返回的结果为..." + cmdResult);
//        System.out.println("#######请求的URL:"+req.getServletPath());
        Map<String, String> param = convertMap(req);
//        System.out.println("######请求的参数"+param.toString());
//        System.out.println("######请求完整URL:"+req.getServletPath()+"?"+param.toString().trim().replace(", ", "&").replace("{", "").replace("}", ""));
        return "OK";
    }

    @RequestMapping(value = "/file")
    public String File(@RequestBody String SN, String cmdid, String fileseq, String contenttype, String count) {
        return "OK";
    }

    @RequestMapping(value = "/testAll")
    public boolean testConnection() {
        accPushService.test();
        Map<String, String> temp = new HashMap<>();
        temp.put("pin", "10001");
        temp.put("time", "2023-08-02-16:23");
        temp.put("SN", "CJDE225260587");
        return true;
//        return accPushService.processSignInOut(temp);
//        return bizAccessInfoDao.selectById(null);
    }

}

