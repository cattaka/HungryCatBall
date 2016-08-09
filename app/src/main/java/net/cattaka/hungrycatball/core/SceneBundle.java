package net.cattaka.hungrycatball.core;

import android.content.Context;

import net.cattaka.hungrycatball.db.HungryCatDbHelper;
import net.cattaka.hungrycatball.stage.StageManager;
import net.cattaka.hungrycatball.utils.DrawingUtil;
import net.cattaka.hungrycatball.utils.SoundUtil;

import javax.microedition.khronos.opengles.GL11;

abstract public class SceneBundle {
    private DrawingUtil mDrawUtil;
    private SoundUtil mSoundUtil;
    private UserInput mUserInput;
    private StageManager mStageManager;
    private IScreenHandler mScreenHandler;
    private boolean mDebugMode;
    private boolean mDebugDraw;

    public SceneBundle() {
        this.mDrawUtil = new DrawingUtil();
        this.mSoundUtil = new SoundUtil();
        this.mUserInput = new UserInput();
        this.mStageManager = new StageManager();
    }

    public void initialize(GL11 gl, Context context) {
        mDrawUtil.loadImages(gl, context.getResources());
        mSoundUtil.loadSound(context);
    }

    public void release() {
        mDrawUtil.release();
    }

    public DrawingUtil getDrawUtil() {
        return mDrawUtil;
    }

    public SoundUtil getSoundUtil() {
        return mSoundUtil;
    }

    public UserInput getUserInput() {
        return mUserInput;
    }

    public void setUserInput(UserInput userInput) {
        this.mUserInput = userInput;
    }

    public StageManager getStageManager() {
        return mStageManager;
    }

    public IScreenHandler getScreenHandler() {
        return mScreenHandler;
    }

    public void setScreenHandler(IScreenHandler screenHandler) {
        this.mScreenHandler = screenHandler;
    }

    public boolean isDebugMode() {
        return mDebugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.mDebugMode = debugMode;
    }

    public boolean isDebugDraw() {
        return mDebugDraw;
    }

    public void setDebugDraw(boolean debugDraw) {
        this.mDebugDraw = debugDraw;
    }

    abstract public HungryCatDbHelper getDbHelper();
}
