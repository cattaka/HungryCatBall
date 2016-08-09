package net.cattaka.hungrycatball.scene;

import net.cattaka.hungrycatball.HungryCatBallConstants;
import net.cattaka.hungrycatball.core.AbstractUiCallback;
import net.cattaka.hungrycatball.core.AbstractUiCallback.UiType;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.core.UserInput.PhysicalButton;
import net.cattaka.hungrycatball.core.UserInput.TouchState;
import net.cattaka.hungrycatball.frame.BackgroundFrame;
import net.cattaka.hungrycatball.frame.ISceneFrameListener;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.ui.UiHowToPlay;
import net.cattaka.hungrycatball.ui.UiLabel;
import net.cattaka.hungrycatball.ui.UiRectButton;
import net.cattaka.hungrycatball.ui.UiSoundButton;
import net.cattaka.hungrycatball.ui.UiView;
import net.cattaka.hungrycatball.utils.DrawingUtil.AlphaMode;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.common.Vec2;

import java.util.ArrayList;
import java.util.List;

public class TitleScene implements IScene, ISceneFrameListener, UiView.OnClickListener {
    private UiLabel mTitleLabel;
    private UiLabel mImageLabel;
    private List<UiView> mViewList;
    private UiHowToPlay mHowToPlay;
    private IScene mNextScene;
    private AbstractUiCallback mUiCallback;
    private GameWorld mGameWorld;
    private BackgroundFrame mBackgroundFrame;
    private float mFrameTime;
    private float mFrameState;

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        mBackgroundFrame.draw(gl, sceneBundle, mGameWorld);

        for (UiView view : mViewList) {
            view.draw(gl, sceneBundle);
        }

        mTitleLabel.draw(gl, sceneBundle);
        mImageLabel.draw(gl, sceneBundle);

        mHowToPlay.draw(gl, sceneBundle);
    }

    @Override
    public void step(SceneBundle sceneBundle) {
        mFrameTime += HungryCatBallConstants.STEP_DT;

        mTitleLabel.step(sceneBundle);
        mImageLabel.step(sceneBundle);
        final Vec2 position = new Vec2(0, 8);
        if (0 < mFrameTime && mFrameTime < 1.5f) {
            if (sceneBundle.getUserInput().currentTouchState == TouchState.PRESSE
                    || sceneBundle.getUserInput().currentMenuState == TouchState.PRESSE
                    || sceneBundle.getUserInput().currentBackState == TouchState.PRESSE
                    ) {
                // タッチで飛ばせるようにする。
                mFrameTime = 1.5f;
            }
            float rate = mFrameTime / 1.5f;
            position.y = 8 + 8 * (float) (Math.cos(Math.PI * rate * 2.5f) / (mFrameTime * 10));
        } else {
            position.y = 8;
        }
        mTitleLabel.setPosition(position);

        if (mFrameState == 0 && mFrameTime >= 1.5f) {
            mFrameState = 1;
            setVisibleStateAll(true);
        }

        if (mHowToPlay.isVisible()) {
            mHowToPlay.step(sceneBundle);
        } else {
            for (UiView view : mViewList) {
                view.step(sceneBundle);
            }
        }

        mBackgroundFrame.step(mGameWorld, sceneBundle, null, this);
    }

    @Override
    public void initialize(SceneBundle sceneBundle) {
        mTitleLabel = UiLabel.createUiPanel(new Vec2(0, 16), new Vec2(12, 3), sceneBundle, TextureId.MSG_TITLE, 0, 0, AlphaMode.ADD);
        mImageLabel = UiLabel.createUiPanel(new Vec2(4, -9), new Vec2(6, 6), sceneBundle, TextureId.IMG_FAILED_CLEARED, 0, 0, AlphaMode.ADD);

        mHowToPlay = UiHowToPlay.createUiPanel(new Vec2(0, 0), new Vec2(12, 12), sceneBundle, AlphaMode.STD);
        mHowToPlay.setVisibleState(false);
        mHowToPlay.setVisiblity(0);

        mViewList = new ArrayList<UiView>();
        UiRectButton howToPlayButton = UiRectButton.createUiPanel(new Vec2(0, 4), new Vec2(8, 2.5f), sceneBundle, "How To Play".toCharArray(), HungryCatBallConstants.CELL_SIZE * 1.2f);
        UiRectButton playButton = UiRectButton.createUiPanel(new Vec2(0, 1), new Vec2(8, 2.5f), sceneBundle, "Play".toCharArray(), HungryCatBallConstants.CELL_SIZE * 1.5f);
        UiRectButton creditsButton = UiRectButton.createUiPanel(new Vec2(0, -2), new Vec2(8, 2.5f), sceneBundle, "Credits".toCharArray(), HungryCatBallConstants.CELL_SIZE * 1.5f);
        UiRectButton quitButton = UiRectButton.createUiPanel(new Vec2(0, -5), new Vec2(8, 2.5f), sceneBundle, "Quit".toCharArray(), HungryCatBallConstants.CELL_SIZE * 1.5f);
        howToPlayButton.setOnClickListener(this, "howToPlay", null);
        playButton.setOnClickListener(this, "play", null);
        creditsButton.setOnClickListener(this, "credits", null);
        quitButton.setOnClickListener(this, "quit", null);
        howToPlayButton.setVisiblity(0);
        playButton.setVisiblity(0);
        creditsButton.setVisiblity(0);
        quitButton.setVisiblity(0);
        howToPlayButton.setVisibleState(false);
        playButton.setVisibleState(false);
        creditsButton.setVisibleState(false);
        quitButton.setVisibleState(false);
        mViewList.add(howToPlayButton);
        mViewList.add(playButton);
        mViewList.add(creditsButton);
        mViewList.add(quitButton);

        quitButton.addBindedButton(PhysicalButton.BACK);

        UiSoundButton soundButton = UiSoundButton.createUiPanel(new Vec2(-5, -10), new Vec2(3, 3), sceneBundle);
        soundButton.setVisiblity(0);
        soundButton.setVisibleState(false);
        mViewList.add(soundButton);

        mGameWorld = new GameWorld();
        mGameWorld.createWorld();
        mGameWorld.setBgImageResource(sceneBundle.getDrawUtil().getImageResource(TextureId.BG_TYPE_1));
        mBackgroundFrame = new BackgroundFrame();
        mBackgroundFrame.initialize(sceneBundle, null);

        mFrameTime = 0;
        mFrameState = 0;
    }

    @Override
    public void onClick(UiView uiView, String action, Object exData) {
        if ("howToPlay".equals(action)) {
            mHowToPlay.setVisibleState(true);
        } else if ("play".equals(action)) {
            setVisibleStateAll(false);
            uiView.setOnVisibleListener(new OnVisibleListenerEx(new CourseSelectScene()));
        } else if ("credits".equals(action)) {
            setVisibleStateAll(false);
            uiView.setOnVisibleListener(new OnVisibleListenerEx(new CreditsScene()));
        } else if ("quit".equals(action)) {
            setVisibleStateAll(false);
            uiView.setOnVisibleListener(new UiView.OnVisibleListener() {
                @Override
                public void onVisible(UiView uiView) {
                }

                @Override
                public void onInvisible(UiView uiView) {
                    mUiCallback = new AbstractUiCallback(UiType.UI_QUIT);
                }
            });
        }
    }

    private void setVisibleStateAll(boolean visibleState) {
        for (UiView view : mViewList) {
            view.setVisibleState(visibleState);
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
        AbstractUiCallback uiCallback = mUiCallback;
        mUiCallback = null;
        return uiCallback;
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
            TitleScene.this.mNextScene = nextScene;
        }
    }
}
