package net.cattaka.hungrycatball.frame;

import net.cattaka.hungrycatball.core.AbstractUiCallback;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.game.GameWorld.LevelState;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.io.SceneData;
import net.cattaka.hungrycatball.stage.StageInfo;
import net.cattaka.hungrycatball.ui.UiButton;
import net.cattaka.hungrycatball.ui.UiLabel;
import net.cattaka.hungrycatball.ui.UiNumberLabel;
import net.cattaka.hungrycatball.ui.UiPanel;
import net.cattaka.hungrycatball.ui.UiStringLabel;
import net.cattaka.hungrycatball.ui.UiView;
import net.cattaka.hungrycatball.utils.DrawingUtil.Align;
import net.cattaka.hungrycatball.utils.DrawingUtil.AlphaMode;
import net.cattaka.hungrycatball.utils.ImageResource;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;

import static net.cattaka.hungrycatball.HungryCatBallConstants.STEP_DT;

public class LevelEndFrame implements ISceneFrame, UiView.OnClickListener {
    private static float CROSS_TIME = 1f;
    private static float STEP_TIME = 1.0f;
    private static float STEP_COUNT_SCORE_TIME = 0.05f;

    private ISceneFrame mNextSceneFrame;
    private float mFrameTime = 0;
    private float mNextFrameTime = 0;
    private int mFrameState = 0;
    private int mRealBonus;
    private int mTempBonus;

    private UiPanel mResultPanel;
    private UiLabel mTitleLabel;
    private UiLabel mImageLabel;
    private UiButton mStageButton;
    private UiButton mRetryButton;
    private UiButton mNextStageButton;

    private UiStringLabel mTimeLabel;
    private UiStringLabel mTimeBonusLabel;
    private UiStringLabel mTimeBonusDesc;
    private UiStringLabel mScoreLabel;
    private UiStringLabel mTimeValue;
    private UiNumberLabel mTimeBonusValue;
    private UiNumberLabel mScoreValue;

    private LevelState mLevelState;
    private Vec2 mCrossPosition;
    private ImageResource mImageResource;

    private boolean mCommandRetry = false;
    private boolean mCommandStageSelect = false;
    private boolean mCommandNext = false;

    public LevelEndFrame(LevelState levelState, Vec2 crossPosition) {
        mLevelState = levelState;
        mCrossPosition = crossPosition;
    }

    @Override
    public void initialize(SceneBundle sceneBundle, SceneData sceneData) {
        mImageLabel = UiLabel.createUiPanel(new Vec2(0, -8), new Vec2(8, 8), sceneBundle, TextureId.IMG_FAILED_CLEARED, 0, 0);

        mResultPanel = UiPanel.createUiPanel(new Vec2(0, 3), new Vec2(9, 14), sceneBundle);
        mTitleLabel = UiLabel.createUiPanel(new Vec2(0, 5.5f), new Vec2(8, 2), sceneBundle, TextureId.MSG_LEVEL_FAILED_CLEARED, 0, 0);

        mStageButton = UiButton.createUiPanel(new Vec2(-2, -4.5f), new Vec2(3, 3), sceneBundle, ImageResource.COL_UI_BUTTON_STAGE);
        mRetryButton = UiButton.createUiPanel(new Vec2(2, -4.5f), new Vec2(3, 3), sceneBundle, ImageResource.COL_UI_BUTTON_RESTART);
        mNextStageButton = UiButton.createUiPanel(new Vec2(2, -4.5f), new Vec2(3, 3), sceneBundle, ImageResource.COL_UI_BUTTON_RIGHT);
        mStageButton.setOnClickListener(this, "stageSelect", null);
        mRetryButton.setOnClickListener(this, "retry", null);
        mNextStageButton.setOnClickListener(this, "nextStage", null);
        mStageButton.setVisiblity(0);
        mRetryButton.setVisiblity(0);
        mNextStageButton.setVisiblity(0);
        mStageButton.setVisibleState(false);
        mRetryButton.setVisibleState(false);
        mNextStageButton.setVisibleState(false);
        mResultPanel.addChild(mTitleLabel);
        mResultPanel.addChild(mStageButton);
        mResultPanel.addChild(mRetryButton);
        mResultPanel.addChild(mNextStageButton);

        mTimeLabel = new UiStringLabel(new Vec2(-3.5f, 4f), new Vec2(), "Time".toCharArray(), 1, Align.LEFT, null);
        mTimeValue = new UiStringLabel(new Vec2(3.5f, 3f), new Vec2(), "00:00".toCharArray(), 1, Align.RIGHT, null);
        mTimeBonusLabel = new UiStringLabel(new Vec2(-3.5f, 2f), new Vec2(), "Time Bonus".toCharArray(), 1, Align.LEFT, null);
        mTimeBonusDesc = new UiStringLabel(new Vec2(3.5f, 1f), new Vec2(), null, 1, Align.RIGHT, null);
        mTimeBonusValue = new UiNumberLabel(new Vec2(3.5f, 0f), new Vec2(), 0, 1, Align.RIGHT, null);
        mScoreLabel = new UiStringLabel(new Vec2(-3.5f, -1f), new Vec2(), "Score".toCharArray(), 1, Align.LEFT, null);
        mScoreValue = new UiNumberLabel(new Vec2(3.5f, -2f), new Vec2(), 0, 1, Align.RIGHT, null);
        mTimeLabel.setVisiblity(0);
        mTimeValue.setVisiblity(0);
        mTimeBonusLabel.setVisiblity(0);
        mTimeBonusDesc.setVisiblity(0);
        mTimeBonusValue.setVisiblity(0);
        mScoreLabel.setVisiblity(0);
        mScoreValue.setVisiblity(0);
        mTimeLabel.setVisibleState(false);
        mTimeValue.setVisibleState(false);
        mTimeBonusLabel.setVisibleState(false);
        mTimeBonusDesc.setVisibleState(false);
        mTimeBonusValue.setVisibleState(false);
        mScoreLabel.setVisibleState(false);
        mScoreValue.setVisibleState(false);
        mResultPanel.addChild(mTimeLabel);
        mResultPanel.addChild(mTimeValue);
        mResultPanel.addChild(mTimeBonusLabel);
        mResultPanel.addChild(mTimeBonusDesc);
        mResultPanel.addChild(mTimeBonusValue);
        mResultPanel.addChild(mScoreLabel);
        mResultPanel.addChild(mScoreValue);

        mResultPanel.setVisibleState(false);
        mResultPanel.setVisiblity(0.0f);

        mImageLabel.setVisibleState(false);
        mImageLabel.setVisiblity(0.0f);

        if (mLevelState == LevelState.LEVEL_CLEARED) {
            mTitleLabel.setImageRow(0);
            mImageLabel.setImageCol(0);
            mNextFrameTime = CROSS_TIME;
        } else if (mLevelState == LevelState.LEVEL_FAILED) {
            mTitleLabel.setImageRow(1);
            mImageLabel.setImageCol(1);
            mNextFrameTime = 0;
        }
        mImageResource = sceneBundle.getDrawUtil().getImageResource(TextureId.PT_CROSS);
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle, GameWorld gameWorld) {
        gameWorld.draw(gl, sceneBundle);

        if (mCrossPosition != null) {
            final Vec3 color = new Vec3(1.0f, 1.0f, 0.5f);
            final Vec2 size = new Vec2(4, 4);
            float sizeF = (mFrameTime < CROSS_TIME / 2f) ? mFrameTime * 32 : (mFrameTime < CROSS_TIME) ? (CROSS_TIME - mFrameTime) * 32 : 0;
            float angle = -mFrameTime * (float) Math.PI * 2;
            size.set(sizeF, sizeF);
            sceneBundle.getDrawUtil().drawBitmap(gl, mImageResource, mCrossPosition, size, angle, 1f, color, AlphaMode.ADD);
        }
        mResultPanel.draw(gl, sceneBundle);
        mImageLabel.draw(gl, sceneBundle);
        if (mLevelState == LevelState.LEVEL_CLEARED) {
            char[] playTime = new char[5];
            gameWorld.getPlayTimeAsCharArray(playTime);
            mTimeValue.setString(playTime);
            mTimeBonusValue.setNumber(mTempBonus);
            mScoreValue.setNumber(gameWorld.getScore());
        }
    }

    @Override
    public void step(GameWorld gameWorld, SceneBundle sceneBundle, SceneData sceneData, ISceneFrameListener listener) {
        if (mFrameState == 0) {
            if (mLevelState == LevelState.LEVEL_CLEARED) {
                if (mFrameTime >= mNextFrameTime) {
                    mFrameState = 1;
                    mNextFrameTime += STEP_TIME;
                    mResultPanel.setVisibleState(true);
                    mImageLabel.setVisibleState(true);
                    {
                        // 純粋なボーナスを計算する
                        int rate = 10 - gameWorld.getPlayTimeCount() / 300;
                        if (rate < 0) {
                            rate = 0;
                        }
                        mRealBonus = gameWorld.getBonus() * rate / 10;
                        char[] cs = String.format("%d x %d.%d = ", gameWorld.getBonus(), rate / 10, rate % 10).toCharArray();
                        mTimeBonusDesc.setString(cs);
                        // 状況を保存する
                        saveScore(sceneBundle, sceneData, gameWorld.getScore() + mRealBonus);
                    }
                }
            } else if (mLevelState == LevelState.LEVEL_FAILED) {
                mFrameState = 99;
                mNextFrameTime += STEP_TIME;
                mResultPanel.setVisibleState(true);
                mImageLabel.setVisibleState(true);
            }
        }
        if (mFrameState == 1) {
            if (mFrameTime >= mNextFrameTime) {
                mNextFrameTime += STEP_TIME;
                mTimeLabel.setVisibleState(true);
                mTimeValue.setVisibleState(true);
                mFrameState = 2;
            }
        }
        if (mFrameState == 2) {
            if (mFrameTime >= mNextFrameTime) {
                mNextFrameTime += STEP_TIME;
                mTimeBonusLabel.setVisibleState(true);
                mTimeBonusDesc.setVisibleState(true);
                mTimeBonusValue.setVisibleState(true);
                mFrameState = 3;
            }
        }
        if (mFrameState == 3) {
            if (mFrameTime >= mNextFrameTime) {
                mNextFrameTime += STEP_TIME;
                mScoreLabel.setVisibleState(true);
                mScoreValue.setVisibleState(true);
                mFrameState = 4;
            }
        }
        if (mFrameState == 4) {
            if (mFrameTime >= mNextFrameTime) {
                mNextFrameTime += STEP_COUNT_SCORE_TIME;
                if (mTempBonus != mRealBonus) {
                    int diff = mRealBonus - mTempBonus;
                    if (diff > 100) {
                        diff = 100;
                    }
                    mTempBonus += diff;
                    gameWorld.addScore(diff);
                } else {
                    mNextFrameTime += STEP_TIME;
                    mFrameState = 5;
                }
            }
        }
        if (mFrameState == 5) {
            if (mFrameTime >= mNextFrameTime) {
                mFrameState = 99;
            }
        }
        if (mFrameState == 99) {
            if (mFrameTime >= mNextFrameTime) {
                mNextFrameTime += STEP_TIME;
                if (mLevelState == LevelState.LEVEL_CLEARED) {
                    mStageButton.setVisibleState(true);
                    mNextStageButton.setVisibleState(true);
                } else if (mLevelState == LevelState.LEVEL_FAILED) {
                    mStageButton.setVisibleState(true);
                    mRetryButton.setVisibleState(true);
                }
                mFrameState = 100;
            }
        }

        mResultPanel.step(sceneBundle);
        mImageLabel.step(sceneBundle);
        mFrameTime += STEP_DT;

        if (mCommandRetry) {
            listener.onAction("retry", null);
        } else if (mCommandStageSelect) {
            listener.onAction("stageSelect", null);
        } else if (mCommandNext) {
            listener.onAction("nextStage", null);
        }
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

    @Override
    public void onClick(UiView uiView, String action, Object exData) {
        if ("retry".equals(action)) {
            mResultPanel.setVisibleState(false);
            mResultPanel.setOnVisibleListener(new UiView.OnVisibleListener() {
                @Override
                public void onVisible(UiView uiView) {
                }

                @Override
                public void onInvisible(UiView uiView) {
                    mCommandRetry = true;
                }
            });
        } else if ("stageSelect".equals(action)) {
            mResultPanel.setVisibleState(false);
            mResultPanel.setOnVisibleListener(new UiView.OnVisibleListener() {
                @Override
                public void onVisible(UiView uiView) {
                }

                @Override
                public void onInvisible(UiView uiView) {
                    mCommandStageSelect = true;
                }
            });
        } else if ("nextStage".equals(action)) {
            mCommandNext = true;
        }
    }

    private void saveScore(SceneBundle sceneBundle, SceneData sceneData, int score) {
        StageInfo oldStageInfo = sceneData.getStageInfo();
        if (oldStageInfo != null) {
            // スコアを保存する
            StageInfo stageInfo = sceneBundle.getDbHelper().findByKey(oldStageInfo.getCourse(), oldStageInfo.getStageNo());
            stageInfo.setHighScore(Math.max(stageInfo.getHighScore(), score));
            stageInfo.setUnlocked(true);
            sceneBundle.getDbHelper().registerStageInfo(stageInfo);

            // 次のステージをアンロックする
            StageInfo nextStageInfo = sceneBundle.getStageManager().getNextStageInfo(oldStageInfo);
            if (nextStageInfo != null) {
                StageInfo t = sceneBundle.getDbHelper().findByKey(nextStageInfo.getCourse(), nextStageInfo.getStageNo());
                if (t != null) {
                    nextStageInfo = t;
                }
            }
            if (nextStageInfo != null && !nextStageInfo.isUnlocked()) {
                nextStageInfo.setUnlocked(true);
                sceneBundle.getDbHelper().registerStageInfo(nextStageInfo);
            }
        }
    }
}
