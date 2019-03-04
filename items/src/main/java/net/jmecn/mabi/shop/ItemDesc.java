package net.jmecn.mabi.shop;

/**
 * 装备描述信息
 */
public class ItemDesc {

	// 这些信息要显示
	private String classId;
	private String charName;// 角色名
	private String itemName;// 装备名
	private int price;// 价格
	private String image;// 图像
	private int color1;// 颜色1
	private int color2;// 颜色2
	private int color3;// 颜色3
	private int count;// 数量
	private String comment;// 备注

	public String getClassId() {
		return classId;
	}

	public String getCharName() {
		return charName;
	}

	public String getName() {
		return itemName;
	}

	public String getPrice() {
		int l = price % 10000;
		int h = price / 10000;
		StringBuffer p = new StringBuffer();
		if (h > 0) {
			p.append(h).append("万");
		}

		if (l > 0) {
			p.append(l);
		}
		return p.toString();
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		if (image != null) {
			this.image = image;
		} else {
			this.image = "";
		}
	}

	public String getColor1() {
		return toHexString(color1);
	}

	public String getColor2() {
		return toHexString(color2);
	}

	public String getColor3() {
		return toHexString(color3);
	}

	/**
	 * 将整数转成16进制代码，用于显示颜色
	 * @param d
	 * @return "000000" ~ "FFFFFF"
	 */
	private String toHexString(int d) {
		String color = Integer.toHexString(d);
		int length = color.length();
		// 如果代码长度超过6，截取后面6位。
		if (length > 6) {
			color = color.substring(length - 6);
		}
		// 如果代码长度小于6，在前面补0。
		if (length < 6) {
			color = "000000" + color;
			color = color.substring(length);
		}
		return color.toUpperCase();
	}

	public int getCount() {
		return count;
	}

	public String getComment() {
		return comment;
	}

	// 这些信息不显示
	private String id;// 流水号
	private String shopName;// 店名 false
	private String area;// 区域 false
	private long startTime;// 开始时间

	public String getId() {
		return id;
	}

	public String getShopName() {
		return shopName;
	}

	public String getArea() {
		return area;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setItem_ID(String itemID) {
		this.id = itemID;
	}

	public void setItem_ClassId(String itemClassId) {
		this.classId = itemClassId;
	}

	public void setChar_Name(String charName) {
		this.charName = charName;
	}

	public void setItem_Name(String itemName) {
		this.itemName = itemName;
	}

	public void setItem_Price(String itemPrice) {
		this.price = Integer.parseInt(itemPrice);
	}

	public void setItem_Color1(String itemColor1) {
		this.color1 = Integer.parseInt(itemColor1);
	}

	public void setItem_Color2(String itemColor2) {
		this.color2 = Integer.parseInt(itemColor2);
	}

	public void setItem_Color3(String itemColor3) {
		this.color3 = Integer.parseInt(itemColor3);
	}

	public void setCount(String count) {
		this.count = Integer.parseInt(count);
	}

	public void setShop_Name(String shopName) {
		this.shopName = shopName;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setStart_Time(String startTime) {
		this.startTime = Long.parseLong(startTime);
	}
}