package com.zkteco.attpush.posid.vo;

import java.util.Date;

public class PosIDFaceConsumeDeviceMessage {

    private String ret;
    private String message;
    private Object data;

    public static PosIDFaceConsumeDeviceMessage successMessage(Object data){
        PosIDFaceConsumeDeviceMessage posIDFaceConsumeDeviceMessage = new PosIDFaceConsumeDeviceMessage();
        posIDFaceConsumeDeviceMessage.setRet("OK");
        posIDFaceConsumeDeviceMessage.setMessage("success");
        posIDFaceConsumeDeviceMessage.setData(data);
        return posIDFaceConsumeDeviceMessage;
    }

    public static PosIDFaceConsumeDeviceMessage failMessage(String message){
        PosIDFaceConsumeDeviceMessage posIDFaceConsumeDeviceMessage = new PosIDFaceConsumeDeviceMessage();
        posIDFaceConsumeDeviceMessage.setRet("failed");
        posIDFaceConsumeDeviceMessage.setMessage(message);
        return posIDFaceConsumeDeviceMessage;
    }

    public static PosIDFaceConsumeDeviceMessage failMessage(String ret,String message){
        PosIDFaceConsumeDeviceMessage posIDFaceConsumeDeviceMessage = new PosIDFaceConsumeDeviceMessage();
        posIDFaceConsumeDeviceMessage.setRet(ret);
        posIDFaceConsumeDeviceMessage.setMessage(message);
        return posIDFaceConsumeDeviceMessage;
    }

    public static PosIDFaceConsumeDeviceMessage unAuthorizedMessage(){
        PosIDFaceConsumeDeviceMessage posIDFaceConsumeDeviceMessage = new PosIDFaceConsumeDeviceMessage();
        posIDFaceConsumeDeviceMessage.setRet("unauthorized");
        posIDFaceConsumeDeviceMessage.setMessage("unauthorized");
        posIDFaceConsumeDeviceMessage.setData(new Date());
        return posIDFaceConsumeDeviceMessage;
    }
    
    public static PosIDFaceConsumeDeviceMessage successMessage(){
        PosIDFaceConsumeDeviceMessage posIDFaceConsumeDeviceMessage = new PosIDFaceConsumeDeviceMessage();
        posIDFaceConsumeDeviceMessage.setRet("OK");
        posIDFaceConsumeDeviceMessage.setMessage("success");
        return posIDFaceConsumeDeviceMessage;
    }
    
    public static PosIDFaceConsumeDeviceMessage failMessage(){
        PosIDFaceConsumeDeviceMessage posIDFaceConsumeDeviceMessage = new PosIDFaceConsumeDeviceMessage();
        posIDFaceConsumeDeviceMessage.setRet("-1");
        posIDFaceConsumeDeviceMessage.setMessage("fail");
        return posIDFaceConsumeDeviceMessage;
    }

	@Override
	public String toString() {
		return "PosIDFaceConsumeDeviceMessage [ret=" + ret + ", message=" + message + ", data=" + data + "]";
	}

    public String getRet() {
        return ret;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
