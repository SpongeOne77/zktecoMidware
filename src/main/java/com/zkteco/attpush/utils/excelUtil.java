package com.zkteco.attpush.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class excelUtil {

    public static Map<String, String> readExcel(String location) {
        Map<String, String> cachedMap = null;
        try {
//            String path = excelUtil.class.getClassLoader().getResource(location).getPath();
            //创建工作簿对象
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(location));
            //获取工作簿下sheet的个数
            int sheetNum = xssfWorkbook.getNumberOfSheets();
            System.out.println("该excel文件中总共有：" + sheetNum + "个sheet");
            cachedMap = new HashMap<>();
            //遍历工作簿中的所有数据
            for (int i = 0; i < sheetNum; i++) {
                //读取第i个工作表
                System.out.println("读取第" + (i + 1) + "个sheet");
                XSSFSheet sheet = xssfWorkbook.getSheetAt(i);
                //获取最后一行的num，即总行数。此处从0开始
                int maxRow = sheet.getLastRowNum();
                for (int row = 2; row <= maxRow; row++) {
                    String employeeNumber = sheet.getRow(row).getCell(0).toString();
                    String employeeName = sheet.getRow(row).getCell(1).toString();
                    if (!cachedMap.containsKey(employeeNumber))
                        cachedMap.put(employeeNumber, employeeName);
                }
//                System.out.println(cachedMap);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return cachedMap;
    }

}