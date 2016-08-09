package net.cattaka.hungrycatball.game;

import android.graphics.RectF;

import net.cattaka.collections.OrderedSet;
import net.cattaka.hungrycatball.HungryCatBallConstants;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.game.entity.BallEntity;
import net.cattaka.hungrycatball.game.entity.GoalEntity;
import net.cattaka.hungrycatball.game.entity.PhysicalEntity;
import net.cattaka.hungrycatball.game.entity.PlayerEntity;
import net.cattaka.hungrycatball.game.entity.ScoreEntity;
import net.cattaka.hungrycatball.game.entity.WallEntity;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.io.SceneData;
import net.cattaka.hungrycatball.utils.DrawingUtil.AlphaMode;
import net.cattaka.hungrycatball.utils.ImageResource;
import net.cattaka.hungrycatball.utils.SoundUtil.SoundId;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.ShapeDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.ContactListener;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactPoint;
import org.jbox2d.dynamics.contacts.ContactResult;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static net.cattaka.hungrycatball.HungryCatBallConstants.GAME_SHOCK_MARGIN;
import static net.cattaka.hungrycatball.HungryCatBallConstants.STEP_DT;

public class GameWorld {
    private OrderedSet<IGameEntity> mGameEntities;
    private OrderedSet<GameEntityAddEvent> mGameEntityAddEvents;
    private OrderedSet<IGameEntity> mAddedGameEntities;
    private OrderedSet<IGameEntity> mRemovedGameEntities;
    private AABB mWorldAABB;
    private World mWorld;
    private int mScore;
    private int mBonus;
    private LevelState mLevelState;
    private ImageResource mBgImageResource;
    private int mPlayTimeCount;
    private Body mFixedWorldBody;
    private List<SoundId> mSoundIds = new ArrayList<SoundId>();
    private boolean mEnableSound;
    private float mLossTime;
    private ContactListener mContactListenerImpl = new ContactListener() {
        @Override
        public void result(ContactResult result) {
            PhysicalEntity entity1 = (PhysicalEntity) result.shape1.m_body.getUserData();
            PhysicalEntity entity2 = (PhysicalEntity) result.shape2.m_body.getUserData();
            entity1.onContactResult(GameWorld.this, mGameEntityAddEvents, entity2, result);
            entity2.onContactResult(GameWorld.this, mGameEntityAddEvents, entity1, result);

            if (result.normalImpulse > GAME_SHOCK_MARGIN) {
                int score = (int) (result.normalImpulse);
                int exScore = 0;
                if (entity1.isEnableScore() && entity2.isEnableScore()) {
                    // 連鎖用Exスコアを与える
                    exScore += entity1.getGivenScore() + entity2.getGivenScore();
                    entity1.setGivenScore(entity1.getGivenScore() + score);
                    entity2.setGivenScore(entity2.getGivenScore() + score);
                }
                if (entity1.isEnableScore() || entity2.isEnableScore()) {
                    // スコアのパーティクルを表示
                    Vec2 position = new Vec2();
                    position.set(result.position);
                    GameEntityAddEvent event = ScoreEntity.createGameEntityAddEvent(position, score + exScore, 1f);
                    mGameEntityAddEvents.add(event);
                    // スコアを加算
                    addScore(score + exScore);
                }
                if (entity1 instanceof GoalEntity || entity2 instanceof GoalEntity) {
                    mSoundIds.add(SoundId.CAN_BOUNCE);
                } else if (entity1 instanceof BallEntity || entity2 instanceof BallEntity) {
                    mSoundIds.add(SoundId.BALL_BOUNCE);
                } else {
                    mSoundIds.add(SoundId.SOFT_BOUNCE);
                }
            }
        }

        @Override
        public void remove(ContactPoint point) {
            PhysicalEntity entity1 = (PhysicalEntity) point.shape1.m_body.getUserData();
            PhysicalEntity entity2 = (PhysicalEntity) point.shape1.m_body.getUserData();
            entity1.onContactRemove(entity2, point);
            entity2.onContactRemove(entity1, point);
        }

        @Override
        public void persist(ContactPoint point) {
            // TODO Auto-generated method stub
        }

        @Override
        public void add(ContactPoint point) {
            PhysicalEntity entity1 = (PhysicalEntity) point.shape1.m_body.getUserData();
            PhysicalEntity entity2 = (PhysicalEntity) point.shape2.m_body.getUserData();
            entity1.onContactAdd(entity2, point);
            entity2.onContactAdd(entity1, point);
        }
    };

    public void createWorld() {
        mGameEntities = new OrderedSet<IGameEntity>(new HashSet<IGameEntity>());
        mGameEntityAddEvents = new OrderedSet<GameEntityAddEvent>(new HashSet<GameEntityAddEvent>());
        mAddedGameEntities = new OrderedSet<IGameEntity>(new HashSet<IGameEntity>());
        mRemovedGameEntities = new OrderedSet<IGameEntity>(new HashSet<IGameEntity>());
        ;

        RectF rect = HungryCatBallConstants.WORLD_RECT;
        Vec2 gravity = HungryCatBallConstants.GRAVITY;
        mWorldAABB = new AABB();
        mWorldAABB.lowerBound.set(rect.left * 2, rect.bottom * 2);
        mWorldAABB.upperBound.set(rect.right * 2, rect.top * 2);
        boolean doSleep = true;
        mWorld = new World(mWorldAABB, gravity, doSleep);
        mWorld.setContactListener(this.mContactListenerImpl);

//		// 壁を追加
//		{
//			GameEntityAddEvent event = new GameEntityAddEvent();
//			event.setGameEntityClass(WallEntity.class);
//			this.mGameEntityAddEvents.add(event);
//		}
//		{
//			GameEntityAddEvent event = new GameEntityAddEvent();
//			event.setGameEntityClass(PlayerEntity.class);
//			event.setPosition(new Vec2(3,3));
//			this.mGameEntityAddEvents.add(event);
//		}
//		// ボールを追加
//		for (int i=0;i<2;i++){
//			Vec2 position = new Vec2();
//			position.x = (float) (Math.random() * 5 - 2.5);
//			position.y = (float) (Math.random() * 5 - 2.5);
//
//			GameEntityAddEvent event = new GameEntityAddEvent();
//			event.setGameEntityClass(BallEntity.class);
//			event.setPosition(position);
//			this.mGameEntityAddEvents.add(event);
//		}
    }

    public void step(SceneBundle sceneBundle) {
        // GameEntityの追加
        {
            for (GameEntityAddEvent event : mGameEntityAddEvents) {
                this.addGameEntity(sceneBundle, event, false);
            }
            mGameEntityAddEvents.clear();
            for (IGameEntity entity : mAddedGameEntities) {
                entity.onAdd(this);
            }
            for (IGameEntity entity : mGameEntities) {
                entity.preStep(this, sceneBundle, mAddedGameEntities);
            }
            mAddedGameEntities.clear();
        }
        mWorld.step(HungryCatBallConstants.STEP_DT, HungryCatBallConstants.STEP_ITERATIONS);
        for (IGameEntity entity : mGameEntities) {
            entity.step(this, sceneBundle, mGameEntityAddEvents, mRemovedGameEntities);
        }
        // GameEntityの削除
        {
            for (IGameEntity entity : mGameEntities) {
                entity.postStep(this, sceneBundle, mRemovedGameEntities);
            }
            for (IGameEntity entity : mRemovedGameEntities) {
                entity.onDelete(this);
            }
            for (IGameEntity entity : mRemovedGameEntities) {
                this.removeGameEntity(entity);
            }
            mRemovedGameEntities.clear();
        }
        // タイマーの更新
        if (mLevelState == LevelState.LEVEL_PLAYING) {
            mPlayTimeCount++;
        }
        // 終了のチェック
        {
            boolean moveFlag = false;
            for (IGameEntity entity : mGameEntities) {
                if (!(entity instanceof PlayerEntity)) {
                    final Vec2 vel = new Vec2();
                    entity.getLinearVelocity(vel);
                    if (vel.lengthSquared() > 0.1f) {
                        moveFlag = true;
                        break;
                    }
                }
            }
            if (moveFlag) {
                mLossTime = 0;
            } else {
                mLossTime += STEP_DT;
                if (mLossTime > 3f) {
                    mLevelState = LevelState.LEVEL_FAILED;
                }
            }
        }


        // サウンドの再生
        if (mEnableSound) {
            for (SoundId soundId : mSoundIds) {
                sceneBundle.getSoundUtil().play(soundId);
            }
        }
        mSoundIds.clear();
    }

    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        final Vec2 position = new Vec2(0, 0);
        final Vec2 size = new Vec2(HungryCatBallConstants.WORLD_RECT.width(), -HungryCatBallConstants.WORLD_RECT.height());
        if (mBgImageResource != null) {
            sceneBundle.getDrawUtil().drawBitmap(gl, mBgImageResource, position, size, 0, 1, null, AlphaMode.STD);
        }
        for (IGameEntity entity : mGameEntities) {
            // FIXME 描画スキップの方法を考える
            //if (!entity.isSkipableDraw()) {
            entity.draw(gl, sceneBundle);
            //}
        }
        if (sceneBundle.isDebugDraw()) {
            for (IGameEntity entity : mGameEntities) {
                entity.drawDebug(gl, sceneBundle);
            }
        }
    }

    /**
     * GameEntityを追加する。Worldへの追加も行う。
     *
     * @param event
     * @param countBonus TODO
     */
    public IGameEntity addGameEntity(SceneBundle sceneBundle, GameEntityAddEvent event, boolean countBonus) {
        IGameEntity rawEntity = event.createGameEntity();
        if (rawEntity.isPhysics()) {
            PhysicalEntity entity = (PhysicalEntity) rawEntity;
            int bodyNum = entity.getBodyNum();
            BodyDef[] bodyDefs = entity.getBodyDefs();
            ShapeDef[] shapeDefs = entity.getShapeDefs();
            for (int i = 0; i < bodyNum; i++) {
                Body body;
                {
                    float ta = bodyDefs[i].angle;
                    bodyDefs[i].angle += event.getAngle();
                    if (event.getPosition() != null) {
                        // 座標指定がある場合はそれを加算する
                        Vec2 tp = bodyDefs[i].position;
                        bodyDefs[i].position = bodyDefs[i].position.add(event.getPosition());
                        body = mWorld.createBody(bodyDefs[i]);
                        bodyDefs[i].position = tp;
                    } else {
                        body = mWorld.createBody(bodyDefs[i]);
                    }
                    bodyDefs[i].angle = ta;
                }

                body.createShape(shapeDefs[i]);
                body.setMassFromShapes();
                if (event.getLinearVelocity() != null) {
                    body.setLinearVelocity(event.getLinearVelocity());
                }
                body.setUserData(entity);
                body.setAngularVelocity(event.getAngularVelocity());
                entity.setBody(i, body);
            }
            rawEntity.initialize(this, sceneBundle, event.getExData(), event.getExFloatData());
            if (countBonus) {
                mBonus += entity.getFullScore();
            }
            if (entity instanceof WallEntity) {
                mFixedWorldBody = ((WallEntity) entity).getBody(0);
            }
        } else {
            final Vec2 position = new Vec2();
            final Vec2 linearVelocity = new Vec2();
            if (event.getPosition() != null) {
                position.set(event.getPosition());
            } else {
                position.set(0, 0);
            }
            if (event.getLinearVelocity() != null) {
                linearVelocity.set(event.getLinearVelocity());
            } else {
                linearVelocity.set(0, 0);
            }
            rawEntity.setXForm(position, event.getAngle());
            rawEntity.setLinearVelocity(event.getLinearVelocity());
            rawEntity.setAngle(event.getAngle());
            rawEntity.setAngularVelocity(event.getAngularVelocity());
            rawEntity.initialize(this, sceneBundle, event.getExData(), null);
        }
        mAddedGameEntities.add(rawEntity);
        mGameEntities.add(rawEntity);
        return rawEntity;
    }

    /**
     * GameEntityを削除する。Worldからも切り離す。
     *
     * @param gameEntity
     */
    public void removeGameEntity(IGameEntity rawEntity) {
        if (rawEntity.isPhysics()) {
            PhysicalEntity entity = (PhysicalEntity) rawEntity;
            int bodyNum = entity.getBodyNum();
            for (int i = 0; i < bodyNum; i++) {
                mWorld.destroyBody(entity.getBody(i));
                entity.setBody(i, null);
            }
        }
        mGameEntities.remove(rawEntity);
    }

    /**
     * GameEntityを削除対象としてスケジュールする。
     *
     * @param gameEntity
     */
    public void removeGameEntityFromOuter(IGameEntity entity) {
        mRemovedGameEntities.add(entity);
    }

    public void resetWorld() {
        while (mGameEntities.size() > 0) {
            removeGameEntity(mGameEntities.iterator().next());
        }
        mGameEntities.clear();
        mGameEntityAddEvents.clear();
        mAddedGameEntities.clear();
        mRemovedGameEntities.clear();
        mScore = 0;
        mBonus = 0;
        mLossTime = 0;
        mPlayTimeCount = 0;
        mLevelState = LevelState.LEVEL_PLAYING;
    }

    public void loadMapData(SceneBundle sceneBundle, SceneData sceneData) {
        resetWorld();
        for (GameEntityAddEvent event : sceneData.getGameEntityAddEvents()) {
            addGameEntity(sceneBundle, event, true);
        }
        mBgImageResource = sceneBundle.getDrawUtil().getImageResource(sceneData.getBgTextureId());
    }

    public OrderedSet<IGameEntity> getGameEntities() {
        return mGameEntities;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        this.mScore = score;
    }

    public void addScore(int score) {
        this.mScore += score;
    }

    public int getBonus() {
        return mBonus;
    }

    public void setBonus(int bonus) {
        this.mBonus = bonus;
    }

    public int getPlayTimeCount() {
        return mPlayTimeCount;
    }

    public void setPlayTimeCount(int frameCount) {
        this.mPlayTimeCount = frameCount;
    }

    public void getPlayTimeAsCharArray(char[] cs) {
        int playTime = (int) (mPlayTimeCount * STEP_DT);
        cs[0] = (char) (0x30 + (playTime / 600) % 10);
        cs[1] = (char) (0x30 + (playTime / 60) % 10);
        cs[2] = ':';
        cs[3] = (char) (0x30 + (playTime / 10) % 6);
        cs[4] = (char) (0x30 + playTime % 10);
    }

    public Body getmFixedWorldBody() {
        return mFixedWorldBody;
    }

    public Joint createJoint(JointDef def) {
        return mWorld.createJoint(def);
    }

    public void destroyJoint(Joint j) {
        mWorld.destroyJoint(j);
    }

    public LevelState getLevelState() {
        return mLevelState;
    }

    public void setLevelState(LevelState levelState) {
        this.mLevelState = levelState;
    }

    public ImageResource getBgImageResource() {
        return mBgImageResource;
    }

    public void setBgImageResource(ImageResource bgImageResource) {
        this.mBgImageResource = bgImageResource;
    }

    public boolean isEnableSound() {
        return mEnableSound;
    }

    public void setEnableSound(boolean enableSound) {
        this.mEnableSound = enableSound;
    }

    public enum LevelState {
        LEVEL_PLAYING,
        LEVEL_CLEARED,
        LEVEL_FAILED
    }
}
