package net.cattaka.hungrycatball.stage;

public class StageInfo extends StageInfoKey {
    private int highScore;
    private boolean unlocked;

    public StageInfo() {
    }

    public StageInfo(int course, int stageNo) {
        super(course, stageNo);
    }

    public StageInfo(int course, int stageNo, int highScore, boolean unlocked) {
        super(course, stageNo);
        this.highScore = highScore;
        this.unlocked = unlocked;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }
}
