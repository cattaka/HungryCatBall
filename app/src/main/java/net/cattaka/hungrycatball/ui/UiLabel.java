package net.cattaka.hungrycatball.ui;

import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.utils.DrawingUtil.AlphaMode;
import net.cattaka.hungrycatball.utils.ImageResource;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.common.Vec2;

public class UiLabel extends UiView {
    private ImageResource mImageResource;
    private int mImageRow;
    private int mImageCol;
    private AlphaMode alphaMode = AlphaMode.STD;

    private UiLabel(Vec2 position, Vec2 size, ImageResource imageResource, int imageRow, int imageCol, AlphaMode alphaMode) {
        super(position, size);
        this.mImageResource = imageResource;
        this.mImageRow = imageRow;
        this.mImageCol = imageCol;
        this.alphaMode = alphaMode;
    }

    public static UiLabel createUiPanel(Vec2 position, Vec2 size, SceneBundle sceneBundle, TextureId textureId, int imageRow, int imageCol) {
        return createUiPanel(position, size, sceneBundle, textureId, imageRow, imageCol, AlphaMode.STD);
    }

    public static UiLabel createUiPanel(Vec2 position, Vec2 size, SceneBundle sceneBundle, TextureId textureId, int imageRow, int imageCol, AlphaMode alphaMode) {
        ImageResource imageResource = sceneBundle.getDrawUtil().getImageResource(textureId);
        return new UiLabel(position, size, imageResource, imageRow, imageCol, alphaMode);
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        super.draw(gl, sceneBundle);
        if (mImageResource != null) {
            gl.glPushMatrix();
            if (getVisiblity() != 1) {
                gl.glTranslatef(mPosition.x, mPosition.y, 0);
                gl.glScalef(1, getVisiblity(), 1);
                gl.glTranslatef(-mPosition.x, -mPosition.y, 0);
            }
            sceneBundle.getDrawUtil().drawBitmap(gl, mImageResource, mImageRow, mImageCol, mPosition, mSize, 0, 1, null, alphaMode);
            gl.glPopMatrix();
        }
    }

    public int getImageRow() {
        return mImageRow;
    }

    public void setImageRow(int imageRow) {
        this.mImageRow = imageRow;
    }

    public int getImageCol() {
        return mImageCol;
    }

    public void setImageCol(int imageCol) {
        this.mImageCol = imageCol;
    }
}
