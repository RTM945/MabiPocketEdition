package net.jmecn.mabi.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.jmecn.mabi.util.Local;

public class SkillLevelDetail {
	public static String[] attrs = {
			"Version",
			"Feature",// 100
			"SkillLevel",
			"CharacterPrepareTime",
			"PrepareTime",
			"Cooltime",
			"AbilityNecessary",
			"StaminaNecessary",
			"StaminaModPreparing",
			"StaminaModWaiting",
			"StaminaModProcessing",
			"ManaNecessary",
			"ManaModPreparing",
			"ManaModWaiting",
			"ManaModProcessing",
			"CombatPower",
			"StackPerCast",
			"EffectDescription",// 100
			"Conditions",// 100
			"Promotion",// 100
			"Var1", "Var2", "Var3", "Var4", "Var5",
			"Var6", "Var7", "Var8", "Var9", "Var10",
			"Var11", "Var12", "Var13", "Var14", "Var15",
			"Var16", "Var17", "Var18", "Var19", "Var20",
			"intVar1",
			"intVar2",
			"XMLData",// 100
			"BonusLife", "BonusMana", "BonusStamina", "BonusSTR",
			"BonusINT", "BonusDEX", "BonusWill", "BonusLuck",
			"AttackRange",
			"LevelDescription",// 100
			"OptionApplyDmgMin", "OptionApplyDmgMax", "OptionApplyCritical",
			"OptionApplyBalance", "OptionApplyWoundMin", "OptionApplyWoundMax",
			"BonusFunction"// 还是不要了吧
	};

	private String skillLevel;
	private int abilityNecessary;
	private int combatPower;
	private String effectDescription;// 100
	private String conditions;// 100
	private String levelDescription;// 100
	
	private String[] var;
	
	private int bonusLife;
	private int bonusMana;
	private int bonusStamina;
	private int bonusStr;
	private int bonusInt;
	private int bonusDex;
	private int bonusWill;
	private int bonusLuck;

	
	SkillLevelDetail() {
		var = new String[20];
		bonusLife = 0;
		bonusMana = 0;
		bonusStamina = 0;
		bonusStr = 0;
		bonusInt = 0;
		bonusDex = 0;
		bonusWill = 0;
		bonusLuck = 0;
	}

	static String[] level = { "练习", "F", "E", "D", "C", "B", "A", "9", "8",
			"7", "6", "5", "4", "3", "2", "1", "一段", "二段", "三段" };
	public String getSkillLevel() {
		int i = Integer.parseInt(skillLevel);
		if (i > 18) {
			System.out.println(i + Local.text(levelDescription));
			return skillLevel;
		}
		return level[i];
	}

	public void setSkillLevel(String skillLevel) {
		this.skillLevel = skillLevel;
	}

	public int getAbilityNecessary() {
		return abilityNecessary;
	}

	public void setAbilityNecessary(String abilityNecessary) {
		this.abilityNecessary = Integer.parseInt(abilityNecessary);
	}

	public int getCombatPower() {
		return combatPower;
	}

	public void setCombatPower(String combatPower) {
		this.combatPower = Integer.parseInt(combatPower);
	}

	public String getEffectDescription() {
		return Local.text(effectDescription);
	}

	public void setEffectDescription(String effectDescription) {
		this.effectDescription = effectDescription;
	}

	public List<String[]> getConditions() {
		String text = Local.text(conditions);
		return parseAdd(text);
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public String getLevelDescription() {
		return Local.text(levelDescription);
	}

	public void setLevelDescription(String levelDescription) {
		this.levelDescription = levelDescription;
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
		return var[index];
	}
	
	public int getBonusLife() {
		return bonusLife;
	}

	public void setBonusLife(String bonusLife) {
		if (bonusLife != null) {
			this.bonusLife = Integer.parseInt(bonusLife);
		}
	}

	public int getBonusMana() {
		return bonusMana;
	}

	public void setBonusMana(String bonusMana) {
		if (bonusMana != null) {
			this.bonusMana = Integer.parseInt(bonusMana);
		}
	}

	public int getBonusStamina() {
		return bonusStamina;
	}

	public void setBonusStamina(String bonusStamina) {
		if (bonusStamina != null) {
			this.bonusStamina = Integer.parseInt(bonusStamina);
		}
	}

	public int getBonusStr() {
		return bonusStr;
	}

	public void setBonusStr(String bonusStr) {
		if (bonusStr != null) {
			this.bonusStr = Integer.parseInt(bonusStr);
		}
	}

	public int getBonusInt() {
		return bonusInt;
	}

	public void setBonusInt(String bonusInt) {
		if (bonusInt != null) {
			this.bonusInt = Integer.parseInt(bonusInt);
		}
	}

	public int getBonusDex() {
		return bonusDex;
	}

	public void setBonusDex(String bonusDex) {
		if (bonusDex != null) {
			this.bonusDex = Integer.parseInt(bonusDex);
		}
	}

	public int getBonusWill() {
		return bonusWill;
	}

	public void setBonusWill(String bonusWill) {
		if (bonusWill != null) {
			this.bonusWill = Integer.parseInt(bonusWill);
		}
	}

	public int getBonusLuck() {
		return bonusLuck;
	}

	public void setBonusLuck(String bonusLuck) {
		if (bonusLuck != null) {
			this.bonusLuck = Integer.parseInt(bonusLuck);
		}
	}

	private List<String[]> parseAdd(String text) {
		List<String[]> list = new ArrayList<String[]>();
		if (text.contains("add(")) {
			Pattern p = Pattern.compile("add([^;]+);");
			Matcher m = p.matcher(text);
			while (m.find()) {
				String str = m.group();
				str = str.substring(4, str.length()-2);// 去掉add();
				str = str.replaceAll("\"", "");// 去掉引号
				String[] args = str.split(",");
				list.add(args);
			}
		}
		return list;
	}
}