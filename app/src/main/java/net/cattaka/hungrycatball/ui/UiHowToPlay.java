package net.cattaka.hungrycatball.ui;

import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.utils.DrawingUtil.AlphaMode;
import net.cattaka.hungrycatball.utils.ImageResource;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.common.Vec2;

public class UiHowToPlay extends UiView {
    private ImageResource mImageResource;
    private AlphaMode alphaMode = AlphaMode.STD;

    private UiHowToPlay(Vec2 position, Vec2 size, ImageResource imageResource, AlphaMode alphaMode) {
        super(position, size);
        mImageResource = imageResource;
        this.alphaMode = alphaMode;
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(UiView uiView, String action, Object exData) {
                setVisibleState(false);
            }
        }, "close", null);
    }

    public static UiHowToPlay createUiPanel(Vec2 position, Vec2 size, SceneBundle sceneBundle, AlphaMode alphaMode) {
        ImageResource imageResource = sceneBundle.getDrawUtil().getImageResource(TextureId.IMG_HOWTOPLAY);
        return new UiHowToPlay(position, size, imageResource, alphaMode);
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        super.draw(gl, sceneBundle);
        if (mImageResource != null) {
            float v = 1 + (1.0f - getVisiblity()) * 2;
            float alpha = getVisiblity();
            float angle = (float) (getVisiblity() * Math.PI) * 2;
            final Vec2 pos = new Vec2();
            final Vec2 size = new Vec2();
            size.set(mSize.x / 2, mSize.y / 2);
            pos.set(mPosition.x - size.x * 0.5f * v, mPosition.y + size.y * 0.5f * v);
            sceneBundle.getDrawUtil().drawBitmap(gl, mImageResource, 0, 0, pos, size, angle, alpha, null, alphaMode);
            pos.set(mPosition.x + size.x * 0.5f * v, mPosition.y + size.y * 0.5f * v);
            sceneBundle.getDrawUtil().drawBitmap(gl, mImageResource, 0, 1, pos, size, -angle, alpha, null, alphaMode);
            pos.set(mPosition.x - size.x * 0.5f * v, mPosition.y - size.y * 0.5f * v);
            sceneBundle.getDrawUtil().drawBitmap(gl, mImageResource, 1, 0, pos, size, -angle, alpha, null, alphaMode);
            pos.set(mPosition.x + size.x * 0.5f * v, mPosition.y - size.y * 0.5f * v);
            sceneBundle.getDrawUtil().drawBitmap(gl, mImageResource, 1, 1, pos, size, angle, alpha, null, alphaMode);
        }
    }
}
