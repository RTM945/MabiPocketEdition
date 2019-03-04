package net.jmecn.mabi;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.material.plugins.J3MLoader;
import com.jme3.shader.plugins.GLSLLoader;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.texture.plugins.DDSLoader;

public class TestAssetFactory {
	
	private AssetManager manager = null;
	
	@Before
	public void init() {
		if (manager == null) {
			manager = new DesktopAssetManager();
			
			manager.registerLocator("/", ClasspathLocator.class);
	
			// Material
			manager.registerLoader(J3MLoader.class, "j3m", "j3md");
			manager.registerLoader(GLSLLoader.class, "vert", "frag", "geom", "tsctrl", "tseval", "glsl", "glsllib");
	
			// Texture
			manager.registerLoader(AWTLoader.class, "png");
			manager.registerLoader(DDSLoader.class, "dds");
			
			AssetFactory.setAssetManager(manager);
		}
		
	}
	
	public void testLoadModel() {
		AssetFactory.loadModel("gfx/char/human/morrighan/morrighan_mesh.pmg");
	}
	
	@Test
	public void testListAll() {
		List<String> results = AssetFactory.listAll("gfx/char/", ".frm");
		for(String str : results) {
			System.out.println(str);
		}
	}
	
	@Test
	public void listTree() {
		List<String> results = AssetFactory.listAll("", "");
		Collections.sort(results);
		String[] files = results.toArray(new String[results.size()]);

		try {
			PrintStream out = new PrintStream(new FileOutputStream("logs/files.txt"));
			for(int i=0; i<files.length; i++) {
				out.println(files[i]);
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
