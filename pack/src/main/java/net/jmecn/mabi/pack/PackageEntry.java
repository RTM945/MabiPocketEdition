package net.jmecn.mabi.pack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PackageEntry {
	/**
	 * 用于直接解压这个文件
	 */
	PackFile parent;
	
	byte nameType;
	int nameLen;
	String name;

	int Seed;
	int Zero;
	int Offset;
	int CompressedSize;
	int DecompressedSize;
	int IsCompressed;
	long CreationTime;
	long CreationTime2;
	long LastAccessTime;
	long ModifiedTime;
	long ModifiedTime2;

	public String getName() {
		return name;
	}
	
	public int size() {
		return (nameType == 5 ? 5:1) + nameLen + 64;
	}
	
	
	public int getSeed() {
		return Seed;
	}

	public int getZero() {
		return Zero;
	}

	public int getOffset() {
		return Offset;
	}

	public int getCompressedSize() {
		return CompressedSize;
	}

	public int getDecompressedSize() {
		return DecompressedSize;
	}

	public boolean getIsCompressed() {
		return IsCompressed != 0;
	}

	public long getCreationTime() {
		return CreationTime;
	}

	public long getLastAccessTime() {
		return LastAccessTime;
	}

	public long getModifiedTime() {
		return ModifiedTime;
	}

	/**
	 * 解压文件数据
	 * @return
	 */
	public boolean extract() {
		if (parent == null)
			return false;
		
		try {
			InputStream in = parent.getInputStream(this);
			if (in == null) {
				return false;
			}
			
			// 创建文件夹
			String path = "data/"+this.name;
			int n = path.lastIndexOf("/");
			
			File folder = new File(path.substring(0, n));
			if (!folder.exists()) {
				folder.mkdirs();
			}
			
			// 保存文件
			int len = -1;
			byte[] buffer = new byte[1024];
			File file = new File(path);
			FileOutputStream out = new FileOutputStream(file);
			while((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.close();
			in.close();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PackageEntry)) {
			return false;
		}
		PackageEntry entry = (PackageEntry) o;
		
		if (entry == this) {
			return true;
		}
		
		return entry.name.equals(this.name);
	}
	
	@Override
	public String toString() {
		String[] names = name.split("/");
		return names[names.length-1];
	}
}