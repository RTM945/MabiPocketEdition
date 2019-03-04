package net.jmecn.mabi.toolset;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jmecn.mabi.pack.PackFile;
import net.jmecn.mabi.pack.PackageEntry;

public class PackManager {
	static Logger logger = LoggerFactory.getLogger(PackManager.class);
	
	private List<PackFile> packFiles;
	private int packCount;
	
	private static PackManager instance = new PackManager();
	
	private PackManager() {
		packFiles = new ArrayList<PackFile>();
		packCount = 0;
		
		locateMabiAsset("I:/game/Mabinogi/package/");
		locateMabiAsset("G:/4 我的资料/mabi/");
		locateMabiAsset("/home/yan/Apps/Mabinogi/package/");
		
		/**
		 * 对pack文件进行排序
		 */
		packFiles.sort(new Comparator<PackFile>(){
			@Override
			public int compare(PackFile a, PackFile b) {
				return b.getRevision() - a.getRevision();
			}
		});
	}
	
	public static PackManager getInstance() {
		return instance;
	}

	/**
	 * 将制定路径下的洛奇pack文件添加到路径中。
	 * @param rootPath
	 */
	private void locateMabiAsset(String rootPath) {
		
		File root = new File(rootPath);
		if (root.exists() && root.isDirectory()) {
			String[] name = root.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".pack");
				}
			});
			
			for(int i=name.length - 1; i>=0; i--) {
				String pack = rootPath + name[i];
				try {
					packFiles.add(new PackFile(pack));
					packCount++;
					logger.info("Add {} to locator.", pack);
				} catch (IOException e) {
					logger.error("{} is not a valid pack file.", pack);
				}
			}
		}
	}

	/**
	 * 查询以特定字符串开头/结尾的文件清单
	 * @param beginsWith
	 * @param endsWith
	 * @return
	 */
	public List<String> listAll(String beginsWith, String endsWith) {
		List<String> results = new ArrayList<String>();
		for(int i=0; i<packCount; i++) {
			PackFile pack = packFiles.get(i);
			results.addAll(pack.getFileNames(beginsWith, endsWith));
		}
		
		return results;
	}
	
	public PackageEntry findPackEntry(String path) {
		PackageEntry entry = null;
		for(int i=0; i<packCount; i++) {
			PackFile pack = packFiles.get(i);
			entry = pack.findInternalFile(path);
			if (entry != null) {
				break;
			}
		}
		return entry;
	}
}