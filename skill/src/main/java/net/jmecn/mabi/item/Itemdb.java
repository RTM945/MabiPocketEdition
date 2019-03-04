package net.jmecn.mabi.item;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import net.jmecn.mabi.item.model.MabiItem;

public class Itemdb {

	private Document itemdb = null;
	public Itemdb() {
		try {
			SAXBuilder sb = new SAXBuilder();
			itemdb = sb.build(new File("data/db/itemdb.xml"));
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public MabiItem get(String id) {
		MabiItem item = null;
		try {
			// 查找指定元素
			String path = "//mabi:Mabi_Item[@ID='" + id + "']";
			XPath xpath = XPath.newInstance(path);
			xpath.addNamespace("mabi", "http://www.tempuri.org/DataSet1.xsd");
			Element mabi_item = (Element) xpath.selectSingleNode(itemdb.getRootElement());
			
			// 赋值
			item = elm2obj(mabi_item);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}
	
	public List<MabiItem> getByCategory(String category) {
		List<MabiItem> items = new ArrayList<MabiItem>();
		try {
			// 查找指定元素
			String path = "//mabi:Mabi_Item[contains(@Category, '" + category + "')]";
			XPath xpath = XPath.newInstance(path);
			xpath.addNamespace("mabi", "http://www.tempuri.org/DataSet1.xsd");
			Element mabi_item = (Element) xpath.selectSingleNode(itemdb.getRootElement());
			
			// 赋值
			MabiItem item = elm2obj(mabi_item);
			if (item != null)
			items.add(item);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return items;
	}
	
	private MabiItem elm2obj(Element element) {
		if (element == null) {
			return null;
		}
		
		// 赋值
		MabiItem item = new MabiItem();
		item.setId(element.getAttributeValue("ID"));
		item.setTextName1(element.getAttributeValue("Text_Name1"));
		return item;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Itemdb db = new Itemdb();
		MabiItem item = db.get("64050");
		System.out.println(item.getTextName1());
		
		String category = "*/handle/*";
		category = category.substring(1, category.length() - 1);
		System.out.println(category);
	}

}
