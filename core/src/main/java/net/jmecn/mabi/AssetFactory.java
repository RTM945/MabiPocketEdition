package net.jmecn.mabi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.animation.Animation;
import com.jme3.animation.Bone;
import com.jme3.animation.BoneTrack;
import com.jme3.animation.Skeleton;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.util.BufferUtils;
import com.jme3.util.TempVars;

import net.jmecn.mabi.pack.PackFile;
import net.jmecn.mabi.plugin.AniLoader;
import net.jmecn.mabi.plugin.FrmLoader;
import net.jmecn.mabi.plugin.PackLocator;
import net.jmecn.mabi.plugin.PmgLoader;
import net.jmecn.mabi.struct.AniFile;
import net.jmecn.mabi.struct.AniFrame;
import net.jmecn.mabi.struct.AniTrack;
import net.jmecn.mabi.struct.BoneAssignment;
import net.jmecn.mabi.struct.FrmBone;
import net.jmecn.mabi.struct.FrmFile;
import net.jmecn.mabi.struct.PmGeometry;
import net.jmecn.mabi.struct.PmgFile;
import net.jmecn.mabi.struct.Skin;

/**
 * 资源工厂
 * 
 * @author yanmaoyuan
 *
 */
public class AssetFactory {

    static Logger logger = LoggerFactory.getLogger(AssetFactory.class);

    private static AssetManager assetManager;

    private static List<PackFile> packFiles = new ArrayList<PackFile>();

    private static HashMap<String, Texture> texCache = new HashMap<String, Texture>();

    /**
     * 初始化Mabinogi的资源加载插件。
     * 
     * @param manager
     *            jME3的AssetManager
     */
    public static void setAssetManager(AssetManager manager) {
        assetManager = manager;

        // Mabinogi plugin
        assetManager.registerLoader(FrmLoader.class, "frm");
        assetManager.registerLoader(PmgLoader.class, "pmg");
        assetManager.registerLoader(AniLoader.class, "ani");

        preload();
    }

    /**
     * 预加载Mabinogi的资源文件夹
     */
    private static void preload() {
        File file = new File("assets.txt");
        if (file.exists()) {
            Scanner input = null;
            try {
                InputStream in = new FileInputStream(file);
                input = new Scanner(in);
                while (input.hasNextLine()) {
                    String line = input.nextLine();
                    line = line.trim();
                    if (line.length() == 0 || line.startsWith("#")) {
                        continue;
                    }

                    locateMabiAsset(line);
                }

            } catch (IOException e) {
                logger.error("读取文件路径失败", e, e);
            } finally {
                if (input != null) {
                    input.close();
                }
            }
        }
    }

    /**
     * 将制定路径下的洛奇pack文件添加到路径中。
     * 
     * @param rootPath
     */
    private static void locateMabiAsset(String rootPath) {

        File root = new File(rootPath);
        if (root.exists() && root.isDirectory()) {

            // 将该文件位置注册到定位器，这样data文件夹就有用了。
            assetManager.registerLocator(rootPath, FileLocator.class);

            String[] name = root.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".pack");
                }
            });

            for (int i = name.length - 1; i >= 0; i--) {
                String pack = rootPath + name[i];
                try {
                    packFiles.add(new PackFile(pack));
                    assetManager.registerLocator(pack, PackLocator.class);
                    logger.info("Add {} to locator.", pack);
                } catch (IOException e) {
                    logger.error("{} is not a valid pack file.", pack);
                }
            }
        }
    }

    /**
     * 查询以特定字符串开头/结尾的文件清单
     * 
     * @param beginsWith
     * @param endsWith
     * @return
     */
    public static List<String> listAll(String beginsWith, String endsWith) {
        List<String> results = new ArrayList<String>();
        for (int i = 0; i < packFiles.size(); i++) {
            PackFile pack = packFiles.get(i);
            results.addAll(pack.getFileNames(beginsWith, endsWith));
        }

        return results;
    }

    public static InputStream openStream(String path) {
        AssetInfo info = assetManager.locateAsset(new AssetKey<Object>(path));
        if (info != null) {
            return info.openStream();
        } else {
            return null;
        }
    }

    public static PmgFile loadPmg(String path) {
        return (PmgFile) assetManager.loadAsset(path);
    }

    public static FrmFile loadFrm(String path) {
        return (FrmFile) assetManager.loadAsset(path);
    }

    public static AniFile loadAni(String path) {
        return (AniFile) assetManager.loadAsset(path);
    }

    /**
     * 装载pmg模型
     * 
     * @param path
     */
    public static Node loadModel(String path) {
        PmgFile pmgFile = loadPmg(path);
        return buildModel(pmgFile, null, null);
    }

    /**
     * 装载pmg模型
     * 
     * @param path
     */
    public static Node loadModel(String path, Skeleton ske) {
        PmgFile pmgFile = loadPmg(path);
        return buildModel(pmgFile, null, ske);
    }

    /**
     * 装载骨骼
     * 
     * @param frm
     */
    public static Skeleton loadSkeleton(String frm) {

        // 骨架
        FrmFile frmFile = loadFrm(frm);

        Skeleton ske = buildSkeleton(frmFile);

        return ske;
    }

    /**
     * 载入动画
     * 
     * @param path
     */
    public static Animation loadAnimation(String path) {
        AniFile aniFile = loadAni(path);
        Animation anim = buildAnimation(aniFile, null);
        return anim;
    }

    /**
     * 载入动画
     * 
     * @param path
     * @param skeleton
     * @return
     */
    public static Animation loadAnimation(String path, Skeleton skeleton) {
        AniFile aniFile = loadAni(path);
        Animation anim = buildAnimation(aniFile, skeleton);
        return anim;
    }

    /**
     * 生成模型。
     * 
     * @param pmgFile
     * @param model
     * @return
     */
    public static Node buildModel(PmgFile pmgFile, Node model) {
        return buildModel(pmgFile, model, null);
    }

    /**
     * 生成模型。
     * 
     * @param pmgFile
     * @param model
     * @return
     */
    public static Node buildModel(PmgFile pmgFile, Node model, Skeleton ske) {
        if (model != null) {
            model.detachAllChildren();
        } else {
            model = new Node(pmgFile.modelName);

            logger.info("Build new node: {}", pmgFile.modelName);
        }

        int count = pmgFile.geomCount;
        for (int k = 0; k < count; k++) {
            PmGeometry pmg = pmgFile.geomDats[k];

            Mesh mesh = buildMesh(pmg);

            // 骨骼蒙皮数据
            if (ske != null) {
                BoneAssignment boneAssignment = pmgFile.boneAssignments[k];
                skinning(mesh, pmg, boneAssignment, ske);
            }

            mesh.updateCounts();
            mesh.updateBound();
            mesh.setStatic();

            Geometry geom = new Geometry(pmg.meshName, mesh);
            /**
             * 该空间变化应该应用到网格顶点上，否则骨骼蒙皮动画无法正常工作。
             */
            // geom.setLocalRotation(pmg.majorMatrix.toRotationQuat());
            // geom.setLocalTranslation(pmg.majorMatrix.toTranslationVector());
            // geom.setLocalScale(pmg.majorMatrix.toScaleVector());

            // 创建材质
            Material mat = buildMaterial(pmg);
            geom.setMaterial(mat);

            model.attachChild(geom);

            // debug only
            //model.attachChild(buildDebugNormals(pmg));
            //model.attachChild(buildDebugSkins(pmg));
        }

        return model;
    }

    /**
     * 创建网格
     * 
     * @param pmg
     * @return
     */
    public static Mesh buildMesh(PmGeometry pmg) {
        Mesh mesh = new Mesh();

        // 顶点数据
        int[] indexes = pmg.indexes;
        Vector3f[] vertexes = new Vector3f[pmg.vertexCount];
        Vector3f[] normals = new Vector3f[pmg.vertexCount];
        ColorRGBA[] vertexColors = new ColorRGBA[pmg.vertexCount];
        Vector2f[] texCoords = new Vector2f[pmg.vertexCount];

        /**
         * 为了保证骨骼蒙皮动画正常，顶点和法线要通过pmg.majorMatrix进行空间转换。
         */
        Matrix4f transform = pmg.majorMatrix;
        Matrix3f rotation = transform.toRotationMatrix();
        
        for (int i = 0; i < pmg.vertexCount; i++) {
            vertexes[i] = transform.mult(pmg.verts[i].getPosition());
            normals[i] = rotation.mult(pmg.verts[i].getNormal());
            vertexColors[i] = pmg.verts[i].getColor();
            texCoords[i] = pmg.verts[i].getTexCoord();
        }

        FloatBuffer vertex = BufferUtils.createFloatBuffer(vertexes);
        FloatBuffer normal = BufferUtils.createFloatBuffer(normals);

        mesh.setBuffer(Type.Position, 3, vertex);
        mesh.setBuffer(Type.Normal, 3, normal);

        mesh.setBuffer(Type.BindPosePosition, 3, vertex);
        mesh.setBuffer(Type.BindPoseNormal, 3, normal);

        mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexes));
        mesh.setBuffer(Type.Color, 4, BufferUtils.createFloatBuffer(vertexColors));
        mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoords));

        return mesh;
    }

    /**
     * 显示模型的法线。
     * 
     * @param pmg
     * @return
     */
    public static Geometry buildDebugNormals(PmGeometry pmg) {
        Mesh mesh = new Mesh();

        int vcount = pmg.vertexCount * 2;
        // 顶点数据
        int[] indexes = new int[vcount];
        Vector3f[] vertexes = new Vector3f[vcount];
        ColorRGBA[] vertexColors = new ColorRGBA[vcount];

        Matrix4f transform = pmg.majorMatrix;
        Matrix3f rotation = transform.toRotationMatrix();
        for (int i = 0; i < pmg.vertexCount; i++) {

            /**
             * 为了保证骨骼蒙皮动画正常，顶点和法线要通过pmg.majorMatrix进行空间转换。
             */
            vertexes[i * 2] = transform.mult(pmg.verts[i].getPosition());
            vertexes[i * 2 + 1] = rotation.mult(pmg.verts[i].getNormal()).multLocal(4f).add(vertexes[i * 2]);

            vertexColors[i * 2] = new ColorRGBA(1, 0, 0, 1);
            vertexColors[i * 2 + 1] = new ColorRGBA(0, 1, 0, 1);

            indexes[i * 2] = i * 2;
            indexes[i * 2 + 1] = i * 2 + 1;

        }

        mesh.setBuffer(Type.Index, 1, BufferUtils.createIntBuffer(indexes));
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertexes));
        mesh.setBuffer(Type.Color, 4, BufferUtils.createFloatBuffer(vertexColors));

        mesh.setMode(Mode.Lines);
        mesh.setStatic();
        mesh.updateBound();

        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseVertexColor", true);

        Geometry geom = new Geometry("nor", mesh);
        geom.setMaterial(mat);
        return geom;
    }

    /**
     * 显示模型的蒙皮数据。
     * 
     * @param pmg
     * @return
     */
    public static Geometry buildDebugSkins(PmGeometry pmg) {
        Mesh mesh = new Mesh();

        int vcount = pmg.skinCount;
        // 顶点数据
        int[] indexes = new int[vcount];
        Vector3f[] vertexes = new Vector3f[vcount];
        ColorRGBA[] vertexColors = new ColorRGBA[vcount];

        Matrix4f transform = pmg.majorMatrix;
        Matrix3f rotation = transform.toRotationMatrix();
        for (int i = 0; i < pmg.skinCount; i++) {
            Skin skin = pmg.skins[i];
            int id = skin.id;
            ColorRGBA color = ColorRGBA.Blue.interpolateLocal(ColorRGBA.Orange, skin.scale);

            /**
             * 为了保证骨骼蒙皮动画正常，顶点和法线要通过pmg.majorMatrix进行空间转换。
             */
            Vector3f pos = transform.mult(pmg.verts[id].getPosition());
            rotation.mult(pmg.verts[id].getNormal()).multLocal(5f).add(pos);
            
            vertexes[i] = rotation.mult(pmg.verts[id].getNormal()).multLocal(5f).add(pos);

            vertexColors[i] = color;

            indexes[i] = i;
        }

        mesh.setBuffer(Type.Index, 1, BufferUtils.createIntBuffer(indexes));
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertexes));
        mesh.setBuffer(Type.Color, 4, BufferUtils.createFloatBuffer(vertexColors));

        mesh.setMode(Mode.Points);
        mesh.setStatic();
        mesh.updateBound();

        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseVertexColor", true);

        Geometry geom = new Geometry("skin", mesh);
        geom.setMaterial(mat);
        return geom;
    }
    
    /**
     * 骨骼蒙皮
     * 
     * @param mesh
     * @param pmg
     * @param boneAssignment
     */
    public static void skinning(Mesh mesh, PmGeometry pmg, BoneAssignment boneAssignment, Skeleton ske) {
        mesh.setMaxNumWeights(4);
        int boneIndex = ske.getBoneIndex(boneAssignment.bone1);
        int boneIndex2 = ske.getBoneIndex(boneAssignment.bone2);

        byte[] boneIndexes = new byte[pmg.vertexCount * 4];
        float[] boneWeights = new float[pmg.vertexCount * 4];
        for (int i = 0; i < pmg.vertexCount; i++) {
            int n = i * 4;
            boneIndexes[n] = (byte) boneIndex;
            boneIndexes[n + 1] = (byte) boneIndex2;
            boneIndexes[n + 2] = 0;
            boneIndexes[n + 3] = 0;

            // default weight
            boneWeights[n] = 1f;
            boneWeights[n + 1] = 0;
            boneWeights[n + 2] = 0;
            boneWeights[n + 3] = 0;
        }

        // FloatBuffer vertex = (FloatBuffer)
        // mesh.getBuffer(Type.Position).getData();
        // FloatBuffer normals = (FloatBuffer)
        // mesh.getBuffer(Type.Normal).getData();

        // mesh.setBuffer(Type.BindPosePosition, 3, vertex);
        // mesh.setBuffer(Type.BindPoseNormal, 3, normals);

        mesh.setBuffer(Type.BoneIndex, 4, BufferUtils.createByteBuffer(boneIndexes));
        mesh.setBuffer(Type.HWBoneIndex, 4, BufferUtils.createByteBuffer(boneIndexes));

        mesh.setBuffer(Type.BoneWeight, 4, BufferUtils.createFloatBuffer(boneWeights));
        mesh.setBuffer(Type.HWBoneWeight, 4, BufferUtils.createFloatBuffer(boneWeights));
    }

    /**
     * 根据frm文件，生成骨骼。
     * 
     * @param frmFile
     * @return
     */
    public static Skeleton buildSkeleton(FrmFile frmFile) {
        int boneCount = frmFile.boneCount;

        Bone[] bones = new Bone[boneCount];
        byte[] parents = new byte[boneCount];
        for (int i = 0; i < boneCount; i++) {
            FrmBone fbone = frmFile.frmBones[i];

            Bone bone = new Bone(fbone.name);

            Matrix4f bindPose = fbone.bindPose;
            bone.setBindTransforms(bindPose.toTranslationVector(), bindPose.toRotationQuat(), bindPose.toScaleVector());

            // 父子关系
            bones[fbone.boneid] = bone;
            parents[fbone.boneid] = fbone.parentid;
        }

        // 继承关系
        for (byte id = 0; id < boneCount; id++) {
            byte parentid = parents[id];
            if (parentid > -1 && parentid < boneCount && parentid != id) {
                bones[parentid].addChild(bones[id]);
            }
        }

        Skeleton ske = new Skeleton(bones);
        return ske;
    }

    /**
     * 生成动画
     * 
     * @param aniFile
     * @param skeleton
     *            骨骼
     * @return
     */
    public static Animation buildAnimation(AniFile aniFile, Skeleton skeleton) {
        // 动画名称
        String name = aniFile.getName();
        // 动画时长
        float length = aniFile.getLength();
        Animation anim = new Animation(name, length);

        /**
         * 根据骨骼姿态，修正动画数据。
         */
        Vector3f bindPosition = null;
        Quaternion bindRotationI = null;
        Quaternion tmpQ = new Quaternion();

        if (skeleton != null) {

            if (aniFile.boneCount != skeleton.getBoneCount()) {
                logger.warn("Skeleton.BoneCount={}, Animation.BoneTrack.Count={}", skeleton.getBoneCount(),
                        aniFile.boneCount);
                skeleton = null;
            } else {
                bindPosition = new Vector3f();
                bindRotationI = new Quaternion();
            }
        }

        for (int i = 0; i < aniFile.boneCount; i++) {
            AniTrack aniTrack = aniFile.aniTracks[i];

            if (aniTrack.frameCount == 0) {
                logger.warn("aniTrack.frameCount == 0");
                continue;
            }

            // 骨骼的初始动作
            if (skeleton != null) {
                Bone bone = skeleton.getBone(i);
                bindPosition.set(bone.getBindPosition());
                bindRotationI.set(bone.getBindRotation().inverse());
            }

            BoneTrack track = new BoneTrack(i);
            anim.addTrack(track);

            float[] times = new float[aniTrack.frameCount];
            Vector3f[] translations = new Vector3f[aniTrack.frameCount];
            Quaternion[] rotations = new Quaternion[aniTrack.frameCount];
            Vector3f[] scales = new Vector3f[aniTrack.frameCount];

            for (int j = 0; j < aniTrack.frameCount; j++) {
                AniFrame aniFrame = aniTrack.aniFrames[j];
                times[j] = (float) aniFrame.frameNo / aniFile.framePerSecond;
                translations[j] = new Vector3f(aniFrame.x, aniFrame.y, aniFrame.z);
                rotations[j] = new Quaternion(aniFrame.qx, aniFrame.qy, aniFrame.qz, -aniFrame.qw);
                scales[j] = new Vector3f(1f, 1f, 1f);

                if (skeleton != null) {
                    // 还原位移
                    translations[j].subtractLocal(bindPosition);

                    // 还原旋转
                    bindRotationI.mult(rotations[j], tmpQ);
                    rotations[j].set(tmpQ);
                }
            }

            track.setKeyframes(times, translations, rotations, scales);
        }

        return anim;
    }

    /**
     * 计算每个顶点的法向量。
     * 
     * @return
     */
    public static Vector3f[] computeNormals(Vector3f[] vertex, int[] indexes) {
        TempVars tmp = TempVars.get();

        Vector3f A;// 三角形的第1个点
        Vector3f B;// 三角形的第2个点
        Vector3f C;// 三角形的第3个点

        Vector3f vAB = tmp.vect1;
        Vector3f vAC = tmp.vect2;
        Vector3f n = tmp.vect4;

        // Here we allocate all the memory we need to calculate the normals
        int nFace = indexes.length / 3;
        int nVertex = vertex.length;
        Vector3f[] tempNormals = new Vector3f[nFace];
        Vector3f[] normals = new Vector3f[nVertex];

        for (int i = 0; i < nFace; i++) {
            A = vertex[indexes[i * 3]];
            B = vertex[indexes[i * 3 + 1]];
            C = vertex[indexes[i * 3 + 2]];

            vAB = B.subtract(A, vAB);
            vAC = C.subtract(A, vAC);
            n = vAB.cross(vAC, n);

            tempNormals[i] = n.normalize();
        }

        Vector3f sum = tmp.vect4;
        int shared = 0;

        for (int i = 0; i < nVertex; i++) {
            // 统计每个点被那些面共用。
            for (int j = 0; j < nFace; j++) {
                if (indexes[j * 3] == i || indexes[j * 3 + 1] == i || indexes[j * 3 + 2] == i) {
                    sum.addLocal(tempNormals[j]);
                    shared++;
                }
            }

            // 求均值
            normals[i] = sum.divideLocal((shared)).normalize();

            sum.zero(); // Reset the sum
            shared = 0; // Reset the shared
        }

        tmp.release();
        return normals;
    }

    /**
     * 创建材质
     * 
     * @param pmg
     * @return
     */
    public static Material buildMaterial(PmGeometry pmg) {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");

        mat.setColor("Diffuse", new ColorRGBA(1, 1, 1, 1));
        mat.setColor("Ambient", new ColorRGBA(1, 1, 1, 1));
        mat.setColor("Specular", new ColorRGBA(1, 1, 1, 1));
        mat.setFloat("Shininess", 127f);
        mat.setBoolean("UseMaterialColors", true);
        mat.setBoolean("UseVertexColor", true);

        // 透明度
        mat.setFloat("AlphaDiscardThreshold", 0.2f);

        try {
            Texture tex = getTexture(pmg.textureName);
            mat.setTexture("DiffuseMap", tex);
        } catch (Exception e) {
            logger.error("can't find:{}", e.getMessage());
        }

        RenderState rs = mat.getAdditionalRenderState();
        rs.setDepthWrite(true);
        rs.setDepthTest(true);
        rs.setColorWrite(true);
        // rs.setBlendMode(BlendMode.Alpha);
        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);

        return mat;
    }

    /**
     * 读取材质
     * 
     * @param textureName
     * @return
     */
    public static Texture getTexture(String textureName) {
        String matName = "material/*" + textureName + ".dds";

        Texture tex = texCache.get(matName);
        if (tex == null) {
            TextureKey key = new TextureKey(matName, false);
            tex = assetManager.loadTexture(key);
            tex.setMagFilter(MagFilter.Bilinear);
            tex.setMinFilter(MinFilter.BilinearNearestMipMap);
            texCache.put(matName, tex);
        }

        return tex;
    }

    /**
     * 获得装备图片的材质
     * 
     * @param imgName
     * @return
     */
    public static Texture getInvImage(String imgName) {
        String name = "gfx/image2/inven/item/" + imgName + ".dds";

        Texture tex = texCache.get(name);
        if (tex == null) {
            TextureKey key = new TextureKey(name, false);
            tex = assetManager.loadTexture(key);
            texCache.put(name, tex);
        }

        return tex;
    }

}