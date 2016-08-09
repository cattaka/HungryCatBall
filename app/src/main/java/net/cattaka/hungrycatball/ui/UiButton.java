package net.cattaka.hungrycatball.ui;

import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.utils.DrawingUtil.AlphaMode;
import net.cattaka.hungrycatball.utils.ImageResource;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.common.Vec2;

public class UiButton extends UiView {
    private ImageResource mImageResource;
    private int mImageCol;

    private UiButton(Vec2 position, Vec2 size, ImageResource imageResource, int imageCol) {
        super(position, size);
        this.mImageResource = imageResource;
        this.mImageCol = imageCol;
    }

    public static UiButton createUiPanel(Vec2 position, Vec2 size, SceneBundle sceneBundle, int imageCol) {
        return new UiButton(position, size,
                sceneBundle.getDrawUtil().getImageResource(TextureId.UI_BUTTON),
                imageCol
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

        if (mImageResource != null) {
            gl.glPushMatrix();
            if (getVisiblity() != 1) {
                gl.glTranslatef(mPosition.x, mPosition.y, 0);
                gl.glScalef(1, getVisiblity(), 1);
                gl.glTranslatef(-mPosition.x, -mPosition.y, 0);
            }
            sceneBundle.getDrawUtil().drawBitmap(gl, mImageResource, row, mImageCol, mPosition, mSize, 0, 1, null, AlphaMode.STD);
            gl.glPopMatrix();
        }
    }

    public int getImageCol() {
        return mImageCol;
    }

    public void setImageCol(int mImageCol) {
        this.mImageCol = mImageCol;
    }
}
