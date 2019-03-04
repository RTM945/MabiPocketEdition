package net.jmecn.mabi.plugin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.animation.Animation;
import com.jme3.animation.Bone;
import com.jme3.animation.BoneTrack;
import com.jme3.animation.Skeleton;
import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import net.jmecn.mabi.AssetFactory;
import net.jmecn.mabi.struct.AniFile;
import net.jmecn.mabi.struct.AniFrame;
import net.jmecn.mabi.struct.AniTrack;
import net.jmecn.mabi.struct.FrmBone;
import net.jmecn.mabi.struct.FrmFile;

public class TestAniLoader {

	static Logger logger = LoggerFactory.getLogger(TestAniLoader.class);

	private AssetManager manager;

	@Before
	public void init() {
		manager = new DesktopAssetManager();
		AssetFactory.setAssetManager(manager);
	}

	@After
	public void clean() {
	}

	private Skeleton ske;
	private Animation anim;
	
	@Test
	public void testLoadAni() {
		FrmFile frmFile = (FrmFile)manager.loadAsset("gfx/char/human/female/female_framework.frm");
		AniFile aniFile = (AniFile)manager.loadAsset("gfx/char/chapter4/human/anim/social_motion/female_ballet.ani");
		
		this.ske = buildSkeleton(frmFile);
		logger.debug("Skeleton:{}", ske);
		
		if (valid(frmFile, aniFile)) {
			this.anim = buildAnimation(aniFile);
			logger.debug("Animation:{}", anim);
		}
	}
	
	/**
	 * 检查动画和骨骼文件是否匹配
	 * @return
	 */
	private boolean valid(FrmFile frmFile, AniFile aniFile) {
		logger.debug("bone:{} track:{}", frmFile.boneCount, aniFile.boneCount);
		return frmFile.boneCount >= aniFile.boneCount;
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
	
	/**
	 * 生成动画
	 * 
	 * @param aniFile
	 * @return
	 */
	private Animation buildAnimation(AniFile aniFile) {
		// 动画名称
		String name = aniFile.getName();
		// 动画时长
		float length = aniFile.getLength();

		Animation anim = new Animation(name, length);

		for (int i = 0; i < aniFile.boneCount; i++) {
			AniTrack aniTrack = aniFile.aniTracks[i];

			if (aniTrack.frameCount == 0) {
				logger.debug("aniTrack.frameCount == 0");
				continue;
			}

			BoneTrack track = new BoneTrack(i);
			anim.addTrack(track);

			float[] times = new float[aniTrack.frameCount];
			Vector3f[] translations = new Vector3f[aniTrack.frameCount];
			Quaternion[] rotations = new Quaternion[aniTrack.frameCount];

			for (int j = 0; j < aniTrack.frameCount; j++) {
				AniFrame aniFrame = aniTrack.aniFrames[j];
				times[j] = (float) aniFrame.frameNo / aniFile.framePerSecond;
				translations[j] = new Vector3f(aniFrame.x, aniFrame.y, aniFrame.z);
				rotations[j] = new Quaternion(-aniFrame.qx, -aniFrame.qy, -aniFrame.qz, aniFrame.qw);

				if (j == 0) {
					// TODO 设定骨骼的初始pose
					Bone bone = ske.getBone(i);
					bone.setBindTransforms(translations[0], rotations[0], null);
				}
			}

			track.setKeyframes(times, translations, rotations);
		}

		return anim;
	}
}
