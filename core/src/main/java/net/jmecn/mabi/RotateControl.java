package net.jmecn.mabi;

import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class RotateControl extends AbstractControl {

	@Override
	protected void controlUpdate(float tpf) {
		spatial.rotate(0, tpf * FastMath.PI, 0);
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
	}

}
