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
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.collision.shapes.ShapeDef;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.joints.GearJointDef;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import static net.cattaka.hungrycatball.HungryCatBallConstants.CELL_MARGIN;
import static net.cattaka.hungrycatball.HungryCatBallConstants.CELL_SIZE;
import static net.cattaka.hungrycatball.HungryCatBallConstants.GAME_BLOCK_RESTITUTION;

public class DoorEntity extends PhysicalEntity {
    private static final Vec2 HINGE_SIZE = new Vec2(1.0f, 1.0f);
    private static final Vec2 BLOCK_SIZE = new Vec2(4, 0.5f);
    private static final Vec2 LEVER_SIZE = new Vec2(1.0f, 1.0f);
    private static BodyDef[] BODY_DEFS;
    private static ShapeDef[] SHAPE_DEFS;

    static {
        PolygonDef pd = new PolygonDef();
        pd.setAsBox(BLOCK_SIZE.x / 2f - CELL_MARGIN, BLOCK_SIZE.y / 2f - CELL_MARGIN, new Vec2(BLOCK_SIZE.x / 2f, 0), 0);
        pd.density = 2.0f;
        pd.friction = 0.5f;
        pd.restitution = GAME_BLOCK_RESTITUTION;

        CircleDef cd = new CircleDef();
        cd.radius = CELL_SIZE * 0.5f;
        cd.density = 2.0f;
        cd.friction = 1.0f;
        cd.restitution = 1.0f;

        BodyDef bd1 = new BodyDef();
        BodyDef bd2 = new BodyDef();

        BODY_DEFS = new BodyDef[]{
                bd1,
                bd2
        };

        SHAPE_DEFS = new ShapeDef[]{
                cd,
                pd
        };
    }

    private ImageResource mHingeImageResource;
    private ImageResource mBlockImageResource;
    private ImageResource mLeverImageResource;

    private Joint[] mJoints;
    private float mStartAngle = 0;
    private float mLowerAngle = (float) Math.PI / 2;
    private float mUpperAngle = -(float) Math.PI / 2;
    private Vec2 mOffset = new Vec2(0, 5);

    public DoorEntity() {
        super(2);
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
        mLeverImageResource = sceneBundle.getDrawUtil().getImageResource(TextureId.ENTITY_LEVER);

        if (exFloatData != null) {
            float DEG_TO_ANGLE = (float) (Math.PI / 180f);
            mOffset.x = (exFloatData.length > 0) ? exFloatData[0] : 0;
            mOffset.y = (exFloatData.length > 1) ? exFloatData[1] : 5;
            mStartAngle = (exFloatData.length > 2) ? exFloatData[2] * DEG_TO_ANGLE : 0;
            mLowerAngle = (exFloatData.length > 3) ? exFloatData[3] * DEG_TO_ANGLE : -(float) (Math.PI / 2f);
            mUpperAngle = (exFloatData.length > 4) ? exFloatData[4] * DEG_TO_ANGLE : (float) (Math.PI / 2f);
        }

        Mat22 rot = mBodys[0].getXForm().R;
        Vec2 pos = new Vec2();
        {    // ドアの座標をオフセットさせる
            rot.mulToOut(mOffset, pos);
            pos.addLocal(mBodys[0].getMemberPosition());
            mBodys[1].setXForm(pos, mBodys[0].getAngle() + mStartAngle);
        }

        Joint rj = null;
        Joint pj = null;
        Joint gj = null;
        {
            Vec2 dir = new Vec2();
            rot.mulToOut(new Vec2(0, 1), dir);
            PrismaticJointDef pjDef = new PrismaticJointDef();
            pjDef.initialize(gameWorld.getmFixedWorldBody(), mBodys[0], mBodys[0].getMemberPosition(), dir);
            pj = gameWorld.createJoint(pjDef);
        }
        {
            RevoluteJointDef rjDef = new RevoluteJointDef();
            rjDef.initialize(gameWorld.getmFixedWorldBody(), mBodys[1], mBodys[1].getMemberPosition());
            if (mLowerAngle < mUpperAngle) {
                rjDef.lowerAngle = mLowerAngle;
                rjDef.upperAngle = mUpperAngle;
            } else {
                rjDef.lowerAngle = mUpperAngle;
                rjDef.upperAngle = mLowerAngle;
            }
            rjDef.enableLimit = true;
            rj = gameWorld.createJoint(rjDef);
        }
        {
            GearJointDef gjDef = new GearJointDef();
            if (mLowerAngle < mUpperAngle) {
                gjDef.ratio = (float) ((2.0f * Math.PI) / BLOCK_SIZE.length());
            } else {
                gjDef.ratio = -(float) ((2.0f * Math.PI) / BLOCK_SIZE.length());
            }
            gjDef.body1 = mBodys[0];
            gjDef.body2 = mBodys[1];
            gjDef.joint1 = rj;
            gjDef.joint2 = pj;
            gj = gameWorld.createJoint(gjDef);
        }

        mJoints = new Joint[]{pj, rj, gj};
    }

    @Override
    public void step(GameWorld gameWorld, SceneBundle sceneBundle,
                     OrderedSet<GameEntityAddEvent> gameEntityAddEvents, OrderedSet<IGameEntity> removedGameEntities) {
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        sceneBundle.getDrawUtil().drawBitmap(gl, mLeverImageResource, mBodys[0].getMemberPosition(), LEVER_SIZE, mBodys[0].getAngle(), 1, null, AlphaMode.STD);
        sceneBundle.getDrawUtil().drawBitmap(gl, mBlockImageResource, mBodys[1].getMemberWorldCenter(), BLOCK_SIZE, mBodys[1].getAngle(), 1, null, AlphaMode.STD);
        sceneBundle.getDrawUtil().drawBitmap(gl, mHingeImageResource, mBodys[1].getMemberPosition(), HINGE_SIZE, mBodys[1].getAngle(), 1, null, AlphaMode.STD);
    }

    @Override
    public void onDelete(GameWorld gameWorld) {
        super.onDelete(gameWorld);
        for (Joint joint : mJoints) {
            gameWorld.destroyJoint(joint);
        }
    }
}
