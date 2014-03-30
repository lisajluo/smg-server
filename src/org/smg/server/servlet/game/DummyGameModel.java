package org.smg.server.servlet.game;

import static org.smg.server.servlet.container.ContainerConstants.GAME_OVER_SCORES;
import static org.smg.server.servlet.container.ContainerConstants.PLAYER_ID;
import static org.smg.server.servlet.container.ContainerConstants.PLAYER_ID_TO_NUMBER_OF_TOKENS_IN_POT;
import static org.smg.server.servlet.developer.DeveloperConstants.FIRST_NAME;
import static org.smg.server.servlet.developer.DeveloperConstants.NICKNAME;
import static org.smg.server.servlet.game.GameConstants.FINISHED_GAMES;
import static org.smg.server.servlet.game.GameConstants.GAME_ID;
import static org.smg.server.servlet.game.GameConstants.GAME_STATISTICS;
import static org.smg.server.servlet.game.GameConstants.HIGH_SCORE;
import static org.smg.server.servlet.game.GameConstants.PLAYERS;
import static org.smg.server.servlet.game.GameConstants.RATING;
import static org.smg.server.servlet.game.GameConstants.SCORE;
import static org.smg.server.servlet.game.GameConstants.TOKENS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.smg.server.database.ContainerDatabaseDriver;
import org.smg.server.database.DatabaseDriverPlayer;
import org.smg.server.servlet.container.ContainerConstants;
import org.smg.server.util.JSONUtil;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class DummyGameModel {
	static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	static public void updateStats() {
		//System.out.println("I am here");
		Map ScoreInfo = new HashMap();
		long virtualId = 1234;
		ScoreInfo.put(virtualId,1000);
		Map<String,Object> winInfo = new HashMap<String,Object> ();
		winInfo.put(GAME_OVER_SCORES,ScoreInfo);
		
	    long gameId = Long.parseLong("5066549580791808");
	    int highScore = 100;
	    Map highScoreMap = new HashMap();
	    List finishedGames = new ArrayList();
	    List players = new ArrayList();
	    Map player;
	    String firstName, nickname;
	    int score;
	    long tokens;

	    Entity statistics = new Entity(GAME_STATISTICS, gameId);
	    highScore = 0;
	    finishedGames = new ArrayList();
	    Map<Object, Object> gameOverScores = (Map<Object, Object>) winInfo.get(GAME_OVER_SCORES);

	    for (Map.Entry<Object, Object> entry : gameOverScores.entrySet()) {
	      long playerId = (Long) entry.getKey();
	      player = new HashMap();
	      if ((int) entry.getValue() >= highScore) {
	        highScoreMap.put(PLAYER_ID, Long.toString(playerId));
	        highScoreMap.put(SCORE, (Integer) entry.getValue());
	      }
	    }

	    statistics.setProperty(HIGH_SCORE, new JSONObject(highScoreMap).toString());
	    statistics.setProperty(FINISHED_GAMES, new Text(new JSONArray(finishedGames).toString()));

	    datastore.put(statistics);
	    
	  }
	public static void  insertMatchEntity() throws JSONException {
		System.out.println("I am here");
		JSONObject match = new JSONObject();
		List playerIdList = new ArrayList();
		playerIdList.add("4785074604081152");
		playerIdList.add("6192449487634432");
		JSONArray playerIdAsJson = new JSONArray(playerIdList);
		match.put(ContainerConstants.PLAYER_IDS,playerIdAsJson);		
	    Entity entity = new Entity(ContainerConstants.MATCH);
	    long gameId = Long.parseLong("5066549580791808");
	    entity.setProperty(ContainerConstants.GAME_ID, gameId);
	    entity.setUnindexedProperty(ContainerConstants.PLAYER_IDS,
	        match.getJSONArray(ContainerConstants.PLAYER_IDS).toString());
	    entity.setProperty(ContainerConstants.PLAYER_THAT_HAS_TURN, 
	        null);
	    entity.setProperty(ContainerConstants.GAME_OVER_REASON, 
	    		ContainerConstants.NOT_OVER);
	    System.out.println("finished");
	    datastore.put(entity);
	  }
	public static void updateMatch() throws IOException {
	    Map<String, Object> match = new HashMap<String, Object>();
	    match.put(ContainerConstants.GAME_ID, Long.valueOf(6403555720167424L));
	    List<Long> playerIds = new ArrayList<Long>();
	    playerIds.add(4996180836614144L);
	    playerIds.add(6122080743456768L);
	    match.put(ContainerConstants.PLAYER_IDS, playerIds);
	    match.put(ContainerConstants.PLAYER_THAT_HAS_TURN, Long.valueOf(6122080743456768L));
	   
	    List<Map<String, Object>> history = new LinkedList<Map<String, Object>>();
	    Map<String, Object> his_entity = new HashMap<String, Object>();
	    Map<String, Object> state = new HashMap<String, Object>();
	    state.put("board", "01000111010101");
	    List<Integer> state2 = new ArrayList<Integer>();
	    state2.add(1);
	    state2.add(2);
	    state.put("state2", state2);
	    List<Object> lastmove = new LinkedList<Object>();
	    Map<String, Object> visibleTo = new HashMap<String, Object>();
	    visibleTo.put("key3", "value3");
	    visibleTo.put("key4", "value4");

	    Map<String, Object> playerIdToNumberOfTokensInPot = new HashMap<String, Object>();
	    playerIdToNumberOfTokensInPot.put("1", 111);
	    playerIdToNumberOfTokensInPot.put("2", 222);
	    Map<String, Object> gameState = new HashMap<String, Object>();
	    gameState.put(ContainerConstants.STATE, state);
	    gameState.put(ContainerConstants.VISIBLE_TO, visibleTo);
	    gameState.put(ContainerConstants.PLAYER_ID_TO_NUMBER_OF_TOKENS_IN_POT,
	        playerIdToNumberOfTokensInPot);

	    Map<String, Object> op1 = new HashMap<String, Object>();
	    op1.put("value", "sd");
	    op1.put("type", "Set");
	    op1.put("visibleToPlayerIds", "ALL");
	    op1.put("key", "k");
	    Map<String, Object> op2 = new HashMap<String, Object>();
	    op2.put("to", 54);
	    op2.put("from", 23);
	    op2.put("type", "SetRandomInteger");
	    op2.put("key", "xcv");
	    lastmove.add(op1);
	    lastmove.add(op2);
	    his_entity.put(ContainerConstants.GAME_STATE, gameState);
	    his_entity.put(ContainerConstants.LAST_MOVE, lastmove);
	    history.add(his_entity);
	    match.put(ContainerConstants.HISTORY, history);

	    long matchId = 5559130790035456L;
	    if (ContainerDatabaseDriver.updateMatchEntity(matchId, match)) {
	      System.out.println("SUCCESS");
	    }
	  }

}
