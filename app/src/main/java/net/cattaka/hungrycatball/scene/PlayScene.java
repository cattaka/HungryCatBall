package net.cattaka.hungrycatball.scene;

import net.cattaka.hungrycatball.core.AbstractUiCallback;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.core.UserInput.PhysicalButton;
import net.cattaka.hungrycatball.core.UserInput.TouchState;
import net.cattaka.hungrycatball.frame.ISceneFrame;
import net.cattaka.hungrycatball.frame.ISceneFrameListener;
import net.cattaka.hungrycatball.frame.ReadyFrame;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.io.SceneData;
import net.cattaka.hungrycatball.stage.StageInfo;
import net.cattaka.hungrycatball.ui.UiButton;
import net.cattaka.hungrycatball.ui.UiPanel;
import net.cattaka.hungrycatball.ui.UiSoundButton;
import net.cattaka.hungrycatball.ui.UiView;
import net.cattaka.hungrycatball.utils.DrawingUtil.Align;

import org.jbox2d.common.Vec2;

import static net.cattaka.hungrycatball.HungryCatBallConstants.CELL_SIZE;
import static net.cattaka.hungrycatball.HungryCatBallConstants.UI_BACK_BUTTON_POS;
import static net.cattaka.hungrycatball.HungryCatBallConstants.WORLD_RECT;
import static net.cattaka.hungrycatball.utils.ImageResource.COL_UI_BUTTON_EDIT;
import static net.cattaka.hungrycatball.utils.ImageResource.COL_UI_BUTTON_PAUSE;
import static net.cattaka.hungrycatball.utils.ImageResource.COL_UI_BUTTON_PLAY;
import static net.cattaka.hungrycatball.utils.ImageResource.COL_UI_BUTTON_RESTART;
import static net.cattaka.hungrycatball.utils.ImageResource.COL_UI_BUTTON_STAGE;

public class PlayScene implements IScene, UiView.OnClickListener, ISceneFrameListener {
    private ISceneFrame mSceneFrame;
    private GameWorld mGameWorld;
    private AbstractUiCallback mUiCallback;
    private UiButton mMenuButton;
    private UiPanel mMenuPanel;
    private SceneData mSceneData;

    private char[] levelChars;

    private IScene mNextScene;
    private boolean mCommandRetry = false;
    private boolean mCommandStageSelect = false;
    private boolean mCommandEdit = false;
    private boolean mCommandNextStage = false;

    public PlayScene() {
        mGameWorld = new GameWorld();
        mGameWorld.setEnableSound(true);
        mGameWorld.createWorld();
    }

    @Override
    public void initialize(SceneBundle sceneBundle) {
        mSceneFrame = new ReadyFrame();
        mSceneFrame.initialize(sceneBundle, mSceneData);

        mMenuButton = UiButton.createUiPanel(UI_BACK_BUTTON_POS, new Vec2(CELL_SIZE * 3, CELL_SIZE * 3), sceneBundle, COL_UI_BUTTON_PAUSE);
        mMenuButton.setOnClickListener(this, "menu", null);

        mMenuPanel = UiPanel.createUiPanel(new Vec2(), new Vec2(13, 8), sceneBundle);
        mMenuPanel.setVisibleState(false);
        mMenuPanel.setVisiblity(0.0f);
        UiButton exitButton = UiButton.createUiPanel(new Vec2(-4, 0), new Vec2(3, 3), sceneBundle, COL_UI_BUTTON_STAGE);
        exitButton.setOnClickListener(this, "stageSelect", null);
        mMenuPanel.addChild(exitButton);
        UiButton retryButton = UiButton.createUiPanel(new Vec2(0, -2), new Vec2(3, 3), sceneBundle, COL_UI_BUTTON_RESTART);
        retryButton.setOnClickListener(this, "retry", null);
        mMenuPanel.addChild(retryButton);
        UiButton playButton = UiButton.createUiPanel(new Vec2(4, 0), new Vec2(3, 3), sceneBundle, COL_UI_BUTTON_PLAY);
        playButton.setOnClickListener(this, "menu", null);
        mMenuPanel.addChild(playButton);
        if (sceneBundle.isDebugMode()) {
            UiButton editButton = UiButton.createUiPanel(new Vec2(4, 4), new Vec2(3, 3), sceneBundle, COL_UI_BUTTON_EDIT);
            editButton.setOnClickListener(this, "edit", null);
            mMenuPanel.addChild(editButton);
        }
        UiSoundButton soundButton = UiSoundButton.createUiPanel(new Vec2(0, 2), new Vec2(3, 3), sceneBundle);
        mMenuPanel.addChild(soundButton);

        mMenuButton.addBindedButton(PhysicalButton.MENU);
        mMenuButton.addBindedButton(PhysicalButton.BACK);
        exitButton.addBindedButton(PhysicalButton.BACK);
    }

    public boolean loadMapFile(SceneBundle sceneBundle, StageInfo stageInfo) {
        SceneData sceneData = sceneBundle.getStageManager().loadSceneData(sceneBundle, stageInfo);
        if (sceneData != null) {
            loadMapData(sceneBundle, sceneData);
        }
        return (sceneData != null);
    }

    public void loadMapData(SceneBundle sceneBundle, SceneData sceneData) {
        mSceneData = sceneData;
        mGameWorld.loadMapData(sceneBundle, sceneData);
        if (sceneData.getStageInfo() != null) {
            sceneData.getStageInfo();
            levelChars = String.format("%d-%d", sceneData.getStageInfo().getCourse(), sceneData.getStageInfo().getStageNo()).toCharArray();
        } else {
            levelChars = null;
        }
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        mSceneFrame.draw(gl, sceneBundle, mGameWorld);

        mMenuButton.draw(gl, sceneBundle);
        mMenuPanel.draw(gl, sceneBundle);

        // スコアの表示
        {
            final Vec2 posLabel = new Vec2(WORLD_RECT.right - CELL_SIZE * 7.0f, WORLD_RECT.top - CELL_SIZE * 1.5f);
            final Vec2 posScore = new Vec2(WORLD_RECT.right - CELL_SIZE * 1.5f, WORLD_RECT.top - CELL_SIZE * 1.5f);
            final char[] labelChars = "Score".toCharArray();
            sceneBundle.getDrawUtil().drawChars(gl, labelChars, posLabel, CELL_SIZE, Align.LEFT, null, 1f);
            sceneBundle.getDrawUtil().drawNumber(gl, mGameWorld.getScore(), 0, posScore, CELL_SIZE, Align.RIGHT, null, 1f);
        }
        // 時間の表示
        {
            final Vec2 posLabel = new Vec2(WORLD_RECT.right - CELL_SIZE * 7.0f, WORLD_RECT.top - CELL_SIZE * 0.5f);
            final Vec2 posTime = new Vec2(WORLD_RECT.right - CELL_SIZE * 1.5f, WORLD_RECT.top - CELL_SIZE * 0.5f);
            final char[] labelChars = "Time".toCharArray();
            final char[] cs = new char[5];
            mGameWorld.getPlayTimeAsCharArray(cs);
            sceneBundle.getDrawUtil().drawChars(gl, labelChars, posLabel, CELL_SIZE, Align.LEFT, null, 1f);
            sceneBundle.getDrawUtil().drawChars(gl, cs, posTime, CELL_SIZE, Align.RIGHT, null, 1f);
        }
        // ステージのラベルを表示
        if (levelChars != null) {
            final Vec2 posLabel = new Vec2(WORLD_RECT.left + CELL_SIZE * 1.5f, WORLD_RECT.top - CELL_SIZE * 0.5f);
            sceneBundle.getDrawUtil().drawChars(gl, levelChars, posLabel, CELL_SIZE, Align.LEFT, null, 1f);
        }
    }

    @Override
    public void step(SceneBundle sceneBundle) {
        if (sceneBundle.getUserInput().currentTouchState == TouchState.PRESSE) {
            // TODO
        }
        if (mMenuPanel.isVisible()) {
            mMenuPanel.step(sceneBundle);
        } else {
            mSceneFrame.step(mGameWorld, sceneBundle, mSceneData, this);
        }
        mMenuButton.step(sceneBundle);

        // Uiからのコマンドを実行
        if (mCommandRetry) {
            mCommandRetry = false;
            loadMapData(sceneBundle, mSceneData);

            mSceneFrame = new ReadyFrame();
            mSceneFrame.initialize(sceneBundle, mSceneData);
        } else if (mCommandEdit) {
            MapEditorScene scene = new MapEditorScene();
            scene.restore(mSceneData);
            mNextScene = new FadeScene(this, scene);
        } else if (mCommandStageSelect) {
            int courceNo = (mSceneData.getStageInfo() != null) ? mSceneData.getStageInfo().getCourse() : 1;
            mNextScene = new FadeScene(this, new StageSelectScene(courceNo));
        } else if (mCommandNextStage) {
            StageInfo nextStageInfo = sceneBundle.getStageManager().getNextStageInfo(mSceneData.getStageInfo());
            if (nextStageInfo != null) {
                PlayScene playScene = new PlayScene();
                playScene.initialize(sceneBundle);
                if (playScene.loadMapFile(sceneBundle, nextStageInfo)) {
                    mNextScene = new FadeScene(this, playScene);
                } else {
                    mNextScene = new FadeScene(this, new CongratulationsScene());
                }
            } else {
                mNextScene = new FadeScene(this, new CongratulationsScene());
            }
        }

        if (mMenuPanel.isVisibleState()) {
            mMenuButton.setImageCol(COL_UI_BUTTON_PLAY);
        } else {
            mMenuButton.setImageCol(COL_UI_BUTTON_PAUSE);
        }

        ISceneFrame nextSceneFrame = mSceneFrame.moveNextSceneFrame();
        if (mSceneFrame != nextSceneFrame) {
            mSceneFrame = nextSceneFrame;
            mSceneFrame.initialize(sceneBundle, mSceneData);
        }
    }

    @Override
    public IScene moveNextScene() {
        if (mNextScene != null) {
            return mNextScene;
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

    @Override
    public void onClick(UiView uiView, String action, Object exData) {
        if ("menu".equals(action)) {
            mMenuPanel.setVisibleState(!mMenuPanel.isVisibleState());
            mMenuPanel.setOnVisibleListener(null);
        } else if ("retry".equals(action)) {
            mMenuPanel.setVisibleState(false);
            mMenuPanel.setOnVisibleListener(new UiView.OnVisibleListener() {
                @Override
                public void onVisible(UiView uiView) {
                }

                @Override
                public void onInvisible(UiView uiView) {
                    mCommandRetry = true;
                }
            });
        } else if ("stageSelect".equals(action)) {
            mMenuPanel.setVisibleState(false);
            mMenuPanel.setOnVisibleListener(new UiView.OnVisibleListener() {
                @Override
                public void onVisible(UiView uiView) {
                }

                @Override
                public void onInvisible(UiView uiView) {
                    mCommandStageSelect = true;
                }
            });
        } else if ("edit".equals(action)) {
            mMenuPanel.setVisibleState(false);
            mMenuPanel.setOnVisibleListener(new UiView.OnVisibleListener() {
                @Override
                public void onVisible(UiView uiView) {
                }

                @Override
                public void onInvisible(UiView uiView) {
                    mCommandEdit = true;
                }
            });
        }
    }

    public void onAction(String action, Object exData) {
        if ("retry".equals(action)) {
            mCommandRetry = true;
        } else if ("stageSelect".equals(action)) {
            mCommandStageSelect = true;
        } else if ("nextStage".equals(action)) {
            mCommandNextStage = true;
        }
    }
}
