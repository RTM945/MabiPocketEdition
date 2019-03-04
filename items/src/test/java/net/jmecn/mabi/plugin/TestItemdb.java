package net.jmecn.mabi.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.TextureKey;

import net.jmecn.mabi.AssetFactory;
import net.jmecn.mabi.db.ItemdbHandler;
import net.jmecn.mabi.db.MabiItem;

public class TestItemdb {

	static Logger logger = LoggerFactory.getLogger(TestItemdb.class);

	private AssetManager manager;

	@Before
	public void init() {
		manager = new DesktopAssetManager();
		AssetFactory.setAssetManager(manager);
	}

	@After
	public void clean() {
	}
	
	@Test
	public void testItemdb() {
		AssetInfo info = manager.locateAsset(new AssetKey<Object>("db/itemdb.xml"));
		InputStream in = info.openStream();
		ItemdbHandler handler = new ItemdbHandler();
		try {
			List<MabiItem> items = handler.getMabiItem(in);
			int len = items.size();
			for(int i=0; i<len; i++) {
				MabiItem item = items.get(i);
				try {
					String img = item.getImage();
					if (img != null) {
						String name = "gfx/image2/inven/*" + item.getImage() + ".dds";
						TextureKey key = new TextureKey(name, false);
						manager.locateAsset(key);
						logger.info("Find {}", name);
					}
				} catch (AssetNotFoundException e) {
					
				}
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

	}
	
}
