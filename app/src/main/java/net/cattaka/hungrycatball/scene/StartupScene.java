package net.cattaka.hungrycatball.scene;

import net.cattaka.hungrycatball.core.AbstractUiCallback;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.core.UserInput.TouchState;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.utils.DrawingUtil.AlphaMode;
import net.cattaka.hungrycatball.utils.ImageResource;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.common.Vec2;

public class StartupScene implements IScene {
    public static final int FADE_IN_STEPS = 30;
    public static final int DISPLAY_STEPS = 60;
    public static final int FADE_OUT_STEPS = 30;

    private AbstractUiCallback mUiCallback;
    private int count;
    private ImageResource mLogoImageResourtce;
    private float mAlpha = 0;
    private boolean mSkipFlag;

    public StartupScene() {
        count = 0;
    }

    @Override
    public void initialize(SceneBundle sceneBundle) {
        mLogoImageResourtce = sceneBundle.getDrawUtil().getImageResource(TextureId.IMG_LOGO);
        mAlpha = 0;
        mSkipFlag = false;
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        final Vec2 position = new Vec2(0, 0);
        final Vec2 size = new Vec2(8, 8);

        sceneBundle.getDrawUtil().drawBitmap(gl, mLogoImageResourtce, position, size, 0, mAlpha, null, AlphaMode.STD);
    }

    @Override
    public void step(SceneBundle sceneBundle) {
        if (count <= FADE_IN_STEPS) {
            mAlpha = (float) count / (float) FADE_IN_STEPS;
        } else if (count <= FADE_IN_STEPS + DISPLAY_STEPS) {
            mAlpha = 1;
        } else if (count <= FADE_IN_STEPS + DISPLAY_STEPS + FADE_OUT_STEPS) {
            mAlpha = 1f - (float) (count - (FADE_IN_STEPS + DISPLAY_STEPS)) / (float) FADE_OUT_STEPS;
        }
        if (sceneBundle.getUserInput().currentTouchState != TouchState.RELEASED) {
            mSkipFlag = true;
        }
        if (mSkipFlag) {
            count += 10;
        } else {
            count++;
        }
    }

    @Override
    public IScene moveNextScene() {
        if (count >= FADE_IN_STEPS + DISPLAY_STEPS + FADE_OUT_STEPS) {
            return new FadeScene(this, new TitleScene());
        } else {
            return this;
        }
    }

    @Override
    public AbstractUiCallback pullUiCallback() {
        AbstractUiCallback result = this.mUiCallback;
        this.mUiCallback = null;
        return result;
    }
}
