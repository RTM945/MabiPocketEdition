package net.jmecn.mabi;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.TechniqueDef;
import com.jme3.material.TechniqueDef.LightMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.PointLightShadowRenderer;

/**
 * 灯光模块
 * 
 * @author yanmoayuan
 *
 */
public class LightState extends BaseAppState {

    private Node rootNode;

    // 光源点
    private Node node = new Node("LightSources");

    private ViewPort viewPort;
    private AssetManager assetManager;

    // 灯光渲染模式
    private LightMode lm = TechniqueDef.LightMode.SinglePass;

    // 光源
    private AmbientLight al;
    private PointLight pl;

    // 阴影渲染器
    private PointLightShadowRenderer plsr;

    // 后置滤镜处理器
    private FilterPostProcessor fpp;

    @Override
    protected void initialize(Application app) {

        viewPort = app.getViewPort();
        viewPort.setBackgroundColor(new ColorRGBA(0.75f, 0.8f, 0.9f, 1f));
        
        assetManager = app.getAssetManager();

        SimpleApplication simpleApp = (SimpleApplication) app;
        rootNode = simpleApp.getRootNode();
        rootNode.setShadowMode(ShadowMode.CastAndReceive);

        // 设置灯光渲染模式为单通道
        RenderManager renderManager = app.getRenderManager();
        renderManager.setPreferredLightMode(lm);
        renderManager.setSinglePassLightBatchSize(2);

        /**
         * 光源
         */
        // 环境光
        al = new AmbientLight(ColorRGBA.White.mult(0.9f));

        // 点光源
        Vector3f position = new Vector3f(6, 16, 6);
        pl = new PointLight(position, ColorRGBA.White.mult(0.1f));
        pl.setRadius(20);

        lightSource(position, ColorRGBA.White);
        
        /**
         * 影子
         */
        // 点光源影子
        plsr = new PointLightShadowRenderer(assetManager, 1024);
        plsr.setLight(pl);// 设置光源
        plsr.setEdgeFilteringMode(EdgeFilteringMode.PCF4);

        /**
         * 发光滤镜
         */
        fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);

        // 4倍抗锯齿
        fpp.setNumSamples(4);
    }

    /**
     * 创建一个小球，表示光源的位置。
     */
    private void lightSource(Vector3f position, ColorRGBA color) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        mat.setColor("GlowColor", color);

        Geometry geom = new Geometry("LightSource", new Sphere(6, 12, 0.2f));
        geom.setMaterial(mat);
        geom.setLocalTranslation(position);
        geom.setShadowMode(ShadowMode.Off);

        node.attachChild(geom);
    }

    @Override
    protected void cleanup(Application app) {}

    @Override
    protected void onEnable() {
        // 添加光源
        rootNode.addLight(al);
        rootNode.addLight(pl);

        // 添加影子
        viewPort.addProcessor(plsr);

        // 添加滤镜
        viewPort.addProcessor(fpp);

        // 添加光源节点
        rootNode.attachChild(node);
    }

    @Override
    protected void onDisable() {
        // 移除光源节点
        node.removeFromParent();
        
        // 移除滤镜
        viewPort.removeProcessor(fpp);

        // 移除影子
        viewPort.removeProcessor(plsr);

        // 移除光源
        rootNode.removeLight(al);
        rootNode.removeLight(pl);
    }

}
