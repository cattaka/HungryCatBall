package net.cattaka.hungrycatball.game.entity;

import net.cattaka.collections.OrderedSet;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.game.GameEntityAddEvent;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.game.IGameEntity;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.utils.DrawingUtil.AlphaMode;
import net.cattaka.hungrycatball.utils.ImageResource;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.collision.shapes.CircleDef;
import org.jbox2d.collision.shapes.ShapeDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;

import static net.cattaka.hungrycatball.HungryCatBallConstants.CELL_SIZE;
import static net.cattaka.hungrycatball.HungryCatBallConstants.WORLD_RECT;

public class BallEntity extends PhysicalEntity {
    private static BodyDef[] BODY_DEFS;

    static {
        BodyDef bd = new BodyDef();

        BODY_DEFS = new BodyDef[]{
                bd
        };

    }

    private float mScale;
    private ImageResource mImageResource;
    private boolean mOut;

    public BallEntity() {
        this(1);
    }

    public BallEntity(float scale) {
        super(1);
        mScale = scale;
    }

    @Override
    public BodyDef[] getBodyDefs() {
        return BODY_DEFS;
    }

    @Override
    public ShapeDef[] getShapeDefs() {
        ShapeDef[] shapeDefs;
        CircleDef pd = new CircleDef();
        pd.radius = CELL_SIZE * mScale;
        pd.density = 1.0f;
        pd.friction = 1.0f;
        pd.restitution = 0.75f;
        shapeDefs = new ShapeDef[]{
                pd
        };
        return shapeDefs;
    }

    @Override
    public void initialize(GameWorld gameWorld, SceneBundle sceneBundle,
                           Object exData, float[] exFloatData) {
        super.initialize(gameWorld, sceneBundle, exData, exFloatData);
        mBodys[0].m_flags = mBodys[0].m_flags | Body.e_bulletFlag;

        mImageResource = sceneBundle.getDrawUtil().getImageResource(TextureId.ENTITY_BALL);
        mOut = false;
    }

    @Override
    public void step(GameWorld gameWorld, SceneBundle sceneBundle,
                     OrderedSet<GameEntityAddEvent> gameEntityAddEvents, OrderedSet<IGameEntity> removedGameEntities) {
        final Vec2 force = new Vec2();
        this.getLinearVelocity(force);
        float l = force.normalize();
        force.mulLocal(-0.025f * l * l * mBodys[0].getMass());

        mBodys[0].applyForce(force, mBodys[0].getMemberPosition());

        if (mBodys[0].getPosition().y < WORLD_RECT.bottom - CELL_SIZE) {
            mOut = true;
        }
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        final Vec2 position = new Vec2();
        final Vec2 size = new Vec2(2 * mScale, 2 * mScale);
        getPosition(position);
        sceneBundle.getDrawUtil().drawBitmap(gl, mImageResource, 0, 0, position, size, getAngle(), 1, null, AlphaMode.STD);
    }

    public boolean isOut() {
        return mOut;
    }
}
