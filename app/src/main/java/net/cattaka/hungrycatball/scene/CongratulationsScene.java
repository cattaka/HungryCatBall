package net.cattaka.hungrycatball.scene;

import net.cattaka.hungrycatball.core.AbstractUiCallback;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.frame.BackgroundFrame;
import net.cattaka.hungrycatball.frame.ISceneFrameListener;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.ui.UiLabel;
import net.cattaka.hungrycatball.ui.UiView;
import net.cattaka.hungrycatball.utils.DrawingUtil.AlphaMode;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.common.Vec2;

public class CongratulationsScene implements IScene, ISceneFrameListener, UiView.OnClickListener {
    private UiLabel mCongratsLabel;
    private IScene mNextScene;
    private GameWorld mGameWorld;
    private BackgroundFrame mBackgroundFrame;

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        mBackgroundFrame.draw(gl, sceneBundle, mGameWorld);
        mCongratsLabel.draw(gl, sceneBundle);
    }

    @Override
    public void step(SceneBundle sceneBundle) {
        mCongratsLabel.step(sceneBundle);

        mBackgroundFrame.step(mGameWorld, sceneBundle, null, this);
    }

    @Override
    public void initialize(SceneBundle sceneBundle) {
        mCongratsLabel = UiLabel.createUiPanel(new Vec2(0, 0), new Vec2(12, 12), sceneBundle, TextureId.IMG_CONGRATULATIONS, 0, 0, AlphaMode.ADD);
        mCongratsLabel.setVisiblity(0);
        mCongratsLabel.setOnClickListener(this, "close", null);

        mGameWorld = new GameWorld();
        mGameWorld.createWorld();
        mGameWorld.setBgImageResource(sceneBundle.getDrawUtil().getImageResource(TextureId.BG_TYPE_1));
        mBackgroundFrame = new BackgroundFrame();
        mBackgroundFrame.initialize(sceneBundle, null);
    }

    @Override
    public void onClick(UiView uiView, String action, Object exData) {
        if ("close".equals(action)) {
            mCongratsLabel.setVisibleState(false);
            mCongratsLabel.setOnVisibleListener(new OnVisibleListenerEx(new TitleScene()));
        }
    }

    @Override
    public IScene moveNextScene() {
        if (mNextScene != null) {
            return new FadeScene(this, mNextScene);
        } else {
            return this;
        }
    }

    @Override
    public AbstractUiCallback pullUiCallback() {
        return null;
    }

    @Override
    public void onAction(String action, Object exData) {
        // none
    }

    class OnVisibleListenerEx implements UiView.OnVisibleListener {
        private IScene nextScene;

        public OnVisibleListenerEx(IScene nextScene) {
            this.nextScene = nextScene;
        }

        @Override
        public void onVisible(UiView uiView) {
        }

        @Override
        public void onInvisible(UiView uiView) {
            CongratulationsScene.this.mNextScene = nextScene;
        }
    }
}
