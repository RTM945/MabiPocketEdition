package net.jmecn.mabi.plugin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;

import net.jmecn.mabi.AssetFactory;

public class TestServerList {

	static Logger logger = LoggerFactory.getLogger(TestServerList.class);

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
	public void testServerXml() {
		manager.locateAsset(new AssetKey<Object>("db/serverlist.xml"));
		// find id, name, sublocale from "serverList/server" where locale="China"
		// assert (id is "mabicn16") && (name is "玛丽") && (sublocale is "ServerGroup01")
	}
	
}
