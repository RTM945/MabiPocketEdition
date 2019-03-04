package net.jmecn.mabi.item;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import net.jmecn.mabi.item.model.MabiItem;
import net.jmecn.mabi.item.model.Upgrade;
import net.jmecn.mabi.util.CodeGenerator;

public class ItemUpgrade {

	private Document itemupgradedb = null;
	private Itemdb db = new Itemdb();
	public ItemUpgrade() {
		try {
			SAXBuilder sb = new SAXBuilder();
			itemupgradedb = sb.build(new File("data/db/itemupgradedb.xml"));
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 统计属性数目
	 */
	@SuppressWarnings("unchecked")
	public void getAttrCount() {
		String path = "/upgrade_db/upgrade";
		
		try {
			List<Element> list = XPath.selectNodes(itemupgradedb.getRootElement(), path);
			int min = 0x7fffffff;
			int max = -1;
			for (Element upgrade : list) {
				int attrCount = upgrade.getAttributes().size();
				if (attrCount > max) max = attrCount;
				if (attrCount < min) min = attrCount;
			}
			String str = String.format("Max:%d Min:%d Total:%d", max, min, list.size());
			System.out.println(str);
		} catch (JDOMException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 统计公共属性
	 */
	@SuppressWarnings("unchecked")
	public void getCommonAttr() {
		String path = "/upgrade_db/upgrade";
		int maxCount = 2307;
		
		try {
			List<Element> list = XPath.selectNodes(itemupgradedb.getRootElement(), path);
			LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();

			// 统计各个属性出现的次数
			for (Element upgrade : list) {
				List<Attribute> attrs = upgrade.getAttributes();
				for (Attribute attr : attrs) {
					String key = attr.getName();
					if (map.containsKey(key)) {
						int n = map.get(key);
						n++;
						map.put(key, n);
					} else {
						map.put(key, 1);
					}
				}
			}
			
			// 列出所有属性
			Set<String> keySet = map.keySet();
			String aryStr = createArrayCode(keySet);
			System.out.println(aryStr);

			// 找出出现次数最多的属性，就是公共属性，其他就是特殊属性
			List<String> commonKeys = new ArrayList<String>();
			List<String> otherKeys = new ArrayList<String>();
			for(String key : keySet) {
				int n = map.get(key);
				if (n == maxCount) {
					commonKeys.add(key);
				} else {
					otherKeys.add(key);
				}
			}
			
			String classCode = CodeGenerator.createSourceCode(commonKeys, "Upgrade");
			System.out.println(classCode);
			classCode = CodeGenerator.createSourceCode(otherKeys, "Upgrade");
			System.out.println(classCode);
		} catch (JDOMException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 宝石改。
	 * 共134种不同的宝石改。
	 */
	@SuppressWarnings("unchecked")
	public List<Upgrade> getGemUpgrade() {
		List<Upgrade> upgrades = new ArrayList<Upgrade>();
		String path = "/upgrade_db/upgrade[@need_gem]";
		try {
			List<Element> list = XPath.selectNodes(itemupgradedb.getRootElement(), path);
			for (Element element : list) {
				Upgrade upgrade = elm2obj(element);
				upgrades.add(upgrade);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		
		return upgrades;
	}
	/**
	 * 查询所有改造
	 */
	@SuppressWarnings("unchecked")
	public List<Upgrade> getAllUpgrade() {
		List<Upgrade> upgrades = new ArrayList<Upgrade>();
		String path = "/upgrade_db/upgrade";
		try {
			List<Element> list = XPath.selectNodes(itemupgradedb.getRootElement(), path);
			for (Element element : list) {
				Upgrade upgrade = elm2obj(element);
				upgrades.add(upgrade);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		
		return upgrades;
	}
	/**
	 * Element 转成 Upgrade 对象
	 * @param element
	 * @return
	 */
	private Upgrade elm2obj(Element element) {
		if (element == null) {
			return null;
		}
		
		Upgrade upgrade = new Upgrade();
		
		String id = element.getAttributeValue("id");
		String name = element.getAttributeValue("name");
		String localname = element.getAttributeValue("localname");
		String icon = element.getAttributeValue("icon");
		String desc = element.getAttributeValue("desc");
		String effect = element.getAttributeValue("effect");
		String need_ep = element.getAttributeValue("need_ep");
		String need_gold = element.getAttributeValue("need_gold");
		String item_filter = element.getAttributeValue("item_filter");
		String available_npc = element.getAttributeValue("available_npc");
		String upgraded_min = element.getAttributeValue("upgraded_min");
		String upgraded_max = element.getAttributeValue("upgraded_max");
		
		upgrade.setId(id);
		upgrade.setName(name);
		upgrade.setLocalname(localname);
		upgrade.setDesc(desc);
		upgrade.setEffect(effect);
		upgrade.setNeedEp(need_ep);
		upgrade.setNeedGold(need_gold);
		upgrade.setItemFilter(item_filter);
		upgrade.setAvailableNpc(available_npc);
		upgrade.setUpgradedMin(upgraded_min);
		upgrade.setUpgradedMax(upgraded_max);
		
		upgrade.setIcon(icon);
		
		String need_gem = element.getAttributeValue("need_gem");
		String gem_upgraded_min = element.getAttributeValue("gem_upgraded_min");
		String gem_upgraded_max = element.getAttributeValue("gem_upgraded_max");
		
		upgrade.setNeedGem(need_gem);
		upgrade.setGemUpgradedMin(gem_upgraded_min);
		upgrade.setGemUpgradedMax(gem_upgraded_max);
		return upgrade;
	}
	
	private List<MabiItem> filter(String item_filter) {
		String[] filters = null;
		if (item_filter.contains("|")) {
			filters = item_filter.split("\\|");
		} else if(item_filter.contains("&")) {
			filters = item_filter.split("&");
		} else {
			filters = new String[] {item_filter};
		}
		List<MabiItem> items = new ArrayList<MabiItem>();
		for (String filter : filters) {
			filter = filter.trim();
			String category = filter.substring(1, filter.length() - 1);
			items.addAll(db.getByCategory(category));
		}
		return items;
	}
	/**
	 * 生成属性数组
	 */
	private String createArrayCode(Collection<String> attrs) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\tpublic static String[] ATTRS = {").append("\r\n\t\t");
		for (String attr: attrs) {
			buffer.append("\"").append(attr).append("\", ");
		}
		buffer.append("\r\n\t};").append("\r\n");
		return buffer.toString();
	}
	
	/**
	 * 控制台打印改造数据
	 * @param upgrades
	 */
	public void createHtml(List<Upgrade> upgrades) {
		try {
			PrintWriter out = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream("html/upgrade.htm"),"utf-8"));
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("\t<head>");
			out.println("\t\t<title>物品改造</title>");
			out.println("\t\t<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">");
			out.println("\t\t<link rel=\"stylesheet\" href=\"css/style.css\" type=\"text/css\"/>");
			out.println("\t</head>");
			out.println("\t<body>");

			out.println("\t\t<table class=\"mabitable\">");
			out.println("\t\t\t<tr><th>改造项目</th><th>可改造物品</th><th>NPC</th><th>效果</th>" +
					"<th>熟练</th><th>价格</th><th>所需道具</th><th>改造顺序</th></tr>");

			int size = upgrades.size();
			int present = 0;
			for (int i=0; i<size; i++) {
				Upgrade upgrade = upgrades.get(i);
				out.println("\t\t\t<tr>");
				out.println("\t\t\t\t<td>" + upgrade.getLocalname() + "</td>");
				
				// Items
				out.print("\t\t\t\t<td>");
				for (MabiItem item : filter(upgrade.getItemFilter())) {
					out.print(item.getTextName1() + "<br/>");
				}
				out.println("</td>");
				// NPC
				out.print("\t\t\t\t<td>");
				for (String npc : upgrade.getAvailableNpc().split(";")) {
					out.print(npc + "<br/>");
				}
				out.println("</td>");
				
				// Effect
				out.print("\t\t\t\t<td>");
				for (String npc : upgrade.getEffect().split(";")) {
					out.print(npc + "<br/>");
				}
				out.println("</td>");
				
				out.println("\t\t\t\t<td>" + upgrade.getNeedEp() + "</td>");
				out.println("\t\t\t\t<td>" + upgrade.getNeedGold() + "</td>");
				if (upgrade.getNeedGem() == null) {
					out.println("\t\t\t\t<td>-</td>");
					out.println("\t\t\t\t<td>" + upgrade.getUpgradedMin() + "~" + upgrade.getUpgradedMax()+ "</td>");
				} else {
					// Gem
					out.print("\t\t\t\t<td>");
					for (String gem : upgrade.getNeedGem().split(";")) {
						String[] a = gem.split(",");
						out.print(db.get(a[0]).getTextName1() + a[1]+ "cm<br/>");
					}
					out.println("</td>");
					
					out.println("\t\t\t\t<td>" + upgrade.getGemUpgradedMin() + "~" + upgrade.getGemUpgradedMax()+ "</td>");
				}
				
				out.println("\t\t\t</tr>");
				
				int n = i * 100 / size + 1;
				if (n > present) {
					present = n;
					System.out.println(present + "%");
				}
			}
			out.println("\t\t</table>");
			out.println("\t</body>");
			out.println("</html>");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ItemUpgrade iu = new ItemUpgrade();
		iu.getCommonAttr();
	}

}
