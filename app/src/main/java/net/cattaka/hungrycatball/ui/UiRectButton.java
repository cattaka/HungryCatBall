package net.cattaka.hungrycatball.ui;

import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.utils.DrawingUtil.Align;
import net.cattaka.hungrycatball.utils.ImageResource;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.common.Vec2;

public class UiRectButton extends UiView {
    private ImageResource mPressedImageResource;
    private ImageResource mReleasedImageResource;
    private char[] labelChars;
    private float scale;

    private UiRectButton(Vec2 position, Vec2 size, ImageResource pressedImageResource, ImageResource releasedImageResource, char[] labelChars, float scale) {
        super(position, size);
        this.mPressedImageResource = pressedImageResource;
        this.mReleasedImageResource = releasedImageResource;
        this.labelChars = labelChars;
        this.scale = scale;
    }

    public static UiRectButton createUiPanel(Vec2 position, Vec2 size, SceneBundle sceneBundle, char[] labelChars, float scale) {
        return new UiRectButton(position, size,
                sceneBundle.getDrawUtil().getImageResource(TextureId.UI_SQUIRE_BUTTON_PRESSED),
                sceneBundle.getDrawUtil().getImageResource(TextureId.UI_SQUIRE_BUTTON_RELEASED),
                labelChars,
                scale
        );
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        ImageResource imageResource;
        super.draw(gl, sceneBundle);
        switch (getTouchState()) {
            default:
            case RELEASE:
            case RELEASED:
                imageResource = mReleasedImageResource;
                break;
            case PRESSE:
            case PRESSED:
                imageResource = mPressedImageResource;
                break;
        }

        gl.glPushMatrix();
        if (getVisiblity() != 1) {
            gl.glTranslatef(mPosition.x, mPosition.y, 0);
            gl.glScalef(1, getVisiblity(), 1);
            gl.glTranslatef(-mPosition.x, -mPosition.y, 0);
        }
        sceneBundle.getDrawUtil().draw9patch(gl, imageResource, mPosition, mSize, 1);
        sceneBundle.getDrawUtil().drawChars(gl, labelChars, mPosition, scale, Align.CENTER, null, 1f);
        gl.glPopMatrix();
    }
}
