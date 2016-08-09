package net.cattaka.hungrycatball.game;

import net.cattaka.collections.OrderedSet;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.gl.CtkGL;

import org.jbox2d.common.Vec2;

public interface IGameEntity {
    public void initialize(GameWorld gameWorld, SceneBundle sceneBundle, Object exData, float[] exFloatData);

    public void onAdd(GameWorld gameWorld);

    public void onDelete(GameWorld gameWorld);

    public void getPosition(Vec2 dst);

    public void getLinearVelocity(Vec2 dst);

    public float getAngle();

    public void setAngle(float angle);

    public float getAngularVelocity();

    public void setAngularVelocity(float angularVelocity);

    public void preStep(GameWorld gameWorld, SceneBundle sceneBundle, OrderedSet<IGameEntity> addedGameEntities);

    public void step(GameWorld gameWorld, SceneBundle sceneBundle, OrderedSet<GameEntityAddEvent> gameEntityAddEvents, OrderedSet<IGameEntity> removedGameEntities);

    public void postStep(GameWorld gameWorld, SceneBundle sceneBundle, OrderedSet<IGameEntity> removedGameEntities);

    public void draw(CtkGL gl, SceneBundle sceneBundle);

    public void drawDebug(CtkGL gl, SceneBundle sceneBundle);

    public boolean isIntersect(Vec2 position);

    public void setXForm(final Vec2 position, final float angle);

    public void setLinearVelocity(Vec2 src);

    public boolean isPhysics();

    public boolean isSkipableDraw();
}
