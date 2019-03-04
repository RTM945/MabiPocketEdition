package net.jmecn.mabi.shop;

import java.util.ArrayList;

/**
 * 广告板数据集
 */
public class AdvertiseItems {

	private int page;		// 当前页码
	private boolean next;	// 是否有下一页
	private boolean pre;	// 是否有上一页
	
	private ArrayList<ItemDesc> items;	// 装备列表
	
	/**
	 * 构造方法
	 * 初始化
	 */
	public AdvertiseItems() {
		page = 1;
		pre = false;
		next = false;
		items = new ArrayList<ItemDesc>();
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public boolean isNext() {
		return next;
	}

	public void setNext(boolean next) {
		this.next = next;
	}

	public boolean isPre() {
		return pre;
	}

	public void setPre(boolean pre) {
		this.pre = pre;
	}

	public ArrayList<ItemDesc> getData() {
		return items;
	}

	public void setData(ArrayList<ItemDesc> data) {
		this.items = data;
	}
}