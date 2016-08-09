package net.cattaka.hungrycatball.game.entity;

import android.graphics.RectF;

import net.cattaka.collections.OrderedSet;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.game.GameEntityAddEvent;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.game.IGameEntity;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.utils.DrawingUtil.AlphaMode;
import net.cattaka.hungrycatball.utils.ImageResource;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.collision.shapes.ShapeDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;

import static net.cattaka.hungrycatball.HungryCatBallConstants.CELL_SIZE;
import static net.cattaka.hungrycatball.HungryCatBallConstants.WORLD_RECT;

public class WallEntity extends PhysicalEntity {
    private static BodyDef[] BODY_DEFS;
    private static ShapeDef[] SHAPE_DEFS;
    private static Vec2 SIZE_SIDE = new Vec2(CELL_SIZE, (WORLD_RECT.top - WORLD_RECT.bottom));
    private static Vec2 SIZE_TOP = new Vec2((WORLD_RECT.right - WORLD_RECT.left) - CELL_SIZE * 2f, CELL_SIZE * 2);
    private static Vec2 SIZE_BOTTOM = new Vec2(CELL_SIZE * 2, CELL_SIZE);

    static {
        RectF rect = WORLD_RECT;

        PolygonDef verticalWallPd = new PolygonDef();
        PolygonDef horizontalWallPd = new PolygonDef();
        PolygonDef smallWallPd = new PolygonDef();
        verticalWallPd.setAsBox(SIZE_SIDE.x / 2f, SIZE_SIDE.y / 2f);
        horizontalWallPd.setAsBox(SIZE_TOP.x / 2f, SIZE_TOP.y / 2f);
        smallWallPd.setAsBox(SIZE_BOTTOM.x / 2f, SIZE_BOTTOM.y / 2f);

        BodyDef topBd = new BodyDef();
        BodyDef leftBd = new BodyDef();
        BodyDef rightBd = new BodyDef();
        BodyDef bottomLeftBd = new BodyDef();
        BodyDef bottomRightBd = new BodyDef();
        topBd.position = new Vec2(0.0f, rect.top - CELL_SIZE * 1.0f);
        leftBd.position = new Vec2(rect.left + CELL_SIZE / 2f, 0.0f);
        rightBd.position = new Vec2(rect.right - CELL_SIZE / 2f, 0.0f);
        bottomLeftBd.position = new Vec2(rect.left + CELL_SIZE * 2f, rect.bottom + CELL_SIZE / 2f);
        bottomRightBd.position = new Vec2(rect.right - CELL_SIZE * 2f, rect.bottom + CELL_SIZE / 2f);
        bottomLeftBd.angle = -(float) (Math.PI / 12f);
        bottomRightBd.angle = (float) (Math.PI / 12f);

//        topBd.position = new Vec2(0.0f, rect.top);
//        bottomBd.position = new Vec2(0.0f, rect.bottom);
//        leftBd.position = new Vec2(rect.left, 0.0f);
//        rightBd.position = new Vec2(rect.right, 0.0f);

        BODY_DEFS = new BodyDef[]{
                topBd,
                leftBd,
                rightBd,
                bottomLeftBd,
                bottomRightBd,
        };

        SHAPE_DEFS = new ShapeDef[]{
                horizontalWallPd,
                verticalWallPd,
                verticalWallPd,
                smallWallPd,
                smallWallPd,
        };
    }

    private ImageResource mBottomImageResource;
    private ImageResource mSideImageResource;
    private ImageResource mTopImageResource;

    public WallEntity() {
        super(5);
    }

    @Override
    public void initialize(GameWorld gameWorld, SceneBundle sceneBundle,
                           Object exData, float[] exFloatData) {
        super.initialize(gameWorld, sceneBundle, exData, exFloatData);
        mBottomImageResource = sceneBundle.getDrawUtil().getImageResource(TextureId.WALL_BOTTOM);
        mSideImageResource = sceneBundle.getDrawUtil().getImageResource(TextureId.WALL_SIDE);
        mTopImageResource = sceneBundle.getDrawUtil().getImageResource(TextureId.WALL_TOP);
    }

    @Override
    public BodyDef[] getBodyDefs() {
        return BODY_DEFS;
    }

    @Override
    public ShapeDef[] getShapeDefs() {
        return SHAPE_DEFS;
    }

    @Override
    public void step(GameWorld gameWorld, SceneBundle sceneBundle,
                     OrderedSet<GameEntityAddEvent> gameEntityAddEvents, OrderedSet<IGameEntity> removedGameEntities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        final Vec2 position = new Vec2();
        float angle;
        position.set(mBodys[0].getMemberPosition());
        angle = mBodys[0].getAngle();
        sceneBundle.getDrawUtil().drawBitmap(gl, mTopImageResource, position, SIZE_TOP, angle, 1, null, AlphaMode.STD);

        position.set(mBodys[1].getMemberPosition());
        angle = mBodys[1].getAngle();
        sceneBundle.getDrawUtil().drawBitmap(gl, mSideImageResource, position, SIZE_SIDE, angle, 1, null, AlphaMode.STD);

        position.set(mBodys[2].getMemberPosition());
        angle = mBodys[2].getAngle();
        sceneBundle.getDrawUtil().drawBitmap(gl, mSideImageResource, position, SIZE_SIDE, angle, 1, null, AlphaMode.STD);

        position.set(mBodys[3].getMemberPosition());
        angle = mBodys[3].getAngle();
        sceneBundle.getDrawUtil().drawBitmap(gl, mBottomImageResource, position, SIZE_BOTTOM, angle, 1, null, AlphaMode.STD);

        position.set(mBodys[4].getMemberPosition());
        angle = mBodys[4].getAngle();
        sceneBundle.getDrawUtil().drawBitmap(gl, mBottomImageResource, position, SIZE_BOTTOM, angle, 1, null, AlphaMode.STD);
    }
}
