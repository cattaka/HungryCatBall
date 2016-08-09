package net.cattaka.hungrycatball.scene;

import android.graphics.Color;
import android.graphics.RectF;

import net.cattaka.hungrycatball.HungryCatBallConstants;
import net.cattaka.hungrycatball.core.AbstractUiCallback;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.core.UserInput.TouchState;
import net.cattaka.hungrycatball.frame.ISceneFrame;
import net.cattaka.hungrycatball.frame.PlayFrame;
import net.cattaka.hungrycatball.game.GameEntityAddEvent;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.game.IGameEntity;
import net.cattaka.hungrycatball.game.entity.BallEntity;
import net.cattaka.hungrycatball.game.entity.BallSmallEntity;
import net.cattaka.hungrycatball.game.entity.DoorEntity;
import net.cattaka.hungrycatball.game.entity.GoalEntity;
import net.cattaka.hungrycatball.game.entity.HardblockEntity;
import net.cattaka.hungrycatball.game.entity.HardblockSmallEntity;
import net.cattaka.hungrycatball.game.entity.PlayerEntity;
import net.cattaka.hungrycatball.game.entity.RollerEntity;
import net.cattaka.hungrycatball.game.entity.RollerLargeEntity;
import net.cattaka.hungrycatball.game.entity.RollingBarEntity;
import net.cattaka.hungrycatball.game.entity.SoftblockEntity;
import net.cattaka.hungrycatball.game.entity.SoftblockSmallEntity;
import net.cattaka.hungrycatball.game.entity.WallEntity;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.io.SceneData;
import net.cattaka.hungrycatball.io.SceneIo;
import net.cattaka.hungrycatball.utils.DrawingUtil.AlphaMode;
import net.cattaka.hungrycatball.utils.ImageResource;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapEditorScene implements IScene {
    private static float BUTTON_SIZE = 4;

    enum OperationMode {
        MOVE,
        ROTATE
    }

    ;

    static class AddItem {
        public String name;
        public Class<? extends IGameEntity> gameEntityClass;

        public AddItem(String name, Class<? extends IGameEntity> gameEntityClass) {
            super();
            this.name = name;
            this.gameEntityClass = gameEntityClass;
        }

        @Override
        public String toString() {
            return String.valueOf(name);
        }
    }

    ;

    private ISceneFrame mSceneFrame;
    private GameWorld mGameWorld;
    private AbstractUiCallback mUiCallback;

    private List<GameEntityAddEvent> mPreGameEntityAddEvents;
    private SceneData mSceneData;
    private int mSelectedGameEntityIdx = -1;

    private boolean menuDisplayed = false;
    private Vec2 mCursorPosition = new Vec2();
    private float mGridSize = 0.5f;
    private OperationMode mOperationMode = OperationMode.MOVE;
    private File mCurrentFile;
    private ImageResource menuImageResource;
    private long mFrameCount;

    private TextureId mBgTextureId;
    // FIXME テストコード
    private IScene mmNextScene;

    public MapEditorScene() {
        mGameWorld = new GameWorld();
        mGameWorld.createWorld();

        mPreGameEntityAddEvents = new ArrayList<GameEntityAddEvent>();
        mSceneData = new SceneData();

        restore(null);
    }

    @Override
    public void initialize(SceneBundle sceneBundle) {
        mSceneFrame = new PlayFrame();
        mSceneFrame.initialize(sceneBundle, mSceneData);

        menuImageResource = sceneBundle.getDrawUtil().getImageResource(TextureId.MENU_BUTTONS);

        mFrameCount = 0;
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        mSceneFrame.draw(gl, sceneBundle, mGameWorld);

        // メニューボタンの表示
        if (menuDisplayed) {
            RectF rect = HungryCatBallConstants.WORLD_RECT;
            final Vec2 pos = new Vec2();
            final Vec2 size = new Vec2();
            pos.set(0, rect.bottom - BUTTON_SIZE / 2f);
            size.set(rect.width(), BUTTON_SIZE);
            sceneBundle.getDrawUtil().drawBitmap(gl, menuImageResource, pos, size, 0f, 1f, null, AlphaMode.STD);
        }

        // カーソルを表示
        sceneBundle.getDrawUtil().drawMarker(gl, mCursorPosition.x, mCursorPosition.y, 0.5f, Color.BLUE);

        // 選択中のオブジェクトがあれば強調表示
        if (mSelectedGameEntityIdx != -1) {
            final Vec3 color = new org.jbox2d.common.Vec3(1, 1, 0);
            IGameEntity entity = mGameWorld.getGameEntities().get(mSelectedGameEntityIdx);
            final Vec2 pos = new Vec2();
            entity.getPosition(pos);
            float radius = 2f * (1f - (float) (mFrameCount % 10) / 10f);
            sceneBundle.getDrawUtil().drawCicle(gl, pos.x, pos.y, radius, null);
            sceneBundle.getDrawUtil().drawLine(gl, pos.x - 4, pos.y, pos.x + 4, pos.y, color);
            sceneBundle.getDrawUtil().drawLine(gl, pos.x, pos.y - 4, pos.x, pos.y + 4, color);
        }
    }

    @Override
    public void step(SceneBundle sceneBundle) {
        // 追加前のものを追加する
        for (GameEntityAddEvent event : mPreGameEntityAddEvents) {
            mGameWorld.addGameEntity(sceneBundle, event, false);
            mSceneData.getGameEntityAddEvents().add(event);
        }
        mPreGameEntityAddEvents.clear();

        RectF rect = HungryCatBallConstants.WORLD_RECT;
        Vec2 cp = sceneBundle.getUserInput().currentPosition;

        if (sceneBundle.getUserInput().currentTouchState == TouchState.PRESSE) {
            float hc = 0;//HungryCatBallConstants.CELL_SIZE / 2;
            if (cp.y < rect.bottom) {
                // メニューボタンを処理する
                int x = (int) ((cp.x - rect.left - hc) / 2f);
                int y = (int) ((rect.bottom - cp.y) / 2f);
                onButtonClick(sceneBundle, x + y * 7);
            } else {
                // カーソルを更新
                mCursorPosition.set(cp);
                // オブジェクトをピックする
                mSelectedGameEntityIdx = -1;
                for (int i = 0; i < mGameWorld.getGameEntities().size(); i++) {
                    IGameEntity entity = mGameWorld.getGameEntities().get(i);
                    if (entity instanceof WallEntity || entity instanceof PlayerEntity) {
                        // 操作させない
                        continue;
                    }
                    if (entity.isIntersect(cp)) {
                        mSelectedGameEntityIdx = i;
                        break;
                    }
                }
                //Log.d(HungryCatBallConstants.TAG, String.valueOf(selectedGameEntityIdx));
            }
        }
        if (sceneBundle.getUserInput().currentTouchState == TouchState.PRESSED) {
            if (mSelectedGameEntityIdx != -1) {
                if (cp.y >= rect.bottom) {
                    if (mOperationMode == OperationMode.MOVE) {
                        float x = (mGridSize > 0) ? (cp.x - (cp.x % mGridSize)) : cp.x;
                        float y = (mGridSize > 0) ? (cp.y - (cp.y % mGridSize)) : cp.y;

                        GameEntityAddEvent event = mSceneData.getGameEntityAddEvents().get(mSelectedGameEntityIdx);
                        IGameEntity entity = mGameWorld.getGameEntities().get(mSelectedGameEntityIdx);
                        Vec2 bodyPos = new Vec2(x, y);
                        event.setPosition(bodyPos);
                        entity.setXForm(bodyPos, entity.getAngle());
                    } else if (mOperationMode == OperationMode.ROTATE) {
                        GameEntityAddEvent event = mSceneData.getGameEntityAddEvents().get(mSelectedGameEntityIdx);
                        IGameEntity entity = mGameWorld.getGameEntities().get(mSelectedGameEntityIdx);
                        final Vec2 pos = new Vec2();
                        final Vec2 dir = new Vec2();
                        entity.getPosition(pos);
                        dir.set(cp);
                        dir.subLocal(pos);
                        dir.normalize();
                        float angle = (float) (Math.acos(dir.y) * ((dir.x <= 0) ? 1 : -1));
                        if (mGridSize > 0) {
                            angle *= (180f / Math.PI) + (mGridSize / 2f);
                            angle = angle - angle % (mGridSize * 10);
                            angle *= Math.PI / 180f;
                        }
                        event.setAngle(angle);
                        Vec2 bodyPos = new Vec2();
                        entity.getPosition(bodyPos);
                        entity.setXForm(bodyPos, angle);
                    }
                }
            }
        }
        if (sceneBundle.getUserInput().currentMenuState == TouchState.PRESSE) {
            switchDisplay(sceneBundle);
        }

        ISceneFrame nextSceneFrame = mSceneFrame.moveNextSceneFrame();
        if (mSceneFrame != nextSceneFrame) {
            mSceneFrame = nextSceneFrame;
            mSceneFrame.initialize(sceneBundle, mSceneData);
        }

        if (mBgTextureId != null) {
            mSceneData.setBgTextureId(mBgTextureId);
            mGameWorld.setBgImageResource(sceneBundle.getDrawUtil().getImageResource(mBgTextureId));
            mBgTextureId = null;
        }

        mFrameCount++;
    }

    private void switchDisplay(SceneBundle sceneBundle) {
        menuDisplayed = !menuDisplayed;
        if (menuDisplayed) {
            sceneBundle.getScreenHandler().setupMatrix(true, BUTTON_SIZE);
        } else {
            sceneBundle.getScreenHandler().setupMatrix(true, 0);
        }
    }

    private void onButtonClick(SceneBundle sceneBundle, int idx) {
        switch (idx) {
            case 0: {    // Add
                List<Object> addItems = new ArrayList<Object>();
                addItems.add(new AddItem("Softblock", SoftblockEntity.class));
                addItems.add(new AddItem("Hardblock", HardblockEntity.class));
                addItems.add(new AddItem("Softblock(S)", SoftblockSmallEntity.class));
                addItems.add(new AddItem("Hardblock(S)", HardblockSmallEntity.class));
                addItems.add(new AddItem("Door", DoorEntity.class));
                addItems.add(new AddItem("RollingBar", RollingBarEntity.class));
                addItems.add(new AddItem("Roller", RollerEntity.class));
                addItems.add(new AddItem("Roller(L)", RollerLargeEntity.class));
                addItems.add(new AddItem("Ball", BallEntity.class));
                addItems.add(new AddItem("Ball(S)", BallSmallEntity.class));
                addItems.add(new AddItem("Goal", GoalEntity.class));
                class AddItemUiCallback extends AbstractUiCallback {
                    private Vec2 position;

                    public AddItemUiCallback(List<Object> selectItems, Vec2 position) {
                        super("Add entity", selectItems);
                        this.position = position;
                    }

                    public void onFinished(Object value) {
                        if (value instanceof AddItem) {
                            AddItem item = (AddItem) value;

                            GameEntityAddEvent event = new GameEntityAddEvent();
                            event.setGameEntityClass(item.gameEntityClass);
                            event.setPosition(position);
                            mPreGameEntityAddEvents.add(event);
                        }
                    }

                    ;
                }
                ;
                this.mUiCallback = new AddItemUiCallback(addItems, mCursorPosition.clone());
                break;
            }
            case 1: {    // Del
                removeGameEntity(mSelectedGameEntityIdx);
                break;
            }
            case 2: {    // Mov
                mOperationMode = OperationMode.MOVE;
                break;
            }
            case 3: {    // Rot
                mOperationMode = OperationMode.ROTATE;
                break;
            }
            case 4: {    // Ext
                class ExtUiCallback extends AbstractUiCallback {
                    GameEntityAddEvent srcEvent;

                    public ExtUiCallback(String inputDefault, GameEntityAddEvent srcEvent) {
                        super("Input exFloatData", inputDefault);
                        this.srcEvent = srcEvent;
                    }

                    @Override
                    public void onFinished(Object value) {
                        if (value instanceof String) {
                            float[] exFloatData = null;
                            if (((String) value).length() >= 0) {
                                String[] strs = ((String) value).split("\\s*,\\s*");
                                exFloatData = new float[strs.length];
                                for (int i = 0; i < strs.length; i++) {
                                    try {
                                        exFloatData[i] = Float.valueOf(strs[i]);
                                    } catch (NumberFormatException e) {
                                        // 無視
                                    }
                                }
                            }
                            srcEvent.setExFloatData(exFloatData);
                            // 一度削除して、再度追加する
                            removeGameEntity(mSelectedGameEntityIdx);
                            mPreGameEntityAddEvents.add(srcEvent);
                        }
                    }
                }
                if (mSelectedGameEntityIdx != -1) {
                    GameEntityAddEvent srcEvent = mSceneData.getGameEntityAddEvents().get(mSelectedGameEntityIdx);
                    StringBuilder sb = new StringBuilder();
                    if (srcEvent.getExFloatData() != null) {
                        for (int i = 0; i < srcEvent.getExFloatData().length; i++) {
                            if (sb.length() > 0) {
                                sb.append(",");
                            }
                            sb.append(srcEvent.getExFloatData()[i]);
                        }
                    }

                    mUiCallback = new ExtUiCallback(sb.toString(), srcEvent);
                }
                break;
            }
            case 6: {    // Clone
                if (mSelectedGameEntityIdx != -1) {
                    GameEntityAddEvent srcEvent = mSceneData.getGameEntityAddEvents().get(mSelectedGameEntityIdx);
                    GameEntityAddEvent event = new GameEntityAddEvent(srcEvent);
                    mPreGameEntityAddEvents.add(event);
                }
                break;
            }
            case 7: {    // Test
                // FIXME テストコード
                PlayScene testScene = new PlayScene();
                testScene.loadMapData(sceneBundle, mSceneData);
                mmNextScene = testScene;
                switchDisplay(sceneBundle);
                break;
            }
            case 8: {    // Background
                List<Object> gridItems = new ArrayList<Object>();
                gridItems.add(TextureId.BG_TYPE_1);
                gridItems.add(TextureId.BG_TYPE_2);
                mUiCallback = new AbstractUiCallback("Background", gridItems) {
                    @Override
                    public void onFinished(Object value) {
                        if (value instanceof TextureId) {
                            mBgTextureId = (TextureId) value;
                        }
                    }
                };
                break;
            }
            case 9: {    // Grid
                List<Object> gridItems = new ArrayList<Object>();
                gridItems.add(new Float(1.0));
                gridItems.add(new Float(0.5));
                gridItems.add(new Float(0.25));
                mUiCallback = new AbstractUiCallback("Grid setting", gridItems) {
                    @Override
                    public void onFinished(Object value) {
                        if (value instanceof Float) {
                            mGridSize = (Float) value;
                        }
                    }
                };
                break;
            }
            case 10: {    // New
                restore(null);
                mCurrentFile = null;
                break;
            }
            case 11: {    // Open
                class FileWrapper {
                    public File file;

                    public FileWrapper(File file) {
                        super();
                        this.file = file;
                    }

                    @Override
                    public String toString() {
                        return (file != null) ? file.getName() : "";
                    }
                }
                List<File> mapFileList = SceneIo.getMapFileList();
                List<FileWrapper> mapFileWrapperList = new ArrayList<FileWrapper>();
                for (File file : mapFileList) {
                    mapFileWrapperList.add(new FileWrapper(file));
                }

                mUiCallback = new AbstractUiCallback("Open file", new ArrayList<Object>(mapFileWrapperList)) {
                    public void onFinished(Object value) {
                        if (value instanceof FileWrapper) {
                            File file = ((FileWrapper) value).file;
                            SceneData sceneData = SceneIo.openMap(file);
                            if (sceneData != null) {
                                restore(sceneData);
                                mCurrentFile = file;
                            }
                        }
                    }

                    ;
                };
                break;
            }
            case 12: {    // Save
                if (mCurrentFile != null) {
                    SceneIo.saveMap(mCurrentFile, mSceneData);
                    break;
                } else {
                    // SaveAsへ流す
                }
            }
            case 13: {    // SaveAs
                class SaveAsUiCallback extends AbstractUiCallback {
                    SceneData sceneData;

                    public SaveAsUiCallback(String inputDefault, SceneData sceneData) {
                        super("Save as", inputDefault);
                        this.sceneData = sceneData;
                    }

                    @Override
                    public void onFinished(Object value) {
                        if (value instanceof String) {
                            mCurrentFile = SceneIo.saveMap(value.toString(), sceneData);
                        }
                    }
                }
                String inputDefault = "001.map";
                if (mCurrentFile != null) {
                    inputDefault = mCurrentFile.getName();
                }
                mUiCallback = new SaveAsUiCallback(inputDefault, mSceneData);
                break;
            }
        }
        //Log.d(HungryCatBallConstants.TAG, "Button clicked : " + idx);
    }

    public void removeGameEntity(int idx) {
        if (idx != -1) {
            mSceneData.getGameEntityAddEvents().remove(idx);
            IGameEntity entity = mGameWorld.getGameEntities().get(idx);
            mGameWorld.removeGameEntity(entity);
            mSelectedGameEntityIdx = -1;
        }
    }

    public void restore(SceneData sceneData) {
        mPreGameEntityAddEvents.clear();
        mSceneData.getGameEntityAddEvents().clear();
        mSelectedGameEntityIdx = -1;
        mGameWorld.resetWorld();
        if (sceneData != null) {
            mBgTextureId = sceneData.getBgTextureId();
            mPreGameEntityAddEvents.addAll(sceneData.getGameEntityAddEvents());
        } else {
            mBgTextureId = TextureId.BG_TYPE_1;
            // 壁を追加
            {
                GameEntityAddEvent event = new GameEntityAddEvent();
                event.setGameEntityClass(WallEntity.class);
                mPreGameEntityAddEvents.add(event);
            }
            // Ballを追加
            {
                GameEntityAddEvent event = new GameEntityAddEvent();
                event.setGameEntityClass(BallEntity.class);
                mPreGameEntityAddEvents.add(event);
            }
            // Playerを追加
            {
                Vec2 position = new Vec2(0, HungryCatBallConstants.WORLD_RECT.bottom / 2);

                GameEntityAddEvent event = new GameEntityAddEvent();
                event.setGameEntityClass(PlayerEntity.class);
                event.setPosition(position);
                mPreGameEntityAddEvents.add(event);
            }
        }
    }

    @Override
    public IScene moveNextScene() {
        if (mmNextScene != null) {
            return mmNextScene;
        } else {
            return this;
        }
    }

    @Override
    public AbstractUiCallback pullUiCallback() {
        AbstractUiCallback result = this.mUiCallback;
        this.mUiCallback = null;
        return result;
    }
}
