package net.jmecn.mabi.plugin;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoadException;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.AssetManager;

import net.jmecn.mabi.pack.PackFile;
import net.jmecn.mabi.pack.PackageEntry;

/**
 * PackLocator is a locator that looks up resources in a .pack file.
 * 
 * @author yanmaoyuan
 *
 */
public class PackLocator implements AssetLocator {

	static Logger logger = LoggerFactory.getLogger(PackLocator.class);

	private PackFile packFile = null;

	class PackAssetInfo extends AssetInfo {

		private PackageEntry entry;

		public PackAssetInfo(AssetManager manager, AssetKey<?> key, PackageEntry entry) {
			super(manager, key);
			this.entry = entry;

		}

		@Override
		public InputStream openStream() {
			try {
				return packFile.getInputStream(entry);
			} catch (IOException e) {
				throw new AssetLoadException("Failed to load pack entry: " + entry);
			}
		}

	}

	@Override
	public void setRootPath(String rootPath) {
		try {
			packFile = new PackFile(rootPath);
		} catch (IOException e) {
			throw new AssetLoadException("Failed to open pack file: " + rootPath, e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public AssetInfo locate(AssetManager manager, AssetKey key) {
		if (packFile == null) {
			return null;
		}

		String name = key.getName();
		if (name.startsWith("/"))
			name = name.substring(1);
		
		if (name.startsWith("data/")) {
			name = name.substring(5);
		}

		PackageEntry entry = packFile.findInternalFile(name);
		if (entry == null) {
			return null;
		}
		
		logger.debug("{}, ver{} -> {}", packFile.getName(), packFile.getRevision(), entry.getName());
		return new PackAssetInfo(manager, key, entry);
	}

}
