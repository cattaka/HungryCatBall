package net.cattaka.hungrycatball.frame;

import net.cattaka.hungrycatball.core.AbstractUiCallback;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.game.GameEntityAddEvent;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.game.IGameEntity;
import net.cattaka.hungrycatball.game.entity.BallEntity;
import net.cattaka.hungrycatball.game.entity.GoalEntity;
import net.cattaka.hungrycatball.game.entity.HardblockEntity;
import net.cattaka.hungrycatball.game.entity.SoftblockEntity;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.io.SceneData;

import org.jbox2d.common.Vec2;

import static net.cattaka.hungrycatball.HungryCatBallConstants.CELL_SIZE;
import static net.cattaka.hungrycatball.HungryCatBallConstants.HEALTH_HARDBLOCK;
import static net.cattaka.hungrycatball.HungryCatBallConstants.HEALTH_SOFTBLOCK;
import static net.cattaka.hungrycatball.HungryCatBallConstants.STEP_DT;
import static net.cattaka.hungrycatball.HungryCatBallConstants.WORLD_RECT;

public class BackgroundFrame implements ISceneFrame {
    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle, GameWorld gameWorld) {
        gameWorld.draw(gl, sceneBundle);
    }

    @Override
    public void initialize(SceneBundle sceneBundle, SceneData sceneData) {
    }

    @Override
    public ISceneFrame moveNextSceneFrame() {
        return this;
    }

    @Override
    public AbstractUiCallback pullUiCallback() {
        return null;
    }

    @Override
    public void step(GameWorld gameWorld, SceneBundle sceneBundle, SceneData sceneData, ISceneFrameListener listener) {
        if (2.0 > (Math.random() / STEP_DT)) {
            GameEntityAddEvent event = new GameEntityAddEvent();
            switch ((int) (Math.random() * 7)) {
                case 0:
                case 1: {
                    float x = (float) (WORLD_RECT.width() * Math.random() - (WORLD_RECT.width() / 2f));
                    float angularVelocity = (float) (Math.PI * 4 * (Math.random() - 0.5));
                    event.setGameEntityClass(SoftblockEntity.class);
                    event.setPosition(new Vec2(x, WORLD_RECT.top + CELL_SIZE * 2));
                    event.setAngularVelocity(angularVelocity);
                    event.setExData((float) (HEALTH_SOFTBLOCK / 2));
                    break;
                }
                case 2:
                case 3: {
                    float x = (float) (WORLD_RECT.width() * Math.random() - (WORLD_RECT.width() / 2f));
                    float angularVelocity = (float) (Math.PI * 4 * (Math.random() - 0.5));
                    event.setGameEntityClass(HardblockEntity.class);
                    event.setPosition(new Vec2(x, WORLD_RECT.top + CELL_SIZE * 2));
                    event.setAngularVelocity(angularVelocity);
                    event.setExData((float) (HEALTH_HARDBLOCK / 2));
                    break;
                }
                case 4: {
                    float x = (float) (WORLD_RECT.width() * Math.random() - (WORLD_RECT.width() / 2f));
                    float angularVelocity = (float) (Math.PI * 4 * (Math.random() - 0.5));
                    event.setGameEntityClass(GoalEntity.class);
                    event.setPosition(new Vec2(x, WORLD_RECT.top + CELL_SIZE * 2));
                    event.setAngularVelocity(angularVelocity);
                    event.setExData((float) (HEALTH_HARDBLOCK / 2));
                    break;
                }
                case 5:
                case 6: {
                    float x = (float) (WORLD_RECT.width() * Math.random() - (WORLD_RECT.width() / 2f));
                    float angularVelocity = (float) (Math.PI * 4 * (Math.random() - 0.5));
                    event.setGameEntityClass(BallEntity.class);
                    event.setPosition(new Vec2(x, WORLD_RECT.bottom - CELL_SIZE * 2));
                    event.setAngularVelocity(angularVelocity);
                    event.setLinearVelocity(new Vec2(x, Math.abs(WORLD_RECT.height())));
                    break;
                }
            }
            gameWorld.addGameEntity(sceneBundle, event, false);
        }
        gameWorld.step(sceneBundle);
        final Vec2 position = new Vec2();
        for (IGameEntity entity : gameWorld.getGameEntities()) {
            entity.getPosition(position);
            if (position.y < WORLD_RECT.bottom - CELL_SIZE * 4) {
                gameWorld.removeGameEntityFromOuter(entity);
            }
        }

    }
}
