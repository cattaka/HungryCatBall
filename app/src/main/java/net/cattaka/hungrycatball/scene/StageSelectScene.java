package net.cattaka.hungrycatball.scene;

import net.cattaka.hungrycatball.HungryCatBallConstants;
import net.cattaka.hungrycatball.core.AbstractUiCallback;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.core.UserInput.PhysicalButton;
import net.cattaka.hungrycatball.frame.BackgroundFrame;
import net.cattaka.hungrycatball.frame.ISceneFrameListener;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.stage.StageInfo;
import net.cattaka.hungrycatball.stage.StageInfoKey;
import net.cattaka.hungrycatball.ui.UiButton;
import net.cattaka.hungrycatball.ui.UiPanel;
import net.cattaka.hungrycatball.ui.UiStageButton;
import net.cattaka.hungrycatball.ui.UiView;
import net.cattaka.hungrycatball.utils.ImageResource;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.common.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.cattaka.hungrycatball.HungryCatBallConstants.CELL_SIZE;
import static net.cattaka.hungrycatball.HungryCatBallConstants.UI_BACK_BUTTON_POS;
import static net.cattaka.hungrycatball.utils.ImageResource.COL_UI_BUTTON_EDIT;

public class StageSelectScene implements IScene, UiView.OnClickListener, ISceneFrameListener {
    private static final int COURSE_NUM = 3;
    private static final float ANIM_STEP = 0.1f;
    private List<UiPanel> mMenuPanelList;
    private UiButton mBackButton;
    private UiButton mLeftButton;
    private UiButton mRightButton;
    private UiButton mEditButton;
    private float currentPosition = 0;
    private float offsetPosition = 0;
    private StageInfo mNextStageInfo;
    private int mCourseNo = 1;
    private IScene mNextScene;
    private GameWorld mGameWorld;
    private BackgroundFrame mBackgroundFrame;
    public StageSelectScene(int courceNo) {
        mCourseNo = courceNo;
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        mBackgroundFrame.draw(gl, sceneBundle, mGameWorld);

        mBackButton.draw(gl, sceneBundle);
        mLeftButton.draw(gl, sceneBundle);
        mRightButton.draw(gl, sceneBundle);
        if (sceneBundle.isDebugMode()) {
            mEditButton.draw(gl, sceneBundle);
        }
        for (UiPanel menuPanel : mMenuPanelList) {
            menuPanel.draw(gl, sceneBundle);
        }
    }

    @Override
    public void step(SceneBundle sceneBundle) {
        mBackButton.step(sceneBundle);
        mLeftButton.step(sceneBundle);
        mRightButton.step(sceneBundle);
        if (sceneBundle.isDebugMode()) {
            mEditButton.step(sceneBundle);
        }

        // TODO Auto-generated method stub
        if (offsetPosition < currentPosition) {
            offsetPosition += ANIM_STEP;
            if (offsetPosition > currentPosition) {
                offsetPosition = currentPosition;
            }
        } else if (offsetPosition > currentPosition) {
            offsetPosition -= ANIM_STEP;
            if (offsetPosition < currentPosition) {
                offsetPosition = currentPosition;
            }
        }

        final Vec2 pos = new Vec2();
        float w = HungryCatBallConstants.WORLD_RECT.width();
        for (int i = 0; i < mMenuPanelList.size(); i++) {
            UiPanel menuPanel = mMenuPanelList.get(i);
            pos.set(w * i - w * offsetPosition, 0);
            menuPanel.setPosition(pos);
            menuPanel.step(sceneBundle);
        }

        if (mNextStageInfo != null) {
            if (mNextStageInfo.isUnlocked() || sceneBundle.isDebugMode()) {
                PlayScene nextScene = new PlayScene();
                nextScene.initialize(sceneBundle);
                if (nextScene.loadMapFile(sceneBundle, mNextStageInfo)) {
                    // お行儀が悪いが、１つめのパネルのイベントに登録する
                    setVisibleStateAll(false);
                    mMenuPanelList.get(0).setOnVisibleListener(new OnVisibleListenerEx(nextScene));
                }
            }
            mNextStageInfo = null;
        }

        mBackgroundFrame.step(mGameWorld, sceneBundle, null, this);
    }

    private void setVisibleStateAll(boolean visibleState) {
        for (UiPanel uiPanel : mMenuPanelList) {
            uiPanel.setVisibleState(visibleState);
        }
        mBackButton.setVisibleState(visibleState);
        mLeftButton.setVisibleState(visibleState);
        mRightButton.setVisibleState(visibleState);
        mEditButton.setVisibleState(visibleState);
    }

    @Override
    public void initialize(SceneBundle sceneBundle) {
        Map<StageInfoKey, StageInfo> stageInfoMap = sceneBundle.getDbHelper().findAllMap(mCourseNo);
        // 初回の場合はアンロックされたステージがないのでダミーを入れる
        if (stageInfoMap.size() == 0) {
            StageInfo stageInfo = new StageInfo(mCourseNo, 1, 0, true);
            stageInfoMap.put(stageInfo, stageInfo);
        }

        mMenuPanelList = new ArrayList<UiPanel>();
        float x = 0;
        int stageNo = 1;
        for (int i = 0; i < COURSE_NUM; i++) {
            UiPanel menuPanel = UiPanel.createUiPanel(new Vec2(x, 0), new Vec2(13, 21), sceneBundle);
            menuPanel.setEnableDraw(false);
            menuPanel.setVisibleState(true);
            menuPanel.setVisiblity(0.0f);
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 3; c++) {
                    String action = "start";
                    StageInfo stageInfo = stageInfoMap.get(new StageInfoKey(mCourseNo, stageNo));
                    if (stageInfo == null) {
                        stageInfo = new StageInfo(mCourseNo, stageNo);
                    }
                    UiStageButton stageButton = UiStageButton.createUiPanel(new Vec2(CELL_SIZE * 4 * (c - 1), CELL_SIZE * 4 * -(r - 1) + CELL_SIZE * 2), new Vec2(CELL_SIZE * 3, CELL_SIZE * 3), sceneBundle, stageInfo);
                    stageButton.setOnClickListener(this, action, stageInfo);
                    menuPanel.addChild(stageButton);
                    stageNo++;
                }
            }

            mMenuPanelList.add(menuPanel);
            x += HungryCatBallConstants.WORLD_RECT.width();
        }
        mBackButton = UiButton.createUiPanel(UI_BACK_BUTTON_POS, new Vec2(CELL_SIZE * 3, CELL_SIZE * 3), sceneBundle, ImageResource.COL_UI_BUTTON_LEFT);
        mBackButton.setVisiblity(0.0f);
        mBackButton.setOnClickListener(this, "back", null);
        mLeftButton = UiButton.createUiPanel(new Vec2(CELL_SIZE * 4 * -1, CELL_SIZE * 4 * -2 - CELL_SIZE * 2), new Vec2(CELL_SIZE * 3, CELL_SIZE * 3), sceneBundle, ImageResource.COL_UI_BUTTON_LEFT);
        mLeftButton.setVisiblity(0.0f);
        mLeftButton.setOnClickListener(this, "left", null);
        mRightButton = UiButton.createUiPanel(new Vec2(CELL_SIZE * 4 * 1, CELL_SIZE * 4 * -2 - CELL_SIZE * 2), new Vec2(CELL_SIZE * 3, CELL_SIZE * 3), sceneBundle, ImageResource.COL_UI_BUTTON_RIGHT);
        mRightButton.setVisiblity(0.0f);
        mRightButton.setOnClickListener(this, "right", null);
        mEditButton = UiButton.createUiPanel(new Vec2(CELL_SIZE * 4, CELL_SIZE * 8 + CELL_SIZE * 2), new Vec2(CELL_SIZE * 3, CELL_SIZE * 3), sceneBundle, COL_UI_BUTTON_EDIT);
        mEditButton.setVisiblity(0.0f);
        mEditButton.setOnClickListener(this, "edit", null);

        mBackButton.addBindedButton(PhysicalButton.BACK);

        mGameWorld = new GameWorld();
        mGameWorld.createWorld();
        mGameWorld.setBgImageResource(sceneBundle.getDrawUtil().getImageResource(TextureId.BG_TYPE_1));
        mBackgroundFrame = new BackgroundFrame();
        mBackgroundFrame.initialize(sceneBundle, null);

        updateLeftRightButton();
    }

    @Override
    public void onClick(UiView uiView, String action, Object exData) {
        if ("left".equals(action)) {
            currentPosition -= 1f;
            if (currentPosition <= 0) {
                currentPosition = 0;
            }
            updateLeftRightButton();
        } else if ("right".equals(action)) {
            currentPosition += 1f;
            if (currentPosition >= COURSE_NUM - 1) {
                currentPosition = COURSE_NUM - 1;
            }
            updateLeftRightButton();
        } else if ("start".equals(action)) {
            if (exData instanceof StageInfo) {
                mNextStageInfo = (StageInfo) exData;
            }
        } else if ("edit".equals(action)) {
            setVisibleStateAll(false);
            mMenuPanelList.get(0).setOnVisibleListener(new OnVisibleListenerEx(new MapEditorScene()));
        } else if ("back".equals(action)) {
            setVisibleStateAll(false);
            mMenuPanelList.get(0).setOnVisibleListener(new OnVisibleListenerEx(new CourseSelectScene()));
        }
    }

    private void updateLeftRightButton() {
        mLeftButton.setVisibleState(currentPosition > 0);
        mRightButton.setVisibleState(currentPosition < COURSE_NUM - 1);
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onAction(String action, Object exData) {
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
            StageSelectScene.this.mNextScene = nextScene;
        }
    }
}
