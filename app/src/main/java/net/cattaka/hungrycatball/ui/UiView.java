package net.cattaka.hungrycatball.ui;

import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.core.UserInput.PhysicalButton;
import net.cattaka.hungrycatball.core.UserInput.TouchState;
import net.cattaka.hungrycatball.gl.CtkGL;

import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;

import java.util.ArrayList;
import java.util.List;

public class UiView {
    protected Vec2 mPosition = new Vec2();
    protected Vec2 mSize = new Vec2();
    protected TouchState mTouchState = TouchState.RELEASED;
    private OnClickListener mOnClickListener;
    private OnVisibleListener mOnVisibleListener;
    private String mAction;
    private Object mExData;
    private boolean mVisibleState = true;
    private float mVisiblity = 1.0f;
    private List<PhysicalButton> mBindedButtons;
    public UiView(Vec2 position, Vec2 size) {
        super();
        this.mPosition.set(position);
        this.mSize.set(size);
    }

    public void getPosition(Vec2 dst) {
        dst.set(mPosition);
    }

    public void setPosition(Vec2 src) {
        mPosition.set(src);
    }

    public void getSize(Vec2 dst) {
        dst.set(mSize);
    }

    public void setSize(Vec2 src) {
        mSize.set(src);
    }

    public TouchState getTouchState() {
        return mTouchState;
    }

    public void setTouchState(TouchState touchState) {
        this.mTouchState = touchState;
    }

    public void step(SceneBundle sceneBundle) {
        int lastState = (mVisiblity == 0.0f) ? -1 : (mVisiblity == 1.0f) ? 1 : 0;
        mVisiblity += (mVisibleState) ? 0.1f : -0.1f;
        mVisiblity = Math.min(1.0f, mVisiblity);
        mVisiblity = Math.max(0.0f, mVisiblity);
        int nextState = (mVisiblity == 0.0f) ? -1 : (mVisiblity == 1.0f) ? 1 : 0;
        if (mOnVisibleListener != null) {
            if (nextState == -1 && lastState != -1) {
                mOnVisibleListener.onInvisible(this);
            }
            if (nextState == 1 && lastState != 1) {
                mOnVisibleListener.onVisible(this);
            }
        }
        if (mVisiblity < 1.0f) {
            return;
        }
        Vec2 position = sceneBundle.getUserInput().currentPosition;

        // タッチステートのハンドリング
        if (!sceneBundle.getUserInput().currentTouchStateConsumed) {
            if (isIntersect(position)) {
                sceneBundle.getUserInput().currentTouchStateConsumed = true;
                switch (sceneBundle.getUserInput().currentTouchState) {
                    case PRESSE:
                        mTouchState = TouchState.PRESSE;
                        break;
                    case PRESSED:
                        if (mTouchState == TouchState.PRESSE) {
                            mTouchState = TouchState.PRESSED;
                        }
                        break;
                    case RELEASE:
                        if (mTouchState == TouchState.PRESSE || mTouchState == TouchState.PRESSED) {
                            mTouchState = TouchState.RELEASE;
                        }
                        break;
                    case RELEASED:
                        mTouchState = TouchState.RELEASED;
                        break;
                    default:
                        break;
                }
            } else {
                mTouchState = TouchState.RELEASED;
            }
        } else {
            mTouchState = TouchState.RELEASED;
        }
        // 物理ボタンのハンドリング
        if (mBindedButtons != null) {
            for (PhysicalButton bindedButton : mBindedButtons) {
                boolean consumed = false;
                TouchState state = TouchState.RELEASED;
                // FIXME ボタン少ないから良いけど、数を増やすならちゃんと汎化すること
                if (bindedButton == PhysicalButton.MENU) {
                    consumed = sceneBundle.getUserInput().currentMenuStateConsumed;
                    state = sceneBundle.getUserInput().currentMenuState;
                    sceneBundle.getUserInput().currentMenuStateConsumed = true;
                } else if (bindedButton == PhysicalButton.BACK) {
                    consumed = sceneBundle.getUserInput().currentBackStateConsumed;
                    state = sceneBundle.getUserInput().currentBackState;
                    sceneBundle.getUserInput().currentBackStateConsumed = true;
                }
                // 物理ボタンの状態をこのUiに引き渡す
                if (!consumed) {
                    if (mTouchState.ordinal() < state.ordinal()) {
                        mTouchState = state;
                    }
                }
            }
        }
        if (mOnClickListener != null) {
            if (mTouchState == TouchState.RELEASE) {
                mOnClickListener.onClick(this, mAction, mExData);
            }
        }
    }

    public void draw(CtkGL gl, SceneBundle sceneBundle) {
    }

    public void drawDebug(CtkGL gl, SceneBundle sceneBundle) {
        final Vec3 color = new org.jbox2d.common.Vec3(1, 1, 0);
        sceneBundle.getDrawUtil().drawLine(gl, mPosition.x - mSize.x / 2, mPosition.y - mSize.y / 2, mPosition.x + mSize.x / 2, mPosition.y - mSize.y / 2, color);
        sceneBundle.getDrawUtil().drawLine(gl, mPosition.x + mSize.x / 2, mPosition.y - mSize.y / 2, mPosition.x + mSize.x / 2, mPosition.y + mSize.y / 2, color);
        sceneBundle.getDrawUtil().drawLine(gl, mPosition.x + mSize.x / 2, mPosition.y + mSize.y / 2, mPosition.x - mSize.x / 2, mPosition.y + mSize.y / 2, color);
        sceneBundle.getDrawUtil().drawLine(gl, mPosition.x - mSize.x / 2, mPosition.y + mSize.y / 2, mPosition.x - mSize.x / 2, mPosition.y - mSize.y / 2, color);
    }

    public boolean isIntersect(Vec2 position) {
        final Vec2 diff = new Vec2();
        diff.set(position);
        diff.subLocal(mPosition);
        return -mSize.x / 2f <= diff.x && diff.x <= mSize.x / 2f
                && -mSize.y / 2f <= diff.y && diff.y <= mSize.y / 2f;
    }

    public boolean isVisible() {
        return mVisibleState || mVisiblity > 0;
    }

    public boolean isVisibleState() {
        return mVisibleState;
    }

    public void setVisibleState(boolean visibleState) {
        this.mVisibleState = visibleState;
    }

    public float getVisiblity() {
        return mVisiblity;
    }

    public void setVisiblity(float visiblity) {
        this.mVisiblity = visiblity;
    }

    public void addBindedButton(PhysicalButton bindedButton) {
        if (mBindedButtons == null) {
            mBindedButtons = new ArrayList<PhysicalButton>();
        }
        mBindedButtons.add(bindedButton);
    }

    public void setOnClickListener(OnClickListener onClickListener, String action, Object exData) {
        this.mOnClickListener = onClickListener;
        this.mAction = action;
        this.mExData = exData;
    }

    public OnVisibleListener getOnVisibleListener() {
        return mOnVisibleListener;
    }

    public void setOnVisibleListener(OnVisibleListener onVisibleListener) {
        this.mOnVisibleListener = onVisibleListener;
    }

    public interface OnClickListener {
        public void onClick(UiView uiView, String action, Object exData);
    }

    public interface OnVisibleListener {
        public void onVisible(UiView uiView);

        public void onInvisible(UiView uiView);
    }
}
