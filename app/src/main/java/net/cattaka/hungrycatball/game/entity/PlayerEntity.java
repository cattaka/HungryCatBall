package net.cattaka.hungrycatball.game.entity;

import net.cattaka.collections.OrderedSet;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.core.UserInput.TouchState;
import net.cattaka.hungrycatball.game.GameEntityAddEvent;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.game.GameWorld.LevelState;
import net.cattaka.hungrycatball.game.IGameEntity;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.utils.DrawingUtil.AlphaMode;
import net.cattaka.hungrycatball.utils.ImageResource;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.collision.shapes.CircleDef;
import org.jbox2d.collision.shapes.ShapeDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.contacts.ContactResult;

import static net.cattaka.hungrycatball.HungryCatBallConstants.CELL_SIZE;
import static net.cattaka.hungrycatball.HungryCatBallConstants.PLAYER_MAX_SPEED;
import static net.cattaka.hungrycatball.HungryCatBallConstants.STEP_DT;
import static net.cattaka.hungrycatball.HungryCatBallConstants.WORLD_RECT;

public class PlayerEntity extends PhysicalEntity {
    private static BodyDef[] BODY_DEFS;
    private static ShapeDef[] SHAPE_DEFS;

    static {
        float cellSize = CELL_SIZE;

        CircleDef pd = new CircleDef();
        pd.radius = cellSize;
        pd.density = 0.5f;
        pd.friction = 1.0f;
        pd.restitution = 1.0f;

        BodyDef bd = new BodyDef();
        bd.fixedRotation = true;

        BODY_DEFS = new BodyDef[]{
                bd
        };

        SHAPE_DEFS = new ShapeDef[]{
                pd
        };
    }

    private ImageResource imageResource;
    private Vec2 mMoveToPosition = new Vec2();
    private Vec2 mDir = new Vec2();
    private boolean mGoalCatched;

    public PlayerEntity() {
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
        mBodys[0].m_flags = mBodys[0].m_flags | Body.e_bulletFlag;
        imageResource = sceneBundle.getDrawUtil().getImageResource(TextureId.ENTITY_PLAYER);
        this.getPosition(mMoveToPosition);
        mGoalCatched = false;
    }

    @Override
    public void step(GameWorld gameWorld, SceneBundle sceneBundle,
                     OrderedSet<GameEntityAddEvent> gameEntityAddEvents, OrderedSet<IGameEntity> removedGameEntities) {
        final Vec2 vel = new Vec2();
        final Vec2 force = new Vec2();

        // 画面をタッチされた場合、そこを目標とする
        if (!sceneBundle.getUserInput().currentTouchStateConsumed
                && (sceneBundle.getUserInput().currentTouchState == TouchState.PRESSE
                || sceneBundle.getUserInput().currentTouchState == TouchState.PRESSED)) {
            mMoveToPosition.set(sceneBundle.getUserInput().currentPosition);
            if (mMoveToPosition.y > -CELL_SIZE * 2f) {
                mMoveToPosition.y = -CELL_SIZE * 2f;
            }
            if (mMoveToPosition.y < WORLD_RECT.bottom + CELL_SIZE * 2f) {
                mMoveToPosition.y = WORLD_RECT.bottom + CELL_SIZE * 2f;
            }
            if (mMoveToPosition.x < WORLD_RECT.left + CELL_SIZE * 2f) {
                mMoveToPosition.x = WORLD_RECT.left + CELL_SIZE * 2f;
            }
            if (mMoveToPosition.x > WORLD_RECT.right - CELL_SIZE * 2f) {
                mMoveToPosition.x = WORLD_RECT.right - CELL_SIZE * 2f;
            }
        }

        // 現在の速度を取り消す力
        vel.set(mBodys[0].getLinearVelocity());
        vel.mulLocal(-1f / (STEP_DT) * mBodys[0].getMass());
        // 目標へ移動する力
        force.set(mMoveToPosition);
        force.subLocal(mBodys[0].getPosition());
        float dist = force.normalize();
        if (dist > PLAYER_MAX_SPEED) {
            dist = PLAYER_MAX_SPEED;
        }
        if (dist > 0.1f) {
            mDir.set(force);
        }
        force.mulLocal(dist / (STEP_DT * STEP_DT) * mBodys[0].getMass());
        force.addLocal(vel);

        mBodys[0].applyForce(force, mBodys[0].getMemberPosition());

        if (mGoalCatched) {
            gameWorld.setLevelState(LevelState.LEVEL_CLEARED);
        }
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        final Vec2 position = new Vec2();
        final Vec2 size = new Vec2(2, 2);
        getPosition(position);

        int col = 0;
        if (-0.5f < mDir.x && mDir.x < 0.5f) {
            if (mDir.y > 0) {
                col = 1;
            } else {
                col = 0;
            }
        } else {
            if (mDir.x > 0) {
                col = 3;
            } else {
                col = 2;
            }
        }
        sceneBundle.getDrawUtil().drawBitmap(gl, imageResource, 0, col, position, size, getAngle(), 1, null, AlphaMode.STD);
    }

    @Override
    public void onContactResult(GameWorld gameWorld,
                                OrderedSet<GameEntityAddEvent> gameEntityAddEvents,
                                PhysicalEntity entity, ContactResult point) {
        super.onContactResult(gameWorld, gameEntityAddEvents, entity, point);
        if (entity instanceof GoalEntity) {
            mGoalCatched = true;
        }
    }
}
