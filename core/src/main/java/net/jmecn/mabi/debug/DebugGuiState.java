package net.jmecn.mabi.debug;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.ActionButton;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.CallMethodAction;
import com.simsilica.lemur.Checkbox;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.OptionPanelState;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.style.ElementId;

/**
 * 调试界面
 * 
 * @author yanmaoyuan
 *
 */
public class DebugGuiState extends BaseAppState {

	static Logger logger = LoggerFactory.getLogger(DebugGuiState.class);
	static Class[] classes = {
		CharacterState.class,
		EquipmentState.class
	};
	static String[] names = {
		"Character",
		"Equipment",
	};
	
	private List<ToggleChild> toggles = new ArrayList<>();
	
	private Node guiNode = new Node("Debug Gui Root Node");
	private OptionPanelState optionPanel;
	
	/**
	 * 屏幕分辨率
	 */
	private float width;
	private float height;

	@Override
	protected void initialize(Application app) {
		// 分辨率
		width = app.getCamera().getWidth();
		height = app.getCamera().getHeight();

		optionPanel = app.getStateManager().getState(OptionPanelState.class);
		optionPanel.setGuiNode(guiNode);

		Container wnd = new Container();
		guiNode.attachChild(wnd);

		wnd.addChild(new Label("Contorller", new ElementId("title")));

		Container actions = new Container(new BoxLayout(Axis.Y, FillMode.Even));
		wnd.addChild(actions);
		
		for(int i=0; i<classes.length; i++) {
            ToggleChild toggle = new ToggleChild(names[i], classes[i]);
            //toggles.add(toggle);
            Checkbox cb = actions.addChild(new Checkbox(toggle.getName()));
            cb.addClickCommands(toggle);
            cb.setInsets(new Insets3f(2, 2, 2, 2));
        }
 
        ActionButton exit = wnd.addChild(new ActionButton(new CallMethodAction("Exit", app, "stop")));
        exit.setInsets(new Insets3f(10, 10, 10, 10)); 
        
		Vector3f size = wnd.getPreferredSize();
		wnd.setLocalTranslation(width - size.x - 10, height - 10, 1);
		
		getStateManager().attach(new AxisState());
		getStateManager().attach(new AnimationState());
	}

	@Override
	protected void cleanup(Application app) {
	}

	@Override
	protected void onEnable() {
		SimpleApplication simpleApp = (SimpleApplication) getApplication();
		simpleApp.getGuiNode().attachChild(guiNode);
	}

	@Override
	protected void onDisable() {
		guiNode.removeFromParent();
	}

    /**
     *  For states that close themselves, this lets the master list know that the
     *  particular child demo is no longer open.  Basically, this lets the checkbox
     *  update.
     */   
    public void closeChild( AppState child ) {
        for( ToggleChild toggle : toggles ) {
            if( toggle.child == child ) {
                toggle.close();
            }
        }
    }
    
	protected void showError(String title, String error) {
		getState(OptionPanelState.class).show(title, error);
	}

	private class ToggleChild implements Command<Button> {
		private String name;
		private Checkbox check;
		private Class<? extends AppState> type;
		private AppState child;

		public ToggleChild(String name, Class<? extends AppState> type) {
			this.name = name;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void execute(Button button) {
			this.check = (Checkbox) button;
			if (check.isChecked()) {
				open();
			} else {
				close();
			}
		}

		public void open() {
			if (child != null) {
				// Already open
				return;
			}
			try {
				child = (AppState) type.newInstance();
				getStateManager().attach(child);
			} catch (Exception e) {
				showError("Error for demo:" + type.getSimpleName(), e.toString());
			}
		}

		public void close() {
			if (check != null) {
				check.setChecked(false);
			}
			if (child != null) {
				getStateManager().detach(child);
				child = null;
			}
		}
	}
}
