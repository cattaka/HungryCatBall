package net.cattaka.hungrycatball.frame;

import net.cattaka.hungrycatball.core.AbstractUiCallback;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.io.SceneData;

public interface ISceneFrame {
    public void initialize(SceneBundle sceneBundle, SceneData sceneData);

    public void step(GameWorld gameWorld, SceneBundle sceneBundle, SceneData sceneData, ISceneFrameListener listener);

    public void draw(CtkGL gl, SceneBundle sceneBundle, GameWorld gameWorld);

    public ISceneFrame moveNextSceneFrame();

    public AbstractUiCallback pullUiCallback();
}
