package net.cattaka.hungrycatball.game.entity;

import android.graphics.Paint;

import net.cattaka.collections.OrderedSet;
import net.cattaka.hungrycatball.HungryCatBallConstants;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.game.GameEntityAddEvent;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.game.IGameEntity;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.utils.DrawingUtil.Align;

import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;

public class ScoreEntity implements IGameEntity {
    private Vec2 position = new Vec2();
    private float angle;
    private float lifeTime = HungryCatBallConstants.DRAW_SCORE_LIFE_TIME;
    private int mScoreDigit = 1;
    private Paint mPaint;

    private ExData mExData;

    public ScoreEntity() {
        mPaint = new Paint();
        mPaint.setTextSize(1);
        mPaint.setColor(0xFFFF0000);
    }

    public static GameEntityAddEvent createGameEntityAddEvent(Vec2 position, int score, float scale) {
        ExData exData = new ExData();
        exData.score = score;
        exData.scale = scale;

        GameEntityAddEvent event = new GameEntityAddEvent();
        event.setGameEntityClass(ScoreEntity.class);
        event.setPosition(position);
        event.setExData(exData);
        return event;
    }

    @Override
    public void initialize(GameWorld gameWorld, SceneBundle sceneBundle, Object exData, float[] exFloatData) {
        mExData = (ExData) exData;
        int t = mExData.score;
        mScoreDigit = 0;
        do {
            t /= 10;
            mScoreDigit++;
        } while (t > 0);
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        final Vec2 position = new Vec2();
        final Vec3 color = new Vec3(1, 1, 0);
        getPosition(position);

        float alpha = lifeTime / HungryCatBallConstants.DRAW_SCORE_LIFE_TIME;
        sceneBundle.getDrawUtil().drawNumber(gl, mExData.score, mScoreDigit, position, mExData.scale, Align.CENTER, color, alpha);
    }

    @Override
    public void drawDebug(CtkGL gl, SceneBundle sceneBundle) {
        final Vec3 color = new Vec3(1, 1, 0);
        sceneBundle.getDrawUtil().drawCicle(gl, this.position.x, this.position.y, 0.1f, color);
    }

    @Override
    public float getAngle() {
        return this.angle;
    }

    @Override
    public void setAngle(float angle) {
    }

    @Override
    public void getLinearVelocity(Vec2 dst) {
        dst.set(0, 0);
    }

    @Override
    public void setLinearVelocity(Vec2 src) {
    }

    @Override
    public void getPosition(Vec2 dst) {
        dst.set(this.position);
    }

    @Override
    public float getAngularVelocity() {
        return 0;
    }

    @Override
    public void setAngularVelocity(float angularVelocity) {
    }

    @Override
    public boolean isIntersect(Vec2 position) {
        return false;
    }

    @Override
    public boolean isSkipableDraw() {
        return true;
    }

    @Override
    public boolean isPhysics() {
        return false;
    }

    @Override
    public void setXForm(Vec2 position, float angle) {
        this.position.set(position);
        this.angle = angle;
    }

    @Override
    public void step(GameWorld gameWorld, SceneBundle sceneBundle, OrderedSet<GameEntityAddEvent> gameEntityAddEvents, OrderedSet<IGameEntity> removedGameEntities) {
        lifeTime -= HungryCatBallConstants.STEP_DT;
        if (lifeTime <= 0) {
            removedGameEntities.add(this);
        }
    }

    @Override
    public void preStep(GameWorld gameWorld, SceneBundle sceneBundle,
                        OrderedSet<IGameEntity> addedGameEntities) {
    }

    @Override
    public void postStep(GameWorld gameWorld, SceneBundle sceneBundle,
                         OrderedSet<IGameEntity> removedGameEntities) {
    }

    @Override
    public void onAdd(GameWorld gameWorld) {
    }

    @Override
    public void onDelete(GameWorld gameWorld) {
    }

    public static class ExData {
        private int score = 0;
        private float scale = 1;
    }
}
