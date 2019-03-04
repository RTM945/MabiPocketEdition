package net.jmecn.mabi.skill;

import java.io.*;
import java.util.*;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.jmecn.mabi.util.Local;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

public class SkillEngine {
	private Document SkillLevelDescriptions = null;
	private Document Skillinfos = null;

	/**
	 * 构造方法，载入数据
	 */
	public SkillEngine() {
		try {
			SAXBuilder sb = new SAXBuilder();
			SkillLevelDescriptions = sb.build(new File("data/db/skillleveldescription.xml"));
			Skillinfos = sb.build(new File("data/db/skillinfo.xml"));
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Skillinfo> getSkill(int k) {
		String path = "/SkillInfo/SkillList/Skill[@HowToGetDesc and @SkillCategory='"
				+ k + "']";
		List<Skillinfo> infos = new ArrayList<Skillinfo>();
		try {
			List<Element> list = XPath.selectNodes(Skillinfos.getRootElement(),
					path);
			for (Element skill : list) {
				Skillinfo info = new Skillinfo();
				info.setSkillID(skill.getAttributeValue("SkillID"));
				info.setSkillEngName(skill.getAttributeValue("SkillEngName"));
				info.setSkillLocalName(skill
						.getAttributeValue("SkillLocalName"));
				info.setDescName(skill.getAttributeValue("DescName"));
				info.setClosedDesc(skill.getAttributeValue("ClosedDesc"));
				info.setSkillDesc(skill.getAttributeValue("SkillDesc"));
				info.setHowToGetDesc(skill.getAttributeValue("HowToGetDesc"));
				for (int i = 1; i <= 20; i++) {
					String param = "Var" + i;
					String value = skill.getAttributeValue(param);
					info.setVar(i - 1, value);
				}
				infos.add(info);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return infos;
	}

	@SuppressWarnings("unchecked")
	public List<SkillLevelDetail> getSkillLevelDetail(String descName) {
		String path = "/Skill_LevelDetail/" + descName + "/SkillLevelDetail";
		List<SkillLevelDetail> details = new ArrayList<SkillLevelDetail>();
		try {
			List<Element> list = XPath.selectNodes(SkillLevelDescriptions
					.getRootElement(), path);
			for (Element skillDetail : list) {
				SkillLevelDetail detail = new SkillLevelDetail();
				detail.setSkillLevel(skillDetail
						.getAttributeValue("SkillLevel"));
				detail.setAbilityNecessary(skillDetail
						.getAttributeValue("AbilityNecessary"));
				detail.setCombatPower(skillDetail
						.getAttributeValue("CombatPower"));
				detail.setEffectDescription(skillDetail
						.getAttributeValue("EffectDescription"));
				detail.setConditions(skillDetail
						.getAttributeValue("Conditions"));
				detail.setLevelDescription(skillDetail
						.getAttributeValue("LevelDescription"));

				for (int i = 1; i <= 20; i++) {
					String param = "Var" + i;
					String value = skillDetail.getAttributeValue(param);
					detail.setVar(i - 1, value);
				}

				detail.setBonusLife(skillDetail.getAttributeValue("BonusLife"));
				detail.setBonusMana(skillDetail.getAttributeValue("BonusMana"));
				detail.setBonusStamina(skillDetail
						.getAttributeValue("BonusStamina"));
				detail.setBonusStr(skillDetail.getAttributeValue("BonusSTR"));
				detail.setBonusInt(skillDetail.getAttributeValue("BonusINT"));
				detail.setBonusDex(skillDetail.getAttributeValue("BonusDEX"));
				detail.setBonusWill(skillDetail.getAttributeValue("BonusWill"));
				detail.setBonusLuck(skillDetail.getAttributeValue("BonusLuck"));

				details.add(detail);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}

		return details;
	}

	public List<String[]> table(Skillinfo info, List<SkillLevelDetail> details) {
		List<String[]> data = new ArrayList<String[]>();

		String[] ranks = new String[17];// Rank
		String[] aps = new String[17];// AP
		String[] cps = new String[17];// 战斗力
		String[] lifes = new String[17];
		String[] manas = new String[17];
		String[] staminas = new String[17];
		String[] strs = new String[17];
		String[] dexs = new String[17];
		String[] ints = new String[17];
		String[] wills = new String[17];
		String[] lucks = new String[17];

		ranks[0] = "Rank";
		aps[0] = "AP";
		cps[0] = "战斗力";
		lifes[0] = "生命值";
		manas[0] = "魔法值";
		staminas[0] = "耐力值";
		strs[0] = "力量";
		dexs[0] = "敏捷";
		ints[0] = "智力";
		wills[0] = "意志";
		lucks[0] = "幸运";
		int sumAP = 0;
		int sumLife = 0;
		int sumMana = 0;
		int sumStamina = 0;
		int sumStr = 0;
		int sumDex = 0;
		int sumInt = 0;
		int sumWill = 0;
		int sumLuck = 0;
		for (int j = 0; j < 16; j++) {
			SkillLevelDetail detail = details.get(j);
			ranks[j + 1] = detail.getSkillLevel();

			int ap = detail.getAbilityNecessary();
			sumAP += ap;
			aps[j + 1] = ap + "";

			cps[j + 1] = detail.getCombatPower() + "";

			// bonus
			int LIFE = detail.getBonusLife();
			sumLife += LIFE;
			if (LIFE == 0)
				lifes[j + 1] = "";
			else
				lifes[j + 1] = "+" + LIFE;
				
			int MANA = detail.getBonusMana();
			sumMana += MANA;
			if (MANA == 0)
				manas[j + 1] = "";
			else
				manas[j + 1] = "+" + MANA;
			
			int Stamina = detail.getBonusStamina();
			sumStamina += Stamina;
			if (Stamina == 0)
				staminas[j + 1] = "";
			else
				staminas[j + 1] = "+" + Stamina;
			
			int STR = detail.getBonusStr();
			sumStr += STR;
			if (STR == 0)
				strs[j + 1] = "";
			else
				strs[j + 1] = "+" + STR;
			
			int DEX = detail.getBonusDex();
			sumDex += DEX;
			if (DEX == 0)
				dexs[j + 1] = "";
			else
				dexs[j + 1] = "+" + DEX;
			
			int INT = detail.getBonusInt();
			sumInt += INT;
			if (INT == 0)
				ints[j + 1] = "";
			else
				ints[j + 1] = "+" + INT;
			
			int WIL = detail.getBonusWill();
			sumWill += WIL;
			if (WIL == 0)
				wills[j + 1] = "";
			else
				wills[j + 1] = "+" + WIL;
			
			int LUK = detail.getBonusLuck();
			sumLuck += LUK;
			if (LUK == 0)
				lucks[j + 1] = "";
			else
				lucks[j + 1] = "+" + LUK;
		}
		data.add(ranks);
		data.add(aps);
		data.add(cps);
		if (sumLife > 0)
			data.add(lifes);
		if (sumMana > 0)
			data.add(manas);
		if (sumStamina > 0)
			data.add(staminas);
		if (sumStr > 0)
			data.add(strs);
		if (sumDex > 0)
			data.add(dexs);
		if (sumInt > 0)
			data.add(ints);
		if (sumWill > 0)
			data.add(wills);
		if (sumLuck > 0)
			data.add(lucks);

		// 筛选功能项
		List<Integer> ids = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++) {
			String value = info.getVar(i);
			if (value != null) {
				ids.add(i);
			}
		}
		for (int i : ids) {
			String[] func = new String[17];
			String str = info.getVar(i);
			boolean flag = str.contains("时间");
			if (flag) {
				str += "[秒]";
			}
			func[0] = str;
			for (int j = 0; j < 16; j++) {
				SkillLevelDetail detail = details.get(j);
				str = detail.getVar(i);
				if (flag) {
					float t = Integer.parseInt(str) / 1000;
					if (t <= 0.01) {
						flag = false;
					}else{
						str = t + "";
					}
				}
				func[j + 1] = str;
			}
			data.add(func);
		}

		return data;

	}


	/**
	 * new WritableCell
	 * 
	 * @param text
	 * @return
	 */
	private jxl.write.WritableCell newCell(int col, int row, String text) {
		// _LT[xml.localtext.id]
		text = Local.text(text);

		jxl.write.WritableCell cell = new Label(col, row, text);
		if (text != null) {
			// 数字
			try {
				int n = Integer.parseInt(text);
				return new jxl.write.Number(col, row, n);
			} catch (Exception e) {
			}
			// true
			if ("true".equalsIgnoreCase(text)) {
				WritableFont wfc = new WritableFont(WritableFont.ARIAL, 10,
						WritableFont.BOLD, false,
						jxl.format.UnderlineStyle.NO_UNDERLINE,
						jxl.format.Colour.RED);
				WritableCellFormat wcfFC = new WritableCellFormat(wfc);
				cell.setCellFormat(wcfFC);
			}
		}
		return cell;
	}

	/**
	 * 查询所有技能分类
	 * 
	 * @throws JDOMException
	 */
	@SuppressWarnings("unchecked")
	public void getSkillsExcel() {
		String[] columns = Skillinfo.attrs;
		try {
			// 打开文件
			WritableWorkbook book = Workbook.createWorkbook(new File(
					"skillinfos.xls"));

			for (int k = 1; k < Skillinfo.category.length - 1; k++) {
				String path = "/SkillInfo/SkillList/Skill[@HowToGetDesc and @SkillCategory='"
						+ k + "']";
				List<Element> list = XPath.selectNodes(Skillinfos
						.getRootElement(), path);

				// 生成名为“第一页”的工作表，参数0表示这是第一页
				WritableSheet sheet = book
						.createSheet(Skillinfo.category[k], k);

				// 将定义好的单元格添加到工作表中
				for (int j = 0; j < columns.length; j++) {
					sheet.addCell(new Label(j, 0, columns[j]));
				}

				for (int i = 0; i < list.size(); i++) {
					Element skill = list.get(i);
					for (int j = 0; j < columns.length; j++) {
						String text = skill.getAttributeValue(columns[j]);
						sheet.addCell(newCell(j, i + 1, text));// SkillID
					}
				}
			}
			// 写入数据并关闭文件
			book.write();
			book.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void frameset() {
		try {
			PrintWriter out = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream("html/left.htm"), "utf-8"));
			out.println("<html>");
			out.println("\t<head>");
			out.println("\t\t<title>Index</title>");
			out.println("\t</head>");
			out.println("\t<body>");

			SkillEngine engine = new SkillEngine();
			for (int i = 1; i < 10; i++) {
				String dir = Skillinfo.category[i];
				out.println("\t\t<dl>");
				out.println("\t\t\t<dt>" + dir + "</dt>");

				File file = new File("html/" + dir);
				if (!file.exists()) {
					file.mkdirs();
				}
				List<Skillinfo> list = engine.getSkill(i);
				for (Skillinfo info : list) {
					String fileName = dir + "/" + info.getDescName() + ".htm";
					out.println("\t\t\t<dd><a href=\"" + fileName
							+ "\" target=\"main\">" + info.getSkillLocalName()
							+ "</a></dd>");
					engine.createHtml(info, "html/" + dir);
				}
				out.println("\t\t</dl>");
			}

			out.println("\t</body>");
			out.println("</html>");
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void createHtml(Skillinfo info, String dir) {
		String descName = info.getDescName();
		List<SkillLevelDetail> details = getSkillLevelDetail(descName);
		List<String[]> data = new ArrayList<String[]>();
		if (details.size() > 0) {
			data = table(info, details);
		}
		try {
			PrintWriter out = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(dir + "/" + descName + ".htm"),
					"utf-8"));
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("\t<head>");
			out.println("\t\t<title>" + info.getSkillLocalName() + "</title>");
			out.println("\t\t<link rel=\"stylesheet\" href=\"../css/style.css\" type=\"text/css\"/>");
			out.println("\t</head>");
			out.println("\t<body>");

			out.println("<h2>" + info.getSkillLocalName() + "</h2>");
			out.println("<p>" + info.getSkillDesc() + "</p>");
			out.println("<h4>如何获得</h4>");
			out.println("<p>" + info.getHowToGetDesc() + "</p>");

			out.println("\t\t<table class=\"mabitable\">");
			for (String[] args : data) {
				out.println("\t\t\t<tr>");
				out.println("\t\t\t\t<th>" + args[0] + "</th>");
				for (int i=1; i<17; i++) {
					String str = args[i];
					out.println("\t\t\t\t<td>" + str + "</td>");
				}
				out.println("\t\t\t</tr>");
			}
			out.println("\t\t</table>");

			int sum = 0;
			for (SkillLevelDetail detail : details) {
				out.println("<h4>Rank " + detail.getSkillLevel() + "</h4>");
				out.println("<p>" + detail.getLevelDescription() + "</p>");

				int ap = detail.getAbilityNecessary();
				sum += ap;
				out.println("<p>- 必要AP:" + ap + " (累计:" + sum + ")<br/>");
				out.println("- 战斗力: " + detail.getCombatPower() + "<br/>");
				out.println("- 效果:<br/>" + detail.getEffectDescription()
						+ "</p>");

				out.println("\t\t<table class=\"mabitable\">");
				out
						.println("\t\t\t<tr><th>获得经验</th><th>回数</th><th>经验</th><th>修炼方法</th></tr>");
				for (String[] args : detail.getConditions()) {
					double ex = Double.parseDouble(args[0]);
					int n = Integer.parseInt(args[1].trim());
					String condition = args[2].trim();
					double exp = ex * n;
					String str = "\t\t\t<tr><td>"
							+ ex
							+ String
									.format(
											"</td><td>%d</td><td>%.2f</td><td>%s</td></tr>",
											n, exp, condition);
					out.println(str);
				}
				out.println("\t\t</table>");
			}

			out.println("\t</body>");
			out.println("</html>");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		SkillEngine engine = new SkillEngine();
		engine.frameset();
	}
}
