package org.smg.server.database.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Model of player's friend black list in the datastore. 
 * 
 * @author Archer
 * 
 */
public class FriendBlackList {
  public static enum FriendType {
    Google, Facebook;
    
    /**
     * return PlayerProperty by value.
     * If not found, return null;
     * @param value
     * @return
     */
    public static FriendType findByValue(String value) {
      for (FriendType p: values()) {
        System.out.println("verifying to string"+p.toString());
        if (p.toString().equals(value)){
          return p;
        }
      }
      return null;
    }
  }
  
  private String id;
  private Set<String> googleList;
  private Set<String> facebookList;
  
  /**
   * constructor
   * @param id
   * @param gList could be null
   * @param fList could be null
   */
  public FriendBlackList(String id, List<String> gList, List<String> fList) {
    if (id == null) {
      throw new IllegalArgumentException();
    }
    this.id = id;
    if (gList == null){
      this.googleList = new HashSet<String>();
    } else {
      this.googleList = new HashSet<String>(gList);
    }
    if (fList == null){
      this.facebookList = new HashSet<String>();
    } else {
      this.facebookList = new HashSet<String>(fList);
    }
  }
  
  /**
   * return google friend block
   * @return
   */
  public Set<String> getGoogleSet() {
    return this.googleList;
  }
  
  /**
   * return facebook friend block list
   * @return
   */
  public Set<String> getFacebookSet() {
    return this.facebookList;
  }
  
  /**
   * return id
   * @return
   */
  public String getId() {
    return this.id;
  }
  
  /**
   * add a friend into blocking
   * @param id
   * @param ft
   */
  public void addFriend(String id, FriendType ft) {
    if (id == null) {
      return;
    }
    if (ft == FriendType.Facebook) {
      this.facebookList.add(id);
    } else if (ft == FriendType.Google) {
      this.googleList.add(id);
    }
  }
  
  /**
   * remove a friend from blocking
   * @param id
   * @param ft
   */
  public void removeFriend(String id, FriendType ft) {
    if (id == null) {
      return;
    }
    if (ft == FriendType.Facebook) {
      this.facebookList.remove(id);
    } else if (ft == FriendType.Google) {
      this.googleList.remove(id);
    }
  }
  
  public boolean equals(FriendBlackList other) {
    if (!this.id.equals(other.id)) {
      return false;
    }
    if (!this.facebookList.equals(other.facebookList)) {
      return false;
    }
    if (!this.googleList.equals(other.googleList)) {
      return false;
    }
    return true;
  }
}
