package org.smg.server.database;

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
}
