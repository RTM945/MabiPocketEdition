package net.jmecn.mabi.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jmecn.mabi.pack.PackFile;
import net.jmecn.mabi.pack.PackageEntry;

public class TestPackFile {
	static Logger logger = LoggerFactory.getLogger(TestPackFile.class);

	@Test
	public void testListFileName() {
		String root = "172_to_173.pack";
		root = "/home/yan/Apps/Mabinogi/package/262_full.pack";

		PackFile packFile;
		try {
			packFile = new PackFile(root);
			List<String> list = packFile.getFileNames("gfx/char", "pet_c4_maplestory_fox01_s_mesh.pmg");
			for (String name : list) {
				logger.debug("File: {}", name);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testExtract() {
		String root = "207_to_208.pack";
		root = "/home/yan/Apps/Mabinogi/package/language.pack";
		String path = "data/";
		String fileName = "local/xml/dungeon_ruin.china.txt";

		PackFile packFile;
		try {
			packFile = new PackFile(root);
			PackageEntry packEntry = packFile.findInternalFile(fileName);
			
			if (packEntry == null) {
				logger.debug("找不到文件:{}", fileName);
				return;
			}
			
			// 创建文件夹
			String dir = path;
			int idx = fileName.lastIndexOf("/");
			if (idx > 0) {
				dir += fileName.substring(0, idx+1);
			}
			logger.debug("folder:{}", dir);
			File folder = new File(dir);
			if (!folder.exists()) {
				folder.mkdirs();
			}
						
			// 保存文件
			InputStream in = packFile.getInputStream(packEntry);
			FileOutputStream out = new FileOutputStream(path + fileName);
			
			byte[] buf = new byte[1024];
			int count;
			while ((count = in.read(buf)) != -1) {
				out.write(buf, 0, count);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
