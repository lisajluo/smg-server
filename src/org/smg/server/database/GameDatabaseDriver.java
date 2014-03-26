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
  
  Entity getGame(long gameId) throws EntityNotFoundException {
    // TODO implement this method
    return null;
  }
  
  List<Long> getAllPlayableGameIds(long playerId) {
    // TODO implement this method
    return null;
  }
  
  //@Override
  // TODO override when Container is finished
  void updateStats(Map<String, Object> winInfo) {
    /*
     * winInfo:
      PlayerIds: [ ] (long)
      Score: Map<long playerId, int score>
      GameId: long
      WinnerIds: [ ]
      Tokens: Optional.of(Map<long playerId, int tokenNumber>)
     */
  }
}
