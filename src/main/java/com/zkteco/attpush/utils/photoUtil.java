package com.zkteco.attpush.utils;

import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class photoUtil {
    /**
     * imgFile 图片本地存储路径
     */
    public static String getImgFileToBase64(String imgFile) {
        File file = new File(imgFile);
        if (!file.exists()) {
            System.out.println(imgFile + "does not exist");
            return null;
        }

        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream inputStream = null;
        byte[] buffer = null;
        //读取图片字节数组
        try {
            inputStream = new FileInputStream(imgFile);
            int count = 0;
            while (count == 0) {
                count = inputStream.available();
            }
            buffer = new byte[count];
            inputStream.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    // 关闭inputStream流
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 对字节数组Base64编码
        return Base64.getEncoder().encodeToString(buffer);
    }

}