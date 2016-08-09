package net.cattaka.hungrycatball.game.entity;

import net.cattaka.collections.OrderedSet;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.game.GameEntityAddEvent;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.game.IGameEntity;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.utils.DrawingUtil.AlphaMode;
import net.cattaka.hungrycatball.utils.ImageResource;

import org.jbox2d.collision.shapes.ShapeDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.contacts.ContactResult;

import static net.cattaka.hungrycatball.HungryCatBallConstants.GAME_SHOCK_MARGIN;

public abstract class BlockEntity extends PhysicalEntity {
    protected ImageResource mImageResource;
    protected Class<? extends BlockEntity> mPieceClass;
    protected int mPieceType = 0;    // 0:Normal, 1:Small
    protected float mMaxHealth;
    protected Vec2 mSize;

    private float mHealth;

    public BlockEntity() {
        super(1);
    }

    @Override
    public abstract BodyDef[] getBodyDefs();

    @Override
    public abstract ShapeDef[] getShapeDefs();

    @Override
    public void initialize(GameWorld gameWorld, SceneBundle sceneBundle, Object exData, float[] exFloatData) {
        super.initialize(gameWorld, sceneBundle, exData, exFloatData);
        initializeEx(gameWorld, sceneBundle, exData);
        mHealth = mMaxHealth;
        if (exData != null && exData instanceof Float) {
            mHealth = (Float) exData;
        }
    }

    public abstract void initializeEx(GameWorld gameWorld, SceneBundle sceneBundle, Object exData);

    @Override
    public void step(GameWorld gameWorld, SceneBundle sceneBundle,
                     OrderedSet<GameEntityAddEvent> gameEntityAddEvents, OrderedSet<IGameEntity> removedGameEntities) {

        if (mBodys[0].m_type == Body.e_staticType && mHealth < mMaxHealth * (2f / 3f)) {
            mBodys[0].setMassFromShapes();
        }
        if (mHealth < 0) {
            if (mPieceClass != null) {
                Vec2 pos = new Vec2();
                Vec2 vel = new Vec2();
                this.getPosition(pos);
                this.getLinearVelocity(vel);
                float angularVelocity = this.getAngularVelocity();

                if (mPieceType == 0) {
                    for (int i = 0; i < 4; i++) {
                        GameEntityAddEvent event = new GameEntityAddEvent();
                        event.setGameEntityClass(mPieceClass);
                        Vec2 tPos = new Vec2();
                        Vec2 tVel = new Vec2();
                        if (i == 0) {
                            tPos.set(-mSize.x / 4, mSize.y / 4);
                            tVel.set(-1, +1);
                        } else if (i == 1) {
                            tPos.set(+mSize.x / 4, mSize.y / 4);
                            tVel.set(+1, +1);
                        } else if (i == 2) {
                            tPos.set(-mSize.x / 4, -mSize.y / 4);
                            tVel.set(-1, -1);
                        } else {
                            tPos.set(+mSize.x / 4, -mSize.y / 4);
                            tVel.set(+1, -1);
                        }
                        tVel.addLocal(-tPos.y * angularVelocity, tPos.x * angularVelocity);
                        mBodys[0].getMemberXForm().R.mulToOut(tPos, tPos);
                        mBodys[0].getMemberXForm().R.mulToOut(tVel, tVel);
                        tPos.addLocal(pos);
                        tVel.addLocal(vel);
                        event.setPosition(tPos);
                        event.setLinearVelocity(tVel);
                        event.setAngularVelocity(angularVelocity);
                        event.setExData(i);
                        gameEntityAddEvents.add(event);
                    }
                } else if (mPieceType == 1) {
                    for (int i = 0; i < 2; i++) {
                        GameEntityAddEvent event = new GameEntityAddEvent();
                        event.setGameEntityClass(mPieceClass);
                        Vec2 tPos = new Vec2();
                        Vec2 tVel = new Vec2();
                        if (i == 0) {
                            tPos.set(0, +mSize.y / 4);
                            tVel.set(0, +1);
                        } else if (i == 1) {
                            tPos.set(0, -mSize.y / 4);
                            tVel.set(0, -1);
                        }
                        tVel.addLocal(-tPos.y * angularVelocity, tPos.x * angularVelocity);
                        mBodys[0].getMemberXForm().R.mulToOut(tPos, tPos);
                        mBodys[0].getMemberXForm().R.mulToOut(tVel, tVel);
                        tPos.addLocal(pos);
                        tVel.addLocal(vel);
                        event.setPosition(tPos);
                        event.setLinearVelocity(tVel);
                        event.setAngularVelocity(angularVelocity);
                        event.setExData(i);
                        gameEntityAddEvents.add(event);
                    }
                }
            }
            removedGameEntities.add(this);
        }
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        final Vec2 position = new Vec2();
        getPosition(position);
        if (mHealth < mMaxHealth * (1f / 3f)) {
            sceneBundle.getDrawUtil().drawBitmap(gl, mImageResource, 0, 2, position, mSize, getAngle(), 1, null, AlphaMode.STD);
        } else if (mHealth < mMaxHealth * (2f / 3f)) {
            sceneBundle.getDrawUtil().drawBitmap(gl, mImageResource, 0, 1, position, mSize, getAngle(), 1, null, AlphaMode.STD);
        } else {
            sceneBundle.getDrawUtil().drawBitmap(gl, mImageResource, 0, 0, position, mSize, getAngle(), 1, null, AlphaMode.STD);
        }
    }

    @Override
    public boolean isEnableScore() {
        return true;
    }

    @Override
    public void onContactResult(GameWorld gameWorld, OrderedSet<GameEntityAddEvent> gameEntityAddEvents, PhysicalEntity entity, ContactResult point) {
        super.onAdd(gameWorld);
        if (point.normalImpulse > GAME_SHOCK_MARGIN) {
            int tm = (int) (point.normalImpulse);
            mHealth -= (int) tm;
        }
    }

    @Override
    public int getFullScore() {
        if (mPieceType == 0) {
            return (int) (mMaxHealth * 2);
        } else if (mPieceType == 1) {
            return (int) (mMaxHealth * 1.5);
        } else {
            return 0;
        }
    }
}
