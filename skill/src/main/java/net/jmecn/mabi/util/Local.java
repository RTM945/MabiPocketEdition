package net.jmecn.mabi.util;

import java.io.*;
import java.util.*;

public class Local {
	private static String XML_PATH = "data/local/xml/";
	private static Map<String, Map<String, String>> xml = new HashMap<String, Map<String, String>>();
	
	private Local() {
	}

	/**
	 * 获得本地文本
	 * @param text
	 * @return
	 */
	public static String text(String text) {
		if (text != null && text.startsWith("_LT[xml.")) {
			int index = text.lastIndexOf(".");
			String name = text.substring(8, index);
			String id = text.substring(index + 1, text.length() - 1);
			
			Map<String, String> map = null;
			if (!xml.containsKey(name)) {
				map = readText(name);
				if (map != null) {
					xml.put(name, readText(name));
				}
			} else {
				map = xml.get(name);
			}
			
			if (map != null) {
				text = map.get(id);
				text = cutN(text);
			}
		}
		return text;
	}
	
	/**
	 * 读取data/local/xml中的TXT数据文件
	 * @param name
	 */
	private static Map<String, String> readText(String name) {
		Map<String, String> map = null;
		// 文件名
		String fileName = XML_PATH + name + ".china.txt";
		BufferedReader in = null;
		
		try {
			// 打开文件
			in = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName), "unicode"));
			// 逐行读取、解析文件
			map = new LinkedHashMap<String, String>();
			String line;
			while ((line = in.readLine()) != null) {
				String[] a = line.split("	");
				String id = a[0];
				String value = null;
				if (a.length == 2) {
					value = a[1];
				}
				map.put(id, value);
			}
			in.close();
			in = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭文件
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return map;
	}
	
	/**
	 * 去掉字符串中的\n符号
	 * @param text
	 * @return
	 */
	private static String cutN(String t) {
		if (t == null) return null;
		String text = t;
		StringBuffer buffer = new StringBuffer();
		while (text.contains("\\n")) {
			int i = text.indexOf("\\n");
			String str = text.substring(0, i);
			buffer.append(str).append("\n");
			text = text.substring(i + 2);
		}
		buffer.append(text);
		return buffer.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(Local.cutN("生命恢复速度 400%\\n体力恢复速度 400%\\n负伤每2秒恢复 3point"));
	}
}
