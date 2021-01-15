package Minigames.games.rubik;

import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;
import Minigames.games.input.bindings.MouseHoldObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
        modelBuilder.node().translation.set(2.1f, 2.1f, 2.1f);
        face(modelBuilder, "back");
        face(modelBuilder, "top");
        face(modelBuilder, "right");
        modelBuilder.node().translation.set(-2.1f, 2.1f, 2.1f);
        face(modelBuilder, "back");
        face(modelBuilder, "top");
        face(modelBuilder, "left");
        modelBuilder.node().translation.set(2.1f, -2.1f, 2.1f);
        face(modelBuilder, "back");
        face(modelBuilder, "bottom");
        face(modelBuilder, "right");
        modelBuilder.node().translation.set(-2.1f, -2.1f, 2.1f);
        face(modelBuilder, "back");
        face(modelBuilder, "bottom");
        face(modelBuilder, "left");
        modelBuilder.node().translation.set(2.1f, 2.1f, -2.1f);
        face(modelBuilder, "front");
        face(modelBuilder, "top");
        face(modelBuilder, "right");
        modelBuilder.node().translation.set(-2.1f, 2.1f, -2.1f);
        face(modelBuilder, "front");
        face(modelBuilder, "top");
        face(modelBuilder, "left");
        modelBuilder.node().translation.set(2.1f, -2.1f, -2.1f);
        face(modelBuilder, "front");
        face(modelBuilder, "bottom");
        face(modelBuilder, "right");
        modelBuilder.node().translation.set(-2.1f, -2.1f, -2.1f);
        face(modelBuilder, "front");
        face(modelBuilder, "bottom");
        face(modelBuilder, "left");
        model = modelBuilder.end();
        instance = new ModelInstance(model);
    }

    private void face(ModelBuilder modelBuilder, String face)
    {
        int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        switch (face) {
            case "front":
                modelBuilder.part("front", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(Color.GREEN)))
                        .rect(-2, -2, -2, -2, 2, -2, 2, 2, -2, 2, -2, -2, 0, 0, -1);
                break;
            case "back":
                modelBuilder.part("back", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(Color.BLUE)))
                        .rect(-2,2,2, -2,-2,2, 2,-2,2, 2,2,2, 0,0,1);
                break;
            case "bottom":
                modelBuilder.part("bottom", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(Color.YELLOW)))
                        .rect(-2,-2,2, -2,-2,-2, 2,-2,-2, 2,-2,2, 0,-1,0);
                break;
            case "top":
                modelBuilder.part("top", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(Color.WHITE)))
                        .rect(-2,2,-2, -2,2,2, 2,2,2, 2,2,-2, 0,1,0);
                break;
            case "left":
                modelBuilder.part("left", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(Color.ORANGE)))
                        .rect(-2,-2,2, -2,2,2, -2,2,-2, -2,-2,-2, -1,0,0);
                break;
            case "right":
                modelBuilder.part("right", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(Color.RED)))
                        .rect(2,-2,-2, 2,2,-2, 2,2,2, 2,-2,2, 1,0,0);
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
                ((float)x) / SIZE * 2f,
                ((float)y) / SIZE * 2f,
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
