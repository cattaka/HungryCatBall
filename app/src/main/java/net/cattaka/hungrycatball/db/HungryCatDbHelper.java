package net.cattaka.hungrycatball.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.cattaka.hungrycatball.stage.StageInfo;
import net.cattaka.hungrycatball.stage.StageInfoKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HungryCatDbHelper extends SQLiteOpenHelper {

    public HungryCatDbHelper(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE STAGE_INFO(ID INTEGER PRIMARY KEY AUTOINCREMENT, COURSE INTEGER, STAGE_NO INTEGER, HIGH_SCORE INTEGER, UNLOCKED INTEGER, UNIQUE(COURSE, STAGE_NO))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public StageInfo findByKey(int course, int stageNo) {
        SQLiteDatabase db = getReadableDatabase();
        StageInfo stageInfo;
        try {
            Cursor cursor = db.rawQuery("SELECT ID, COURSE, STAGE_NO, HIGH_SCORE, UNLOCKED FROM STAGE_INFO WHERE COURSE=? AND STAGE_NO=?", new String[]{String.valueOf(course), String.valueOf(stageNo)});
            if (cursor.moveToNext()) {
                int highScore = cursor.getInt(3);
                boolean unlocked = (cursor.getInt(4) != 0);
                stageInfo = new StageInfo(course, stageNo, highScore, unlocked);
            } else {
                stageInfo = new StageInfo(course, stageNo, 0, false);
            }
            cursor.close();
        } finally {
            db.close();
        }
        return stageInfo;
    }

    public Map<StageInfoKey, StageInfo> findAllMap(int course) {
        Map<StageInfoKey, StageInfo> stageInfoMap = new TreeMap<StageInfoKey, StageInfo>();
        List<StageInfo> stageInfos = findAllList(course);
        for (StageInfo stageInfo : stageInfos) {
            stageInfoMap.put(new StageInfoKey(stageInfo.getCourse(), stageInfo.getStageNo()), stageInfo);
        }
        return stageInfoMap;
    }

    public List<StageInfo> findAllList(int argCourse) {
        List<StageInfo> stageInfos = new ArrayList<StageInfo>();
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT ID, COURSE, STAGE_NO, HIGH_SCORE, UNLOCKED FROM STAGE_INFO WHERE COURSE=? ORDER BY COURSE, STAGE_NO", new String[]{String.valueOf(argCourse)});
            while (cursor.moveToNext()) {
                int course = cursor.getInt(1);
                int stageNo = cursor.getInt(2);
                int highScore = cursor.getInt(3);
                boolean unlocked = (cursor.getInt(4) != 0);
                StageInfo stageInfo = new StageInfo(course, stageNo, highScore, unlocked);
                stageInfos.add(stageInfo);
            }
            cursor.close();
        } finally {
            db.close();
        }
        return stageInfos;
    }

    public void registerStageInfo(StageInfo stageInfo) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("HIGH_SCORE", stageInfo.getHighScore());
            values.put("UNLOCKED", (stageInfo.isUnlocked() ? 1 : 0));
            if (db.update("STAGE_INFO", values, "COURSE=? AND STAGE_NO=?", new String[]{String.valueOf(stageInfo.getCourse()), String.valueOf(stageInfo.getStageNo())}) == 0) {
                values.put("COURSE", stageInfo.getCourse());
                values.put("STAGE_NO", stageInfo.getStageNo());
                db.insert("STAGE_INFO", null, values);
            }
        } finally {
            db.close();
        }
    }
}
