package net.cattaka.hungrycatball.scene;

import android.graphics.Paint;

import net.cattaka.hungrycatball.core.AbstractUiCallback;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.gl.CtkGL;

import static net.cattaka.hungrycatball.HungryCatBallConstants.STEP_DT;

public class FadeScene implements IScene {
    private IScene mFromScene;
    private IScene mToScene;
    private float mAlpha;
    private Paint mPaint;

    public FadeScene(IScene mFromScene, IScene mToScene) {
        super();
        this.mFromScene = mFromScene;
        this.mToScene = mToScene;
        this.mPaint = new Paint();
    }

    @Override
    public void initialize(SceneBundle sceneBundle) {
        mAlpha = 0f;
        mToScene.initialize(sceneBundle);
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        gl.glColor4f(1, 1, 1, 1);
        mFromScene.draw(gl, sceneBundle);
        gl.glColor4f(1, 1, 1, mAlpha);
        mToScene.draw(gl, sceneBundle);
    }

    @Override
    public void step(SceneBundle sceneBundle) {
        mAlpha += STEP_DT * 2;
        if (mAlpha >= 1f) {
            mAlpha = 1f;
        }
        mPaint.setAlpha((int) (0xFF * mAlpha));
    }

    @Override
    public IScene moveNextScene() {
        if (mAlpha >= 1f) {
            return mToScene;
        } else {
            return this;
        }
    }

    @Override
    public AbstractUiCallback pullUiCallback() {
        return null;
    }
}
