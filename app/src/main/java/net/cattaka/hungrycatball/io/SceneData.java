package net.cattaka.hungrycatball.io;

import net.cattaka.hungrycatball.game.GameEntityAddEvent;
import net.cattaka.hungrycatball.stage.StageInfo;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import java.util.ArrayList;
import java.util.List;

public class SceneData {
    private List<GameEntityAddEvent> gameEntityAddEvents;
    private TextureId bgTextureId;
    private StageInfo stageInfo;

    public SceneData() {
        gameEntityAddEvents = new ArrayList<GameEntityAddEvent>();
    }

    public List<GameEntityAddEvent> getGameEntityAddEvents() {
        return gameEntityAddEvents;
    }

    public void setGameEntityAddEvents(List<GameEntityAddEvent> gameEntityAddEvents) {
        this.gameEntityAddEvents = gameEntityAddEvents;
    }

    public TextureId getBgTextureId() {
        return bgTextureId;
    }

    public void setBgTextureId(TextureId bgTextureId) {
        this.bgTextureId = bgTextureId;
    }

    public StageInfo getStageInfo() {
        return stageInfo;
    }

    public void setStageInfo(StageInfo stageInfo) {
        this.stageInfo = stageInfo;
    }
}
