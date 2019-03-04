package net.jmecn.mabi;

import com.jme3.system.AppSettings;

public class Main {

	public final static int DEFAULT_WIDTH = 1024;
	public final static int DEFAULT_HEIGHT = 720;
	
	public static void main(String[] args) {
		
		AppSettings settings = new AppSettings(true);
		settings.setTitle("Mabinogi口袋版");
		settings.setResolution(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		settings.setSamples(4);// 4倍抗锯齿
		settings.setFrameRate(120);
		settings.setVSync(true);
		
		MabiPE app = new MabiPE();
		app.setShowSettings(false);
		app.setSettings(settings);
		app.start();
	}


}
