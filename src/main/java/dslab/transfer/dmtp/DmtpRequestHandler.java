package dslab.transfer.dmtp;

import dslab.util.datastructures.Email;
import dslab.transfer.MessageDistributer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DmtpRequestHandler extends Thread {

  private Email receivedEmail = new Email();
  private boolean transferBegan = false;
  private final MessageDistributer messageDistributer;
  private final Logger logger = Logger.getLogger(this.getClass().getName());

  public DmtpRequestHandler(MessageDistributer messageDistributer){
    this.messageDistributer = messageDistributer;
    Thread.currentThread().setName("DmtpRequestHandlerThread");
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
        return "error invalid Request";
    }
  }

  private String parseTo(String request) {
    if (!transferBegan) {
      return "error invalid request";
    }
    var recipients = Arrays.stream(request.split(" "))
            .filter(s -> !s.equals(",") && !s.equals("to"))
            .collect(Collectors.toList());

    receivedEmail.setTo(recipients.stream()
            .reduce("", (emails, email) -> emails
                    + (Objects.equals(emails, "") ? "" : " , ")
                    + email));

    List<String> domainList = new ArrayList<>();
    for (String recipient : recipients) {
      domainList.add(recipient.split("@")[1]);
    }
    receivedEmail.setDomains(domainList.stream().distinct().collect(Collectors.toList()));
    return "ok " + domainList.size();
  }

  private String parseFrom(String request) {
    if (!transferBegan) {
      return "error invalid request";
    }
    var splitRequest = request.split(" ");
    if (splitRequest.length > 2) {
      return "error only one sender possible";
    }
    var email = splitRequest[1];
    if (!email.matches("(.*)@(.*)")) {
      return "error invalid Email";
    }
    receivedEmail.setFrom(email);
    return "ok";

  }

  private String parseSubject(String request) {
    if (!transferBegan) {
      return "error invalid request";
    }
    receivedEmail.setSubject(request.substring(7).trim());
    return "ok";
  }

  private String parseData(String request) {
    if (!transferBegan) {
      return "error invalid request";
    }
    receivedEmail.setData(request.substring(4).trim());
    return "ok";

  }

  private String parseSend() {
    logger.info("parseSend");
    if (!transferBegan) {
      return "error";
    }
    if (!allEmailAttributesSet()) {
      return "error";
    }
    try {
      logger.info("call MessageDistributer: " + receivedEmail.toString());
      messageDistributer.distribute(receivedEmail);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    this.transferBegan = false;
    this.receivedEmail = new Email();
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
      return "error invalid request";
    }
    if (transferBegan) {
      return "error invalid request";
    }
    transferBegan = true;
    return "ok";
  }

  public void stopThread(){
    messageDistributer.stopThread();
  }

}



