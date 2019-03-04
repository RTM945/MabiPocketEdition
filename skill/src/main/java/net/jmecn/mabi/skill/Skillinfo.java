package net.jmecn.mabi.skill;

import net.jmecn.mabi.util.Local;

public class Skillinfo {
	public static final String[] category = { "Unknown1", "生活", "战斗", "魔法", "炼金术", "格斗术", "音乐",
		"人偶术", "双枪", "隐藏才能", "变身", "半神化", "Unknown2" };
	public static String[] attrs = { "SkillID", "SkillEngName", "SkillLocalName",
		"SkillType", "SkillTypeRebalance", "TriggerType", "SkillCategory",
		"Feature", "Season", "locale", "Version", "DescName", "UIType",
		"MaxStackNum", "AutoStack", "StackLimitTime", "UseType",
		"RaceBasic", "BasicType", "NeedToSave", "IsHidden",
		"IsSpecialAction", "LvZeroUsable", "OnceALife", "TransformType",
		"ParentSkill", "TargetPreference", "TargetRange",
		"TargetPreparedType", "ProcessTargetType", "ImageFile",
		"PositionX", "PositionY", "ClosedDesc", "SkillDesc",
		"IsMovingSkill", "WaitLock", "MasterTitle", "PrepareLock",
		"ProcessLock", "CompleteLock", "DecreaseDuraByBrionac",
		"DecreaseDagdaCount", "Showicon", "AvailableRace", "PublicSeason",
		"Public", "HowToGetDesc", "Venturer", "Knight", "Wizard", "Archer",
		"Merchant", "Alchemist", "Fighter", "Bard", "PuppetMaster",
		"travel", "combat", "magic", "archery", "commerce",
		"battlealchemy", "fight", "music", "puppet", "lance", "bless",
		"transmutealchemy", "cook", "blacksmith", "sewing", "pharmacy",
		"carpentry", "dualgun", "druid", "boreadae", "AutoGetLevel",
		"Var1", "Var2", "Var3", "Var4", "Var5", "Var6", "Var7", "Var8",
		"Var9", "Var10", "Var11", "Var12", "Var13", "Var14", "Var15",
		"Var16", "Var17", "Var18", "Var19", "Var20" };
	
	private String skillID;
	private String skillEngName;
	private String skillLocalName;
	private String descName;
	private String closedDesc;
	private String skillDesc;
	private String howToGetDesc;
	
	private String[] var = new String[20];
	
	public String getSkillID() {
		return skillID;
	}
	public void setSkillID(String skillID) {
		this.skillID = skillID;
	}
	public String getSkillEngName() {
		return skillEngName;
	}
	public void setSkillEngName(String skillEngName) {
		this.skillEngName = skillEngName;
	}
	public String getSkillLocalName() {
		return Local.text(skillLocalName);
	}
	public void setSkillLocalName(String skillLocalName) {
		this.skillLocalName = skillLocalName;
	}
	public String getDescName() {
		return descName;
	}
	public void setDescName(String descName) {
		this.descName = descName;
	}
	public String getClosedDesc() {
		return Local.text(closedDesc);
	}
	public void setClosedDesc(String closedDesc) {
		this.closedDesc = closedDesc;
	}
	public String getSkillDesc() {
		return Local.text(skillDesc);
	}
	public void setSkillDesc(String skillDesc) {
		this.skillDesc = skillDesc;
	}
	public String getHowToGetDesc() {
		return Local.text(howToGetDesc);
	}
	public void setHowToGetDesc(String howToGetDesc) {
		this.howToGetDesc = howToGetDesc;
	}
	public void setVar(int index, String value) {
		if (index < 0 || index > 19) {
			return;
		}
		var[index] = value;
	}
	public String getVar(int index) {
		if (index < 0 || index > 19) {
			return null;
		}
		return Local.text(var[index]);
	}
	public String toString() {
		return Local.text(skillLocalName);
	}
}
