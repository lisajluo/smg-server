package org.smg.server.database.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Model of normal player in the datastore. Field are defined in
 * {@code PlayerProperty} Mandatory fields: playerId (after insertion), email,
 * hashedPassword
 * 
 * @author Archer
 * 
 */
public class Player {
  private Map<String,String> properties = new HashMap<String, String>();
  
  /**
   * enum for player properties
   * @author Archer
   *
   */
  public static enum PlayerProperty {
    PLAYERID, HASHEDPASSWORD, ACCESSSIGNATURE,
    EMAIL,
    FIRSTNAME, LASTNAME, NICKNAME;
    
    /**
     * return PlayerProperty by value.
     * If not found, return null;
     * @param value
     * @return
     */
    
    public static PlayerProperty findByValue(String value) {
      for (PlayerProperty p: values()) {
        if (p.toString().equals(value)){
          return p;
        }
      }
      return null;
    }
  }
  
  public Player(){
  }
  
  /**
   * get a property of a player
   * property must be one of the {@code PlayerProperty} and cannot be null
   * @return
   */
  public String getProperty(PlayerProperty property){
    if (property == null) {
      throw new IllegalArgumentException();
    }
    String p = properties.get(property.toString()); 
    return p == null? "": p;
  }
  
  /**
   * get all properties in the player instance.
   * 
   */
  public Map<String,String> getAllProperties() {
    return this.properties;
  }
  
  /**
   * Set property of player
   * If property is null then throw IllegalArgumentException, which indicate an external failure.
   * 
   * @param property
   * @param value
   * @return
   */
  public boolean setProperty(PlayerProperty property, String value) {
    if (property == null) {
      throw new IllegalArgumentException();
    }
    if (this.validateProperty(property,value)) {
      properties.put(property.toString(), value);
      return true;
    } 
    return false;
  }
  
  /**
   * TODO
   * @param property
   * @param value
   * @return
   */
  private boolean validateProperty(PlayerProperty property, String value) {
    return true;
  }
  
  /**
   * See if a player instance has all the property of another player instance
   * @param subPlayer
   * @return
   */
  public boolean isContain(Player subPlayer) {
    Map<String,String> subPlayerInfo = subPlayer.getAllProperties();
    for (String key: subPlayerInfo.keySet()) {
      if (key.equals(PlayerProperty.HASHEDPASSWORD.toString())) {
        continue;
      }
      if (!this.properties.containsKey(key) || 
          !this.properties.get(key).equals(subPlayerInfo.get(key))) {
        return false;
      }
    }
    return true;
  }
}
