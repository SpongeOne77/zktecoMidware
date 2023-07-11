package com.zkteco.attpush.acc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GenerateCmd {
	public static List<String> cmd() {
		List<String> cmds = new ArrayList<>();
		StringBuilder cmdTemp = new StringBuilder();
//		InputStream is = GenerateCmd.class.getResourceAsStream("./cmd.txt");
		StringBuffer sb = new StringBuffer();
		int count = 1;
		try {
			FileInputStream fis = new FileInputStream(new File("./cmd.txt"));
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = "";
			while((line = br.readLine()) == null) {
				if(line.equals("")) {
					//cmdTemp.append("\r\n");
					System.out.println(cmdTemp);
					cmds.add(cmdTemp.toString());
					cmdTemp.setLength(0);
					//System.out.println(1);
					sb.append("\r\n");
				}else {
					if(!format(line)){
						cmdTemp.append("C:"+(count++)+":"+line+"\r\n");
						//sb.append("C:"+(count++)+":"+line+"\r\n");
					}else{
						cmdTemp.append(line+"\r\n");
						sb.append(line);
						cmds.add(cmdTemp.toString());
					}
					//System.out.println(line);
				}
			}
			//System.out.println(sb);
		}catch (Exception e) {
		}
		sb.append("\r\n");
//		cmds.add("DATA DELETE multimcard *\r\n");
//		cmds.add("GET OPTIONS \r\n");

		System.out.println(cmds.size());
		return cmds;
	}

	private static boolean format(String line) {
		return line.startsWith("ID")||line.startsWith("Number")||line.startsWith("Reader");
	}

	public static void main(String[] args) {
		cmd();
	}
}
