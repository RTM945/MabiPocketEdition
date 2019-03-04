package net.jmecn.mabi.struct;

import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;

public class FrmBone {
	public Matrix4f globalToLocal;// 64B
	public Matrix4f localToGlobal;// 64B
	public Matrix4f bindPose;// 64B
	public String name;// 32
	public byte boneid;
	public byte parentid;
	public int empty02;// 0x00*2
	
	public Quaternion quad1;
	public Quaternion quad2;
	
}
