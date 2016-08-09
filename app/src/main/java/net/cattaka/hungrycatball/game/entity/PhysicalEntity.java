package net.cattaka.hungrycatball.game.entity;

import net.cattaka.collections.OrderedSet;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.game.GameEntityAddEvent;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.game.IGameEntity;
import net.cattaka.hungrycatball.gl.CtkGL;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.collision.shapes.ShapeDef;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.contacts.ContactPoint;
import org.jbox2d.dynamics.contacts.ContactResult;

public abstract class PhysicalEntity implements IGameEntity {
    protected Body[] mBodys;
    private int mGivenScore;

    public PhysicalEntity(int bodyNum) {
        this.mBodys = new Body[bodyNum];
    }

    public int getBodyNum() {
        return mBodys.length;
    }

    public Body getBody(int idx) {
        return mBodys[idx];
    }

    public void setBody(int idx, Body body) {
        this.mBodys[idx] = body;
    }

    @Override
    public void initialize(GameWorld gameWorld, SceneBundle sceneBundle, Object exData, float[] exFloatData) {
    }

    @Override
    public void onAdd(GameWorld gameWorld) {
    }

    @Override
    public void onDelete(GameWorld gameWorld) {
    }

    @Override
    public void getPosition(Vec2 dst) {
        dst.set(mBodys[0].getMemberPosition());
    }

    @Override
    public void getLinearVelocity(Vec2 dst) {
        dst.set(mBodys[0].getLinearVelocity());
    }

    @Override
    public void setLinearVelocity(Vec2 src) {
        // FIXME 0だけで大丈夫か？
        mBodys[0].setLinearVelocity(src);
    }

    @Override
    public float getAngle() {
        // FIXME 0だけで大丈夫か？
        return mBodys[0].getAngle();
    }

    @Override
    public void setAngle(float angle) {
        // FIXME 0だけで大丈夫か？
        mBodys[0].setXForm(mBodys[0].getMemberPosition(), angle);
    }

    @Override
    public float getAngularVelocity() {
        return mBodys[0].getAngularVelocity();
    }

    @Override
    public void setAngularVelocity(float angularVelocity) {
        mBodys[0].setAngularVelocity(angularVelocity);
    }

    @Override
    public void preStep(GameWorld gameWorld, SceneBundle sceneBundle,
                        OrderedSet<IGameEntity> addedGameEntities) {
    }

    @Override
    public void postStep(GameWorld gameWorld, SceneBundle sceneBundle,
                         OrderedSet<IGameEntity> removedGameEntities) {
        // TODO Auto-generated method stub

    }

    abstract public BodyDef[] getBodyDefs();

    abstract public ShapeDef[] getShapeDefs();

    @Override
    public void drawDebug(CtkGL gl, SceneBundle sceneBundle) {
        final Vec3 color = new Vec3(1, 1, 0);
        for (Body body : mBodys) {
            Shape shape = body.getShapeList();
            sceneBundle.getDrawUtil().drawShape(gl, body.getXForm(), shape, color);
        }
    }

    @Override
    public boolean isIntersect(Vec2 position) {
        for (Body body : mBodys) {
            Shape shape = body.getShapeList();
            if (shape.testPoint(body.getXForm(), position)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setXForm(Vec2 position, float angle) {
        if (mBodys.length > 0) {
            Vec2 nextPos = new Vec2();
            Vec2 diff = new Vec2();
            float angleDiff = angle - mBodys[0].getAngle();
            if (angleDiff != 0) {    // 回転させる
                Mat22 m = Mat22.createRotationalTransform(angleDiff);
                for (int i = 0; i < mBodys.length; i++) {
                    diff.set(mBodys[i].getMemberPosition());
                    diff.subLocal(mBodys[0].getMemberPosition());
                    Mat22.mulToOut(m, diff, nextPos);
                    nextPos.addLocal(mBodys[0].getMemberPosition());
                    mBodys[i].setXForm(nextPos, mBodys[i].getAngle() + angleDiff);
                }
            }
            {    // 平行移動させる
                diff.set(position);
                diff.subLocal(mBodys[0].getMemberPosition());

                for (int i = 0; i < mBodys.length; i++) {
                    Body body = mBodys[i];
                    nextPos.set(body.getMemberPosition());
                    nextPos.addLocal(diff);
                    body.setXForm(nextPos, mBodys[i].getAngle());
                }
            }
        }
    }

    public int getGivenScore() {
        return mGivenScore;
    }

    public void setGivenScore(int givenScore) {
        this.mGivenScore = givenScore;
    }

    @Override
    public boolean isPhysics() {
        return true;
    }

    @Override
    public boolean isSkipableDraw() {
        return false;
    }

    public boolean isEnableScore() {
        return false;
    }

    public int getFullScore() {
        return 0;
    }

    public void onContactResult(GameWorld gameWorld, OrderedSet<GameEntityAddEvent> gameEntityAddEvents, PhysicalEntity entity, ContactResult point) {
    }

    public void onContactAdd(PhysicalEntity entity, ContactPoint point) {
    }

    public void onContactRemove(PhysicalEntity entity, ContactPoint point) {
    }
}
