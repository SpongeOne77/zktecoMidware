package com.zkteco.attpush.posid.vo;

import java.io.Serializable;

public class ZKResultMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    /**ok=成功，非ok都是失败,fail*/
    private String ret = "ok";
    private String msg = "common_op_succeed";
    private Object data;

    private Object[] i18nArgs;

    public ZKResultMsg() {
        super();
    }
    public ZKResultMsg(String ret, String msg) {
        super();
        this.ret = ret;
        this.msg = msg;
    }
    public ZKResultMsg(String ret, String msg, Object data) {
        super();
        this.ret = ret;
        this.msg = msg;
        this.data = data;
    }

    public ZKResultMsg(Object data) {
        super();
        this.data = data;
    }

    public Object[] getI18nArgs() {
        return i18nArgs;
    }

    public void setI18nArgs(Object... i18nArgs) {
        this.i18nArgs = i18nArgs;
    }

    public String getRet() {
        return ret;
    }
    public void setRet(String ret) {
        this.ret = ret;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return "ok".equals(this.ret);
    }
    public static ZKResultMsg getFailMsg(String msg) {
        return new ZKResultMsg("fail", msg);
    }
    public static ZKResultMsg msg(boolean rs) {
        if (rs) {
            return new ZKResultMsg();
        } else {
            return new ZKResultMsg("fail", "common_op_failed");
        }
    }
    public static ZKResultMsg successMsg() {
        return new ZKResultMsg("ok","common_op_succeed");
    }

    public static ZKResultMsg failMsg() {
        return new ZKResultMsg("fail", "common_op_failed");
    }

    public static ZKResultMsg failMsg(String msg,Object... i18nArgs) {
        ZKResultMsg rs = new ZKResultMsg("fail", msg);
        rs.setI18nArgs(i18nArgs);
        return rs;
    }
}
