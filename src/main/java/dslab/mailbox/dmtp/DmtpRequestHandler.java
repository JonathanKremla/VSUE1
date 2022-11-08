package dslab.mailbox.dmtp;

import dslab.mailbox.Email;
import dslab.mailbox.MessageStorage;
import dslab.util.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DmtpRequestHandler {

  private final String domain;
  private final Config userConfig;
  private final List<String> userList;
  private Email receivedEmail = new Email();
  private boolean transferBegan = false;
  private List<String> recipients = new ArrayList<>();

  public DmtpRequestHandler(String domain, String userConfig) {
    this.domain = domain;
    this.userConfig = new Config(userConfig);
    userList = new ArrayList<>(this.userConfig.listKeys());
  }

  public String handleRequest(String request) {
    switch (request.split(" ")[0]) {
      case "begin":
        return parseBegin(request);
      case "to":
        return parseTo(request);
      case "from":
        return parseFrom(request);
      case "subject":
        return parseSubject(request);
      case "data":
        return parseData(request);
      case "send":
        return parseSend();
      default:
        return "invalid Request";
    }
  }

  private String parseTo(String request) {
    if(!transferBegan) return "invalid request";
    //filters out all emails with other domain than this Mailbox
    var recipientList = Arrays.stream(request.split(" "))
            .filter(s -> !s.equals(",") && !s.equals("to"))
            .filter(s -> s.matches("(.*)@" + domain))
            .collect(Collectors.toList());
    if (recipientList.stream().findAny().isEmpty()) {
      return "error unknown";
    }

    if (recipientList.stream().anyMatch(s -> !userList.contains(s.split("@")[0]))) {
      return "error unknown";
    }

    receivedEmail.setTo(recipientList.stream().reduce("", (emails, email) -> emails
            + (Objects.equals(emails, "") ? "" : " , ")
            + email));

    for(String recipient : recipientList){
      this.recipients.add(recipient.split("@")[0]);
    }
    return "ok " + recipientList.size();
  }

  private String parseFrom(String request) {
    if(!transferBegan) return "invalid request";
    var splitRequest = request.split(" ");
    if (splitRequest.length > 2) {
      return "Only one sender possible";
    }
    var email = splitRequest[1];
    if (!email.matches("(.*)@(.*)")) {
      return "Invalid Email";
    }
    receivedEmail.setFrom(email);
    return "ok";

  }

  private String parseSubject(String request) {
    if(!transferBegan) return "invalid request";
    receivedEmail.setSubject(request.substring(7).trim());
    return "ok";
  }

  private String parseData(String request) {
    if(!transferBegan) return "invalid request";
    receivedEmail.setData(request.substring(4).trim());
    return "ok";

  }

  private String parseSend() {
    if(!transferBegan) return "invalid request";
    if(!allEmailAttributesSet()){
      return "Some attributes of email not set";
    }
    this.transferBegan = false;
    for(String recipient : recipients ) {
      MessageStorage.put(recipient, receivedEmail);
    }
    this.receivedEmail = new Email();
    this.recipients.clear();
    return "ok";
  }

  private boolean allEmailAttributesSet() {
    return receivedEmail.getFrom() != null &&
            receivedEmail.getTo() != null &&
            receivedEmail.getSubject() != null &&
            receivedEmail.getData() != null;
  }

  private String parseBegin(String request){
    if(request.split(" ").length > 1){
      return "invalid request";
    }
    if(transferBegan){
      return "invalid request";
    }
    transferBegan = true;
    return "ok";
  }
}