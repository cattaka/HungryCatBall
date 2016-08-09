package net.cattaka.hungrycatball.ui;

import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.utils.ImageResource;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.common.Vec2;

import java.util.ArrayList;
import java.util.List;

public class UiPanel extends UiView {
    private ImageResource mImageResource;
    private List<UiView> mChildViews;
    private boolean mEnableDraw = true;

    private UiPanel(Vec2 position, Vec2 size, ImageResource imageResource) {
        super(position, size);
        mImageResource = imageResource;
        mChildViews = new ArrayList<UiView>();
    }

    public static UiPanel createUiPanel(Vec2 position, Vec2 size, SceneBundle sceneBundle) {
        return new UiPanel(position, size, sceneBundle.getDrawUtil().getImageResource(TextureId.UI_PANEL));
    }

    @Override
    public void step(SceneBundle sceneBundle) {
        if (!isVisible()) {
            return;
        }
        final Vec2 currentPosition = new Vec2();
        currentPosition.set(sceneBundle.getUserInput().currentPosition);
        sceneBundle.getUserInput().currentPosition.subLocal(mPosition);
        for (UiView uiView : mChildViews) {
            uiView.step(sceneBundle);
        }
        sceneBundle.getUserInput().currentPosition.set(currentPosition);
        super.step(sceneBundle);
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        if (isVisible()) {
            gl.glPushMatrix();
            if (getVisiblity() != 1) {
                gl.glTranslatef(mPosition.x, mPosition.y, 0);
                gl.glScalef(1, getVisiblity(), 1);
                gl.glTranslatef(-mPosition.x, -mPosition.y, 0);
            }
            if (mEnableDraw) {
                sceneBundle.getDrawUtil().draw9patch(gl, mImageResource, mPosition, mSize, null);
            }
            gl.glTranslatef(mPosition.x, mPosition.y, 0);
            for (UiView uiView : mChildViews) {
                uiView.draw(gl, sceneBundle);
            }
            gl.glPopMatrix();
        }
    }

    public void addChild(UiView uiView) {
        mChildViews.add(uiView);
    }

    public boolean isEnableDraw() {
        return mEnableDraw;
    }

    public void setEnableDraw(boolean enableDraw) {
        this.mEnableDraw = enableDraw;
    }
}
