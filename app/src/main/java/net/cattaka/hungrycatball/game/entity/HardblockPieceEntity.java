package net.cattaka.hungrycatball.game.entity;

import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.collision.shapes.ShapeDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;

import static net.cattaka.hungrycatball.HungryCatBallConstants.CELL_MARGIN;
import static net.cattaka.hungrycatball.HungryCatBallConstants.CELL_SIZE;
import static net.cattaka.hungrycatball.HungryCatBallConstants.GAME_BLOCK_RESTITUTION;
import static net.cattaka.hungrycatball.HungryCatBallConstants.HEALTH_HARDBLOCK;

public class HardblockPieceEntity extends BlockPieceEntity {
    private static final Vec2 SIZE = new Vec2(CELL_SIZE * 1 - CELL_MARGIN, CELL_SIZE * 0.5f - CELL_MARGIN);
    private static BodyDef[] BODY_DEFS;
    private static ShapeDef[] SHAPE_DEFS;

    static {
        PolygonDef pd = new PolygonDef();
        pd.setAsBox(SIZE.x / 2, SIZE.y / 2);
        pd.density = 1.0f;
        pd.friction = 0.5f;
        pd.restitution = GAME_BLOCK_RESTITUTION;

        BodyDef bd = new BodyDef();

        BODY_DEFS = new BodyDef[]{
                bd
        };

        SHAPE_DEFS = new ShapeDef[]{
                pd
        };
    }

    public HardblockPieceEntity() {
        super();
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
    public void initializeEx(GameWorld gameWorld, SceneBundle sceneBundle, Object exData) {
        mImageResource = sceneBundle.getDrawUtil().getImageResource(TextureId.ENTITY_HARDBLOCK_PIECE);
        //mPieceClass = HardblockPieceEntity.class;
        mMaxHealth = HEALTH_HARDBLOCK / 4f;
        mSize = SIZE;
        //mBodys[0].setMass(new MassData());
    }
}
