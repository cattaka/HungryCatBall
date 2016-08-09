package net.cattaka.hungrycatball.scene;

import net.cattaka.hungrycatball.core.AbstractUiCallback;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.gl.CtkGL;

public interface IScene {
    public void initialize(SceneBundle sceneBundle);

    public void step(SceneBundle sceneBundle);

    public void draw(CtkGL gl, SceneBundle sceneBundle);

    public IScene moveNextScene();

    public AbstractUiCallback pullUiCallback();
}
