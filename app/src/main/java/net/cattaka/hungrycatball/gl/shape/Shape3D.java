package net.cattaka.hungrycatball.gl.shape;

import javax.microedition.khronos.opengles.GL11;

public interface Shape3D {
    public void draw(GL11 gl);

    public void initialize(GL11 gl);
}
