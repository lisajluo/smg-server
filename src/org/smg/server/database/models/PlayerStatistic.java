package org.smg.server.database.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Model of game statistic for a player in a game. Fields are defined in {@code StatisticProperty}
 * Mandatory Field: PLAYERID, GAMEID
 * @author Archer
 *
 */
public class PlayerStatistic {
  private Map<String,String> properties = new HashMap<String, String>();
  
  /**
   * enum for statistic properties
   * PLAYERID, GAMEID, WIN, LOST, DRAW, SCORE, TOKEN
   * @author Archer
   *
   */
  public static enum StatisticProperty {
    PLAYERID, GAMEID, 
    WIN, LOST, DRAW, 
    HIGHSCORE, TOKEN;
    /**
     * return StatisticProperty by value.
     * If not found, return null;
     * @param value
     * @return
     */
    
    public static StatisticProperty findByValue(String value) {
      for (StatisticProperty p: values()) {
        if (p.toString().equals(value.toUpperCase())){
          return p;
        }
      }
      return null;
    }
  }
  
  public PlayerStatistic(){
  }
  
  /**
   * get a property of a StatisticProperty
   * property must be one of the {@code StatisticProperty} and cannot be null
   * If property is null then throw IllegalArgumentException, which indicate an external failure.
   * 
   * @return
   */
  public String getProperty(StatisticProperty property){
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
   * Set property of PlayerStatistic
   * If property is null then throw IllegalArgumentException, which indicate an external failure.
   * 
   * @param property
   * @param value
   * @return
   */
  public boolean setProperty(StatisticProperty property, String value) {
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
  private boolean validateProperty(StatisticProperty property, String value) {
    return true;
  }
  
}
