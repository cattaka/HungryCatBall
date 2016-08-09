package net.cattaka.hungrycatball.stage;

import android.util.Log;

import net.cattaka.hungrycatball.HungryCatBallConstants;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.io.SceneData;
import net.cattaka.hungrycatball.io.SceneIo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class StageManager {
    public StageInfo getNextStageInfo(StageInfo src) {
        if (src == null) {
            return null;
        }
        if (0 < src.getStageNo() && src.getStageNo() < 36) {
            return new StageInfo(src.getCourse(), src.getStageNo() + 1);
        }
        return null;
    }

    public int getHighScore(StageInfo stageInfo) {
        // TODO 実装する
        return 0;
    }

    public SceneData loadSceneData(SceneBundle sceneBundle, StageInfo stageInfo) {
        String fileName;
        if (sceneBundle.isDebugMode()) {
            fileName = HungryCatBallConstants.MAP_DIRECTORY + String.format("/s%02d_%02d.map", stageInfo.getCourse(), stageInfo.getStageNo());
        } else {
            fileName = String.format("s%02d_%02d.map", stageInfo.getCourse(), stageInfo.getStageNo());
        }
        Reader reader = null;
        SceneData sceneData = null;
        try {
            if (sceneBundle.isDebugMode()) {
                reader = new InputStreamReader(new FileInputStream(fileName));
            } else {
                reader = new InputStreamReader(sceneBundle.getScreenHandler().open(fileName));
            }
            sceneData = SceneIo.read(reader);
            sceneData.setStageInfo(stageInfo);
        } catch (IOException e) {
            Log.e(HungryCatBallConstants.TAG, e.getMessage(), e);
        }
        return sceneData;
    }
}
