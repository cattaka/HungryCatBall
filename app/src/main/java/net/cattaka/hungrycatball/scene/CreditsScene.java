package net.cattaka.hungrycatball.scene;

import net.cattaka.hungrycatball.core.AbstractUiCallback;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.core.UserInput.PhysicalButton;
import net.cattaka.hungrycatball.core.UserInput.TouchState;
import net.cattaka.hungrycatball.frame.BackgroundFrame;
import net.cattaka.hungrycatball.frame.ISceneFrameListener;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.ui.UiButton;
import net.cattaka.hungrycatball.ui.UiView;
import net.cattaka.hungrycatball.utils.DrawingUtil.Align;
import net.cattaka.hungrycatball.utils.DrawingUtil.AlphaMode;
import net.cattaka.hungrycatball.utils.ImageResource;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;

import java.util.ArrayList;
import java.util.List;

import static net.cattaka.hungrycatball.HungryCatBallConstants.CELL_SIZE;
import static net.cattaka.hungrycatball.HungryCatBallConstants.UI_BACK_BUTTON_POS;
import static net.cattaka.hungrycatball.HungryCatBallConstants.VERSION;
import static net.cattaka.hungrycatball.HungryCatBallConstants.WORLD_RECT;

public class CreditsScene implements IScene, UiView.OnClickListener, ISceneFrameListener {
    private static final float ANIM_STEP = 0.1f;
    private static final float SCROLL_MARGIN = 2f;
    private UiButton mBackButton;
    private List<ScrollItem> mScrollItems;
    private IScene mNextScene;
    private GameWorld mGameWorld;
    private BackgroundFrame mBackgroundFrame;
    private Vec2 mLastTouchPosition;
    private float mOffset;
    public CreditsScene() {
        mLastTouchPosition = new Vec2();
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        mBackgroundFrame.draw(gl, sceneBundle, mGameWorld);

        final Vec2 pos = new Vec2();
        pos.y = WORLD_RECT.bottom + mOffset - SCROLL_MARGIN;
        for (ScrollItem scrolltem : mScrollItems) {
            if (WORLD_RECT.bottom - SCROLL_MARGIN <= pos.y && pos.y <= WORLD_RECT.top + SCROLL_MARGIN) {
                if (scrolltem.getChars() != null) {
                    sceneBundle.getDrawUtil().drawChars(gl, scrolltem.getChars(), pos, scrolltem.getScale(), scrolltem.getAlign(), scrolltem.getColor(), 1);
                }
                if (scrolltem.getImageResource() != null) {
                    sceneBundle.getDrawUtil().drawBitmap(gl, scrolltem.getImageResource(), scrolltem.getImageRow(), scrolltem.getImageCol(), pos, scrolltem.getSize(), 0, 1, scrolltem.getColor(), AlphaMode.ADD);
                }
            }
            pos.y -= 1;
        }

        mBackButton.draw(gl, sceneBundle);
    }

    @Override
    public void step(SceneBundle sceneBundle) {
        mBackButton.step(sceneBundle);
        mBackgroundFrame.step(mGameWorld, sceneBundle, null, this);

        if (sceneBundle.getUserInput().currentTouchState == TouchState.PRESSED) {
            mOffset += sceneBundle.getUserInput().currentPosition.y - mLastTouchPosition.y;
        } else {
            mOffset += ANIM_STEP;
        }
        float height = mScrollItems.size() + Math.abs(WORLD_RECT.height()) + SCROLL_MARGIN * 2;
        if (mOffset > height) {
            mOffset -= height;
        }
        if (mOffset < 0) {
            mOffset += height;
        }


        mLastTouchPosition.set(sceneBundle.getUserInput().currentPosition);
    }

    @Override
    public void initialize(SceneBundle sceneBundle) {
        mBackButton = UiButton.createUiPanel(UI_BACK_BUTTON_POS, new Vec2(CELL_SIZE * 3, CELL_SIZE * 3), sceneBundle, ImageResource.COL_UI_BUTTON_LEFT);
        mBackButton.setVisiblity(0.0f);
        mBackButton.setOnClickListener(this, "back", null);
        mBackButton.addBindedButton(PhysicalButton.BACK);

        mGameWorld = new GameWorld();
        mGameWorld.createWorld();
        mGameWorld.setBgImageResource(sceneBundle.getDrawUtil().getImageResource(TextureId.BG_TYPE_1));
        mBackgroundFrame = new BackgroundFrame();
        mBackgroundFrame.initialize(sceneBundle, null);

        Vec3 colorYellow = new Vec3(1, 1, 0);
        Vec3 colorWhite = new Vec3(1, 1, 1);
        mScrollItems = new ArrayList<ScrollItem>();
        mScrollItems.add(new ScrollItem(sceneBundle.getDrawUtil().getImageResource(TextureId.MSG_TITLE), new Vec2(12, 3), 0, 0));
        mScrollItems.add(new ScrollItem());
        mScrollItems.add(new ScrollItem(String.format("version %s", VERSION).toCharArray(), Align.CENTER, 1, colorWhite));
        mScrollItems.add(new ScrollItem("Copyright 2011".toCharArray(), Align.CENTER, 1, colorWhite));
        mScrollItems.add(new ScrollItem("by Takao Sumitomo".toCharArray(), Align.CENTER, 1, colorWhite));
        mScrollItems.add(new ScrollItem());
        mScrollItems.add(new ScrollItem("Credits".toCharArray(), Align.CENTER, 2, colorYellow));
        mScrollItems.add(new ScrollItem());
        mScrollItems.add(new ScrollItem("- Producer -".toCharArray(), Align.CENTER, 1.5f, colorYellow));
        mScrollItems.add(new ScrollItem("Takao Sumitomo".toCharArray(), Align.CENTER, 1, colorWhite));
        mScrollItems.add(new ScrollItem());
        mScrollItems.add(new ScrollItem("- Programmer -".toCharArray(), Align.CENTER, 1.5f, colorYellow));
        mScrollItems.add(new ScrollItem("Takao Sumitomo".toCharArray(), Align.CENTER, 1, colorWhite));
        mScrollItems.add(new ScrollItem());
        mScrollItems.add(new ScrollItem("- Designer -".toCharArray(), Align.CENTER, 1.5f, colorYellow));
        mScrollItems.add(new ScrollItem("Takao Sumitomo".toCharArray(), Align.CENTER, 1, colorWhite));
        mScrollItems.add(new ScrollItem());
        mScrollItems.add(new ScrollItem("- Graphic Artist -".toCharArray(), Align.CENTER, 1.5f, colorYellow));
        mScrollItems.add(new ScrollItem("Takao Sumitomo".toCharArray(), Align.CENTER, 1, colorWhite));
        mScrollItems.add(new ScrollItem());
        mScrollItems.add(new ScrollItem("- Level Designer -".toCharArray(), Align.CENTER, 1.5f, colorYellow));
        mScrollItems.add(new ScrollItem("Takao Sumitomo".toCharArray(), Align.CENTER, 1, colorWhite));
        mScrollItems.add(new ScrollItem());
        mScrollItems.add(new ScrollItem("- Sound -".toCharArray(), Align.CENTER, 1.5f, colorYellow));
        mScrollItems.add(new ScrollItem("the freesound project".toCharArray(), Align.CENTER, 1, colorWhite));
        mScrollItems.add(new ScrollItem("http://www.freesound.org/".toCharArray(), Align.CENTER, 1, colorWhite));
        mScrollItems.add(new ScrollItem("MisterTood".toCharArray(), Align.CENTER, 1, colorWhite));
        mScrollItems.add(new ScrollItem("3bagbrew".toCharArray(), Align.CENTER, 1, colorWhite));
        mScrollItems.add(new ScrollItem("adcbicycle".toCharArray(), Align.CENTER, 1, colorWhite));
        mScrollItems.add(new ScrollItem("NoiseCollector".toCharArray(), Align.CENTER, 1, colorWhite));
        mScrollItems.add(new ScrollItem("zeuss".toCharArray(), Align.CENTER, 1, colorWhite));
        mScrollItems.add(new ScrollItem("hum".toCharArray(), Align.CENTER, 1, colorWhite));
        mScrollItems.add(new ScrollItem());
        mScrollItems.add(new ScrollItem("- Powered By -".toCharArray(), Align.CENTER, 1.5f, colorYellow));
        mScrollItems.add(new ScrollItem("JBox2D".toCharArray(), Align.CENTER, 1, colorWhite));

        mOffset = 0;
    }

    @Override
    public void onClick(UiView uiView, String action, Object exData) {
        if ("back".equals(action)) {
            uiView.setVisibleState(false);
            uiView.setOnVisibleListener(new OnVisibleListenerEx(new TitleScene()));
        }
    }

    @Override
    public IScene moveNextScene() {
        if (mNextScene != null) {
            return new FadeScene(this, mNextScene);
        } else {
            return this;
        }
    }

    @Override
    public AbstractUiCallback pullUiCallback() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onAction(String action, Object exData) {
    }

    class ScrollItem {
        private char[] chars;
        private Align align;
        private float scale;
        private ImageResource imageResource;
        private Vec2 size;
        private int imageRow;
        private int imageCol;
        private Vec3 color;

        public ScrollItem() {
        }

        public ScrollItem(char[] chars, Align align, float scale, Vec3 color) {
            super();
            this.chars = chars;
            this.align = align;
            this.scale = scale;
            this.color = color;
        }

        public ScrollItem(ImageResource imageResource, Vec2 size, int imageRow, int imageCol) {
            super();
            this.imageResource = imageResource;
            this.size = size;
            this.imageRow = imageRow;
            this.imageCol = imageCol;
        }

        public char[] getChars() {
            return chars;
        }

        public void setChars(char[] chars) {
            this.chars = chars;
        }

        public ImageResource getImageResource() {
            return imageResource;
        }

        public void setImageResource(ImageResource imageResource) {
            this.imageResource = imageResource;
        }

        public Vec2 getSize() {
            return size;
        }

        public void setSize(Vec2 size) {
            this.size = size;
        }

        public int getImageRow() {
            return imageRow;
        }

        public void setImageRow(int imageRow) {
            this.imageRow = imageRow;
        }

        public int getImageCol() {
            return imageCol;
        }

        public void setImageCol(int imageCol) {
            this.imageCol = imageCol;
        }

        public Vec3 getColor() {
            return color;
        }

        public void setColor(Vec3 color) {
            this.color = color;
        }

        public Align getAlign() {
            return align;
        }

        public void setAlign(Align align) {
            this.align = align;
        }

        public float getScale() {
            return scale;
        }

        public void setScale(float scale) {
            this.scale = scale;
        }
    }

    class OnVisibleListenerEx implements UiView.OnVisibleListener {
        private IScene nextScene;

        public OnVisibleListenerEx(IScene nextScene) {
            this.nextScene = nextScene;
        }

        @Override
        public void onVisible(UiView uiView) {
        }

        @Override
        public void onInvisible(UiView uiView) {
            CreditsScene.this.mNextScene = nextScene;
        }
    }
}
