package net.cattaka.hungrycatball.core;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import net.cattaka.hungrycatball.core.UserInput.TouchState;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.scene.IScene;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import static net.cattaka.hungrycatball.HungryCatBallConstants.TAG;
import static net.cattaka.hungrycatball.HungryCatBallConstants.WORLD_RECT;

abstract public class GameRenderer implements GLSurfaceView.Renderer, IScreenHandler {
    long mNextFrameTime;
    private CtkGL mCtkGl;
    private IUiCallbackHander mUiCallbackHander;
    private Context mContext;
    private IScene mScene;
    private SceneBundle mSceneBundle;
    private Object mutex = new Object();
    private UserInput mTempUserInput = new UserInput();
    private Handler mHandler;

    public GameRenderer(Context mContext, IScene mScene, SceneBundle mSceneBundle, IUiCallbackHander uiCallbackHander) {
        super();
        this.mContext = mContext;
        this.mScene = mScene;
        this.mSceneBundle = mSceneBundle;
        this.mUiCallbackHander = uiCallbackHander;
        this.mHandler = new Handler();
    }

    public void initialize() {
        mSceneBundle.setScreenHandler(this);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCtkGl = (CtkGL) gl;
        double ww = WORLD_RECT.width();
        double wh = WORLD_RECT.height();

        double cw = width;
        double ch = height;
        double canvasAspect = cw / ch;
        double worldAspect = Math.abs(ww / wh);
        if (canvasAspect > worldAspect) {
            gl.glViewport((int) (cw - ch * worldAspect) / 2, 0, (int) (ch * worldAspect), (int) ch);
        } else {
            gl.glViewport(0, (int) (ch - cw / worldAspect) / 2, (int) cw, (int) (cw / worldAspect));
        }

        setupMatrix(mCtkGl, false, 0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCtkGl = (CtkGL) gl;
        // 背景塗り潰し色の指定
        gl.glClearColor(0f, 0f, 0f, 1);

//		float[] square = new float[] {	// 頂点
//				WORLD_RECT.left	+ 1.0f,  WORLD_RECT.bottom +1f, 0.0f,		//
//				WORLD_RECT.right- 1.0f,  WORLD_RECT.bottom +1f, 0.0f,		//
//				WORLD_RECT.left	+ 1.0f,  WORLD_RECT.top -1f, 0.0f,		//
//				WORLD_RECT.right- 1.0f,  WORLD_RECT.top -1f, 0.0f		//
//		};
//		float[] vertices = {
//				// 画像データの左上を原点に、0.0~1.0の範囲の座標
//				0.0f, 1.0f,		//左下
//				1.0f, 1.0f,		//右下
//				0.0f, 0.0f,		//左上
//				1.0f, 0.0f,		//右上
//		};
//		squareBuff = getFloatBuffer(square);// 頂点バッファ
//		textureBuff = getFloatBuffer(vertices);// 頂点バッファ
//
//
//		ByteBuffer ibb = ByteBuffer.allocateDirect(vertices.length * 2);
//		ibb.order(ByteOrder.nativeOrder());
//		indexBuffer = ibb.asShortBuffer();
//		for (int i = 0; i < vertices.length; i++) {
//			indexBuffer.put((short) i);
//		}
//		indexBuffer.position(0);
        mNextFrameTime = SystemClock.elapsedRealtime() + 33;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mCtkGl = (CtkGL) gl;
        gl.glDisable(GL11.GL_DITHER);
        gl.glDisable(GL11.GL_DEPTH_TEST);
        gl.glEnable(GL11.GL_BLEND);
        gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        //gl.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // 各種クリア
        gl.glTexEnvx(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        gl.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL11.GL_MODELVIEW);

        // 共通の設定
        gl.glFrontFace(GL11.GL_CCW);
        gl.glEnable(GL11.GL_TEXTURE_2D);
        gl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        gl.glTexParameterx(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP_TO_EDGE);
        gl.glTexParameterx(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP_TO_EDGE);

//		if (false) {
//			ImageResource imageResource = mSceneBundle.getDrawUtil().getImageResource(TextureId.BG_TYPE_1);
//			gl.glActiveTexture(GL11.GL_TEXTURE0);
//			gl.glBindTexture(GL11.GL_TEXTURE_2D, imageResource.getGlTextureId(0, 0));
//
//			//色の指定
//			gl.glColor4f(1,0,0,0.5f);
//
//			//頂点配列の指定
//			gl.glVertexPointer(3,GL11.GL_FLOAT,0,squareBuff);
//			gl.glTexCoordPointer(2, GL11.GL_FLOAT, 0, textureBuff);
//			gl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
//
//			//プリミティブの描画
//			gl.glDrawArrays(GL11.GL_TRIANGLE_STRIP,0,4);
//		}
//
//		if (false) {
//			ImageResource imageResource = mSceneBundle.getDrawUtil().getImageResource(TextureId.BG_TYPE_1);
//			gl.glActiveTexture(GL11.GL_TEXTURE0);
//			gl.glBindTexture(GL11.GL_TEXTURE_2D, imageResource.getGlTextureId(0, 0));
//
//			//色の指定
//			gl.glColor4f(1,0,0,0.5f);
//
//			//頂点配列の指定
//			gl.glVertexPointer(3,GL11.GL_FLOAT,0,squareBuff);
//			gl.glTexCoordPointer(2, GL11.GL_FLOAT, 0, textureBuff);
//			gl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
//
//			//プリミティブの描画
//			//gl.glDrawArrays(GL11.GL_TRIANGLE_STRIP,0,4);
//			gl.glDrawElements(GL11.GL_TRIANGLE_STRIP, 4,
//					GL11.GL_UNSIGNED_SHORT, indexBuffer);
//		}

        long weight = 0;
        synchronized (mutex) {
            final int MAX_FRAME_SKIP = 5;
            for (int i = 0; i < MAX_FRAME_SKIP; i++) {
                synchronized (mTempUserInput) {
                    mSceneBundle.getUserInput().set(mTempUserInput);
                    mTempUserInput.step();
                }
                {    // ゲームのステップの処理をここで行う
                    mScene.step(mSceneBundle);
                    IScene newScene = mScene.moveNextScene();
                    if (mScene != newScene) {
                        newScene.initialize(mSceneBundle);
                        mScene = newScene;
                        i = MAX_FRAME_SKIP;        // スキップ禁止
                    }
                    AbstractUiCallback uiCallback = mScene.pullUiCallback();
                    if (uiCallback != null && mUiCallbackHander != null) {
                        mHandler.post(new PublishProgress(uiCallback));
                        i = MAX_FRAME_SKIP;        // スキップ禁止
                    }
                }
                {    // フレームスキップすべきか否かをここで判定する
                    long ct = SystemClock.elapsedRealtime();
                    long diff = mNextFrameTime - ct;
                    if (0 <= diff && diff <= 99) {
                        weight = diff;
                        mNextFrameTime += 33;
                        // breakして描画処理に移る (通常はこれ)
                        i = MAX_FRAME_SKIP;                // スキップ不要
                    } else if (-99 <= diff && diff < 0) {
                        mNextFrameTime += 33;
                        // フレームのスキップをする
                    } else {
                        mNextFrameTime = ct + 33;
                        // フレームのスキップをする
                    }
                }
            }
            mSceneBundle.getSoundUtil().step();
            mScene.draw(mCtkGl, mSceneBundle);
        }
        if (weight > 0) {
            try {
                Thread.sleep(weight);
            } catch (InterruptedException e) {
                Log.d(TAG, e.getMessage(), e);
            }
        }
    }


//	FloatBuffer squareBuff;// 頂点バッファ
//	FloatBuffer textureBuff;
//	ShortBuffer indexBuffer;
//	
//	private FloatBuffer getFloatBuffer(float[] array) {
//		FloatBuffer floatBuffer;
//		ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
//		bb.order(ByteOrder.nativeOrder());
//		floatBuffer = bb.asFloatBuffer();
//		floatBuffer.put(array);
//		floatBuffer.position(0);
//		return floatBuffer;
//	}

    @Override
    public void setupMatrix(boolean fromScene, float bottomMargin) {
        if (mCtkGl != null) {
            setupMatrix(mCtkGl, fromScene, bottomMargin);
        }
    }

    public void setupMatrix(CtkGL ctkGl, boolean fromScene, float bottomMargin) {
        if (fromScene) {
            // 射影行列の指定
            ctkGl.glMatrixMode(GL11.GL_PROJECTION);
            ctkGl.glLoadIdentity();

            // 視点の調整
            ctkGl.glFrustumf(WORLD_RECT.left / 2f, WORLD_RECT.right / 2f, (WORLD_RECT.bottom - bottomMargin) / 2f, WORLD_RECT.top / 2f, 5, 15);
            GLU.gluLookAt(ctkGl, 0, 0, 10, 0, 0, 0, 0, 1, 0);
        } else {
            synchronized (mutex) {
                // 射影行列の指定
                ctkGl.glMatrixMode(GL11.GL_PROJECTION);
                ctkGl.glLoadIdentity();

                // 視点の調整
                ctkGl.glFrustumf(WORLD_RECT.left / 2f, WORLD_RECT.right / 2f, (WORLD_RECT.bottom - bottomMargin) / 2f, WORLD_RECT.top / 2f, 5, 15);
                GLU.gluLookAt(ctkGl, 0, 0, 10, 0, 0, 0, 0, 1, 0);
            }
        }
    }

    public InputStream open(String fileName) throws IOException {
        return mContext.getAssets().open(fileName);
    }

    public void inputTouchEvent(MotionEvent e) {
        synchronized (mTempUserInput) {
            final float[] dest = new float[2];
            mCtkGl.calcWorldPos(dest, e.getX(), e.getY());

            //Log.d(TAG, String.format("A : %f, %f", dest[0], dest[1]));
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                mTempUserInput.currentTouchStateConsumed = false;
                mTempUserInput.currentTouchState = TouchState.PRESSE;
            } else if (e.getAction() == MotionEvent.ACTION_UP) {
                mTempUserInput.currentTouchStateConsumed = false;
                mTempUserInput.currentTouchState = TouchState.RELEASE;
            }
            mTempUserInput.currentPosition.set(dest[0], dest[1]);
        }
    }

    public boolean onKey(int keyCode, KeyEvent e) {
        synchronized (mTempUserInput) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    mTempUserInput.currentMenuStateConsumed = false;
                    mTempUserInput.currentMenuState = TouchState.PRESSE;
                } else if (e.getAction() == MotionEvent.ACTION_UP) {
                    mTempUserInput.currentMenuStateConsumed = false;
                    mTempUserInput.currentMenuState = TouchState.RELEASE;
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    mTempUserInput.currentBackStateConsumed = false;
                    mTempUserInput.currentBackState = TouchState.PRESSE;
                } else if (e.getAction() == MotionEvent.ACTION_UP) {
                    mTempUserInput.currentBackStateConsumed = false;
                    mTempUserInput.currentBackState = TouchState.RELEASE;
                }
                return true;
            }
            return false;
        }
    }

    public void proceedUiCallback(AbstractUiCallback uiCallback, Object value) {
        synchronized (mutex) {
            if (value != null) {
                uiCallback.onFinished(value);
            } else {
                uiCallback.onCancel();
            }
        }
    }

    public static interface IUiCallbackHander {
        public void handle(AbstractUiCallback uiCallback);
    }

    class PublishProgress implements Runnable {
        private AbstractUiCallback uiCallback;

        public PublishProgress(AbstractUiCallback uiCallback) {
            this.uiCallback = uiCallback;
        }

        @Override
        public void run() {
            mUiCallbackHander.handle(uiCallback);
        }
    }
}
