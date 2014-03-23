package org.smg.server.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.smg.server.servlet.container.ContainerConstants;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

public class DummyDataGen {
  
  private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  
  
  // for container test
  public static void addPlayer(){
    Entity player = new Entity("Player");
    player.setProperty("email", "as@foo.bar");
    player.setProperty("password", "1234");
    player.setProperty("firstname", "Antonio");
    player.setProperty("lastname", "Salieri");
    player.setProperty("nickname", "AS");
    player.setProperty("accessSignature", "HJJIIKKSJF");    
    datastore.put(player);
    
    Entity player2 = new Entity("Player");
    player2.setProperty("email", "bc@foo.bar");
    player2.setProperty("password", "5678");
    player2.setProperty("firstname", "Bella");
    player2.setProperty("lastname", "Chris");
    player2.setProperty("nickname", "bella");
    player2.setProperty("accessSignature", "ISUDJKAKKA");    
    datastore.put(player2);
  }
  
  public static void addGame(){
    Entity game = new Entity("Game");
    game.setProperty("gameName","Cheat");
    datastore.put(game);
  }
  
  public static void updateMatch() throws IOException {
    Map<String, Object> match = new HashMap<String, Object>();
    match.put(ContainerConstants.GAME_ID, Long.valueOf(4644337115725824L));
    List<Long> playerIds = new ArrayList<Long>();
    playerIds.add(5348024557502464L);
    playerIds.add(6473924464345088L);
    match.put(ContainerConstants.PLAYER_IDS, playerIds);
    match.put(ContainerConstants.PLAYER_THAT_HAS_TURN, Long.valueOf(5348024557502464L));
    List<Map<String,Object>> history = new LinkedList<Map<String,Object>>();
    Map<String,Object> his_entity = new HashMap<String,Object>();
    Map<String,Object> state = new HashMap<String,Object>();
    state.put("board", "01000111010101");
    List<Integer> state2 = new ArrayList<Integer>();
    state2.add(1);
    state2.add(2);
    state.put("state2", state2);
    List<Object> lastmove = new LinkedList<Object>();
    Map<String,Object> op1 = new HashMap<String,Object>();
    op1.put("value", "sd");
    op1.put("type", "Set");    
    op1.put("visibleToPlayerIds", "ALL");
    op1.put("key", "k");
    Map<String,Object> op2 = new HashMap<String,Object>();
    op2.put("to", 54);
    op2.put("from", 23);
    op2.put("type", "SetRandomInteger");
    op2.put("key", "xcv");
    lastmove.add(op1);
    lastmove.add(op2);
    his_entity.put(ContainerConstants.GAME_STATE, state);
    his_entity.put(ContainerConstants.LAST_MOVE, lastmove);
    history.add(his_entity);
    match.put(ContainerConstants.HISTORY,history);
    
    long matchId = 5770237022568448L;
    if (DatabaseDriver.updateMatchEntity(matchId, match)) {
      System.out.println("SUCCESS");
    }
  }
    
}
