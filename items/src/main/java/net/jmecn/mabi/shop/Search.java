package net.jmecn.mabi.shop;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 搜索条件
 */
public class Search {
	// 地址
	private String URL = "http://app%s.luoqi.com.cn/ShopAdvertise/ShopAdvertise.asp?Name_Server=mabicn%s&Page=%s&Row=%s&SortType=%s&SortOption=%s&SearchType=%s&SearchWord=%s";

	private String app;
	private String nameServer;
	private String page;// 页码
	private String row;// 每页显示条数
	private String sortType;// 排序类型
	private String sortOption;// 升/降
	private String searchType;// 搜索类型
	private String searchWord;// 关键字

	private static Map<String, String> serverMap = new HashMap<String, String>();
	static {
		serverMap.put("16", "01");// 玛丽
		serverMap.put("17", "01");// 鲁拉里
		// serverMap.put("18", "03");// 塔拉克 - 已废弃
		// serverMap.put("19", "07");// 薇娜 - 已废弃
		// serverMap.put("20", "08");// 莫利尔 - 已废弃
		serverMap.put("21", "04");// 克里斯特
		// serverMap.put("22", "09");// 福格斯 - 已废弃
		// serverMap.put("23", "09");// 普莱达 - 已废弃
		// serverMap.put("24", "03");// 普雷丝 - 已废弃
		serverMap.put("27", "09");// 莉莉丝
		serverMap.put("28", "03");// 伊文
		serverMap.put("29", "10");// 西蒙
	}

	/**
	 * 构造方法
	 */
	public Search() {
		app = "01";
		nameServer = "16";
		page = "1";
		row = "10";
		searchType = "4";
		sortType = "";
		sortOption = "1";
		searchWord = "";
	}

	/**
	 * 设置服务器
	 * 
	 * @param server
	 *            服务器编号
	 */
	public void setServer(String server) {
		String app = serverMap.get(server);
		if (app != null) {
			this.nameServer = server;
			this.app = app;
		}
	}

	public String getServer() {
		return nameServer;
	}

	public String getSearchType() {
		return searchType;
	}

	public String getSortType() {
		return sortType;
	}

	public String getSortOption() {
		return sortOption;
	}

	public String getURL() {
		String searchWord = this.searchWord;
		try {
			searchWord = URLEncoder.encode(searchWord, "gbk");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return String.format(URL, app, nameServer, page, row, sortType,
				sortOption, searchType, searchWord);
	}

	public int getPage() {
		return Integer.parseInt(page);
	}

	public int getRow() {
		return Integer.parseInt(row);
	}

	public String getSearchWord() {
		return searchWord;
	}

	public void setPage(String page) {
		if (page != null) {
			this.page = page;
		}
	}

	public void setRow(String row) {
		if (row != null) {
			this.row = row;
		}
	}

	public void setSearchType(String searchType) {
		if (searchType != null)
			this.searchType = searchType;
	}

	public void setSortType(String sortType) {
		if (sortType != null)
			this.sortType = sortType;
	}

	public void setSortOption(String sortOption) {
		if (sortOption != null)
			this.sortOption = sortOption;
	}

	public void setSearchWord(String searchWord) {
		if (searchWord != null) {
			searchWord = searchWord.trim();
			// 去掉关键字中间的空格
			if (searchWord.contains(" ")) {
				searchWord = searchWord.substring(searchWord.lastIndexOf(" ") + 1);
			}
			this.searchWord = searchWord;
		}
	}
}
