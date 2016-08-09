package net.cattaka.hungrycatball.io;

import android.util.Log;

import net.cattaka.hungrycatball.HungryCatBallConstants;
import net.cattaka.hungrycatball.game.GameEntityAddEvent;
import net.cattaka.hungrycatball.game.entity.BallEntity;
import net.cattaka.hungrycatball.game.entity.BallSmallEntity;
import net.cattaka.hungrycatball.game.entity.DoorEntity;
import net.cattaka.hungrycatball.game.entity.GoalEntity;
import net.cattaka.hungrycatball.game.entity.HardblockEntity;
import net.cattaka.hungrycatball.game.entity.HardblockSmallEntity;
import net.cattaka.hungrycatball.game.entity.PhysicalEntity;
import net.cattaka.hungrycatball.game.entity.PlayerEntity;
import net.cattaka.hungrycatball.game.entity.RollerEntity;
import net.cattaka.hungrycatball.game.entity.RollerLargeEntity;
import net.cattaka.hungrycatball.game.entity.RollingBarEntity;
import net.cattaka.hungrycatball.game.entity.SoftblockEntity;
import net.cattaka.hungrycatball.game.entity.SoftblockSmallEntity;
import net.cattaka.hungrycatball.game.entity.WallEntity;
import net.cattaka.hungrycatball.utils.ImageResource.TextureId;

import org.jbox2d.common.Vec2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneIo {
    public static final Map<String, Class<? extends PhysicalEntity>> GAME_ENTITY_MAP;

    static {
        HashMap<String, Class<? extends PhysicalEntity>> gameEntityMap = new HashMap<String, Class<? extends PhysicalEntity>>();
        gameEntityMap.put("wall", WallEntity.class);
        gameEntityMap.put("player", PlayerEntity.class);
        gameEntityMap.put("goal", GoalEntity.class);
        gameEntityMap.put("ball", BallEntity.class);
        gameEntityMap.put("ballSmall", BallSmallEntity.class);
        gameEntityMap.put("softblock", SoftblockEntity.class);
        gameEntityMap.put("hardblock", HardblockEntity.class);
        gameEntityMap.put("softblockSmall", SoftblockSmallEntity.class);
        gameEntityMap.put("hardblockSmall", HardblockSmallEntity.class);
        gameEntityMap.put("door", DoorEntity.class);
        gameEntityMap.put("rollingBar", RollingBarEntity.class);
        gameEntityMap.put("roller", RollerEntity.class);
        gameEntityMap.put("rollerLarge", RollerLargeEntity.class);

        GAME_ENTITY_MAP = Collections.synchronizedMap(gameEntityMap);
    }

    public static SceneData read(Reader reader) throws IOException {
        List<GameEntityAddEvent> result = new ArrayList<GameEntityAddEvent>();
        StringBuilder sb = new StringBuilder();
        int r;
        while ((r = reader.read()) != -1) {
            sb.append((char) r);
        }
        SceneData sceneData = new SceneData();
        // 初期値
        sceneData.setBgTextureId(TextureId.BG_TYPE_1);
        try {
            JSONObject jsonObject = new JSONObject(sb.toString());
            if (!jsonObject.isNull("background")) {
                try {
                    TextureId textureId = TextureId.valueOf(jsonObject.getString("background"));
                    sceneData.setBgTextureId(textureId);
                } catch (IllegalArgumentException e) {
                    Log.e(HungryCatBallConstants.TAG, e.getMessage(), e);
                }
            }

            JSONArray jsonEvents = jsonObject.getJSONArray("events");
            for (int i = 0; i < jsonEvents.length(); i++) {
                JSONObject jsonEvent = jsonEvents.getJSONObject(i);
                GameEntityAddEvent event = new GameEntityAddEvent();
                Class<? extends PhysicalEntity> gameEntityClass = GAME_ENTITY_MAP.get(jsonEvent.getString("cls"));
                event.setGameEntityClass(gameEntityClass);
                if (jsonEvent.has("pX") && jsonEvent.has("pY")) {
                    float x = (float) jsonEvent.getDouble("pX");
                    float y = (float) jsonEvent.getDouble("pY");
                    event.setPosition(new Vec2(x, y));
                }
                if (jsonEvent.has("lvX") && jsonEvent.has("lvY")) {
                    float x = (float) jsonEvent.getDouble("lvX");
                    float y = (float) jsonEvent.getDouble("lvY");
                    event.setLinearVelocity(new Vec2(x, y));
                }
                if (jsonEvent.has("ang")) {
                    float ang = (float) jsonEvent.getDouble("ang");
                    event.setAngle(ang);
                }
                if (jsonEvent.has("exFloat")) {
                    JSONArray jsonArray = jsonEvent.getJSONArray("exFloat");
                    float[] exFloatData = new float[jsonArray.length()];
                    for (int s = 0; s < jsonArray.length(); s++) {
                        exFloatData[s] = (float) jsonArray.getDouble(s);
                    }
                    event.setExFloatData(exFloatData);
                }
                result.add(event);
            }
        } catch (JSONException e) {
            throw new IOException(e.getMessage());
        }
        sceneData.setGameEntityAddEvents(result);
        return sceneData;
    }

    public static void write(Writer writer, SceneData sceneData) throws IOException {
        HashMap<Class<? extends PhysicalEntity>, String> rGameEntityMap = new HashMap<Class<? extends PhysicalEntity>, String>();
        for (Map.Entry<String, Class<? extends PhysicalEntity>> entry : GAME_ENTITY_MAP.entrySet()) {
            rGameEntityMap.put(entry.getValue(), entry.getKey());
        }

        JSONObject result = new JSONObject();
        try {
            result.put("background", sceneData.getBgTextureId().name());
            JSONArray jsonEvents = new JSONArray();
            for (GameEntityAddEvent event : sceneData.getGameEntityAddEvents()) {
                JSONObject jsonEvent = new JSONObject();
                String cls = rGameEntityMap.get(event.getGameEntityClass());
                if (cls == null) {
                    throw new IOException("Not supported class : " + event.getGameEntityClass().getName());
                }
                jsonEvent.put("cls", cls);
                if (event.getPosition() != null) {
                    jsonEvent.put("pX", event.getPosition().x);
                    jsonEvent.put("pY", event.getPosition().y);
                }
                if (event.getLinearVelocity() != null) {
                    jsonEvent.put("lvX", event.getLinearVelocity().x);
                    jsonEvent.put("lvY", event.getLinearVelocity().y);
                }
                if (event.getAngle() != 0) {
                    jsonEvent.put("ang", event.getAngle());
                }
                if (event.getExFloatData() != null) {
                    JSONArray jsonArray = new JSONArray();
                    for (int s = 0; s < event.getExFloatData().length; s++) {
                        jsonArray.put(event.getExFloatData()[s]);
                    }
                    jsonEvent.put("exFloat", jsonArray);
                }
                jsonEvents.put(jsonEvent);
            }
            result.put("events", jsonEvents);
        } catch (JSONException e) {
            throw new IOException(e.getMessage());
        }
        writer.write(result.toString());
    }

    public static SceneData openMap(File file) {
        try {
            Reader reader = new FileReader(file);
            try {
                return read(reader);
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            Log.e(HungryCatBallConstants.TAG, e.getMessage(), e);
            return null;
        }
    }

    public static File saveMap(String fileName, SceneData events) {
        File mapDir = new File(HungryCatBallConstants.MAP_DIRECTORY);
        if (!mapDir.exists()) {
            mapDir.mkdirs();
        }
        File file = new File(mapDir.getAbsolutePath() + File.separatorChar + fileName);
        if (saveMap(file, events)) {
            return file;
        } else {
            return null;
        }
    }

    public static boolean saveMap(File file, SceneData events) {
        try {
            Writer writer = new FileWriter(file);
            try {
                write(writer, events);
                writer.flush();
            } finally {
                writer.close();
            }
            return true;
        } catch (IOException e) {
            Log.e(HungryCatBallConstants.TAG, e.getMessage(), e);
            return false;
        }
    }

    public static List<File> getMapFileList() {
        List<File> results = new ArrayList<File>();
        File mapDir = new File(HungryCatBallConstants.MAP_DIRECTORY);
        if (mapDir.exists() && mapDir.isDirectory()) {
            for (File file : mapDir.listFiles()) {
                results.add(file);
            }
            Collections.sort(results);
        }
        return results;
    }
}
