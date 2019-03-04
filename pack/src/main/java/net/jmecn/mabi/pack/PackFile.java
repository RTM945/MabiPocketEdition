package net.jmecn.mabi.pack;

import static net.jmecn.mabi.utils.InputStreamUtil.getString;
import static net.jmecn.mabi.utils.InputStreamUtil.skip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.util.LittleEndien;

import net.jmecn.mabi.utils.MersenneTwister;

/**
 * 此类用于解析Mabinogi的pack文件。
 * 
 * @author yanmaoyuan
 *
 */
public class PackFile {

	static Logger logger = LoggerFactory.getLogger(PackFile.class);

	static byte[] ValidHeader = { 0x50, 0x41, 0x43, 0x4B, 0x02, 0x01, 0x00, 0x00 };

	private File packFile;
	private boolean fileOpen = false;
	private LittleEndien in;
	private PackageHeader header;
	private List<PackageEntry> packageEntries;

	public Map<String, PackageEntry> entryMap;

	public PackFile(String name) throws IOException {
		this.packageEntries = new ArrayList<PackageEntry>();
		this.entryMap = new HashMap<String, PackageEntry>();
		openPackage(name);
	}

	/**
	 * 打开.pack文件，扫描文件目录。
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	private boolean openPackage(String filename) throws IOException {
		if (fileOpen)
			return true;

		packFile = new File(filename);
		if (packFile.exists()) {
			if (packFile.canRead()) {
				fileOpen = true;

				in = new LittleEndien(new FileInputStream(packFile));
				if (readPackageHeader()) {
					readPackageEntries();
				}
				in.close();
			}
		}
		return fileOpen;
	}

	/**
	 * 读取.pack文件头，解析文件版本信息和文件数量。
	 * 
	 * @return 解析成功返回 true，解析失败返回 false。
	 * @throws IOException
	 */
	private boolean readPackageHeader() throws IOException {
		if (!fileOpen)
			return false;

		String header = getString(in, 4);
		int version = in.readInt();

		if (header.equals("PACK") && version == 0x0102) {

			PackageHeader h = new PackageHeader();

			h.revision = in.readInt();
			h.entryCount = in.readInt();
			h.fileTime1 = in.readLong();
			h.fileTime2 = in.readLong();
			h.dataPath = getString(in, 480);

			h.entryCount = in.readInt();
			h.headerSize = in.readInt();
			h.blankSize = in.readInt();
			h.contentSize = in.readInt();
			in.read(h.zero);

			this.header = h;
			return true;
		} else {
			logger.warn("NOT A VALID PACK FILE");
			return false;
		}
	}

	/**
	 * 扫描整个.pack文件，读取所有资源文件入口信息。建立资源辞典，便于检索。
	 * 
	 * @return 解析成功返回true，解析失败返回false。
	 */
	private boolean readPackageEntries() {
		if (!fileOpen)
			return false;

		try {
			for (int i = 0; i < header.entryCount; i++) {

				// 读取文件名长度
				int nameLen;
				byte nameType = in.readByte();

				switch (nameType) {
				case 0:// 16 = 0x10
				case 1:// 32 = 0x20
				case 2:// 48 = 0x30
				case 3:// 64 = 0x40
					nameLen = 0x10 * (nameType + 1) - 1;
					break;
				case 4:// 96 = 0x60
					nameLen = 0x60 - 1;
					break;
				case 5:// dyn
					nameLen = in.readInt();
					break;
				default:
					logger.warn("Unknown name type:{}", nameType);
					return false;
				}

				String name = getString(in, nameLen);

				// Change the windows style path separators to unix style
				name = name.replaceAll("\\\\", "/");

				// read PackageItemInfo
				PackageEntry entry = new PackageEntry();

				entry.parent = this;
				entry.nameType = nameType;
				entry.nameLen = nameLen;
				entry.name = name;

				entry.Seed = in.readInt();
				entry.Zero = in.readInt();
				entry.Offset = in.readInt();
				entry.CompressedSize = in.readInt();
				entry.DecompressedSize = in.readInt();
				entry.IsCompressed = in.readInt();
				entry.CreationTime = in.readLong();
				entry.CreationTime2 = in.readLong();
				entry.LastAccessTime = in.readLong();
				entry.ModifiedTime = in.readLong();
				entry.ModifiedTime2 = in.readLong();

				if (entry.Zero != 0) {
					logger.debug("Entry {} is corrupted!", name);
				} else {
					packageEntries.add(entry);
					entryMap.put(name, entry);
				}

			}

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * 获得此pack文件的版本号
	 * @return
	 */
	public int getRevision() {
		return header.revision;
	}
	
	/**
	 * 释放文件数据
	 * 
	 * @param filename 文件名
	 * @return 文件数据，byte[]类型。
	 */
	public byte[] extractFile(String filename) {
		PackageEntry entry = findInternalFile(filename);
		return (entry != null) ? extractFile(entry) : null;
	}

	/**
	 * 释放文件数据
	 * 
	 * @param entry 文件入口信息
	 * @return 文件数据，byte[]类型。
	 */
	public byte[] extractFile(PackageEntry entry) {
		int fileSize = entry.CompressedSize;
		if (entry.IsCompressed == 0)// not compressed
			fileSize = entry.DecompressedSize;

		FileInputStream in = null;
		try {
			int start = 512 + 32 + header.headerSize;

			// Read data
			in = new FileInputStream(packFile);
			byte[] data = new byte[fileSize];
			skip(in, entry.Offset + start);
			in.read(data, 0, fileSize);
			in.close();

			// Decode
			long seed = ((long) entry.Seed << 7) ^ 0xA9C36DE1L;
			MersenneTwister mt = new MersenneTwister(seed);
			for (int i = 0; i < fileSize; i++) {
				data[i] = (byte) (data[i] ^ mt.genrandInt32());
			}

			// Decompress
			byte[] bytes;
			if (entry.IsCompressed != 0)
				bytes = decompress(data, entry.DecompressedSize);
			else {
				bytes = data;
			}

			return bytes;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 释放文件数据
	 * 
	 * @param entry 文件入口信息
	 * @return 资源数据的输入流
	 */
	public InputStream getInputStream(PackageEntry entry) throws IOException {
		int fileSize = entry.CompressedSize;
		if (entry.IsCompressed == 0)// not compressed
			fileSize = entry.DecompressedSize;
		
		FileInputStream in = null;
		int start = 512 + 32 + header.headerSize;

		// Read data
		in = new FileInputStream(packFile);
		byte[] data = new byte[fileSize];
		skip(in, entry.Offset + start);
		in.read(data, 0, fileSize);
		in.close();

		// Decode
		long seed = ((long) entry.Seed << 7) ^ 0xA9C36DE1L;
		MersenneTwister mt = new MersenneTwister(seed);
		for (int i = 0; i < fileSize; i++) {
			data[i] = (byte) (data[i] ^ mt.genrandInt32());
		}

		// Decompress
		InputStream zin;
		if (entry.IsCompressed != 0) {
			zin = new InflaterInputStream(new ByteArrayInputStream(data));
		} else {
			zin = new ByteArrayInputStream(data);
		}

		return zin;

	}

	/**
	 * 解压缩
	 * 
	 * @param data
	 *            待压缩的数据
	 * @return byte[] 解压缩后的数据
	 */
	public static byte[] decompress(byte[] data, int length) {
		byte[] output = new byte[0];

		Inflater decompresser = new Inflater();
		decompresser.reset();
		decompresser.setInput(data);

		ByteArrayOutputStream o = new ByteArrayOutputStream(length);
		try {
			byte[] buf = new byte[1024];
			while (!decompresser.finished()) {
				int i = decompresser.inflate(buf);
				o.write(buf, 0, i);
			}
			output = o.toByteArray();
		} catch (Exception e) {
			output = data;
			e.printStackTrace();
		} finally {
			try {
				o.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		decompresser.end();
		return output;
	}

	/**
	 * 查找文件
	 * 
	 * @param filename
	 * @return
	 */
	public PackageEntry findInternalFile(String filename) {
		// if it contains a *, allow a wildcard search
		if (filename.contains("*")) {
			String[] s = filename.split("\\*");
			if (s.length == 2) {
				for (PackageEntry entry : packageEntries) {
					if (entry.name.startsWith(s[0]) && entry.name.endsWith(s[1]))
						return entry;
				}
			} else {
				logger.debug("could not use wildcard, invalid count {}", filename);
			}
		} else {
			return entryMap.get(filename);
		}
		return null;
	}

	/**
	 * 判断文件是否存在
	 * @param filename
	 * @return
	 */
	public boolean fileExists(String filename) {
		return findFile(filename).length() > 0;
	}

	/**
	 * 查找文件
	 * @param filename 文件名
	 * @return 文件入口。
	 */
	public String findFile(String filename) {
		// if it contains a *, allow a wildcard search
		PackageEntry e = findInternalFile(filename);
		if (e != null)
			return e.name;
		return "";
	}

	/**
	 * 查询以特定字符串开头/结尾的文件清单
	 * @param beginsWith
	 * @param endsWith
	 * @return
	 */
	public List<String> getFileNames(String beginsWith, String endsWith) {
		if (beginsWith == null)
			beginsWith = "";

		if (endsWith == null) {
			endsWith = "";
		}

		List<String> fileNames = new ArrayList<String>();
		for (PackageEntry entry : packageEntries) {
			if (entry.name.startsWith(beginsWith) && entry.name.endsWith(endsWith))
				fileNames.add(entry.name);
		}
		return fileNames;
	}

	/**
	 * 查找文理
	 * @param texture
	 * @return
	 */
	public String findTexture(String texture) {
		for (PackageEntry entry : packageEntries) {
			String entryName = entry.name;
			if (entryName.endsWith(".dds")) {
				String[] names = entryName.split("/");
				String textureName = names[names.length - 1];
				if (textureName == texture + ".dds")
					return entryName;
			}
		}
		return "";
	}

	/**
	 * 返回pack文件名
	 * @return
	 */
	public String getName() {
		return packFile.getName();
	}

}
