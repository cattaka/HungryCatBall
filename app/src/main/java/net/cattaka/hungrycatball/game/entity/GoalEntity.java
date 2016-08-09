package net.cattaka.hungrycatball.game.entity;

import net.cattaka.collections.OrderedSet;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.game.GameEntityAddEvent;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.game.GameWorld.LevelState;
import net.cattaka.hungrycatball.game.IGameEntity;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.utils.DrawingUtil.AlphaMode;
import net.cattaka.hungrycatball.utils.ImageResource;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.collision.shapes.ShapeDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;

import static net.cattaka.hungrycatball.HungryCatBallConstants.CELL_MARGIN;
import static net.cattaka.hungrycatball.HungryCatBallConstants.CELL_SIZE;
import static net.cattaka.hungrycatball.HungryCatBallConstants.WORLD_RECT;

public class GoalEntity extends PhysicalEntity {
    private static final Vec2 SIZE = new Vec2(CELL_SIZE * 2 - CELL_MARGIN, CELL_SIZE * 2 - CELL_MARGIN);
    private static BodyDef[] BODY_DEFS;
    private static ShapeDef[] SHAPE_DEFS;

    static {
        PolygonDef pd = new PolygonDef();
        pd.setAsBox(SIZE.x / 2, SIZE.y / 2);
        pd.density = 0.5f;
        pd.friction = 0.5f;
        pd.restitution = 0.5f;

        BodyDef bd = new BodyDef();

        BODY_DEFS = new BodyDef[]{
                bd
        };

        SHAPE_DEFS = new ShapeDef[]{
                pd
        };
    }

    private ImageResource mImageResource;

    public GoalEntity() {
        super(1);
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
    public void initialize(GameWorld gameWorld, SceneBundle sceneBundle, Object exData, float[] exFloatData) {
        super.initialize(gameWorld, sceneBundle, exData, exFloatData);
        mImageResource = sceneBundle.getDrawUtil().getImageResource(TextureId.ENTITY_GOAL);
    }


    @Override
    public void step(GameWorld gameWorld, SceneBundle sceneBundle,
                     OrderedSet<GameEntityAddEvent> gameEntityAddEvents, OrderedSet<IGameEntity> removedGameEntities) {

        if (mBodys[0].getPosition().y < WORLD_RECT.bottom - CELL_SIZE) {
            gameWorld.setLevelState(LevelState.LEVEL_FAILED);
        }
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        final Vec2 position = new Vec2();
        final Vec2 size = new Vec2(2, 2);
        getPosition(position);
        sceneBundle.getDrawUtil().drawBitmap(gl, mImageResource, 0, 0, position, size, getAngle(), 1, null, AlphaMode.STD);
    }
}
