package net.cattaka.hungrycatball.frame;

import net.cattaka.hungrycatball.core.AbstractUiCallback;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.game.GameWorld.LevelState;
import net.cattaka.hungrycatball.game.IGameEntity;
import net.cattaka.hungrycatball.game.entity.GoalEntity;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.io.SceneData;
import net.cattaka.hungrycatball.utils.SoundUtil.SoundId;

import org.jbox2d.common.Vec2;

public class PlayFrame implements ISceneFrame {
    private ISceneFrame mNextFrame;

    @Override
    public void initialize(SceneBundle sceneBundle, SceneData sceneData) {
        mNextFrame = null;
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle, GameWorld gameWorld) {
        gameWorld.draw(gl, sceneBundle);
    }

    @Override
    public void step(GameWorld gameWorld, SceneBundle sceneBundle, SceneData sceneData, ISceneFrameListener listener) {
        gameWorld.step(sceneBundle);
        if (gameWorld.getLevelState() == LevelState.LEVEL_FAILED) {
            sceneBundle.getSoundUtil().play(SoundId.LEVEL_FAILED);
            mNextFrame = new LevelEndFrame(gameWorld.getLevelState(), null);
        } else if (gameWorld.getLevelState() == LevelState.LEVEL_CLEARED) {
            Vec2 crossPosition = null;
            for (IGameEntity entity : gameWorld.getGameEntities()) {
                if (entity instanceof GoalEntity) {
                    crossPosition = new Vec2();
                    entity.getPosition(crossPosition);
                    break;
                }
            }
            sceneBundle.getSoundUtil().play(SoundId.LEVEL_SUCCEED);
            mNextFrame = new LevelEndFrame(gameWorld.getLevelState(), crossPosition);
        }
    }

    @Override
    public ISceneFrame moveNextSceneFrame() {
        if (mNextFrame != null) {
            return mNextFrame;
        } else {
            return this;
        }
    }

    @Override
    public AbstractUiCallback pullUiCallback() {
        return null;
    }
}
