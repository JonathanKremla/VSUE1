package dslab.transfer.dmtp;

import dslab.mailbox.ClientCommunicator;
import dslab.transfer.MessageDistributer;

import java.util.Objects;

public class DmtpCommunicationThread extends Thread {

  private final ClientCommunicator communicator;
  private boolean stopped = false;
  private final MessageDistributer messageDistributer = new MessageDistributer();
  private DmtpRequestHandler requestHandler;

  public DmtpCommunicationThread(ClientCommunicator communicator) {
    this.communicator = communicator;
    Thread.currentThread().setName("DmtpCommunicationThread");
  }

  public void run() {

    requestHandler = new DmtpRequestHandler(messageDistributer);
    requestHandler.start();
    sender.start();
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
    try {
      requestHandler.stopThread();
    }
    catch (NullPointerException ignored){
      //happens if no request was sent to server => server got shutdown
      //before any connections were established
    }
    this.stopped = true;
  }

  Thread sender = new Thread(() -> {
    Thread.currentThread().setName("senderThread");
    messageDistributer.forward();
  });


}

