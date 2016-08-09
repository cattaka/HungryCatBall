package net.cattaka.hungrycatball.gl.shape;

import android.graphics.RectF;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL11;

public class ShapeSquare implements Shape3D {
    private final static int VERTS = 4;
    private static final float[] texCoords = {
            // X, Y, Z
            -0.5f, 0.5f, 0,
            0.5f, 0.5f, 0,
            -0.5f, -0.5f, 0,
            0.5f, -0.5f, 0,
    };
    private FloatBuffer mFVertexBuffer;
    private FloatBuffer mTexBuffer;
    private ShortBuffer mIndexBuffer;
    private int[] mVbo;
    //private float scale = 1;
    // A unit-sided equalateral triangle centered on the origin.
    private float[] verCoords;

    public ShapeSquare(RectF rect) {
        verCoords = new float[]{
                // X, Y, Z
                rect.left, rect.top, 0,
                rect.right, rect.top, 0,
                rect.left, rect.bottom, 0,
                rect.right, rect.bottom, 0
        };

        // Buffers to be passed to gl*Pointer() functions
        // must be direct, i.e., they must be placed on the
        // native heap where the garbage collector cannot
        // move them.
        //
        // Buffers with multi-byte datatypes (e.g., short, int, float)
        // must have their byte order set to native order

        ByteBuffer vbb = ByteBuffer.allocateDirect(VERTS * 3 * 4);
        vbb.order(ByteOrder.nativeOrder());
        mFVertexBuffer = vbb.asFloatBuffer();

        ByteBuffer tbb = ByteBuffer.allocateDirect(VERTS * 2 * 4);
        tbb.order(ByteOrder.nativeOrder());
        mTexBuffer = tbb.asFloatBuffer();

        ByteBuffer ibb = ByteBuffer.allocateDirect(VERTS * 2);
        ibb.order(ByteOrder.nativeOrder());
        mIndexBuffer = ibb.asShortBuffer();

        updateVerCoods();

        for (int i = 0; i < VERTS; i++) {
            for (int j = 0; j < 2; j++) {
                mTexBuffer.put(texCoords[i * 3 + j] + 0.5f);
            }
        }

        for (int i = 0; i < VERTS; i++) {
            mIndexBuffer.put((short) i);
        }

        mTexBuffer.position(0);
        mIndexBuffer.position(0);
    }

    @Override
    public void initialize(GL11 gl) {
        float[] data = new float[]{
                -0.5f, 0.5f, 0, 0f, 1f,
                0.5f, 0.5f, 0, 1f, 1f,
                -0.5f, -0.5f, 0, 0f, 0f,
                0.5f, -0.5f, 0, 1f, 0f,
        };
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(data);
        fb.position(0);

        mVbo = new int[1];
        gl.glGenBuffers(1, mVbo, 0);
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVbo[0]);
        gl.glBufferData(GL11.GL_ARRAY_BUFFER, 4 * data.length, fb, GL11.GL_STATIC_DRAW);
        gl.glVertexPointer(3, GL11.GL_FLOAT, 4 * 5, 0);
        gl.glTexCoordPointer(2, GL11.GL_FLOAT, 4 * 5, 4 * 3);
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
    }

    public void draw(GL11 gl) {
        //gl.glFrontFace(GL11.GL_CCW);
//		gl.glVertexPointer(3, GL11.GL_FLOAT, 0, mFVertexBuffer);
//		gl.glTexCoordPointer(2, GL11.GL_FLOAT, 0, mTexBuffer);
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVbo[0]);
        gl.glVertexPointer(3, GL11.GL_FLOAT, 4 * 5, 0);
        gl.glTexCoordPointer(2, GL11.GL_FLOAT, 4 * 5, 4 * 3);

        gl.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, VERTS);
    }


//	public float getScale() {
//		return scale;
//	}
//
//	public void setScale(float scale) {
//		this.scale = scale;
//		updateVerCoods();
//	}

    private void updateVerCoods() {
        mFVertexBuffer.clear();
        for (int i = 0; i < VERTS; i++) {
            for (int j = 0; j < 3; j++) {
                mFVertexBuffer.put(verCoords[i * 3 + j]);
                //mFVertexBuffer.put(verCoords[i * 3 + j] * scale);
            }
        }
        mFVertexBuffer.position(0);
    }
}
