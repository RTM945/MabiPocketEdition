package net.jmecn.mabi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.animation.SkeletonControl;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.ChaseCamera;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.SkeletonDebugger;
import com.simsilica.lemur.core.VersionedList;

/**
 * 用于加载和显示Mabinogi的模型文件
 * 
 * @author yanmaoyuan
 *
 */
public class ModelState extends BaseAppState {

	static Logger logger = LoggerFactory.getLogger(ModelState.class);

	/**
	 * 缩放系数
	 */
	private float scaleFactor = 0.05f;
	
	private Node camFocus;
	private ChaseCamera chaseCam;

	/**
	 * 骨骼及其控制器
	 */
	private Skeleton skeleton;
	private SkeletonControl skeletonControl;
	
	/**
	 * 场景中的可视部分
	 */
	private Node rootNode;// 场景根节点
	private Node roleNode;// 角色根节点
	
	private SkeletonDebugger debugger;// 骨骼调试器
	private Material sdMat;

	/**
	 * 动画数据及其控制器
	 */
	private VersionedList<String> anims;
	private AnimControl animControl;

	/**
	 * 初始化关键对象
	 */
	public ModelState() {
		// 场景根节点
		rootNode = new Node("ModelRoot");
		rootNode.setShadowMode(ShadowMode.Cast);

		// 摄像机的焦点
		camFocus = new Node("focus");
		rootNode.attachChild(camFocus);
		
		// 角色模型根节点
		roleNode = new Node("role root");
		rootNode.attachChild(roleNode);
		
		// 调整大小和方向
		roleNode.setLocalScale(scaleFactor);
		roleNode.rotate(0, FastMath.PI, 0);
		
		// 动画数据列表
		anims = new VersionedList<String>();
	}

	@Override
	protected void initialize(Application app) {
		// 镜头的焦点
		chaseCam = new ChaseCamera(app.getCamera(), camFocus, app.getInputManager());
		chaseCam.setTrailingEnabled(true);
		chaseCam.setMinDistance(10);
		chaseCam.setMaxDistance(30);
		chaseCam.setSmoothMotion(true);
		chaseCam.setRotationSensitivity(10);
		chaseCam.setInvertVerticalAxis(true);

		// 骨骼的材质
		sdMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		sdMat.setColor("Color", ColorRGBA.Red);
		sdMat.getAdditionalRenderState().setDepthTest(false);
	}

	@Override
	protected void cleanup(Application app) {
	}

	@Override
	protected void onEnable() {
		SimpleApplication app = (SimpleApplication) getApplication();
		app.getRootNode().attachChild(rootNode);
	}

	@Override
	protected void onDisable() {
		rootNode.removeFromParent();
	}

	public void detachAll() {
	    
	    if (debugger != null) {
            debugger.removeFromParent();
        }
	    
	    camFocus.setLocalTranslation(0, 0, 0);
	    
        roleNode.detachAllChildren();
	}
	
	/**
	 * 设置骨骼
	 * 
	 * @param frm
	 */
	public void setSkeleton(String frm) {
		// 骨架
		Skeleton skeleton = AssetFactory.loadSkeleton(frm);
		setSkeleton(skeleton);
	}
	
	/**
	 * 设置骨骼
	 * @param ske
	 */
	public void setSkeleton(Skeleton ske) {
		
		/**
		 * remove old skeleton
		 */
		if (skeletonControl != null) {
			skeleton = null;
			debugger.removeFromParent();
			rootNode.removeControl(skeletonControl);
		}
		
		/**
		 * clear old animations
		 */
		if (animControl != null) {
			animControl.clearChannels();
			
			rootNode.removeControl(animControl);
			anims.clear();
		}
		
		/**
		 * apply new skeleton
		 */
		if (ske != null) {
			// reset Skeleton
			debugger = new SkeletonDebugger("SkeletonDebugger", ske);
			debugger.setMaterial(sdMat);
			debugger.setShadowMode(ShadowMode.Off);
			
			// 调整大小和方向
			debugger.setLocalScale(scaleFactor);
			debugger.rotate(0, FastMath.PI, 0);
			
			rootNode.attachChild(debugger);
			
			skeletonControl = new SkeletonControl(ske);
			rootNode.addControl(skeletonControl);
			
			// reset animation
			animControl = new AnimControl(ske);

			rootNode.addControl(animControl);
			
			// reset camera focus
			resetFocus(ske);
		}
		
		this.skeleton = ske;
	}

	/**
	 * 根据骨架的大小，计算BoundingBox
	 * 
	 * @param ske
	 */
	private void resetFocus(Skeleton ske) {
		float minY = Float.MAX_VALUE;
		float maxY = -Float.MAX_VALUE;
		for (int i = 0; i < ske.getBoneCount(); i++) {
			Bone bone = ske.getBone(i);
			Vector3f head = bone.getModelSpacePosition();
			
			if (head.y > maxY) {
				maxY = head.y;
			}
			
			if (head.y < minY) {
				minY = head.y;
			}
		}
		
		camFocus.setLocalTranslation(0, (minY+maxY) * 0.5f * 0.05f, 0);
	}

	/**
	 * 添加角色部件
	 * 
	 * @param path
	 */
	public void addRolePart(String path) {
		Node model = AssetFactory.loadModel(path, skeleton);
		addRolePart(model);
	}

	/**
	 * 添加角色部件
	 * @param part
	 */
	public void addRolePart(Spatial part) {
		roleNode.attachChild(part);
	}
	
	/**
	 * 加载动画
	 * 
	 * @param path
	 */
	public void addAnimation(String path) {
		if (skeleton != null) {
			Animation anim = AssetFactory.loadAnimation(path, skeleton);
			addAnimation(anim);
		} else {
			Animation anim = AssetFactory.loadAnimation(path);
			addAnimation(anim);
		}
	}

	/**
	 * 加载动画
	 * 
	 * @param anim
	 */
	public void addAnimation(Animation anim) {
		anims.add(anim.getName());
		animControl.addAnim(anim);
	}
	
	/**
	 * 播放动画
	 * 
	 * @param anim
	 */
	public void playAnim(String anim) {
	    logger.info("play animation: {}", anim);
		if (skeleton != null) {
			clearBonePose();
			getAnimChannel().setAnim(anim);
		} else {
			logger.warn("no skeleton");
		}
	}
	
	/**
	 * 可能是jme3引擎的问题，播放动画时，第一帧是不能有数值的。。
	 * 
	 * @param ske
	 */
	public void clearBonePose() {
		for (int i = 0; i < skeleton.getBoneCount(); i++) {
			Bone bone = skeleton.getBone(i);
			bone.setBindTransforms(new Vector3f(), new Quaternion(), null);
		}
	}
	
    /**
     * 显示或隐藏SkeletonDebugger
     */
	public void toggleSkeletonDebugger() {
		if (debugger == null) {
			return;
		}
		
        if (debugger.getParent() != null) {
            debugger.removeFromParent();
        } else {
            rootNode.attachChild(debugger);
        }
    }
    
	/**
	 * 启用/禁用AnimControl
	 */
	public void toggleAnimControl() {
		if (animControl == null) {
			return;
		}
		
		if (animControl.getSpatial() != null) {
			rootNode.removeControl(animControl);
		} else {
			rootNode.addControl(animControl);
		}
	}
    /**
     * 启用/禁用SkeletonContorl
     */
    public void toggleSkeletonControl() {
    	if (skeletonControl == null) {
    		return;
    	}
    	
        skeletonControl.setEnabled(!skeletonControl.isEnabled());
    }

    /**
     * 获得动画名称
     * @return
     */
	public VersionedList<String> getAnimationNames() {
		return anims;
	}
	
	/**
	 * 获得动画频道
	 * 
	 * @return
	 */
	public AnimChannel getAnimChannel() {
		if (animControl == null) {
			return null;
		}
		
		if (animControl.getNumChannels() > 0) {
			return animControl.getChannel(0);
		} else {
			return animControl.createChannel();
		}
	}
	
	public Skeleton getSkeleton() {
		return skeleton;
	}
	
}
