package net.jmecn.mabi.plugin;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.texture.Texture;

import net.jmecn.mabi.AssetFactory;
import net.jmecn.mabi.struct.AniFile;
import net.jmecn.mabi.struct.PmgFile;
import net.jmecn.mabi.utils.MersenneTwister;

public class TestPackLocator {

	static Logger logger = LoggerFactory.getLogger(TestPackLocator.class);

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
	public void testLocator() {
		manager.locateAsset(new AssetKey<PmgFile>("gfx/char*pet_c4_maplestory_fox01_s_mesh.pmg"));
		manager.locateAsset(new AssetKey<Object>("local/xml/dungeon_ruin.china.txt"));
		manager.locateAsset(new AssetKey<Object>("db/itemdb.xml"));
		manager.locateAsset(new AssetKey<AniFile>("gfx*pet/anim/maplefox/pet_maplefox_s_attack_01.ani"));
		manager.locateAsset(new AssetKey<Object>("local/xml/itemdb.china.txt"));
		
		manager.locateAsset(new AssetKey<Texture>("material/*female_c4_2012christmas_m.dds"));
		manager.locateAsset(new AssetKey<Texture>("material/*morrighan_hair.dds"));
	}

	@Test
	public void testMT19337() {
		long[] rands = {
				0xDEB75F54L,
				0xA12DB3E0L,
				0x92DD5846L,
				0x22F49F76L,
				0x521F96B0L,
				0x46742A8FL,
				0xEFC984B6L,
				0x97137CEAL,
				0xC704E843L,
				0x5564A9C7L };

		long seed = (208L << 7) ^ 0xA9C36DE1L;
		MersenneTwister mt2 = new MersenneTwister(seed);
		for (int i = 0; i < 10; i++) {
			assertEquals(rands[i], mt2.genrandInt32());
		}
	}
	
}
