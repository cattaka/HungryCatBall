package net.cattaka.hungrycatball.utils;

public class ImageResource {
    public static final int COL_UI_BUTTON_BLANK = 0;

    ;
    public static final int COL_UI_BUTTON_PLAY = 1;
    public static final int COL_UI_BUTTON_PAUSE = 2;
    public static final int COL_UI_BUTTON_STAGE = 3;
    public static final int COL_UI_BUTTON_RESTART = 4;
    public static final int COL_UI_BUTTON_EDIT = 5;
    public static final int COL_UI_BUTTON_LEFT = 6;
    public static final int COL_UI_BUTTON_RIGHT = 7;
    public static final int COL_UI_BUTTON_SOUND_ON = 8;
    public static final int COL_UI_BUTTON_SOUND_OFF = 9;
    private int resourceId;
    private int rows;
    private int cols;
    private int[][] glTextureIds;
    public ImageResource(int resourceId, int rows, int cols) {
        this.resourceId = resourceId;
        this.rows = rows;
        this.cols = cols;
    }

    public ImageResource(int resourceId) {
        this(resourceId, 1, 1);
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getResourceId() {
        return resourceId;
    }

    public int getGlTextureId(int r, int c) {
        return glTextureIds[r][c];
    }

    public int[][] getGlTextureIds() {
        return glTextureIds;
    }

    public void setGlTextureIds(int[][] glTextureIds) {
        this.glTextureIds = glTextureIds;
    }

    public enum TextureId {
        MENU_BUTTONS,
        ENTITY_PLAYER,
        ENTITY_BALL,
        ENTITY_GOAL,
        ENTITY_SOFTBLOCK,
        ENTITY_SOFTBLOCK_PIECE,
        ENTITY_HARDBLOCK,
        ENTITY_HARDBLOCK_PIECE,
        ENTITY_SOFTBLOCK_SMALL,
        ENTITY_SOFTBLOCK_SMALL_PIECE,
        ENTITY_HARDBLOCK_SMALL,
        ENTITY_HARDBLOCK_SMALL_PIECE,
        ENTITY_HINGE,
        ENTITY_RIGIDBLOCK_THIN,
        ENTITY_LEVER,
        ENTITY_ROLLER,
        WALL_BOTTOM,
        WALL_SIDE,
        WALL_TOP,
        UI_PANEL,
        UI_BUTTON,
        UI_CHAR,
        UI_SQUIRE_BUTTON_RELEASED,
        UI_SQUIRE_BUTTON_PRESSED,
        UI_STAGE_BUTTON,
        UI_LOCK,
        MSG_TITLE,
        MSG_READY,
        MSG_LEVEL_FAILED_CLEARED,
        IMG_FAILED_CLEARED,
        IMG_HOWTOPLAY,
        IMG_CONGRATULATIONS,
        IMG_LOGO,
        BG_TYPE_1,
        BG_TYPE_2,
        PT_CROSS,
        DEBUG_MARKER,
    }

}
