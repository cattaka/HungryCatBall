package net.cattaka.hungrycatball;

import android.graphics.RectF;
import android.os.Environment;

import org.jbox2d.common.Vec2;

public class HungryCatBallConstants {
    public static String MAP_DIRECTORY = Environment.getExternalStorageDirectory() + "/HungryCatBall";

    public static String TAG = "HungryCatBall";
    public static String DB_NAME = "data.db";
    public static String VERSION = "";

    /**
     * ワールドの大きさ。
     * １セル32x32で想定して、14x24セル(480px:800pxの１セルを余白としたもの)
     */
    //public static RectF WORLD_RECT = new RectF(-14, 12, 14, -12);
    public static RectF WORLD_RECT = new RectF(-7.5f, 12.5f, 7.5f, -12.5f);
    public static Vec2 GRAVITY = new Vec2(0.0f, -5.0f);
    public static float CELL_SIZE = 1f;
    public static float CELL_MARGIN = 0.1f;

    public static Vec2 UI_BACK_BUTTON_POS = new Vec2(WORLD_RECT.left + CELL_SIZE * 1.5f, WORLD_RECT.top - CELL_SIZE * 2.5f);

    public static float PLAYER_MAX_SPEED = 2;
    public static float HEALTH_SOFTBLOCK = 180;
    public static float HEALTH_HARDBLOCK = 360;

    /**
     * ステップの間隔。30fpsを想定。
     */
    public static float STEP_DT = 1f / 30f;
    /**
     * ステップの変数。何に使うかわからない。
     */
    public static int STEP_ITERATIONS = 10;

    public static float DRAW_SCORE_LIFE_TIME = 1f;
    public static float GAME_BLOCK_RESTITUTION = 0.5f;
    public static float GAME_SHOCK_MARGIN = 5f;
}
