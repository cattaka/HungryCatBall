package net.cattaka.hungrycatball.game;

import org.jbox2d.common.Vec2;

public class GameEntityAddEvent {
    private Class<? extends IGameEntity> gameEntityClass;
    private Vec2 position;
    private Vec2 linearVelocity;
    private float angle;
    private float angularVelocity;
    private Object exData;
    private float[] exFloatData;

    public GameEntityAddEvent() {
    }

    public GameEntityAddEvent(GameEntityAddEvent src) {
        gameEntityClass = src.gameEntityClass;
        position = (src.getPosition() != null) ? src.getPosition().clone() : null;
        linearVelocity = (src.getLinearVelocity() != null) ? src.getLinearVelocity() : null;
        angle = src.angle;
        angularVelocity = src.angularVelocity;
        exData = src.getExData();
    }

    public IGameEntity createGameEntity() {
        try {
            return gameEntityClass.newInstance();
        } catch (InstantiationException e) {
            // 乱暴だけどいいや、、
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            // 乱暴だけどいいや、、
            throw new RuntimeException(e);
        }
    }

    public Class<? extends IGameEntity> getGameEntityClass() {
        return gameEntityClass;
    }

    public void setGameEntityClass(Class<? extends IGameEntity> gameEntityClass) {
        this.gameEntityClass = gameEntityClass;
    }

    public Vec2 getPosition() {
        return position;
    }

    public void setPosition(Vec2 position) {
        this.position = position;
    }

    public Vec2 getLinearVelocity() {
        return linearVelocity;
    }

    public void setLinearVelocity(Vec2 linearVelocity) {
        this.linearVelocity = linearVelocity;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public Object getExData() {
        return exData;
    }

    public void setExData(Object exData) {
        this.exData = exData;
    }

    public float[] getExFloatData() {
        return exFloatData;
    }

    public void setExFloatData(float[] exFloatData) {
        this.exFloatData = exFloatData;
    }
}
