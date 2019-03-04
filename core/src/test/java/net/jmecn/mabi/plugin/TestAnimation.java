package net.jmecn.mabi.plugin;

import org.junit.Before;
import org.junit.Test;

import com.jme3.animation.Animation;
import com.jme3.animation.Bone;
import com.jme3.animation.BoneTrack;
import com.jme3.animation.Skeleton;
import com.jme3.animation.Track;
import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import net.jmecn.mabi.AssetFactory;

public class TestAnimation {
	// 资源管理器
	AssetManager manager;

	@Before
	public void init() {
		manager = new DesktopAssetManager();
		AssetFactory.setAssetManager(manager);
	}
	
	@Test
	public void testBuildSkeleton() {
		
		Skeleton ske = AssetFactory.loadSkeleton("gfx/char/human/female/female_framework.frm");
		Animation anim = AssetFactory.loadAnimation("gfx/char/chapter4/human/anim/social_motion/female_ballet.ani");
		
		int count = ske.getBoneCount();
		Track[] tracks = anim.getTracks();
		for(int i=0; i<count; i++) {
			// 骨骼的初始动作
			Bone bone = ske.getBone(i);
			Vector3f bindPosition = bone.getBindPosition();
			Quaternion bindRotation = bone.getBindRotation();
			System.out.printf("Bone: %s %s\n", bindPosition, bindRotation);
			
			// 动画的第一帧
			BoneTrack track = (BoneTrack)tracks[i];
			Vector3f translation = track.getTranslations()[0];
			Quaternion rotation = track.getRotations()[0];
			System.out.printf("Track: %s %s\n", translation, rotation);
			
			// 差值
			Vector3f deltaTranslation = translation.subtract(bindPosition);
			Quaternion deltaRotation = new Quaternion();
			deltaRotation = bindRotation.inverse().mult(rotation, deltaRotation);
			
			System.out.printf("Delta: %s %s\n\n", deltaTranslation, deltaRotation);
			
			// Mult
			System.out.printf("Rotate: %s * %s = %s\n\n", bindRotation, deltaRotation, bindRotation.mult(deltaRotation));
			
		}
	}
}
