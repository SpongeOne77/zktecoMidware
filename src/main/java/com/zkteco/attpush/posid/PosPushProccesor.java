package com.zkteco.attpush.posid;

import com.zkteco.attpush.posid.vo.ZKResultMsg;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/token.do")
public class PosPushProccesor {
    @RequestMapping(params = "getToken",method = RequestMethod.GET)
    @ResponseBody
    public ZKResultMsg Token(String username,String password){
        ZKResultMsg zkResultMsg = new ZKResultMsg();
        zkResultMsg.setRet("ok");
        zkResultMsg.setMsg("操作成功");
        Map map = new HashMap();
        String sb = "CE26435855918BF9379D7188E50C2CC62A87E3B6C97B72280931F572AFE06EDF";
        map.put("token",sb);
        zkResultMsg.setData(map);
        System.out.println(username);
        System.out.println(password);
        return zkResultMsg;
    }

}
