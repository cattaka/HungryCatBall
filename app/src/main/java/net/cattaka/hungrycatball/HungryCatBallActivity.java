
package net.cattaka.hungrycatball;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import net.cattaka.hungrycatball.core.AbstractUiCallback;
import net.cattaka.hungrycatball.core.GameRenderer;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.db.HungryCatDbHelper;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.scene.IScene;
import net.cattaka.hungrycatball.scene.StartupScene;
import net.cattaka.hungrycatball.setting.GameSetting;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class HungryCatBallActivity extends Activity {
    private static final int DIALOG_SELECT = 1;

    private static final int DIALOG_INPUT = 2;

    class GameRendererEx extends GameRenderer {
        public GameRendererEx(Context mContext, IScene mScene, SceneBundle mSceneBundle,
                              net.cattaka.hungrycatball.core.GameRenderer.IUiCallbackHander uiCallbackHander) {
            super(mContext, mScene, mSceneBundle, uiCallbackHander);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            super.onSurfaceCreated(gl, config);
            mSceneBundle.initialize((GL11) gl, HungryCatBallActivity.this);
        }
    }

    private GLSurfaceView mSurfaceView;

    private IScene mScene;

    private SceneBundle mSceneBundle;

    private GameRenderer mGameRenderer;

    private View.OnTouchListener mOnTouchListenerImpl = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mGameRenderer != null) {
                mGameRenderer.inputTouchEvent(event);
            }
            return true;
        }
    };

    private AbstractUiCallback mUiCallback;

    private GameRenderer.IUiCallbackHander mUiCallbackHander = new GameRenderer.IUiCallbackHander() {
        @Override
        public void handle(AbstractUiCallback uiCallback) {
            mUiCallback = uiCallback;
            switch (uiCallback.getUiType()) {
                case UI_SELECT:
                    showDialog(DIALOG_SELECT);
                    break;
                case UI_INPUT:
                    showDialog(DIALOG_INPUT);
                    break;
                case UI_QUIT:
                    finish();
                    break;
                default:
                    mUiCallback = null;
                    Log.d(HungryCatBallConstants.TAG,
                            "Unknown UiType:" + uiCallback.getUiType());
                    break;
            }
        }
    };

    private HungryCatDbHelper mDbHelper;

    private GameSetting mGameSetting;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        HungryCatBallConstants.VERSION = getVersion();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mSceneBundle = new SceneBundle() {
            @Override
            public HungryCatDbHelper getDbHelper() {
                return mDbHelper;
            }
        };

        mSurfaceView = (GLSurfaceView) findViewById(R.id.surfaceView);
        mSurfaceView.setGLWrapper(new GLSurfaceView.GLWrapper() {
            public GL wrap(GL gl) {
                return new CtkGL(gl);
            }
        });
        mSurfaceView.setOnTouchListener(mOnTouchListenerImpl);

        // mSceneBundle.setDebugMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        { // 設定をロードする
            loadSetting();
            mSceneBundle.getSoundUtil().setEnableSound(mGameSetting.isEnableSound());
        }
        if (mSceneBundle.isDebugMode()) {
            Intent intent = new Intent(this, TelnetSqliteService.class);
            startService(intent);
        }
        if (mDbHelper != null) {
            mDbHelper.close();
            mDbHelper = null;
        }
        mDbHelper = new HungryCatDbHelper(this, HungryCatBallConstants.DB_NAME);
        if (mGameRenderer == null) {
            // mScene = new TitleScene();
            // mScene = new CongratulationsScene();
            mScene = new StartupScene();
            mGameRenderer = new GameRendererEx(this, mScene, mSceneBundle, mUiCallbackHander);
            mGameRenderer.initialize();
            mScene.initialize(mSceneBundle);
            mSurfaceView.setRenderer(mGameRenderer);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        { // 設定を保存する
            mGameSetting.setEnableSound(mSceneBundle.getSoundUtil().isEnableSound());
            saveSetting();
        }
        mSceneBundle.release();
        if (mDbHelper != null) {
            mDbHelper.close();
            mDbHelper = null;
        }
        if (mSceneBundle.isDebugMode()) {
            Intent intent = new Intent(this, TelnetSqliteService.class);
            stopService(intent);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_SELECT: {
                ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(this,
                        R.layout.item_list_line);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Title");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Object item = null;
                        if (mUiCallback != null && mUiCallback.getSelectItems() != null
                                && 0 <= which && which < mUiCallback.getSelectItems().size()) {
                            item = mUiCallback.getSelectItems().get(which);
                        }
                        if (mGameRenderer != null && item != null) {
                            mGameRenderer.proceedUiCallback(mUiCallback, item);
                        }
                        dialog.dismiss();
                    }
                });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mGameRenderer.proceedUiCallback(mUiCallback, null);
                    }
                });
                return builder.create();
            }
            case DIALOG_INPUT: {
                View view = LayoutInflater.from(this).inflate(R.layout.item_input, null);
                EditText inputEdit = (EditText) view.findViewById(R.id.input_edit);
                class OnClickListenerImpl implements DialogInterface.OnClickListener {
                    private EditText mInputEdit;

                    public OnClickListenerImpl(EditText inputEdit) {
                        mInputEdit = inputEdit;
                    }

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mGameRenderer != null) {
                            if (which == DialogInterface.BUTTON1) {
                                mGameRenderer.proceedUiCallback(mUiCallback,
                                        String.valueOf(mInputEdit.getText()));
                            } else {
                                dialog.cancel();
                            }
                        }
                        dialog.dismiss();
                    }
                }
                ;
                OnClickListenerImpl onClickListenerImpl = new OnClickListenerImpl(inputEdit);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Title");
                builder.setView(view);
                builder.setPositiveButton(android.R.string.ok, onClickListenerImpl);
                builder.setNegativeButton(android.R.string.cancel, onClickListenerImpl);
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mGameRenderer.proceedUiCallback(mUiCallback, null);
                    }
                });
                return builder.create();
            }
            default: {
                return super.onCreateDialog(id);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DIALOG_SELECT: {
                AlertDialog alertDialog = (AlertDialog) dialog;
                ArrayAdapter<Object> adapter = (ArrayAdapter<Object>) alertDialog.getListView()
                        .getAdapter();
                adapter.clear();
                if (mUiCallback != null && mUiCallback.getSelectItems() != null) {
                    alertDialog.setTitle(String.valueOf(mUiCallback.getTitle()));
                    for (Object obj : mUiCallback.getSelectItems()) {
                        adapter.add(obj);
                    }
                }
                adapter.notifyDataSetChanged();
                break;
            }
            case DIALOG_INPUT: {
                AlertDialog alertDialog = (AlertDialog) dialog;
                EditText inputEdit = (EditText) dialog.findViewById(R.id.input_edit);
                if (mUiCallback != null) {
                    alertDialog.setTitle(String.valueOf(mUiCallback.getTitle()));
                    inputEdit.setText(String.valueOf(mUiCallback.getInputDefault()));
                }
                break;
            }
            default: {
                super.onPrepareDialog(id, dialog);
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mGameRenderer != null && mGameRenderer.onKey(keyCode, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mGameRenderer != null && mGameRenderer.onKey(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public String getVersion() {
        String versionName = "";
        String packageName = this.getClass().getPackage().getName();
        PackageManager pm = this.getPackageManager();
        try {
            PackageInfo info = null;
            info = pm.getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (NameNotFoundException e) {
        }
        return versionName;
    }

    private void loadSetting() {
        // TODO もうちょいマシな実装にする
        mGameSetting = new GameSetting();
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            Boolean enableSound = pref.getBoolean("enableSound", true);
            enableSound = (enableSound != null) ? enableSound : true;
            mGameSetting.setEnableSound(enableSound);
        } catch (ClassCastException e) {
            // ありえないと思うが念の為
            Log.e(HungryCatBallConstants.TAG, e.getMessage(), e);
        }
    }

    private void saveSetting() {
        // TODO もうちょいマシな実装にする
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = pref.edit();
        editor.putBoolean("enableSound", mSceneBundle.getSoundUtil().isEnableSound());
        ;
        editor.commit();
    }

}
