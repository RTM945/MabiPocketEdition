package net.jmecn.mabi;

import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.audio.AudioListenerState;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.OptionPanelState;
import com.simsilica.lemur.event.PopupState;
import com.simsilica.lemur.style.BaseStyles;

import net.jmecn.mabi.debug.DebugGuiState;

/**
 * Mabinogi Pocket Edition
 * 
 * @author yanmaoyuan
 *
 */
public class MabiPE extends SimpleApplication {
	
	/**
	 * 构造方法，初始化状态机。
	 */
	public MabiPE() {
		super(new StatsAppState(), new AudioListenerState(),
		        new ModelState(), new LightState(),
		        new OptionPanelState(), new PopupState(),
		        new DebugGuiState());
		
		// 关闭FPS
		setDisplayStatView(false);
        setDisplayFps(false);
	}

	@Override
	public void simpleInitApp() {
		AssetFactory.setAssetManager(assetManager);
		
		// 初始化Lemur GUI
        GuiGlobals.initialize(this);

        // 加载 'glass' 样式
        BaseStyles.loadGlassStyle();

        // 将'glass'设置为GUI默认样式
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
	}
}
