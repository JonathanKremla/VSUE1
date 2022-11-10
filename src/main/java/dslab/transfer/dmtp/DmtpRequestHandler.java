package dslab.transfer.dmtp;

import dslab.mailbox.ClientCommunicator;
import dslab.mailbox.Email;
import dslab.transfer.MessageDistributer;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DmtpRequestHandler extends Thread {

  private Email receivedEmail = new Email();
  private boolean transferBegan = false;
  private List<String> domainList = new ArrayList<>();
  private final MessageDistributer messageDistributer;

  public DmtpRequestHandler(MessageDistributer messageDistributer){
    this.messageDistributer = messageDistributer;
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
    if (!transferBegan) {
      return "invalid request";
    }
    var recipients = Arrays.stream(request.split(" "))
            .filter(s -> !s.equals(",") && !s.equals("to"))
            .collect(Collectors.toList());

    receivedEmail.setTo(recipients.stream()
            .reduce("", (emails, email) -> emails
                    + (Objects.equals(emails, "") ? "" : " , ")
                    + email));

    for (String recipient : recipients) {
      this.domainList.add(recipient.split("@")[1]);
    }
    this.domainList = this.domainList.stream().distinct().collect(Collectors.toList());
    return "ok " + domainList.size();
  }

  private String parseFrom(String request) {
    if (!transferBegan) {
      return "invalid request";
    }
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
    if (!transferBegan) {
      return "invalid request";
    }
    receivedEmail.setSubject(request.substring(7).trim());
    return "ok";
  }

  private String parseData(String request) {
    if (!transferBegan) {
      return "invalid request";
    }
    receivedEmail.setData(request.substring(4).trim());
    return "ok";

  }

  private String parseSend() {
    if (!transferBegan) {
      return "invalid request";
    }
    if (!allEmailAttributesSet()) {
      return "Some attributes of email not set";
    }
    try {
      messageDistributer.distribute(receivedEmail, domainList);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    this.transferBegan = false;
    this.receivedEmail = new Email();
    this.domainList.clear();
    return "ok";
  }

  private boolean allEmailAttributesSet() {
    return receivedEmail.getFrom() != null &&
            receivedEmail.getTo() != null &&
            receivedEmail.getSubject() != null &&
            receivedEmail.getData() != null;
  }

  private String parseBegin(String request) {
    if (request.split(" ").length > 1) {
      return "invalid request";
    }
    if (transferBegan) {
      return "invalid request";
    }
    transferBegan = true;
    return "ok";
  }

}



