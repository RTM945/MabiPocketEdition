package net.jmecn.mabi.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ItemdbHandler extends DefaultHandler {

	private List<MabiItem> list = null;

	public List<MabiItem> getMabiItem(InputStream xmlStream)
			throws SAXException, IOException, ParserConfigurationException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		parser.parse(xmlStream, this);
		return list;
	}

	@Override
	public void startDocument() throws SAXException {
		list = new ArrayList<MabiItem>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("Mabi_Item".equals(qName)) {
			MabiItem item = new MabiItem();
//			System.out.println("----------------");
//			System.out.println(attributes.getValue("ID"));
//			System.out.println(attributes.getValue("Category"));
//			System.out.println(attributes.getValue("Text_Name1"));
//			System.out.println(attributes.getValue("Text_Desc1"));
//			System.out.println(attributes.getValue("File_MaleMesh"));
//			System.out.println(attributes.getValue("File_FemaleMesh"));
//			System.out.println(attributes.getValue("File_FieldMesh"));
//			System.out.println(attributes.getValue("File_InvImage"));
//			System.out.println(attributes.getValue("Inv_XSize"));
//			System.out.println(attributes.getValue("Inv_YSize"));
//			System.out.println(attributes.getValue("App_WeaponActionType"));
//			System.out.println(attributes.getValue("App_WearType"));
//			System.out.println(attributes.getValue("App_UseC4Layer"));
//			System.out.println(attributes.getValue("App_Color1"));
//			System.out.println(attributes.getValue("App_Color2"));
//			System.out.println(attributes.getValue("App_Color3"));
//			System.out.println(attributes.getValue("App_ColorOrder"));
//			System.out.println(attributes.getValue("App_SittingType"));
			
			item.setId(attributes.getValue("ID"));
			item.setName(attributes.getValue("Text_Name0"));
			item.setImage(attributes.getValue("File_InvImage"));
			list.add(item);
		}
	}
}
