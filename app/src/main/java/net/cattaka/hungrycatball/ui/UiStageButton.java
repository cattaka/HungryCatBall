package net.cattaka.hungrycatball.ui;

import net.cattaka.hungrycatball.HungryCatBallConstants;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.stage.StageInfo;
import net.cattaka.hungrycatball.utils.DrawingUtil.Align;
import net.cattaka.hungrycatball.utils.DrawingUtil.AlphaMode;
import net.cattaka.hungrycatball.utils.ImageResource;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.common.Vec2;

public class UiStageButton extends UiView {
    private ImageResource mBackImageResource;
    private ImageResource mLockImageResource;
    private StageInfo stageInfo;

    private UiStageButton(Vec2 position, Vec2 size, ImageResource backImageResource, ImageResource lockImageResource, StageInfo stageInfo) {
        super(position, size);
        this.mBackImageResource = backImageResource;
        this.mLockImageResource = lockImageResource;
        this.stageInfo = stageInfo;
    }

    public static UiStageButton createUiPanel(Vec2 position, Vec2 size, SceneBundle sceneBundle, StageInfo stageInfo) {
        return new UiStageButton(position, size,
                sceneBundle.getDrawUtil().getImageResource(TextureId.UI_STAGE_BUTTON),
                sceneBundle.getDrawUtil().getImageResource(TextureId.UI_LOCK),
                stageInfo
        );
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        super.draw(gl, sceneBundle);
        int row = 0;
        switch (getTouchState()) {
            default:
            case RELEASE:
            case RELEASED:
                row = 0;
                break;
            case PRESSE:
            case PRESSED:
                row = 1;
                break;
        }
        sceneBundle.getDrawUtil().drawBitmap(gl, mBackImageResource, row, 0, mPosition, mSize, 0, 1, null, AlphaMode.STD);

        final Vec2 pos = new Vec2();
        pos.set(mPosition.x, mPosition.y + 0.4f);
        sceneBundle.getDrawUtil().drawNumber(gl, stageInfo.getStageNo(), 0, pos, HungryCatBallConstants.CELL_SIZE * 2, Align.CENTER, null, 1f);
        pos.set(mPosition.x + 0.8f, mPosition.y - 0.8f);
        sceneBundle.getDrawUtil().drawNumber(gl, stageInfo.getHighScore(), 0, pos, HungryCatBallConstants.CELL_SIZE * 0.8f, Align.RIGHT, null, 1f);

        if (!stageInfo.isUnlocked()) {
            final Vec2 size = new Vec2();
            size.set(mSize.x * 0.8f, mSize.y * 0.8f);
            sceneBundle.getDrawUtil().drawBitmap(gl, mLockImageResource, 0, 0, mPosition, size, 0, 1, null, AlphaMode.STD);
        }
    }
}
