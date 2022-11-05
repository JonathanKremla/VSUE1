package dslab.mailbox;

import dslab.util.Config;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores Messages of all Users
 */
public class MessageStorage {
  private static ConcurrentHashMap<String, HashMap<Integer, Email>> messages = new ConcurrentHashMap<>();
  private static HashMap<String, Integer> indexMap = new HashMap<>();

  public static void loadUsers(Config userConfig){
    for(String k : userConfig.listKeys()){
      messages.put(k, new HashMap<>());
      indexMap.put(k, 1);
    }
  }

  public static Email put(String user, Email value){
   HashMap<Integer, Email> newEntry =  new HashMap<>();
   Integer messageId = indexMap.get(user);
   newEntry.put(messageId, value);
    return messages.put(user, newEntry).get(messageId);
  }

  public static void remove(String user, Integer key){
    messages.get(user).remove(key);
  }

  public static Email get(String user, Integer key){
    return messages.get(user).get(key);
  }

  public static Integer getIndex(String user){
    return indexMap.get(user);
  }

}
