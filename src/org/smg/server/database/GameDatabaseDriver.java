package org.smg.server.database;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;

// TODO should implement that interface when Container team is done
public class GameDatabaseDriver /*implements EndGameInterface*/ {
  static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  
  // Huan
  Entity getGame(long gameId) throws EntityNotFoundException {
    // TODO implement this method
	  
    return null;
  }
  
  // Huan
  List<Long> getAllPlayableGameIds(long playerId) {
    // TODO implement this method
    return null;
  }
  
  // lisa
  Map<String, Object> getStats(long gameId) {
    // TODO write this method
    // highScore: Map<String, Object> playerId, score
    // rating: Map<int totalRatings, double averageRating>
    // matches: List of Map<String, Object>:
    /*
     * �firstName�: �Bob�,
           �nickName�: �Ninja�,
           �winner�: true, // boolean
           �score�: 43543, //int
           �tokens:� 2394384 // (for games that have tokens)
     */
    return null;
  }
  
  //@Override
  // TODO override when Container is finished
  // lisa
  void updateStats(Map<String, Object> winInfo) {
    // (iin the beginning there are no stats for a game)
    /*
     * winInfo:
      PlayerIds: [ ] (long)
      Score: Map<long playerId, int score>
      GameId: long
      WinnerIds: [ ]
      Tokens: Optional.of(Map<long playerId, int tokenNumber>)
     */
    //
  }
  
  // lisa
  double updateRatings(long gameId, double newRating) {
    // TODO implement this (updates table) return newAverage
    return 0.00;
  }
}
