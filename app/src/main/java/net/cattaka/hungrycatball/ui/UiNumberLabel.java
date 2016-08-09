package net.cattaka.hungrycatball.ui;

import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.utils.DrawingUtil.Align;

import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;

public class UiNumberLabel extends UiView {
    private int mNumber;
    private float mScale;
    private Align mAlign;
    private Vec3 mColor;

    public UiNumberLabel(Vec2 position, Vec2 size, int mNumber, float mScale,
                         Align mAlign, Vec3 mColor) {
        super(position, size);
        this.mNumber = mNumber;
        this.mScale = mScale;
        this.mAlign = mAlign;
        this.mColor = mColor;
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        super.draw(gl, sceneBundle);
        gl.glPushMatrix();
        if (getVisiblity() != 1) {
            gl.glTranslatef(mPosition.x, mPosition.y, 0);
            gl.glScalef(1, getVisiblity(), 1);
            gl.glTranslatef(-mPosition.x, -mPosition.y, 0);
        }
        sceneBundle.getDrawUtil().drawNumber(gl, mNumber, 0, mPosition, mScale, mAlign, mColor, 1f);
        gl.glPopMatrix();
    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        this.mNumber = number;
    }
}
