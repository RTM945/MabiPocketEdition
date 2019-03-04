<%@ page language="java" pageEncoding="utf-8"%>
<%@ page
	import="java.io.*,java.net.*,java.util.*,java.util.regex.*,org.jdom.*,org.jdom.input.*,org.jdom.xpath.*"%>
<%@ page
	import="java.awt.Color,java.awt.Font,java.awt.Graphics,java.awt.image.BufferedImage,javax.imageio.ImageIO,java.text.SimpleDateFormat"%>
<%!

	private static String readContents(String urlString) {
		URLConnection connection;
		URL url = null;
		BufferedReader in = null;
		StringBuffer sb = new StringBuffer();
		try {
			url = new URL(urlString);
			connection = url.openConnection();
			in = new BufferedReader(new InputStreamReader(connection
					.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
		return sb.toString();
	}

	private static List<String> getId(String regex, String content) {
		List<String> ids = new ArrayList<String>();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			String res = matcher.group();
			ids.add(res.substring(res.lastIndexOf("=") + 1));
		}
		return ids;
	}

	private static List<Element> getQuests(List<String> ids, String filePath) {
		List<Element> quests = null;
		try {
			SAXBuilder sb = new SAXBuilder();
			Document doc = sb.build(filePath);
			quests = new ArrayList<Element>();
			for (String id: ids) {
			Element quest = (Element) XPath.selectSingleNode(doc,
					"/document/quest[sdid=" + id + "]");
			quests.add(quest);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return quests;
	}

	private static void drawpic(List<Element> quests, String filePath) {
		Graphics g = null;
		int width = 360;
		int height = 220;
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g = bufferedImage.getGraphics();

		// 背景色
		g.setColor(Color.white);
		g.fillRect(0,0,width,height);
		
		g.setFont(new Font("宋体", Font.BOLD, 16));
		g.setColor(Color.red);
		g.drawString("今日任务", 6, 16);
		g.drawString("今日VIP", 6, 120);
		
		g.setFont(new Font("宋体", Font.PLAIN, 14));
		g.setColor(Color.black);
		drawQuest(g, quests.get(0), 6, 30);
		drawQuest(g, quests.get(1), 180, 30);
		drawQuest(g, quests.get(2), 6, 134);
		drawQuest(g, quests.get(3), 180, 134);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.SIMPLIFIED_CHINESE);
		g.setFont(new Font("Tahoma", Font.PLAIN, 10));
		g.setColor(Color.blue);
		g.drawString("created @ " + sdf.format(new Date()), 220, 216);
		
		g.dispose();
        String format = "png";
        File f = new File(filePath + "." + format);
        System.out.println(f.getAbsolutePath());
        try {
            ImageIO.write(bufferedImage, format, f);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private static void drawQuest(Graphics g, Element quest, int x, int y) {
		g.setFont(new Font("宋体", Font.BOLD, 13));
		g.setColor(Color.black);
		g.drawString(quest.getChildText("name"), x, y);
		g.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		g.drawString(quest.getChild("level").getChildText("l1"), x, y+14);
		g.drawString(quest.getChild("level").getChildText("l2"), x, y+28);
		g.drawString(quest.getChild("level").getChildText("l3"), x, y+42);
		g.drawString(quest.getChild("level").getChildText("l4"), x, y+56);
		g.drawString(quest.getChild("level").getChildText("l5"), x, y+70);
	}
	
	%>
<%
	String url = "http://mabinogi.fws.tw/";
	String regex = "area_qst\\D*\\d\\d?";
	String quest = request.getSession().getServletContext().getRealPath("quest.xml");
	
	long currentTime = System.currentTimeMillis();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.SIMPLIFIED_CHINESE);
	String today = sdf.format(new Date(currentTime));
	String yestoday = sdf.format(new Date(currentTime - 86400000));
    String todayimg = request.getSession().getServletContext().getRealPath("imgcache") + "/" + today;
    String yestodayimg = request.getSession().getServletContext().getRealPath("imgcache") + "/" + yestoday;
    // 今日8点 时间戳
    Date now = new Date(sdf.parse(today).getTime() + 28800000);
    File todayQuestImg = new File(todayimg + ".png");
    if(currentTime>now.getTime() && !todayQuestImg.exists()) {
	    List<String> ids = getId(regex, readContents(url));
		List<Element> quests = getQuests(ids, quest);
	    drawpic(quests, todayimg);
    }
    
    File yestodayQuestImg = new File(yestodayimg + ".png");
    if(currentTime<now.getTime() && !yestodayQuestImg.exists()) {
	    List<String> ids = getId(regex, readContents(url));
		List<Element> quests = getQuests(ids, quest);
	    drawpic(quests, yestodayimg);   
    }
    File questImg = null;
	if(currentTime>now.getTime()) {
		questImg = todayQuestImg;
    }
	if(currentTime<now.getTime()) {
		questImg = yestodayQuestImg;
	}
	
	response.setContentType("image/png");
	// 获取水印原图
	BufferedImage theImg = ImageIO.read(questImg);
	ImageIO.write(theImg, "png", response.getOutputStream()); 
%>