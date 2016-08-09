package net.cattaka.hungrycatball.game.entity;

import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.utils.DrawingUtil.AlphaMode;

import org.jbox2d.common.Vec2;

public abstract class BlockPieceEntity extends BlockEntity {
    private int mPieceIdx;

    public BlockPieceEntity() {
        super();
    }

    @Override
    public void initialize(GameWorld gameWorld, SceneBundle sceneBundle, Object exData, float[] exFloatData) {
        super.initialize(gameWorld, sceneBundle, exData, exFloatData);
        mPieceIdx = (Integer) exData;
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        final Vec2 position = new Vec2();
        getPosition(position);
        sceneBundle.getDrawUtil().drawBitmap(gl, mImageResource, 0, mPieceIdx, position, mSize, getAngle(), 1, null, AlphaMode.STD);
    }
}
