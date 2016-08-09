package net.cattaka.hungrycatball.scene;

import net.cattaka.hungrycatball.HungryCatBallConstants;
import net.cattaka.hungrycatball.core.AbstractUiCallback;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.core.UserInput.PhysicalButton;
import net.cattaka.hungrycatball.frame.BackgroundFrame;
import net.cattaka.hungrycatball.frame.ISceneFrameListener;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.ui.UiButton;
import net.cattaka.hungrycatball.ui.UiRectButton;
import net.cattaka.hungrycatball.ui.UiStringLabel;
import net.cattaka.hungrycatball.ui.UiView;
import net.cattaka.hungrycatball.utils.DrawingUtil.Align;
import net.cattaka.hungrycatball.utils.ImageResource;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.common.Vec2;

import java.util.ArrayList;
import java.util.List;

import static net.cattaka.hungrycatball.HungryCatBallConstants.CELL_SIZE;
import static net.cattaka.hungrycatball.HungryCatBallConstants.UI_BACK_BUTTON_POS;

public class CourseSelectScene implements IScene, ISceneFrameListener, UiView.OnClickListener {
    private List<UiView> mViewList;
    private IScene mNextScene;
    private AbstractUiCallback mUiCallback;
    private GameWorld mGameWorld;
    private BackgroundFrame mBackgroundFrame;
    private float mFrameTime;

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        mBackgroundFrame.draw(gl, sceneBundle, mGameWorld);

        for (UiView view : mViewList) {
            view.draw(gl, sceneBundle);
        }
    }

    @Override
    public void step(SceneBundle sceneBundle) {
        mFrameTime += HungryCatBallConstants.STEP_DT;
        for (UiView view : mViewList) {
            view.step(sceneBundle);
        }
        mBackgroundFrame.step(mGameWorld, sceneBundle, null, this);
    }

    @Override
    public void initialize(SceneBundle sceneBundle) {
        mViewList = new ArrayList<UiView>();

        UiStringLabel titleLabel = new UiStringLabel(new Vec2(0, 6), new Vec2(10, 2.5f), "Course select".toCharArray(), 2f, Align.CENTER, null);

        titleLabel.setVisiblity(0);
        titleLabel.setVisibleState(false);
        mViewList.add(titleLabel);

        UiButton backButton = UiButton.createUiPanel(UI_BACK_BUTTON_POS, new Vec2(CELL_SIZE * 3, CELL_SIZE * 3), sceneBundle, ImageResource.COL_UI_BUTTON_LEFT);
        backButton.setVisiblity(0.0f);
        backButton.setOnClickListener(this, "back", null);
        backButton.addBindedButton(PhysicalButton.BACK);
        mViewList.add(backButton);

        UiRectButton cource1Button = UiRectButton.createUiPanel(new Vec2(0, 1), new Vec2(10, 2.5f), sceneBundle, "1:Kitchen".toCharArray(), HungryCatBallConstants.CELL_SIZE * 1.2f);
        UiRectButton cource2Button = UiRectButton.createUiPanel(new Vec2(0, -4), new Vec2(10, 2.5f), sceneBundle, "2:Child's room".toCharArray(), HungryCatBallConstants.CELL_SIZE * 1.2f);
        cource1Button.setOnClickListener(this, "play", Integer.valueOf(1));
        cource2Button.setOnClickListener(this, "play", Integer.valueOf(2));
        cource1Button.setVisiblity(0);
        cource2Button.setVisiblity(0);
        cource1Button.setVisibleState(false);
        cource2Button.setVisibleState(false);
        mViewList.add(cource1Button);
        mViewList.add(cource2Button);

        mGameWorld = new GameWorld();
        mGameWorld.createWorld();
        mGameWorld.setBgImageResource(sceneBundle.getDrawUtil().getImageResource(TextureId.BG_TYPE_1));
        mBackgroundFrame = new BackgroundFrame();
        mBackgroundFrame.initialize(sceneBundle, null);

        mFrameTime = 0;
        setVisibleStateAll(true);
    }

    @Override
    public void onClick(UiView uiView, String action, Object exData) {
        if ("play".equals(action)) {
            int courceNo = (exData != null && exData instanceof Integer) ? (Integer) exData : 1;
            setVisibleStateAll(false);
            uiView.setOnVisibleListener(new OnVisibleListenerEx(new StageSelectScene(courceNo)));
        } else if ("back".equals(action)) {
            setVisibleStateAll(false);
            mViewList.get(0).setOnVisibleListener(new OnVisibleListenerEx(new TitleScene()));
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
            CourseSelectScene.this.mNextScene = nextScene;
        }
    }
}
