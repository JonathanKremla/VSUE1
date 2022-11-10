package dslab.transfer.dmtp;

import dslab.mailbox.ClientCommunicator;
import dslab.transfer.MessageDistributer;

import java.util.Objects;

public class DmtpCommunicationThread extends Thread {

  private ClientCommunicator communicator;
  private String domain;
  private boolean stopped = false;
  private final MessageDistributer messageDistributer = new MessageDistributer();

  public DmtpCommunicationThread(ClientCommunicator communicator) {
    this.communicator = communicator;
    this.domain = domain;
  }

  public void run() {

    var senderThread = sender;
    senderThread.start();
    DmtpRequestHandler requestHandler = new DmtpRequestHandler(messageDistributer);
    requestHandler.start();
    String request;
    communicator.println("ok DMTP");
    communicator.flush();
    // read client requests
    while (!stopped && (request = communicator.readLine()) != null && !Objects.equals(request, "quit")) {
      String response;
      response = requestHandler.handleRequest(request);
      communicator.println(response);
      communicator.flush();
    }
    communicator.println("ok bye");
    communicator.flush();
    communicator.close();
  }

  public void stopThread() {
    communicator.close();
    this.stopped = true;
  }

  Thread sender = new Thread(() -> {
    try {
      messageDistributer.forward();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  });


}

