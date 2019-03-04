package net.jmecn.mabi.item.model;

import net.jmecn.mabi.util.Local;

public class MabiItem {

	private String id;
	private String text_Name1;
	public MabiItem() {
		id = "";
		text_Name1 = "";
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTextName1() {
		return Local.text(text_Name1);
	}
	public void setTextName1(String textName1) {
		text_Name1 = textName1;
	}
}
