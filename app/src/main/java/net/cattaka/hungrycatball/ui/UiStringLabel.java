package net.cattaka.hungrycatball.ui;

import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.utils.DrawingUtil.Align;

import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;

public class UiStringLabel extends UiView {
    private char[] mString;
    private float mScale;
    private Align mAlign;
    private Vec3 mColor;

    public UiStringLabel(Vec2 position, Vec2 size, char[] mString, float mScale,
                         Align mAlign, Vec3 mColor) {
        super(position, size);
        this.mString = mString;
        this.mScale = mScale;
        this.mAlign = mAlign;
        this.mColor = mColor;
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        super.draw(gl, sceneBundle);
        if (mString != null) {
            gl.glPushMatrix();
            if (getVisiblity() != 1) {
                gl.glTranslatef(mPosition.x, mPosition.y, 0);
                gl.glScalef(1, getVisiblity(), 1);
                gl.glTranslatef(-mPosition.x, -mPosition.y, 0);
            }
            sceneBundle.getDrawUtil().drawChars(gl, mString, mPosition, mScale, mAlign, mColor, 1f);
            gl.glPopMatrix();
        }
    }

    public char[] getString() {
        return mString;
    }

    public void setString(char[] string) {
        this.mString = string;
    }
}
