package net.cattaka.hungrycatball.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.opengl.GLUtils;

import net.cattaka.hungrycatball.HungryCatBallConstants;
import net.cattaka.hungrycatball.R;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.gl.shape.ShapeSquare;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;
import org.jbox2d.common.XForm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL11;

public class DrawingUtil {
    public static float RAD_TO_DEG = (float) (180.0 / Math.PI);
    private HashMap<ImageResource.TextureId, ImageResource> imageResourceCache = new HashMap<ImageResource.TextureId, ImageResource>();
    private ShapeSquare shapeSquare;

    public DrawingUtil() {
        imageResourceCache.put(TextureId.MENU_BUTTONS, new ImageResource(R.drawable.menu_buttons));
        imageResourceCache.put(TextureId.ENTITY_PLAYER, new ImageResource(R.drawable.entity_player, 1, 4));
        imageResourceCache.put(TextureId.ENTITY_BALL, new ImageResource(R.drawable.entity_ball, 1, 1));
        imageResourceCache.put(TextureId.ENTITY_GOAL, new ImageResource(R.drawable.entity_goal, 1, 1));
        imageResourceCache.put(TextureId.ENTITY_SOFTBLOCK, new ImageResource(R.drawable.entity_softblock, 1, 4));
        imageResourceCache.put(TextureId.ENTITY_SOFTBLOCK_PIECE, new ImageResource(R.drawable.entity_softblock_piece, 1, 4));
        imageResourceCache.put(TextureId.ENTITY_HARDBLOCK, new ImageResource(R.drawable.entity_hardblock, 1, 4));
        imageResourceCache.put(TextureId.ENTITY_HARDBLOCK_PIECE, new ImageResource(R.drawable.entity_hardblock_piece, 1, 4));
        imageResourceCache.put(TextureId.ENTITY_SOFTBLOCK_SMALL, new ImageResource(R.drawable.entity_softblocksmall, 1, 4));
        imageResourceCache.put(TextureId.ENTITY_SOFTBLOCK_SMALL_PIECE, new ImageResource(R.drawable.entity_softblocksmall_piece, 1, 2));
        imageResourceCache.put(TextureId.ENTITY_HARDBLOCK_SMALL, new ImageResource(R.drawable.entity_hardblocksmall, 1, 4));
        imageResourceCache.put(TextureId.ENTITY_HARDBLOCK_SMALL_PIECE, new ImageResource(R.drawable.entity_hardblocksmall_piece, 1, 2));
        imageResourceCache.put(TextureId.ENTITY_HINGE, new ImageResource(R.drawable.entity_hinge, 1, 1));
        imageResourceCache.put(TextureId.ENTITY_RIGIDBLOCK_THIN, new ImageResource(R.drawable.entity_rigidblock_thin, 1, 1));
        imageResourceCache.put(TextureId.ENTITY_LEVER, new ImageResource(R.drawable.entity_lever, 1, 1));
        imageResourceCache.put(TextureId.ENTITY_ROLLER, new ImageResource(R.drawable.entity_roller, 1, 1));
        imageResourceCache.put(TextureId.WALL_BOTTOM, new ImageResource(R.drawable.wall_bottom, 1, 1));
        imageResourceCache.put(TextureId.WALL_SIDE, new ImageResource(R.drawable.wall_side, 1, 1));
        imageResourceCache.put(TextureId.WALL_TOP, new ImageResource(R.drawable.wall_top, 1, 1));
        imageResourceCache.put(TextureId.UI_PANEL, new ImageResource(R.drawable.ui_panel, 3, 3));
        imageResourceCache.put(TextureId.UI_BUTTON, new ImageResource(R.drawable.ui_button, 2, 10));
        imageResourceCache.put(TextureId.UI_CHAR, new ImageResource(R.drawable.ui_char, 6, 16));
        imageResourceCache.put(TextureId.UI_SQUIRE_BUTTON_RELEASED, new ImageResource(R.drawable.ui_button_released, 3, 3));
        imageResourceCache.put(TextureId.UI_SQUIRE_BUTTON_PRESSED, new ImageResource(R.drawable.ui_button_pressed, 3, 3));
        imageResourceCache.put(TextureId.UI_STAGE_BUTTON, new ImageResource(R.drawable.ui_stage_button, 2, 1));
        imageResourceCache.put(TextureId.UI_LOCK, new ImageResource(R.drawable.ui_lock, 1, 1));

        imageResourceCache.put(TextureId.MSG_TITLE, new ImageResource(R.drawable.msg_title, 1, 1));
        imageResourceCache.put(TextureId.MSG_READY, new ImageResource(R.drawable.msg_ready, 1, 1));
        imageResourceCache.put(TextureId.MSG_LEVEL_FAILED_CLEARED, new ImageResource(R.drawable.msg_level_failed_cleared, 2, 1));

        imageResourceCache.put(TextureId.IMG_FAILED_CLEARED, new ImageResource(R.drawable.img_failed_cleared, 1, 2));
        imageResourceCache.put(TextureId.IMG_HOWTOPLAY, new ImageResource(R.drawable.img_howtoplay, 2, 2));
        imageResourceCache.put(TextureId.IMG_CONGRATULATIONS, new ImageResource(R.drawable.img_congratulations, 1, 1));
        imageResourceCache.put(TextureId.IMG_LOGO, new ImageResource(R.drawable.img_logo));

        imageResourceCache.put(TextureId.BG_TYPE_1, new ImageResource(R.drawable.bg_type_1, 1, 1));
        imageResourceCache.put(TextureId.BG_TYPE_2, new ImageResource(R.drawable.bg_type_2, 1, 1));

        imageResourceCache.put(TextureId.PT_CROSS, new ImageResource(R.drawable.pt_cross, 1, 1));

        imageResourceCache.put(TextureId.DEBUG_MARKER, new ImageResource(R.drawable.debug_marker, 1, 2));
    }

    public static FloatBuffer makeFloatBuffer(float[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(array);
        fb.position(0);
        return fb;
    }

    public void loadImages(GL11 gl, Resources res) {
        shapeSquare = new ShapeSquare(new RectF(-0.5f, 0.5f, 0.5f, -0.5f));
        shapeSquare.initialize(gl);
        for (Map.Entry<TextureId, ImageResource> entry : imageResourceCache.entrySet()) {
            loadImage(gl, res, entry.getValue());
        }
    }

    public void release() {
        // TODO Lのテクスチャを破棄する?
    }

    public ImageResource getImageResource(TextureId textureId) {
        return imageResourceCache.get(textureId);
    }

    public void setupMatrix(Matrix dest, float canvasWidth, float canvasHeight, float bottomMargin) {
        double ww = HungryCatBallConstants.WORLD_RECT.width();
        double wh = HungryCatBallConstants.WORLD_RECT.height() - bottomMargin;

        double cw;
        double ch;
        double canvasAspect = canvasWidth / canvasHeight;
        double worldAspect = Math.abs(ww / wh);
        if (canvasAspect > worldAspect) {
            ch = canvasHeight;
            cw = ch * worldAspect;
        } else {
            cw = canvasWidth;
            ch = cw / worldAspect;
        }

        double cbm = (bottomMargin != 0) ? (bottomMargin * ch) / (bottomMargin + wh) : 0;
        dest.reset();
        dest.postScale((float) (cw / ww), (float) (ch / wh));
        dest.postTranslate((float) (canvasWidth / 2), (float) ((ch + cbm) / 2));
    }

    public void drawLine(CtkGL gl, float sx, float sy, float ex, float ey, Vec3 color) {
        float lastAlpha = gl.getAlpha();
        // 色の指定
        if (color != null) {
            gl.glColor4f(color.x, color.y, color.z, lastAlpha);
        } else {
            gl.glColor4f(1f, 1f, 1f, lastAlpha);
        }

        gl.glDisable(GL11.GL_TEXTURE_2D);
        gl.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        float[] fvs = new float[6];
        fvs[0] = sx;
        fvs[1] = sy;
        fvs[2] = 0;
        fvs[3] = ex;
        fvs[4] = ey;
        fvs[5] = 0;
        FloatBuffer vertices = makeFloatBuffer(fvs);
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
        gl.glVertexPointer(3, GL11.GL_FLOAT, 0, vertices);
        gl.glLineWidth(3f);
        gl.glDrawArrays(GL11.GL_LINES, 0, 2);

        gl.glEnable(GL11.GL_TEXTURE_2D);
        gl.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        gl.glColor4f(1f, 1f, 1f, lastAlpha);
    }

    public void drawCicle(CtkGL gl, float cx, float cy, float radius, Vec3 color) {
        final Vec2 position = new Vec2();
        final Vec2 size = new Vec2();
        position.set(cx, cy);
        size.set(radius * 2, radius * 2);
        ImageResource imageResource = getImageResource(TextureId.DEBUG_MARKER);

        drawBitmap(gl, imageResource, 0, 0, position, size, 0, 1f, color, AlphaMode.STD);
    }

    public void drawMarker(CtkGL gl, float cx, float cy, float radius, int color) {
        final Vec2 position = new Vec2();
        final Vec2 size = new Vec2();
        position.set(cx, cy);
        size.set(radius * 2, radius * 2);
        ImageResource imageResource = getImageResource(TextureId.DEBUG_MARKER);

        drawBitmap(gl, imageResource, 0, 1, position, size, 0, 1f, null, AlphaMode.STD);
    }

    public void drawShape(CtkGL gl, XForm xform, Shape shape, Vec3 color) {
        if (shape.getType() == ShapeType.CIRCLE_SHAPE) {
            final CircleShape circle = (CircleShape) shape;
            final float radius = circle.getRadius();

            Vec2 drawingCenter = XForm.mul(xform, circle.getLocalPosition());
            Vec2 lineEnd = new Vec2(0, radius);
            lineEnd = xform.R.mul(lineEnd).add(drawingCenter);

            //final Vec2 axis = xform.R.col1;

            drawCicle(gl, drawingCenter.x, drawingCenter.y, radius, color);
            drawLine(gl, drawingCenter.x, drawingCenter.y, lineEnd.x, lineEnd.y, color);
            //m_debugDraw.drawSolidCircle(drawingCenter, radius, axis, color);
//
//			if (core) {
//				canvas.drawCircle(drawingCenter.x, drawingCenter.y, radius, mPaintCore);
//				//m_debugDraw.drawCircle(drawingCenter, radius - Settings.toiSlop, coreColor);
//			}
        } else if (shape.getType() == ShapeType.POLYGON_SHAPE) {
            final PolygonShape poly = (PolygonShape) shape;
            final int vertexCount = poly.getVertexCount();
            final Vec2[] localVertices = poly.getVertices();

            assert (vertexCount <= Settings.maxPolygonVertices);
            final Vec2[] vertices = new Vec2[vertexCount];

            for (int i = 0; i < vertexCount; ++i) {
                // djm these aren't instantiated so we need to be creating
                // these.  To get rid of these instantiations, we would need
                // to change the DebugDraw so you give it local vertices and the
                // XForm to transform them with
                vertices[i] = XForm.mul(xform, localVertices[i]);
            }

            for (int i = 0; i < vertexCount; i++) {
                int j = (i + 1) % vertexCount;
                float startX = vertices[i].x;
                float startY = vertices[i].y;
                float stopX = vertices[j].x;
                float stopY = vertices[j].y;
                drawLine(gl, startX, startY, stopX, stopY, color);
            }
            //m_debugDraw.drawSolidPolygon(vertices, vertexCount, color);
//
//			if (core) {
//				final Vec2[] localCoreVertices = poly.getCoreVertices();
//				for (int i = 0; i < vertexCount; ++i) {
//					// djm same as above
//					vertices[i] = XForm.mul(xf, localCoreVertices[i]);
//				}
//				for (int i=1;i<vertexCount;i++) {
//					float startX = vertices[i-1].x;
//					float startY = vertices[i-1].y;
//					float stopX = vertices[i].x;
//					float stopY = vertices[i].y;
//					canvas.drawLine(startX, startY, stopX, stopY, mPaintCore);
//				}
//				//m_debugDraw.drawPolygon(vertices, vertexCount, coreColor);
//			}
        }
    }

    public void drawBitmap(CtkGL gl, ImageResource imageResource, Vec2 position, Vec2 size, float angle, float alpha, Vec3 color, AlphaMode alphaMode) {
        drawBitmap(gl, imageResource, 0, 0, position, size, angle, alpha, color, alphaMode);
    }

    public void drawBitmap(CtkGL gl, ImageResource imageResource, int row, int col, Vec2 position, Vec2 size, float angle, float alpha, Vec3 color, AlphaMode alphaMode) {
        float lastAlpha = gl.getAlpha();
        // 色の指定
        if (color != null) {
            gl.glColor4f(color.x, color.y, color.z, lastAlpha * alpha);
        } else {
            gl.glColor4f(1f, 1f, 1f, lastAlpha * alpha);
        }

        // アルファブレンディングの指定
        switch (alphaMode) {
            case ADD:
                gl.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
                break;
            case STD:
            default:
                gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                break;
        }

        // テクスチャの設定
        gl.glActiveTexture(GL11.GL_TEXTURE0);
        gl.glBindTexture(GL11.GL_TEXTURE_2D, imageResource.getGlTextureId(row, col));

        gl.glPushMatrix();
        gl.glTranslatef(position.x, position.y, 0);
        gl.glRotatef(angle * RAD_TO_DEG, 0, 0, 1);
        gl.glScalef(size.x, size.y, 1);
        this.shapeSquare.draw(gl);
        gl.glPopMatrix();
        gl.glColor4f(1f, 1f, 1f, lastAlpha);
    }

    public void draw9patch(CtkGL gl, ImageResource imageResource, Vec2 srcPosition, Vec2 srcSize, float alpha) {
        draw9patch(gl, imageResource, srcPosition, srcSize, null);
    }

    public void draw9patch(CtkGL gl, ImageResource imageResource, Vec2 srcPosition, Vec2 srcSize, Paint paint) {
        final float cs = HungryCatBallConstants.CELL_SIZE;
        final Vec2 pos = new Vec2();
        final Vec2 size = new Vec2();
        size.set(cs, cs);
        pos.set(srcPosition.x - (srcSize.x - cs) / 2f, srcPosition.y + (srcSize.y - cs) / 2f);
        drawBitmap(gl, imageResource, 0, 0, pos, size, 0f, 1f, null, AlphaMode.STD);
        pos.set(srcPosition.x + (srcSize.x - cs) / 2f, srcPosition.y + (srcSize.y - cs) / 2f);
        drawBitmap(gl, imageResource, 0, 2, pos, size, 0f, 1f, null, AlphaMode.STD);
        pos.set(srcPosition.x - (srcSize.x - cs) / 2f, srcPosition.y - (srcSize.y - cs) / 2f);
        drawBitmap(gl, imageResource, 2, 0, pos, size, 0f, 1f, null, AlphaMode.STD);
        pos.set(srcPosition.x + (srcSize.x - cs) / 2f, srcPosition.y - (srcSize.y - cs) / 2f);
        drawBitmap(gl, imageResource, 2, 2, pos, size, 0f, 1f, null, AlphaMode.STD);

        size.set(srcSize.x - cs * 2, cs);
        pos.set(srcPosition.x, srcPosition.y + (srcSize.y - cs) / 2f);
        drawBitmap(gl, imageResource, 0, 1, pos, size, 0f, 1f, null, AlphaMode.STD);
        pos.set(srcPosition.x, srcPosition.y - (srcSize.y - cs) / 2f);
        drawBitmap(gl, imageResource, 2, 1, pos, size, 0f, 1f, null, AlphaMode.STD);

        size.set(cs, srcSize.y - cs * 2);
        pos.set(srcPosition.x - (srcSize.x - cs) / 2f, srcPosition.y);
        drawBitmap(gl, imageResource, 1, 0, pos, size, 0f, 1f, null, AlphaMode.STD);
        pos.set(srcPosition.x + (srcSize.x - cs) / 2f, srcPosition.y);
        drawBitmap(gl, imageResource, 1, 2, pos, size, 0f, 1f, null, AlphaMode.STD);

        size.set(srcSize.x - cs * 2, srcSize.y - cs * 2);
        pos.set(srcPosition.x, srcPosition.y);
        drawBitmap(gl, imageResource, 1, 1, pos, size, 0f, 1f, null, AlphaMode.STD);
    }

    private void loadImage(GL11 gl, Resources res, ImageResource imageResource) {
        int rows = imageResource.getRows();
        int cols = imageResource.getCols();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        // 画像は上下反転する
        Bitmap src = BitmapFactory.decodeResource(res, imageResource.getResourceId());
        int w = src.getWidth() / cols;
        int h = src.getHeight() / rows;

//		if ( Math.abs((Math.log(w) / Math.log(2)) % 1 ) > 0.1) {
//			throw new RuntimeException("Invalid width:" + w);
//		}
//		if ( Math.abs((Math.log(h) / Math.log(2)) % 1 ) > 0.1) {
//			throw new RuntimeException("Invalid width:" + h);
//		}

        int[][] glTextureIds = new int[rows][cols];
        int[] textures = new int[rows * cols];
        gl.glGenTextures(textures.length, textures, 0);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Bitmap tBitmap = Bitmap.createBitmap(src, w * c, h * r, w, h, matrix, false);
                glTextureIds[r][c] = textures[cols * r + c];
                updateTexture(gl, textures[cols * r + c], tBitmap);
                tBitmap.recycle();
            }
        }
        src.recycle();
        imageResource.setGlTextureIds(glTextureIds);
    }

    private void updateTexture(GL11 gl, int TextureId, Bitmap bitmap) {
        gl.glBindTexture(GL11.GL_TEXTURE_2D, TextureId);

        gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                GL11.GL_LINEAR);
        gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
                GL11.GL_LINEAR);

        gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
                GL11.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
                GL11.GL_CLAMP_TO_EDGE);

        gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE,
                GL11.GL_REPLACE);

        GLUtils.texImage2D(GL11.GL_TEXTURE_2D, 0, bitmap, 0);
    }

    public void drawNumber(CtkGL gl, int value, int digit, Vec2 srcPosition, float scale, Align align, Vec3 color, float alpha) {
        if (digit == 0) {
            int t = value;
            do {
                t /= 10;
                digit++;
            } while (t > 0);
        }

        ImageResource imageResource = getImageResource(TextureId.UI_CHAR);
        final Vec2 position = new Vec2();
        final Vec2 size = new Vec2();
        position.set(srcPosition);
        size.set(scale, scale);

        int s = value;
        switch (align) {
            default:
            case LEFT:
                position.x += scale * (float) (digit - 1) / 2f;
                break;
            case CENTER:
                position.x += scale * (float) (digit - 1) / 4f;
                break;
            case RIGHT:
                break;
        }
        for (int i = 0; i < digit; i++) {
            int t = s % 10;
            if (imageResource == null) {
                throw new RuntimeException();
            }
            drawBitmap(gl, imageResource, 1, t, position, size, 0f, alpha, color, AlphaMode.STD);
            s /= 10;
            position.x -= scale / 2f;
        }
    }

    public void drawChars(CtkGL gl, char[] value, Vec2 srcPosition, float scale, Align align, Vec3 color, float alpha) {
        ImageResource imageResource = getImageResource(TextureId.UI_CHAR);
        final Vec2 position = new Vec2();
        final Vec2 size = new Vec2();
        position.set(srcPosition);
        size.set(scale, scale);

        switch (align) {
            default:
            case LEFT:
                break;
            case CENTER:
                position.x -= scale * (float) (value.length - 1) / 4f;
                break;
            case RIGHT:
                position.x -= scale * (float) (value.length - 1) / 2f;
                break;
        }
        if (imageResource == null) {
            throw new RuntimeException();
        }
        for (int i = 0; i < value.length; i++) {
            char v = value[i];
            if (0x20 <= v && v <= 0x7F) {
                int r = (v - 0x20) / 16;
                int c = (v - 0x20) % 16;
                drawBitmap(gl, imageResource, r, c, position, size, 0f, alpha, color, AlphaMode.STD);
            }
            position.x += scale / 2f;
        }
    }

    public enum Align {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum AlphaMode {
        STD,
        ADD
    }
}
