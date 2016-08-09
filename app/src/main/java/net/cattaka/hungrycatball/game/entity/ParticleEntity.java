package net.cattaka.hungrycatball.game.entity;

import net.cattaka.collections.OrderedSet;
import net.cattaka.hungrycatball.HungryCatBallConstants;
import net.cattaka.hungrycatball.core.SceneBundle;
import net.cattaka.hungrycatball.game.GameEntityAddEvent;
import net.cattaka.hungrycatball.game.GameWorld;
import net.cattaka.hungrycatball.game.IGameEntity;
import net.cattaka.hungrycatball.gl.CtkGL;
import net.cattaka.hungrycatball.utils.DrawingUtil.AlphaMode;
import net.cattaka.hungrycatball.utils.ImageResource;

import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;

public class ParticleEntity implements IGameEntity {
    public static final int PARTICLE_MASK_SIZE = (1);
    public static final int PARTICLE_MASK_COLOR = (1 << 1);
    public static final int PARTICLE_MASK_SCALE = (1 << 2);
    public static final int PARTICLE_MASK_LOOP = (1 << 3);
    public int loopTime = 0;
    public int lifeTime = 0;
    private IGameEntity parentEntity;
    private int animIndex = 0;
    private int animCount;
    private Vec2 position = new Vec2();
    private Vec2 linearVelocity = new Vec2();
    private float angle = 0;
    private float angularVel = 0;
    private int mGivenScore;
    private ParticleExData mExData;

    @Override
    public void initialize(GameWorld gameWorld, SceneBundle sceneBundle, Object exData, float[] exFloatData) {
        if (exData == null) {
            throw new NullPointerException();
        }
        mExData = (ParticleExData) exData;
        if (mExData.startSize == null) {
            throw new NullPointerException();
        }
        if (mExData.imageResource == null) {
            throw new NullPointerException();
        }
    }

    @Override
    public void draw(CtkGL gl, SceneBundle sceneBundle) {
        float scale;
        final Vec2 size = new Vec2();
//		int	color = 0xFFFFFFFF;
        int time;
        int maxTime;

        if ((mExData.mask & PARTICLE_MASK_LOOP) != 0) {
            time = this.loopTime;
            maxTime = mExData.maxLoopTime;
        } else {
            time = this.lifeTime;
            maxTime = mExData.maxLifeTime;
        }

        if ((mExData.mask & PARTICLE_MASK_SIZE) != 0 && mExData.endSize != null) {
            size.mulLocal((float) (maxTime - time));
            size.x += (mExData.startSize.x * (float) (maxTime - time) + mExData.endSize.x * (float) (time)) / (float) maxTime;
            size.y += (mExData.startSize.y * (float) (maxTime - time) + mExData.endSize.x * (float) (time)) / (float) maxTime;
        } else {
            size.set(mExData.startSize);
        }
        if ((mExData.mask & PARTICLE_MASK_COLOR) != 0) {
            int a = ((mExData.endColor & 0xFF000000) >> 24) * time + ((mExData.startColor & 0xFF000000) >> 24) * (maxTime - time);
            int r = ((mExData.endColor & 0x00FF0000) >> 16) * time + ((mExData.startColor & 0x00FF0000) >> 16) * (maxTime - time);
            int g = ((mExData.endColor & 0x0000FF00) >> 8) * time + ((mExData.startColor & 0x0000FF00) >> 8) * (maxTime - time);
            int b = ((mExData.endColor & 0x000000FF)) * time + ((mExData.startColor & 0x000000FF)) * (maxTime - time);
            a = (a / maxTime);
            r = (r / maxTime);
            g = (g / maxTime);
            b = (b / maxTime);
//			color = (a<<24)|(r<<16)|(g<<8)|(b);
        } else {
//			color = mExData.startColor;
        }
        if ((mExData.mask & PARTICLE_MASK_SCALE) != 0) {
            scale = mExData.endScale * time + mExData.startScale * (maxTime - time);
            scale /= maxTime;
        } else {
            scale = mExData.startScale;
        }

        // FIXME Colorの処理を入れる
        // FIXME 回転の処理を入れる
        // FIXME Scaleの処理を入れる
        sceneBundle.getDrawUtil().drawBitmap(gl, mExData.imageResource, animIndex, mExData.textureCol, position, size, angle, 1, null, AlphaMode.STD);
    }

    @Override
    public void drawDebug(CtkGL gl, SceneBundle sceneBundle) {
        final Vec3 color = new Vec3(1, 1, 0);
        sceneBundle.getDrawUtil().drawCicle(gl, this.position.x, this.position.y, 0.1f, color);
    }

    @Override
    public float getAngle() {
        return this.angle;
    }

    @Override
    public void setAngle(float angle) {
        this.angle = angle;
    }

    @Override
    public float getAngularVelocity() {
        return angularVel;
    }

    @Override
    public void setAngularVelocity(float angularVelocity) {
        this.angularVel = angularVelocity;
    }

    @Override
    public void getLinearVelocity(Vec2 dst) {
        dst.set(linearVelocity);
    }

    @Override
    public void setLinearVelocity(Vec2 src) {
        linearVelocity.set(src);
    }

    @Override
    public void getPosition(Vec2 dst) {
        dst.set(this.position);
    }

    @Override
    public boolean isIntersect(Vec2 position) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isPhysics() {
        return false;
    }

    @Override
    public boolean isSkipableDraw() {
        return true;
    }

    @Override
    public void onAdd(GameWorld gameWorld) {
    }

    @Override
    public void onDelete(GameWorld gameWorld) {
    }

    @Override
    public void postStep(GameWorld gameWorld, SceneBundle sceneBundle,
                         OrderedSet<IGameEntity> removedGameEntities) {
        if (parentEntity != null) {
            removedGameEntities.contains(parentEntity);
            parentEntity = null;
        }
    }

    @Override
    public void preStep(GameWorld gameWorld, SceneBundle sceneBundle,
                        OrderedSet<IGameEntity> addedGameEntities) {
    }

    @Override
    public void setXForm(Vec2 position, float angle) {
        this.position.set(position);
        this.angle = angle;
    }

    @Override
    public void step(GameWorld gameWorld, SceneBundle sceneBundle, OrderedSet<GameEntityAddEvent> gameEntityAddEvents, OrderedSet<IGameEntity> removedGameEntities) {
        if (mExData.linearAccel != null) {
            linearVelocity.x += mExData.linearAccel.x * HungryCatBallConstants.STEP_DT;
            linearVelocity.y += mExData.linearAccel.y * HungryCatBallConstants.STEP_DT;
        }
        this.position.x += linearVelocity.x * HungryCatBallConstants.STEP_DT;
        this.position.y += linearVelocity.y * HungryCatBallConstants.STEP_DT;
        this.angle += angularVel * HungryCatBallConstants.STEP_DT;

        this.animCount++;
        if (this.animCount > mExData.animStep) {
            this.animCount = 0;
            this.animIndex++;
            if (this.animIndex >= mExData.imageResource.getRows()) {
                this.animIndex = 0;
            }
        }

        this.loopTime++;
        if (this.loopTime >= mExData.maxLoopTime) {
            loopTime = 0;
        }
        this.lifeTime++;
        if (this.lifeTime > mExData.maxLifeTime) {
            removedGameEntities.add(this);
        }
    }

    public void addGivenScore(int score) {
        mGivenScore += score;
    }

    public int getGivenScore() {
        return mGivenScore;
    }

    public static class ParticleExData {
        public Vec2 linearAccel;
        public float startScale = 1;
        public float endScale = 1;
        public Vec2 startSize;
        public Vec2 endSize;
        public int startColor = 0xFFFFFFFF;
        public int endColor = 0xFFFFFFFF;
        public int maxLoopTime = 60;
        public int maxLifeTime = 60;
        public int animStep = 20;
        public boolean backFlag;
        public ImageResource imageResource;
        public int textureCol = 0;
        public int mask = 0;

        public ParticleExData() {
        }

        public ParticleExData(ParticleExData src) {
            super();
            this.linearAccel = src.linearAccel;
            this.startScale = src.startScale;
            this.endScale = src.endScale;
            this.startSize = src.startSize;
            this.endSize = src.endSize;
            this.startColor = src.startColor;
            this.endColor = src.endColor;
            this.maxLoopTime = src.maxLoopTime;
            this.maxLifeTime = src.maxLifeTime;
            this.animStep = src.animStep;
            this.backFlag = src.backFlag;
            this.imageResource = src.imageResource;
            this.textureCol = src.textureCol;
            this.mask = src.mask;
        }
    }
}
