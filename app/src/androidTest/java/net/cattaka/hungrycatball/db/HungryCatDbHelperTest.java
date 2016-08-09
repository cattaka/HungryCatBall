package net.cattaka.hungrycatball.db;

import android.test.AndroidTestCase;

import net.cattaka.hungrycatball.stage.StageInfo;

import java.io.File;
import java.util.List;

public class HungryCatDbHelperTest extends AndroidTestCase {
    public static final String TEST_DB_NAME = "test.db";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        File dbFile = getContext().getDatabasePath(TEST_DB_NAME);
        if (dbFile.exists()) {
            if (!dbFile.delete()) {
                throw new RuntimeException("Deleting test.db. failed.");
            }
        }
    }

    public void testUpdate() {
        HungryCatDbHelper dbHelper = new HungryCatDbHelper(getContext(), TEST_DB_NAME);
        dbHelper.registerStageInfo(new StageInfo(1, 2, 3, false));
        dbHelper.registerStageInfo(new StageInfo(1, 2, 9, true));

        StageInfo si1 = dbHelper.findByKey(1, 2);
        assertNotNull(si1);
        assertEquals(1, si1.getCourse());
        assertEquals(2, si1.getStageNo());
        assertEquals(9, si1.getHighScore());
        assertTrue(si1.isUnlocked());
    }

    public void testFindAndRegist() {
        HungryCatDbHelper dbHelper = new HungryCatDbHelper(getContext(), TEST_DB_NAME);
        dbHelper.registerStageInfo(new StageInfo(1, 2, 3, false));
        dbHelper.registerStageInfo(new StageInfo(7, 8, 9, true));
        dbHelper.registerStageInfo(new StageInfo(4, 5, 6, true));

        StageInfo si1 = dbHelper.findByKey(4, 5);
        assertNotNull(si1);
        assertEquals(4, si1.getCourse());
        assertEquals(5, si1.getStageNo());
        assertEquals(6, si1.getHighScore());
        assertTrue(si1.isUnlocked());

        StageInfo si2 = dbHelper.findByKey(1, 3);
        assertNotNull(si2);
        assertEquals(1, si2.getCourse());
        assertEquals(3, si2.getStageNo());
        assertEquals(0, si2.getHighScore());
        assertFalse(si2.isUnlocked());
    }

    public void testFindAll() {
        HungryCatDbHelper dbHelper = new HungryCatDbHelper(getContext(), TEST_DB_NAME);
        dbHelper.registerStageInfo(new StageInfo(1, 2, 3, false));
        dbHelper.registerStageInfo(new StageInfo(7, 8, 9, true));
        dbHelper.registerStageInfo(new StageInfo(4, 5, 6, false));

        {
            List<StageInfo> stageInfos = dbHelper.findAllList(1);
            assertEquals(1, stageInfos.size());
            assertEquals(1, stageInfos.get(0).getCourse());
            assertEquals(2, stageInfos.get(0).getStageNo());
            assertEquals(3, stageInfos.get(0).getHighScore());
            assertFalse(stageInfos.get(0).isUnlocked());
        }
        {
            List<StageInfo> stageInfos = dbHelper.findAllList(4);
            assertEquals(1, stageInfos.size());
            assertEquals(4, stageInfos.get(0).getCourse());
            assertEquals(5, stageInfos.get(0).getStageNo());
            assertEquals(6, stageInfos.get(0).getHighScore());
            assertFalse(stageInfos.get(0).isUnlocked());
        }
        {
            List<StageInfo> stageInfos = dbHelper.findAllList(7);
            assertEquals(1, stageInfos.size());
            assertEquals(7, stageInfos.get(0).getCourse());
            assertEquals(8, stageInfos.get(0).getStageNo());
            assertEquals(9, stageInfos.get(0).getHighScore());
            assertTrue(stageInfos.get(0).isUnlocked());
        }
    }
}
