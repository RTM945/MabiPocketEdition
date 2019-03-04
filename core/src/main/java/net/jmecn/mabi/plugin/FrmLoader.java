package net.jmecn.mabi.plugin;

import static net.jmecn.mabi.utils.InputStreamUtil.getString;
import static net.jmecn.mabi.utils.InputStreamUtil.readMatrix4f;
import static net.jmecn.mabi.utils.InputStreamUtil.readQuad;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.util.LittleEndien;

import net.jmecn.mabi.struct.FrmBone;
import net.jmecn.mabi.struct.FrmFile;

/**
 * Load .frm file. It can be used to create a skeleton.
 * @author yanmaoyuan
 *
 */
public class FrmLoader implements AssetLoader {

	static Logger logger = LoggerFactory.getLogger(FrmLoader.class);

	@Override
	public Object load(AssetInfo assetInfo) throws IOException {
		LittleEndien in = new LittleEndien(assetInfo.openStream());

		// 文件头
		String head = getString(in, 4);
		int version = in.readShort();

		if (!"pf!".equals(head) || version != 1) {
			logger.warn("不支持的文件格式 head:{}, ver:{}", head, version);
			throw new RuntimeException("Unknown .frm file");
		}

		FrmFile frmFile = new FrmFile();

		// 骨骼数量
		frmFile.boneCount = in.readShort();
		frmFile.frmBones = new FrmBone[frmFile.boneCount];
		for (int i = 0; i < frmFile.boneCount; i++) {
			FrmBone frmBone = new FrmBone();
			frmFile.frmBones[i] = frmBone;

			frmBone.globalToLocal = readMatrix4f(in);
			frmBone.localToGlobal = readMatrix4f(in);
			frmBone.bindPose = readMatrix4f(in);

			String name = getString(in, 32);

			/**
			 * .frm文件中，人类、精灵、巨人的骨骼数量比实际用到的要多。多出来的骨骼名称类似于：
			 * _i0111、_i0112，以i开头、4个数字结尾的形式。
			 * 
			 * 这些骨骼依然有自己的pose，但是目前没有实际的用途，因此直接忽略掉它们。
			 * 
			 * yan @ 2017-01-23
			 */
			if (name.matches("\\w*i\\d+")) {
				frmFile.boneCount = i;
				break;
			}

			/**
			 * 骨骼名称中经常会多一些下划线、横线，而pmg文件中记录的骨骼名字是不带这些符号的。
			 */
			frmBone.name = removePrefix(name);
			frmBone.boneid = in.readByte();
			frmBone.parentid = in.readByte();
			frmBone.empty02 = in.readShort();
			frmBone.quad1 = readQuad(in);
			frmBone.quad2 = readQuad(in);
		}
		in.close();

		return frmFile;
	}

	/**
	 * 骨骼名称中经常会多一些下划线、横线，而pmg文件中记录的骨骼名字是不带这些符号的。
	 * 
	 * @return
	 */
	private String removePrefix(String name) {
		String str = name;
		int idx = str.lastIndexOf("_");
		if (idx >= 0) {
			str = str.substring(idx + 1);
		}
		idx = str.lastIndexOf("-");
		if (idx >= 0) {
			str = str.substring(idx + 1);
		}

		return str;
	}
}
