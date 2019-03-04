package net.jmecn.mabi.debug;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.animation.Animation;
import com.jme3.animation.Skeleton;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.ActionButton;
import com.simsilica.lemur.CallMethodAction;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.OptionPanelState;
import com.simsilica.lemur.style.ElementId;

import net.jmecn.mabi.AssetFactory;
import net.jmecn.mabi.ModelState;
import net.jmecn.mabi.struct.AniFile;

/**
 * 角色面板，用于调整角色参数。
 * 
 * @author yanmaoyuan
 *
 */
public class CharacterState extends BaseAppState {

	static Logger logger = LoggerFactory.getLogger(CharacterState.class);
	
	Container window;
    /**
     * 依赖模块
     */
    private ModelState modelState;
    private OptionPanelState optionPanel;
	/**
	 * 体型
	 */
	String race;// 种族
	String gender;// 性别
	String age;// 年龄影响身高
	String fatness;// 体重
	
	/**
	 * 发型
	 */
	String hairStyle;
	String hairColor;
	
	/**
	 * 眼睛类型
	 */
	String eyeStyle;
	String eyeColor;
	
	/**
	 * 头部
	 */
	String headType;
	String mouthType;
	String emotion;
	String skinColor;
	
	@Override
	protected void initialize(Application app) {
		// 分辨率
        float height = app.getCamera().getHeight();
        float width = app.getCamera().getWidth();

        modelState = app.getStateManager().getState(ModelState.class);
        optionPanel = app.getStateManager().getState(OptionPanelState.class);
        
        window = new Container();

        Label title = new Label("Chooser", new ElementId("title"));
        window.addChild(title);

        window.addChild(new ActionButton(new CallMethodAction("loadMappleFox", this, "loadMappleFox")));
        window.addChild(new ActionButton(new CallMethodAction("loadMorrighan", this, "loadMorrighan")));
        window.addChild(new ActionButton(new CallMethodAction("loadHumanFemale", this, "loadHumanFemale")));
        window.addChild(new ActionButton(new CallMethodAction("loadElfFemale", this, "loadElfFemale")));
        window.addChild(new ActionButton(new CallMethodAction("loadGiantFemale", this, "loadGiantFemale")));
        window.addChild(new ActionButton(new CallMethodAction("loadNao", this, "loadNao")));
        window.addChild(new ActionButton(new CallMethodAction("loadNaoRua", this, "loadNaoRua")));

        Vector3f size = window.getPreferredSize();
        
        window.setLocalTranslation((width - size.x ) * 0.5f, (height + size.y) * 0.5f, 10);
	}

	@Override
	protected void cleanup(Application app) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onEnable() {
		((SimpleApplication)getApplication()).getGuiNode().attachChild(window);
	}

	@Override
	protected void onDisable() {
		window.removeFromParent();
	}

	void loadNao() {

        modelState.detachAll();

        try {
        	modelState.setSkeleton("gfx/char/human/nao/nao_framework.frm");
        	modelState.addRolePart("gfx/char/human/nao/nao_mesh.pmg");
        	
            List<String> anims = AssetFactory.listAll("gfx/char/human/nao/ani/", ".ani");
            tryAddAnimation(anims);
        } catch (Exception e) {
            optionPanel.showError("Error", e);
            logger.error("设置骨骼时发生异常", e);
        }
    }
	
   void loadNaoRua() {

        modelState.detachAll();

        try {
            modelState.setSkeleton("gfx/char/human/nao/nao_framework.frm");
            modelState.addRolePart("gfx/char/human/nao/nao_mesh_rua.pmg");
            
            List<String> anims = AssetFactory.listAll("gfx/char/human/nao/ani/", ".ani");
            tryAddAnimation(anims);
        } catch (Exception e) {
            optionPanel.showError("Error", e);
            logger.error("设置骨骼时发生异常", e);
        }
    }

    void loadMorrighan() {
        modelState.detachAll();

        try {
        	modelState.setSkeleton("gfx/char/human/morrighan/morrighan_framework.frm");
        	modelState.addRolePart("gfx/char/human/morrighan/morrighan_mesh.pmg");
        	
        	List<String> anims = AssetFactory.listAll("gfx/char/human/morrighan/", ".ani");
        	tryAddAnimation(anims);
        } catch (Exception e) {
            optionPanel.showError("Error", e);
            logger.error("设置骨骼时发生异常", e);
        }
    }

    void loadHumanFemale() {
        modelState.detachAll();

        try {
        	modelState.setSkeleton("gfx/char/human/female/female_framework.frm");

            modelState.addRolePart("gfx/char/chapter4/human/female/wear/female_c4_2012christmas_bss.pmg");
            modelState.addRolePart("gfx/char/chapter4/human/female/shoes/female_c4_2012christmas_s10.pmg");
            modelState.addRolePart("gfx/char/chapter4/human/female/helmet/female_c4_2012christmas_h01.pmg");
            modelState.addRolePart("gfx/char/chapter4/human/female/glove/female_c4_2012christmas_g06.pmg");

            List<String> anims = AssetFactory.listAll("gfx/char/chapter4/human/anim/social_motion/female", ".ani");
            tryAddAnimation(anims);

        } catch (Exception e) {
            optionPanel.showError("Error", e);
            logger.error("设置骨骼时发生异常", e);
        }
    }

    void loadElfFemale() {
        modelState.detachAll();

        try {
        	modelState.setSkeleton("gfx/char/human/female/female_framework.frm");
        	
        	modelState.addRolePart("gfx/char/elf/female/hair/elf_female_hair02_t02.pmg");
            modelState.addRolePart("gfx/char/chapter4/elf/female/face/elf_female_c4_portia_face.pmg");
            modelState.addRolePart("gfx/char/chapter4/elf/female/wear/elf_female_c4_darkknight_bss.pmg");
            modelState.addRolePart("gfx/char/chapter4/elf/female/shoes/elf_female_c4_darkknight_s10.pmg");
            modelState.addRolePart("gfx/char/chapter4/elf/female/glove/elf_female_c4_darkknight_g03.pmg");
            modelState.addRolePart("gfx/char/chapter4/elf/female/helmet/elf_female_c4_darkknight_h03.pmg");
            
            List<String> anims = AssetFactory.listAll("gfx/char/chapter4/elf/anim/elf_", ".ani");
            tryAddAnimation(anims);
        } catch (Exception e) {
            optionPanel.showError("Error", e);
            logger.error("设置骨骼时发生异常", e);
        }
    }

    void loadGiantFemale() {
        modelState.detachAll();

        try {
        	modelState.setSkeleton("gfx/char/giant/female/giant_female_framework.frm");
        	
        	modelState.addRolePart("gfx/char/giant/female/hair/giant_female_hair02_t02.pmg");
        	modelState.addRolePart("gfx/char/chapter4/giant/female/face/giant_female_c4_f01.pmg");
            modelState.addRolePart("gfx/char/chapter4/giant/female/wear/giant_female_c4_2012christmas_bss.pmg");
            modelState.addRolePart("gfx/char/chapter4/giant/female/shoes/giant_female_c4_2012christmas_s10.pmg");
            modelState.addRolePart("gfx/char/chapter4/giant/female/glove/giant_female_c4_2012christmas_g06.pmg");

            List<String> anims = AssetFactory.listAll("gfx/char/chapter4/giant/", ".ani");
            tryAddAnimation(anims);
        } catch (Exception e) {
            optionPanel.showError("Error", e);
            logger.error("设置骨骼时发生异常", e);
        }
    }

    void loadMappleFox() {
        modelState.detachAll();

        try {
        	modelState.setSkeleton("gfx/char*pet_c4_maplestory_fox01_s_framework.frm");
        	modelState.addRolePart("gfx/char*pet_c4_maplestory_fox01_s_mesh.pmg");
            
            List<String> anims = AssetFactory.listAll("gfx/char/chapter4/pet/anim/maplefox/pet_maplefox", ".ani");
            tryAddAnimation(anims);
            
        } catch (Exception e) {
            optionPanel.showError("Error", e);
            logger.error("设置骨骼时发生异常", e);
        }
    }
    
    void tryAddAnimation(List<String> anims) {
    	int len = anims.size();
    	for(int i=0; i<len; i++) {
    		AniFile ani = AssetFactory.loadAni(anims.get(i));
    		Skeleton skeleton = modelState.getSkeleton();
    		if (skeleton.getBoneCount() == ani.boneCount) {
    			Animation anim = AssetFactory.buildAnimation(ani, skeleton);
    			modelState.addAnimation(anim);
    		}
    	}
    }
}
