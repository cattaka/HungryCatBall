package net.cattaka.hungrycatball.frame;

import net.cattaka.hungrycatball.HungryCatBallConstants;
import net.cattaka.hungrycatball.core.AbstractUiCallback;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.core.UserInput.TouchState;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.io.SceneData;
import net.cattaka.hungrycatball.utils.DrawingUtil.AlphaMode;
import net.cattaka.hungrycatball.utils.ImageResource;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.common.Vec2;

public class ReadyFrame implements ISceneFrame {
    private ImageResource mImageResource;
    private ISceneFrame mNextSceneFrame;
    private float frameTime = 0;

    @Override
    public void initialize(SceneBundle sceneBundle, SceneData sceneData) {
        mImageResource = sceneBundle.getDrawUtil().getImageResource(TextureId.MSG_READY);
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle, GameWorld gameWorld) {
        gameWorld.draw(gl, sceneBundle);
        final Vec2 position = new Vec2(0, 0);
        final Vec2 size = new Vec2(8, 4);

        float alpha = 0.5f - ((float) Math.cos(frameTime * 4 * Math.PI) / 2f);
        sceneBundle.getDrawUtil().drawBitmap(gl, mImageResource, position, size, 0, alpha, null, AlphaMode.STD);
    }

    @Override
    public void step(GameWorld gameWorld, SceneBundle sceneBundle, SceneData sceneData, ISceneFrameListener listener) {
        if (!sceneBundle.getUserInput().currentTouchStateConsumed
                && sceneBundle.getUserInput().currentTouchState == TouchState.PRESSE) {
            sceneBundle.getUserInput().currentTouchStateConsumed = true;
            mNextSceneFrame = new PlayFrame();
        }
        frameTime = (frameTime + HungryCatBallConstants.STEP_DT) % 1.0f;
    }

    @Override
    public ISceneFrame moveNextSceneFrame() {
        if (mNextSceneFrame != null) {
            return mNextSceneFrame;
        } else {
            return this;
        }
    }

    @Override
    public AbstractUiCallback pullUiCallback() {
        return null;
    }
}
