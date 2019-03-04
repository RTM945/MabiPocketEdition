package net.jmecn.mabi.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.jmecn.mabi.shop.AdvertiseItems;
import net.jmecn.mabi.shop.Search;
import net.jmecn.mabi.shop.ItemDesc;

/**
 * 洛奇广告板
 */
public class ShopAdvertiseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private HashMap<String, String> mabiitem;// ID - InvImage映射表

	/**
	 * 初始化物品信息
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		mabiitem = new HashMap<String, String>();
		try {
			InputStream input = getClass().getResourceAsStream("itemdb.ini");
			Properties properties = new Properties();
			properties.load(input);
			Iterator<Entry<Object, Object>> it = properties.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Object, Object> entry = it.next();
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				if (value.contains(";")) {
					int index = value.lastIndexOf(";");
					value = value.substring(index + 1);
					index = value.lastIndexOf("_");
					value = value.substring(0, index);
				}
				mabiitem.put(key, value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	/**
	 * 处理POST请求
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// 初始化查询条件
		Search search = new Search();
		search.setPage(req.getParameter("page"));
		search.setRow(req.getParameter("row"));
		search.setSortType(req.getParameter("sortType"));
		search.setSortOption(req.getParameter("sortOption"));
		search.setSearchType(req.getParameter("searchType"));

		// 中文编码
		String searchWord = req.getParameter("searchWord");
		if (searchWord != null) {
			searchWord = new String(searchWord.getBytes("iso-8859-1"), "utf-8");
			search.setSearchWord(searchWord);
		}
		search.setServer(req.getParameter("server"));

		System.out.println(search.getURL());
		// 查询
		AdvertiseItems adItems = parse(fetchXml(search.getURL()));

		// 携带数据，返回shop.jsp页面
		req.setAttribute("adItems", adItems);
		req.setAttribute("search", search);
		req.getRequestDispatcher("shop.jsp").forward(req, resp);
	}

	/**
	 * 读取XML文件
	 * 
	 * @param search
	 * @return
	 */
	private String fetchXml(String xmlUrl) {
		String content = null;
		HttpURLConnection httpConn;
		try {
			URL url = new URL(xmlUrl);
			httpConn = (HttpURLConnection) url.openConnection();

			// 1 open http connection
			httpConn.setConnectTimeout(5000);
			httpConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			httpConn.connect();
			// 2 download
			int responseCode = httpConn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream in = httpConn.getInputStream();// input
				BufferedReader bis = new BufferedReader(new InputStreamReader(
						in, "UTF-16"));

				StringBuffer buffer = new StringBuffer();
				String line;
				while ((line = bis.readLine()) != null) {
					buffer.append(line);
				}
				content = buffer.toString();
				in.close();
			}
			// 3 close http connection
			httpConn.disconnect();

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("文件长度: " + content.getBytes().length);
		return content;
	}

	/**
	 * 解析ShopAdvertise.xml
	 * 
	 * @param xml
	 * @return
	 */
	private AdvertiseItems parse(String xml) {
		AdvertiseItems adItems = new AdvertiseItems();
		if (xml == null) {
			return adItems;
		}
		try {
			// 生成Document
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));

			Document doc = db.parse(is);
			// 解析所有ItemDesc节点
			NodeList nodes = doc.getElementsByTagName("ItemDesc");
			if (nodes.getLength() == 0)
				return adItems;

			// 装备列表
			ArrayList<ItemDesc> items = new ArrayList<ItemDesc>();
			// iterate the ItemDesc
			for (int i = 0; i < nodes.getLength(); i++) {
				ItemDesc item = new ItemDesc();

				Element element = (Element) nodes.item(i);
				item.setItem_ID(element.getAttribute("Item_ID"));
				item.setShop_Name(element.getAttribute("Shop_Name"));
				item.setArea(element.getAttribute("Area"));
				item.setComment(element.getAttribute("Comment"));
				item.setStart_Time(element.getAttribute("Start_Time"));
				item.setItem_ClassId(element.getAttribute("Item_ClassId"));
				item.setChar_Name(element.getAttribute("Char_Name"));
				item.setItem_Name(element.getAttribute("Item_Name"));
				item.setItem_Price(element.getAttribute("Item_Price"));
				item.setItem_Color1(element.getAttribute("Item_Color1"));
				item.setItem_Color2(element.getAttribute("Item_Color2"));
				item.setItem_Color3(element.getAttribute("Item_Color3"));
				item.setCount(element.getAttribute("Count"));
				item.setImage(mabiitem.get(item.getClassId()));

				items.add(item);
			}
			// 解析节点
			Element root = (Element) doc.getElementsByTagName("AdvertiseItems")
					.item(0);
			int next = Integer.parseInt(root.getAttribute("NextPage"));
			int page = Integer.parseInt(root.getAttribute("NowPage"));
			adItems.setPage(page);
			adItems.setNext(next == 1);
			adItems.setPre(page > 1);
			adItems.setData(items);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		return adItems;
	}
}
