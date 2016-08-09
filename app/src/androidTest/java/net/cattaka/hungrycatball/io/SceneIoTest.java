package net.cattaka.hungrycatball.io;

import junit.framework.TestCase;

import net.cattaka.hungrycatball.game.GameEntityAddEvent;
import net.cattaka.hungrycatball.game.entity.BallEntity;
import net.cattaka.hungrycatball.game.entity.PlayerEntity;
import net.cattaka.hungrycatball.game.entity.WallEntity;

import org.jbox2d.common.Vec2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class SceneIoTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testWriteRead() throws Exception {
        List<GameEntityAddEvent> events = new ArrayList<GameEntityAddEvent>();
        List<GameEntityAddEvent> events2;

        {
            GameEntityAddEvent event1 = new GameEntityAddEvent();
            event1.setGameEntityClass(WallEntity.class);
            events.add(event1);

            GameEntityAddEvent event2 = new GameEntityAddEvent();
            event2.setGameEntityClass(PlayerEntity.class);
            event2.setPosition(new Vec2(1, 2));
            events.add(event2);

            GameEntityAddEvent event3 = new GameEntityAddEvent();
            event3.setGameEntityClass(BallEntity.class);
            event3.setPosition(new Vec2(3, 4));
            event3.setLinearVelocity(new Vec2(5, 6));
            events.add(event3);
        }

        {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(bout);

            SceneData sceneData = new SceneData();
            sceneData.setGameEntityAddEvents(events);
            SceneIo.write(writer, sceneData);
            writer.flush();

            ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
            Reader reader = new InputStreamReader(bin);

            SceneData sceneData2 = SceneIo.read(reader);
            events2 = sceneData2.getGameEntityAddEvents();
        }
        assertEquals(3, events2.size());
        assertEquals(WallEntity.class, events2.get(0).getGameEntityClass());
        assertNull(events2.get(0).getPosition());
        assertNull(events2.get(0).getLinearVelocity());
        assertEquals(PlayerEntity.class, events2.get(1).getGameEntityClass());
        assertTrue(events2.get(1).getPosition().sub(new Vec2(1, 2)).length() < 0.01f);
        assertNull(events2.get(1).getLinearVelocity());
        assertEquals(BallEntity.class, events2.get(2).getGameEntityClass());
        assertTrue(events2.get(2).getPosition().sub(new Vec2(3, 4)).length() < 0.01f);
        assertTrue(events2.get(2).getLinearVelocity().sub(new Vec2(5, 6)).length() < 0.01f);
    }
}
