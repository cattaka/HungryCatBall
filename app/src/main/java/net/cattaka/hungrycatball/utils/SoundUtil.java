package net.cattaka.hungrycatball.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import net.cattaka.hungrycatball.R;

import java.util.HashMap;
import java.util.Map;

public class SoundUtil {
    private Map<SoundId, SoundHandler> mSoundHandlerMap;
    private SoundPool mSoundPool;
    private boolean mEnableSound;
    public SoundUtil() {
        mSoundHandlerMap = new HashMap<SoundId, SoundHandler>();
        mSoundHandlerMap.put(SoundId.CAN_BOUNCE, new SoundHandler(R.raw.can_bounce));
        mSoundHandlerMap.put(SoundId.BALL_BOUNCE, new SoundHandler(R.raw.ball_bounce));
        mSoundHandlerMap.put(SoundId.SOFT_BOUNCE, new SoundHandler(R.raw.soft_bounce));
        mSoundHandlerMap.put(SoundId.LEVEL_FAILED, new SoundHandler(R.raw.level_failed));
        mSoundHandlerMap.put(SoundId.LEVEL_SUCCEED, new SoundHandler(R.raw.level_succeed));
    }

    public void loadSound(Context context) {
        if (mSoundPool == null) {
            mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
            for (SoundHandler sh : mSoundHandlerMap.values()) {
                if (sh.getRawSoundId() == -1) {
                    int rawSoundId = mSoundPool.load(context, sh.getResourceId(), 1);
                    sh.setRawSoundId(rawSoundId);
                }
            }
        }
    }

    public void release() {
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
            for (SoundHandler sh : mSoundHandlerMap.values()) {
                sh.setRawSoundId(-1);
            }
        }
    }

    public void play(SoundId soundId) {
        if (mEnableSound) {
            SoundHandler sh = mSoundHandlerMap.get(soundId);
            if (sh != null) {
                sh.setPlay(true);
            }
        }
    }

    public void step() {
        if (mEnableSound) {
            for (SoundHandler sh : mSoundHandlerMap.values()) {
                if (sh.isPlay()) {
                    sh.setPlay(false);
                    mSoundPool.play(sh.getRawSoundId(), 1, 1, 1, 0, 1);
                }
            }
        }
    }

    public boolean isEnableSound() {
        return mEnableSound;
    }

    public void setEnableSound(boolean enableSound) {
        this.mEnableSound = enableSound;
    }

    public enum SoundId {
        CAN_BOUNCE,
        BALL_BOUNCE,
        SOFT_BOUNCE,
        LEVEL_FAILED,
        LEVEL_SUCCEED,
    }

    static class SoundHandler {
        private int resourceId;
        private int rawSoundId = -1;
        private boolean play;

        public SoundHandler(int resourceId) {
            super();
            this.resourceId = resourceId;
        }

        public int getResourceId() {
            return resourceId;
        }

        public void setResourceId(int resourceId) {
            this.resourceId = resourceId;
        }

        public int getRawSoundId() {
            return rawSoundId;
        }

        public void setRawSoundId(int rawSoundId) {
            this.rawSoundId = rawSoundId;
        }

        public boolean isPlay() {
            return play;
        }

        public void setPlay(boolean play) {
            this.play = play;
        }
    }
}
