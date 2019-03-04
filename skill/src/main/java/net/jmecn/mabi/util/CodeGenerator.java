package net.jmecn.mabi.util;

import java.util.Collection;

/**
 * 代码生成器
 * 
 */
public class CodeGenerator {
	/**
	 * 生成类代码文件
	 * @param keys
	 * @return
	 */
	public static String createSourceCode(Collection<String> keys, String className) {
		StringBuffer buffer = new StringBuffer();
		// 生成类文件
		buffer.append("public class " + className + " {").append("\r\n");// 类声明
		
		buffer.append("\r\n");
		buffer.append("\t// 私有属性").append("\r\n");
		for (String key: keys) {
			buffer.append("\tprivate String " + key + ";").append("\r\n");
		}
		
		buffer.append("\r\n");
		buffer.append("\t// 构造方法初始化").append("\r\n");
		buffer.append("\tpublic " + className + "() {").append("\r\n");
		for (String key: keys) {
			buffer.append("\t\t" + key + " = \"\";").append("\r\n");
		}
		buffer.append("\t}").append("\r\n");
		buffer.append("}").append("\r\n");
		
		return buffer.toString();
	}
	/**
	 * 生成属性名
	 * as_xss -> asXss
	 * @param name
	 * @return
	 */
	String getAttributeName(String name) {
		StringBuffer buffer = new StringBuffer();

		// 首字母小写
		return buffer.toString();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
