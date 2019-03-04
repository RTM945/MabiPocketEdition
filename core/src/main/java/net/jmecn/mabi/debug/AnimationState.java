package net.jmecn.mabi.debug;

import java.util.List;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.ActionButton;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.CallMethodAction;
import com.simsilica.lemur.Checkbox;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.RangedValueModel;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.event.PopupState;
import com.simsilica.lemur.list.SelectionModel;
import com.simsilica.lemur.style.ElementId;

import net.jmecn.lemur.SeekBar;
import net.jmecn.mabi.ModelState;

/**
 * 动画控制模块
 * @author yanmaoyuan
 *
 */
public class AnimationState extends BaseAppState {

	private Node guiNode = new Node("Animation Gui Root");
	private Container windows;
	private Container animWindows;
	private ActionButton animName;
	
	private ModelState modelState;

	private float width;

	// 动画进度
	private RangedValueModel progressModel;
	private VersionedReference<Double> progress;

	/**
	 * 动画列表
	 */
	private VersionedReference<Boolean> isLoop;
	private VersionedReference<List<String>> animNames;
	private SelectionModel selection = new SelectionModel();

	@Override
	protected void initialize(Application app) {
		modelState = app.getStateManager().getState(ModelState.class);
		
		// 分辨率
		width = app.getCamera().getWidth();
		
		/**
		 * 动画选择窗口
		 */
		animWindows = new Container();
		
		animWindows.addChild(new Label("Animation", new ElementId("title")));
		
		animNames = modelState.getAnimationNames().createReference();
		ListBox<String> listBox = new ListBox<String>(modelState.getAnimationNames());
        this.selection = listBox.getSelectionModel();
        animWindows.addChild(listBox);
		
        animWindows.addChild(new ActionButton(new CallMethodAction("OK", 
                animWindows, "removeFromParent")));
        
	    /**
	     * 创建主窗口
	     */
        windows = new Container();
        guiNode.attachChild(windows);
        
        // 动画名称按钮
        animName = windows.addChild(new ActionButton(new CallMethodAction("Animation: NONE", this, "selectAnim")));
        animName.setBackground(new QuadBackgroundComponent(new ColorRGBA(0.2f, 0.3f, 0.4f, 1f)));
        animName.setInsets(new Insets3f(2, 5, 2, 5));
        
        // 控制面板
        Container container = new Container(new BoxLayout(Axis.X, FillMode.Even));
        windows.addChild(container);
        container.addChild(new ActionButton(new CallMethodAction("Play", this, "playAnim")));
        
        final Checkbox cbLoopMode = new Checkbox("Loop");
        cbLoopMode.setChecked(true);
        container.addChild(cbLoopMode);
        isLoop = cbLoopMode.getModel().createReference();
        
        // 播放动画
        Checkbox cb = container.addChild(new Checkbox("Auto"));
        cb.setChecked(true);
        cb.addClickCommands(new CallMethodAction("Auto", modelState, "toggleAnimControl"));
        
        container.addChild(new Label("Skeleton: "));
        container.addChild(new ActionButton(new CallMethodAction("Show", modelState, "toggleSkeletonDebugger")));
        container.addChild(new ActionButton(new CallMethodAction("Enable", modelState, "toggleSkeletonControl")));
        
        // 将窗口置于屏幕底部
        Vector3f size = windows.getPreferredSize();
        size.x = width - 20;
        windows.setPreferredSize(size);
        windows.setLocalTranslation(10, size.y + 10, 0);
        
        
        /**
         * 创建动画进度条
         */
        SeekBar seekBar = new SeekBar();
        progressModel = seekBar.getModel();
        progress = progressModel.createReference();
      
        Vector3f size2 = seekBar.getPreferredSize();
        size2.x = width - 20;
        seekBar.setPreferredSize(size2);
        seekBar.setLocalTranslation(10, size2.y + size.y + 24, 0);
      
        guiNode.attachChild(seekBar);
	}

	@Override
    public void update(float tpf) {
		
		AnimChannel channel = modelState.getAnimChannel();
		
		// update anim controller
		if (animNames.update()) {
			progressModel.setPercent(0);
			if (channel != null) {
				channel.setTime(0);
			}
			
			selection.setSelection(-1);
			animName.setText("Animation: NONE");
		}
    	
    	if (channel != null) {
    		// change loop mode
    		if (isLoop != null && isLoop.update()) {
    			channel.setLoopMode(isLoop.get()?LoopMode.Loop:LoopMode.DontLoop);
    		}
    		
    		// 更新动画播放进度条
    		AnimControl control = channel.getControl();
    		if (control.getSpatial() != null) {
	    		float time = channel.getTime();
				float maxTime = channel.getAnimMaxTime();
				progressModel.setPercent(time/maxTime);
    		} else {
    			if (progress.update()) {
	    			float maxTime = channel.getAnimMaxTime();
	    			float time = (float) (progressModel.getPercent() * maxTime);
	    			channel.setTime(time);
	    			control.update(0f);
    			}
    		}
    	}
    }
    
    /**
     * 播放动画
     */
    public void playAnim() {
    	Integer index = selection.getSelection();
    	if (index != null) {
    		AnimChannel channel = modelState.getAnimChannel();
    		if (channel == null)
    			return;
    		
    		String name = modelState.getAnimationNames().get(index);
    		channel.setAnim(name);
    		channel.setLoopMode(isLoop.get()?LoopMode.Loop:LoopMode.DontLoop);
    		channel.getControl().update(0);
    	}
    }
    
    public void selectAnim() {
        Vector3f size = animWindows.getPreferredSize();
        size.x = width - 20;
        animWindows.setPreferredSize(size);
        animWindows.setLocalTranslation(10, size.y + windows.getPreferredSize().y + 12, 9f);
        getStateManager().getState(PopupState.class).showPopup(animWindows, new Command<PopupState>() {
            
            public void execute( PopupState state ) {
                // If the state has no active popups then we'll remove this
                // state also
                if( !state.hasActivePopups() ) {
                    setAnim();
                }
            }
    });
    }
    
    public void setAnim() {
        Integer index = selection.getSelection();
        if (index != null) {
            String name = modelState.getAnimationNames().get(index);
            animName.setText("Animation: " + name);
            
            AnimChannel channel = modelState.getAnimChannel();
    		if (channel == null)
    			return;
    		
    		channel.setAnim(name);
    		channel.setLoopMode(isLoop.get()?LoopMode.Loop:LoopMode.DontLoop);
    		channel.getControl().update(0);
        }
    }
    
	@Override
	protected void cleanup(Application app) {}

	@Override
	protected void onEnable() {
		((SimpleApplication) getApplication())
			.getGuiNode().attachChild(guiNode);
		
	    /**
	     * 更新动画列表
	     */
    	if (modelState.getAnimationNames().size() != 0) {
    		selection.setSelection(0);
    	}
	}

	@Override
	protected void onDisable() {
		guiNode.removeFromParent();
	}

}
