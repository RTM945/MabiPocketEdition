package net.jmecn.mabi.plugin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.math.Matrix4f;

import net.jmecn.mabi.AssetFactory;
import net.jmecn.mabi.struct.FrmBone;
import net.jmecn.mabi.struct.FrmFile;

public class TestFrmLoader {

	static Logger logger = LoggerFactory.getLogger(TestFrmLoader.class);

	private AssetManager manager;

	@Before
	public void init() {
		manager = new DesktopAssetManager();
		AssetFactory.setAssetManager(manager);
	}

	@After
	public void clean() {
	}

	@Test(timeout = 100)
	public void testLoadFrm() {
		manager.loadAsset("gfx/char/human/female/female_framework.frm");
	}
	
	@Test
	public void testBuildSkeleton() {
		FrmFile frmFile = (FrmFile) manager.loadAsset("gfx/char/human/female/female_framework.frm");
		
		Skeleton ske = buildSkeleton(frmFile);
		logger.debug("Skeleton:{}", ske);
	}

	/**
	 * 根据frm文件，生成骨骼。
	 * 
	 * @param frmFile
	 * @return
	 */
	private Skeleton buildSkeleton(FrmFile frmFile) {
		int boneCount = frmFile.boneCount;

		Bone[] bones = new Bone[boneCount];
		byte[] parents = new byte[boneCount];
		for (int i = 0; i < boneCount; i++) {
			FrmBone fbone = frmFile.frmBones[i];

			Bone bone = new Bone(fbone.name);

			Matrix4f bindPose = fbone.bindPose;
			bone.setBindTransforms(bindPose.toTranslationVector(), bindPose.toRotationQuat(),
					bindPose.toScaleVector());

			// 父子关系
			bones[fbone.boneid] = bone;
			parents[fbone.boneid] = fbone.parentid;
		}

		// 继承关系
		for (byte id = 0; id < boneCount; id++) {
			byte parentid = parents[id];
			if (parentid > -1 && parentid < boneCount && parentid != id) {
				bones[parentid].addChild(bones[id]);
			}
		}

		Skeleton ske = new Skeleton(bones);
		return ske;
	}
}
