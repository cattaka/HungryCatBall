package net.cattaka.hungrycatball.stage;

public class StageInfoKey implements Comparable<StageInfoKey> {
    private int course;
    private int stageNo;

    public StageInfoKey() {
    }

    public StageInfoKey(int course, int stageNo) {
        super();
        this.course = course;
        this.stageNo = stageNo;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public int getStageNo() {
        return stageNo;
    }

    public void setStageNo(int stageNo) {
        this.stageNo = stageNo;
    }

    @Override
    public int compareTo(StageInfoKey another) {
        int r = getCourse() - another.getCourse();
        if (r == 0) {
            r = getStageNo() - another.getStageNo();
        }
        return r;
    }
}
