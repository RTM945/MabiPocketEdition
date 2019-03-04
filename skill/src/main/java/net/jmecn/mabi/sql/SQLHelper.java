package net.jmecn.mabi.sql;

import java.io.*;
import java.util.*;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import net.jmecn.mabi.skill.SkillLevelDetail;
import net.jmecn.mabi.skill.Skillinfo;
import net.jmecn.mabi.util.Local;

public class SQLHelper {
	public static String DB_PATH = "data/db/";
	public static String XML_PATH = "data/local/xml/";
	public static String SQL_PATH = "sql/";
	
	private Document SkillLevelDescriptions = null;
	private Document Skillinfos = null;

	/**
	 * 构造方法，载入Skillinfo数据
	 */
	public SQLHelper() {
		try {
			SAXBuilder sb = new SAXBuilder();
			SkillLevelDescriptions = sb.build(new File("data/db/skillleveldescription.xml"));
			Skillinfos = sb.build(new File("data/db/skillinfo.xml"));
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 读取data/local/xml中的TXT数据文件，将其生成sql脚本
	 * @param name
	 */
	public void txt2sql(String name) {
		// 文件名
		String fileName = XML_PATH + name + ".china.txt";
		// SQL脚本名
		String tableName = name + "_china_txt";

		BufferedReader in = null;
		PrintStream out = null;
		
		try {
			// 打开文件
			in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "unicode"));
			out = new PrintStream(new FileOutputStream(SQL_PATH + tableName+".sql"));
			
			String comment = "/* " + fileName + " */";
			String droptable = "DROP TABLE IF EXISTS "+ tableName;
			String createtable = "CREATE TABLE " + tableName + "(id INT(8) NOT NULL PRIMARY KEY,text TEXT)ENGINE=INNODB;";
			out.println(comment);
			out.println(droptable);
			out.println(createtable);
			// 逐行读取、解析文件
			String line;
			while ((line = in.readLine()) != null) {
				String[] a = line.split("	");
				String id = a[0];
				String value = null;
				if (a.length == 2) {
					value = a[1];
					value = value.replace("\"", "\'");
					value = "\"" + value + "\"";
				}
				String sql = "INSERT INTO " + tableName + "(id, text) VALUES (" + id + ", " + value + ");";
				out.println(sql);
			}
			out.close();
			out = null;
			in.close();
			in = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭文件
			if (out != null) {
				out.close();
			}
			// 关闭文件
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 扫描Skillinfo.xml，记录所有字段名。
	 */
	@SuppressWarnings("unchecked")
	public void getSkillinfoAttributes() {
		try {
			// 打开skillinfo.xml文件
			SAXBuilder sb = new SAXBuilder();
			Document doc = sb.build(new File("data/db/skillinfo.xml"));
			
			// 统计skill元素所有出现过的属性名
//			String path = "/SkillInfo/SkillList/Skill";
			String path = "/SkillInfo/SkillList/Skill[@HowToGetDesc]";
			List<Element> list = (List<Element>)XPath.selectNodes(doc.getRootElement(), path);
			Map<String, Integer> map = new LinkedHashMap<String, Integer>();
			for (Element skill : list) {
				String skillName = skill.getAttributeValue("SkillLocalName");
				String howToGetDesc = skill.getAttributeValue("DescName");
				System.out.println(Local.text(skillName) + " " + Local.text(howToGetDesc));
				List<Attribute> attributes = skill.getAttributes();
				for (Attribute attr : attributes) {
					String name = attr.getName();
					if (map.get(name) == null) {
						map.put(name, 1);
					} else {
						map.put(name, map.get(name) + 1);
					}
				}
			}
			
			// 打印所有出现过的属性名
			StringBuffer buffer = new StringBuffer("String[] attrs = {");
			Set<String> keySet = map.keySet();
			for (String column : keySet) {
				buffer.append("\n\"").append(column).append("\",");
				// System.out.println(column + ", " + map.get(column));
			}
			buffer.append("\n};");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * 生成Skillinfo.sql
	 */
	@SuppressWarnings("unchecked")
	public void createSQL() throws JDOMException, IOException {

		PrintStream out = null;
		out = new PrintStream(new FileOutputStream("Skillinfo.sql"));
		String path = "/SkillInfo/SkillList/Skill";
		List<Element> list = XPath
				.selectNodes(Skillinfos.getRootElement(), path);

		for (Element skill : list) {
			StringBuffer buffer = new StringBuffer(
					"INSERT INTO Skillinfo(SkillID, SkillEngName, SkillLocalName, SkillType, SkillTypeRebalance, TriggerType, SkillCategory, Feature, Season, locale, Version, DescName, UIType, MaxStackNum, AutoStack, StackLimitTime, UseType, RaceBasic, BasicType, NeedToSave, IsHidden, IsSpecialAction, LvZeroUsable, OnceALife, TransformType, ParentSkill, TargetPreference, TargetRange, TargetPreparedType, ProcessTargetType, ImageFile, PositionX, PositionY, ClosedDesc, SkillDesc, IsMovingSkill, WaitLock, MasterTitle, PrepareLock, ProcessLock, CompleteLock, DecreaseDuraByBrionac, DecreaseDagdaCount, Showicon, AvailableRace, PublicSeason, Public, HowToGetDesc, Venturer, Knight, Wizard, Archer, Merchant, Alchemist, Fighter, Bard, PuppetMaster, travel, combat, magic, archery, commerce, battlealchemy, fight, music, puppet, lance, bless, transmutealchemy, cook, blacksmith, sewing, pharmacy, carpentry, dualgun, druid, boreadae, AutoGetLevel, Var1, Var2, Var3, Var4, Var5, Var6, Var7, Var8, Var9, Var10, Var11, Var12, Var13, Var14, Var15, Var16, Var17, Var18, Var19, Var20) VALUES(");
			buffer.append(skill.getAttributeValue(Skillinfo.attrs[0]));
			for (int i = 1; i < 98; i++) {
				String value = skill.getAttributeValue(Skillinfo.attrs[i]);
				if (value == null) {
					buffer.append(", null");
				} else {
					buffer.append(", \"").append(value).append("\"");
				}
			}
			buffer.append(");");
			out.println(buffer.toString());
		}

		out.close();
	}

	/**
	 * 扫描SkillLevelDescription.xml，记录所有字段名。
	 */
	@SuppressWarnings("unchecked")
	public void getAttributes2() throws JDOMException {

		Map<String, Integer> map = new HashMap<String, Integer>();
		String path = "//SkillLevelDetail";
		List<Element> list = XPath.selectNodes(SkillLevelDescriptions
				.getRootElement(), path);

		for (Element skill : list) {
			List<Attribute> attributes = skill.getAttributes();
			for (Attribute attr : attributes) {
				String name = attr.getName();
				if (map.get(name) == null) {
					map.put(name, 1);
				} else {
					map.put(name, map.get(name) + 1);
				}
			}
		}
		StringBuffer buffer = new StringBuffer("String[] attrs = {");
		Set<String> keySet = map.keySet();
		for (String column : keySet) {
			buffer.append("\n\"").append(column).append("\",");
			System.out.println(column + ", " + map.get(column));
		}
		buffer.append("\n};");
		System.out.println(buffer.toString());
	}

	/**
	 * 生成SkillLevelDescription.sql
	 */
	@SuppressWarnings("unchecked")
	public void createSQL2() throws JDOMException, IOException {

		PrintStream out = null;
		out = new PrintStream(new FileOutputStream("SkillLevelDescription.sql"));
		String path = "//SkillLevelDetail";
		List<Element> list = XPath.selectNodes(SkillLevelDescriptions
				.getRootElement(), path);

		for (Element skill : list) {
			StringBuffer buffer = new StringBuffer(
					"INSERT INTO SkillLevelDescription(DescName, race, Version, Feature, SkillLevel, CharacterPrepareTime, PrepareTime, Cooltime, AbilityNecessary, StaminaNecessary, StaminaModPreparing, StaminaModWaiting, StaminaModProcessing, ManaNecessary, ManaModPreparing, ManaModWaiting, ManaModProcessing, CombatPower, StackPerCast, EffectDescription, Conditions, Promotion, Var1, Var2, Var3, Var4, Var5, Var6, Var7, Var8, Var9, Var10, Var11, Var12, Var13, Var14, Var15, Var16, Var17, Var18, Var19, Var20, intVar1, intVar2, XMLData, BonusLife, BonusMana, BonusStamina, BonusSTR, BonusINT, BonusDEX, BonusWill, BonusLuck, AttackRange, LevelDescription, OptionApplyDmgMin, OptionApplyDmgMax, OptionApplyCritical, OptionApplyBalance, OptionApplyWoundMin, OptionApplyWoundMax) VALUES(");
			Element s = (Element) skill.getParent();
			buffer.append("\"").append(s.getName()).append("\"");
			buffer.append(", \"").append(s.getAttributeValue("race")).append(
					"\"");
			for (int i = 0; i < SkillLevelDetail.attrs.length; i++) {
				String value = skill.getAttributeValue(SkillLevelDetail.attrs[i]);
				if (value == null) {
					buffer.append(", null");
				} else {
					buffer.append(", \"").append(value).append("\"");
				}
			}
			buffer.append(");");
			out.println(buffer.toString());
		}

		out.close();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SQLHelper helper = new SQLHelper();
		//helper.txt2sql("skillinfo");
		//helper.txt2sql("skillleveldescription");
		helper.getSkillinfoAttributes();
	}

}
