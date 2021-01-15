package Minigames.games.rubik;

import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;
import Minigames.games.input.bindings.MouseHoldObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.*;
import com.megacrit.cardcrawl.core.Settings;

public class RubikMinigame extends AbstractMinigame
{
    private PerspectiveCamera camera;
    private FrameBuffer fbo;
    private ModelBatch mb;
    private Environment environment;
    private Model model;
    private ModelInstance instance;

    private Vector2 lastMousePos;

    @Override
    public void initialize()
    {
        super.initialize();

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, SIZE, SIZE, true);

        mb = new ModelBatch();

        camera = new PerspectiveCamera(67, SIZE, SIZE);
        camera.position.set(10f, 10f, 10f);
        camera.lookAt(Vector3.Zero);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.node().id = "back_top_right";
        faceTranslation.set(2.1f, 2.1f, 2.1f);
        face(modelBuilder, "back");
        face(modelBuilder, "top");
        face(modelBuilder, "right");

        modelBuilder.node().id = "back_top_left";
        faceTranslation.set(-2.1f, 2.1f, 2.1f);
        face(modelBuilder, "back");
        face(modelBuilder, "top");
        face(modelBuilder, "left");

        modelBuilder.node().id = "back_bottom_right";
        faceTranslation.set(2.1f, -2.1f, 2.1f);
        face(modelBuilder, "back");
        face(modelBuilder, "bottom");
        face(modelBuilder, "right");

        modelBuilder.node().id = "back_bottom_left";
        faceTranslation.set(-2.1f, -2.1f, 2.1f);
        face(modelBuilder, "back");
        face(modelBuilder, "bottom");
        face(modelBuilder, "left");

        modelBuilder.node().id = "front_top_right";
        faceTranslation.set(2.1f, 2.1f, -2.1f);
        face(modelBuilder, "front");
        face(modelBuilder, "top");
        face(modelBuilder, "right");

        modelBuilder.node().id = "front_top_left";
        faceTranslation.set(-2.1f, 2.1f, -2.1f);
        face(modelBuilder, "front");
        face(modelBuilder, "top");
        face(modelBuilder, "left");

        modelBuilder.node().id = "front_bottom_right";
        faceTranslation.set(2.1f, -2.1f, -2.1f);
        face(modelBuilder, "front");
        face(modelBuilder, "bottom");
        face(modelBuilder, "right");

        modelBuilder.node().id = "front_bottom_left";
        faceTranslation.set(-2.1f, -2.1f, -2.1f);
        face(modelBuilder, "front");
        face(modelBuilder, "bottom");
        face(modelBuilder, "left");
        model = modelBuilder.end();
        instance = new ModelInstance(model);
    }

    private Vector3 faceTranslation = new Vector3();

    private void face(ModelBuilder modelBuilder, String face)
    {
        int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        Matrix4 transform = new Matrix4().translate(faceTranslation);
        MeshPartBuilder part;

        switch (face) {
            case "front":
                part = modelBuilder.part("front", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(Color.GREEN)));
                part.setVertexTransform(transform);
                part.rect(-2, -2, -2, -2, 2, -2, 2, 2, -2, 2, -2, -2, 0, 0, -1);
                break;
            case "back":
                part = modelBuilder.part("back", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(Color.BLUE)));
                part.setVertexTransform(transform);
                part.rect(-2,2,2, -2,-2,2, 2,-2,2, 2,2,2, 0,0,1);
                break;
            case "bottom":
                part = modelBuilder.part("bottom", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(Color.YELLOW)));
                part.setVertexTransform(transform);
                part.rect(-2,-2,2, -2,-2,-2, 2,-2,-2, 2,-2,2, 0,-1,0);
                break;
            case "top":
                part = modelBuilder.part("top", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(Color.WHITE)));
                part.setVertexTransform(transform);
                part.rect(-2,2,-2, -2,2,2, 2,2,2, 2,2,-2, 0,1,0);
                break;
            case "left":
                part = modelBuilder.part("left", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(Color.ORANGE)));
                part.setVertexTransform(transform);
                part.rect(-2,-2,2, -2,2,2, -2,2,-2, -2,-2,-2, -1,0,0);
                break;
            case "right":
                part = modelBuilder.part("right", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(Color.RED)));
                part.setVertexTransform(transform);
                part.rect(2,-2,-2, 2,2,-2, 2,2,2, 2,-2,2, 1,0,0);
                break;
        }
    }

    @Override
    public void dispose()
    {
        super.dispose();

        mb.dispose();
        model.dispose();
        fbo.dispose();
    }

    private Vector3 arcballVector(int x, int y)
    {
        x -= Settings.WIDTH / 2;
        y -= Settings.HEIGHT / 2;

        Vector3 p = new Vector3(
                ((float)x) / (SIZE * 1.5f) * 2f,
                ((float)y) / (SIZE * 1.5f) * 2f,
                0
        );
        float opSquared = p.x * p.x + p.y * p.y;
        if (opSquared <= 1f * 1f) {
            p.z = (float) Math.sqrt(1 - opSquared);
        } else {
            p = p.nor();
        }
        return p;
    }

    @Override
    protected BindingGroup getBindings()
    {
        BindingGroup bindings = new BindingGroup();

        bindings.addMouseBind(
                (x, y, pointer) -> isWithinArea(x, y),
                (p) -> lastMousePos = p.cpy(),
                new MouseHoldObject(
                        (x, y) -> {
                            Vector3 va = arcballVector((int) lastMousePos.x, (int) lastMousePos.y);
                            Vector3 vb = arcballVector(x, y);
                            float angle = (float) Math.acos(Math.min(1f, va.dot(vb)));
                            Vector3 axisInCamCoord = va.cpy().crs(vb);
                            Matrix3 cameraToObject = new Matrix3().set(camera.view);
                            cameraToObject.mul(new Matrix3().set(instance.transform));
                            cameraToObject.inv();
                            Vector3 axisInObjCoord = axisInCamCoord.mul(cameraToObject);
                            instance.transform.rotate(axisInObjCoord, 2f * angle * MathUtils.radiansToDegrees);

                            lastMousePos = new Vector2(x, y);
                        },
                        null
                )
        );

        return bindings;
    }

    @Override
    public void update(float elapsed)
    {
        super.update(elapsed);
    }

    @Override
    public void render(SpriteBatch sb)
    {
        super.render(sb);

        sb.end();
        fbo.begin();

        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        mb.begin(camera);
        mb.render(instance, environment);
        mb.end();
        fbo.end();
        sb.begin();

        drawTexture(
                sb,
                fbo.getColorBufferTexture(),
                0, 0,
                0,
                SIZE, SIZE,
                false, true
        );
    }
}
