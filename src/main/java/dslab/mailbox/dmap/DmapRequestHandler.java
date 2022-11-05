package dslab.mailbox.dmap;

import dslab.mailbox.Email;
import dslab.mailbox.MessageStorage;
import dslab.util.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * handles clients request on one thread
 */
public class DmapRequestHandler {
  //map containing the responses(can be multiple or single) for a specific request
  private HashMap<String, List<String>> responseMap = new HashMap<>();
  private Config config;
  private String domain;
  private String currentUser;

  public DmapRequestHandler(String userConfig, String domain) {
    this.domain = domain;
    this.config = new Config(userConfig);
    MessageStorage.loadUsers(config);
    fillResponseMap();
    //TODO REMOVE LATER: Fill MessageStorage with dummy data
    MessageStorage.put("zaphod", new Email("someone", "trillian", "notjing", "nothing"));
  }

  /**
   * handles a specific request(key)
   * @param key request to handle
   * @return List of responses to be sent to the client
   */
  public List<String> handle(String key) {
    //TODO throw exceptions
    fillResponseMap();
    if (responseMap.containsKey(key)) {
      var answer =  responseMap.get(key);
      handleLogicalRequests(key);
      return answer;
    }
    //ToDO change to fitting exception
    List<String> invalidRequest = new ArrayList<>();
    invalidRequest.add("invalid Request");
    return invalidRequest;
  }

  private void handleLogicalRequests(String key){
    if(currentUser == null){
      if(key.startsWith("login")){
        currentUser = key.split(" ")[1];
        responseMap.remove("login " + currentUser + " " + config.getString(currentUser));
      }
    }
    else{
      if(key.startsWith("delete")){
        deleteMessage(Integer.parseInt(key.split(" ")[1]));
      }
      if(key.startsWith("logout")){
        logoutUser();
      }
    }

  }

  private void fillResponseMap() {
    if(currentUser == null) {
      loginResponse();
    }
    if (currentUser != null) {
      listRespone();
      deleteResponse();
      showResponse();
      logoutResponse();
    }
  }

  private void loginResponse() {
    List<String> responseList = new ArrayList<>();
    responseList.add("ok");
    var keys = config.listKeys();
    for (String key : keys) {
      responseMap.put("login" + " " + key + " " + config.getString(key), responseList);
    }
  }

  private void listRespone() {
    List<String> responseList = new ArrayList<>();
    for (int i = 1; i <= MessageStorage.getIndex(currentUser); i++) {
      Email message = MessageStorage.get(currentUser, i);
      if (message != null) {
        responseList.add(i + " " + message.getFrom() + " " + message.getSubject());
      }
    }
    responseMap.put("list", responseList);
  }

  private void showResponse() {
    int maxAmountOfMessages = MessageStorage.getIndex(currentUser);
    for (int i = 1; i <= maxAmountOfMessages; i++) {
      Email message = MessageStorage.get(currentUser, i);
      if (message != null) {
        List<String> responseList = new ArrayList<>();
        responseList.add("from " + message.getFrom() + "\n" +
                "to " + message.getTo() + "\n" +
                "subject " + message.getSubject() + "\n" +
                "data " + message.getData());
        responseMap.put("show " + i, responseList);
      }
    }
  }

  private void deleteResponse() {
    List<String> responseList = new ArrayList<>();
    responseList.add("ok");
    for (int i = 1; i <= MessageStorage.getIndex(currentUser); i++) {
      Email message = MessageStorage.get(currentUser, i);
      if (message != null) {
        responseMap.put("delete " + i, responseList);
      }
    }
  }

  private void logoutResponse() {
    List<String> responseList = new ArrayList<>();
    responseList.add("ok");
    responseMap.put("logout", responseList);
  }

  private void deleteMessage(int id){
    responseMap.remove("list");
    responseMap.remove("show " + id);
    responseMap.remove("delete " + id);
    MessageStorage.remove(currentUser, id);
  }

  private void logoutUser(){
    currentUser = null;
    responseMap.clear();
    fillResponseMap();
  }

}
