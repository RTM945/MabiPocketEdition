package net.jmecn.mabi.plugin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.material.plugins.J3MLoader;
import com.jme3.shader.plugins.GLSLLoader;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.texture.plugins.DDSLoader;

import net.jmecn.mabi.AssetFactory;

public class TestMabiLoader {

	static Logger logger = LoggerFactory.getLogger(TestMabiLoader.class);

	private AssetManager manager;

	@Before
	public void init() {
		manager = new DesktopAssetManager();
		manager.registerLocator("/", ClasspathLocator.class);

		// Material
		manager.registerLoader(J3MLoader.class, "j3m", "j3md");
		manager.registerLoader(GLSLLoader.class, "vert", "frag", "geom", "tsctrl", "tseval", "glsl", "glsllib");

		// Texture
		manager.registerLoader(AWTLoader.class, "jpg", "bmp", "gif", "png", "jpeg");
		manager.registerLoader(DDSLoader.class, "dds");

		// Mabinogi plugins
		AssetFactory.setAssetManager(manager);
	}

	@After
	public void clean() {
	}

	@Test
	public void loadFrm() {
		manager.loadAsset("gfx/char/chapter4/pet/mesh/fox/pet_c4_maplestory_fox01_s_framework.frm");
	}

	@Test
	public void loadPmg() {
		manager.loadAsset("gfx/char/chapter4/pet/mesh/fox/pet_c4_maplestory_fox01_s_mesh.pmg");
	}

	@Test
	public void testLoadAni() {
		manager.loadAsset("gfx/char/chapter4/pet/anim/maplefox/pet_maplefox_s_attack_01.ani");
		manager.loadAsset("gfx/char/chapter4/pet/anim/maplefox/pet_maplefox_s_walk.ani");
		manager.loadAsset("gfx/char/chapter4/pet/anim/maplefox/pet_maplefox_s_natural_sit_01.ani");
		manager.loadAsset("gfx/char/chapter4/pet/anim/maplefox/pet_maplefox_s_stand_friendly.ani");
	}
}
