package net.jmecn.mabi.item.model;

import net.jmecn.mabi.util.Local;

public class Upgrade {
	public static String[] ATTRS = {
		"id", "name", "localname", "desc", "effect",
		"icon", "need_ep", "need_gold", "item_filter",
		"upgraded_min", "upgraded_max", "available_npc",
		"rebalanced", "feature", "need_gem", "gem_upgraded_min",
		"gem_upgraded_max", "generation", "season", "hidden_id",
		"discovery_rate"
	};
	// 公共私有属性，出现2307次
	private String id;
	private String name;
	private String localname;
	private String desc;
	private String effect;
	private String need_ep;
	private String need_gold;
	private String item_filter;
	private String available_npc;
	
	// 特殊私有属性
	private String icon;// 2302
	private String upgraded_min;// 2173
	private String upgraded_max;// 2173
	private String rebalanced;// 1770
	private String feature;// 54
	private String need_gem;// 134
	private String gem_upgraded_min;// 134
	private String gem_upgraded_max;// 134
	private String generation;// 881
	private String season;// 881
	private String hidden_id;// 22
	private String discovery_rate;// 22
	
	// 构造方法初始化
	public Upgrade() {
		id = "";
		name = "";
		localname = "";
		desc = "";
		effect = "";
		need_ep = "";
		need_gold = "";
		item_filter = "";
		available_npc = "";
		
		icon = "";
		upgraded_min = "";
		upgraded_max = "";
		rebalanced = "";
		feature = "";
		need_gem = "";
		gem_upgraded_min = "";
		gem_upgraded_max = "";
		generation = "";
		season = "";
		hidden_id = "";
		discovery_rate = "";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocalname() {
		return Local.text(localname);
	}

	public void setLocalname(String localname) {
		this.localname = localname;
	}

	public String getDesc() {
		return Local.text(desc);
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getEffect() {
		return effect;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}

	public String getNeedEp() {
		return need_ep;
	}

	public void setNeedEp(String needEp) {
		need_ep = needEp;
	}

	public String getNeedGold() {
		return need_gold;
	}

	public void setNeedGold(String needGold) {
		need_gold = needGold;
	}

	public String getItemFilter() {
		return item_filter;
	}

	public void setItemFilter(String itemFilter) {
		item_filter = itemFilter;
	}

	public String getAvailableNpc() {
		return available_npc;
	}

	public void setAvailableNpc(String availableNpc) {
		available_npc = availableNpc;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getUpgradedMin() {
		return upgraded_min;
	}

	public void setUpgradedMin(String upgradedMin) {
		upgraded_min = upgradedMin;
	}

	public String getUpgradedMax() {
		return upgraded_max;
	}

	public void setUpgradedMax(String upgradedMax) {
		upgraded_max = upgradedMax;
	}

	public String getRebalanced() {
		return rebalanced;
	}

	public void setRebalanced(String rebalanced) {
		this.rebalanced = rebalanced;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public String getNeedGem() {
		return need_gem;
	}

	public void setNeedGem(String needGem) {
		need_gem = needGem;
	}

	public String getGemUpgradedMin() {
		return gem_upgraded_min;
	}

	public void setGemUpgradedMin(String gemUpgradedMin) {
		gem_upgraded_min = gemUpgradedMin;
	}

	public String getGemUpgradedMax() {
		return gem_upgraded_max;
	}

	public void setGemUpgradedMax(String gemUpgradedMax) {
		gem_upgraded_max = gemUpgradedMax;
	}

	public String getGeneration() {
		return generation;
	}

	public void setGeneration(String generation) {
		this.generation = generation;
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public String getHiddenId() {
		return hidden_id;
	}

	public void setHiddenId(String hiddenId) {
		hidden_id = hiddenId;
	}

	public String getDiscoveryRate() {
		return discovery_rate;
	}

	public void setDiscoveryRate(String discoveryRate) {
		discovery_rate = discoveryRate;
	}
}