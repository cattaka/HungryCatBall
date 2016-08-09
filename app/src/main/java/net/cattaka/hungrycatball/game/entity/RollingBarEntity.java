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

import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.collision.shapes.ShapeDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import static net.cattaka.hungrycatball.HungryCatBallConstants.CELL_MARGIN;
import static net.cattaka.hungrycatball.HungryCatBallConstants.GAME_BLOCK_RESTITUTION;

public class RollingBarEntity extends PhysicalEntity {
    private static final Vec2 HINGE_SIZE = new Vec2(1.0f, 1.0f);
    private static final Vec2 BLOCK_SIZE = new Vec2(4, 0.5f);
    private static BodyDef[] BODY_DEFS;
    private static ShapeDef[] SHAPE_DEFS;

    static {
        PolygonDef pd = new PolygonDef();
        pd.setAsBox(BLOCK_SIZE.x / 2f - CELL_MARGIN, BLOCK_SIZE.y / 2f - CELL_MARGIN);
        pd.density = 2.0f;
        pd.friction = 0.5f;
        pd.restitution = GAME_BLOCK_RESTITUTION;

        BodyDef bd1 = new BodyDef();

        BODY_DEFS = new BodyDef[]{
                bd1,
        };

        SHAPE_DEFS = new ShapeDef[]{
                pd,
        };
    }

    private ImageResource mHingeImageResource;
    private ImageResource mBlockImageResource;

    private Joint mJoint;
    private float mAngularVelocity = (float) Math.PI / 2;

    public RollingBarEntity() {
        super(1);
    }

    @Override
    public BodyDef[] getBodyDefs() {
        return BODY_DEFS;
    }

    @Override
    public ShapeDef[] getShapeDefs() {
        return SHAPE_DEFS;
    }

    @Override
    public void initialize(GameWorld gameWorld, SceneBundle sceneBundle,
                           Object exData, float[] exFloatData) {
        super.initialize(gameWorld, sceneBundle, exData, exFloatData);
        mHingeImageResource = sceneBundle.getDrawUtil().getImageResource(TextureId.ENTITY_HINGE);
        mBlockImageResource = sceneBundle.getDrawUtil().getImageResource(TextureId.ENTITY_RIGIDBLOCK_THIN);

        if (exFloatData != null) {
            float DEG_TO_ANGLE = (float) (Math.PI / 180f);
            mAngularVelocity = (exFloatData.length > 0) ? exFloatData[0] * DEG_TO_ANGLE : mAngularVelocity;
        }

        {
            RevoluteJointDef rjDef = new RevoluteJointDef();
            rjDef.initialize(gameWorld.getmFixedWorldBody(), mBodys[0], mBodys[0].getMemberPosition());
            rjDef.motorSpeed = mAngularVelocity;
            rjDef.maxMotorTorque = mBodys[0].getMass() * 1000;
            rjDef.enableMotor = true;
            mJoint = gameWorld.createJoint(rjDef);
        }
    }

    @Override
    public void step(GameWorld gameWorld, SceneBundle sceneBundle,
                     OrderedSet<GameEntityAddEvent> gameEntityAddEvents, OrderedSet<IGameEntity> removedGameEntities) {
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        sceneBundle.getDrawUtil().drawBitmap(gl, mBlockImageResource, mBodys[0].getMemberWorldCenter(), BLOCK_SIZE, mBodys[0].getAngle(), 1, null, AlphaMode.STD);
        sceneBundle.getDrawUtil().drawBitmap(gl, mHingeImageResource, mBodys[0].getMemberPosition(), HINGE_SIZE, mBodys[0].getAngle(), 1, null, AlphaMode.STD);
    }

    @Override
    public void onDelete(GameWorld gameWorld) {
        super.onDelete(gameWorld);
        gameWorld.destroyJoint(mJoint);
    }
}
