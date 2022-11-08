package dslab.mailbox.dmtp;

import dslab.mailbox.ClientCommunicator;
import java.util.List;
import java.util.Objects;

public class DmtpCommunicationThread extends Thread{

  private ClientCommunicator communicator;
  private String users;
  private String domain;
  private boolean stopped = false;

  public DmtpCommunicationThread(ClientCommunicator communicator, String users, String domain){
    this.communicator = communicator;
    this.users = users;
    this.domain = domain;
  }

  public void run(){

    DmtpRequestHandler requestHandler = new DmtpRequestHandler(domain, users);
    String request;
    communicator.println("ok DMTP");
    communicator.flush();
    // read client requests
    while (!stopped && (request = communicator.readLine()) != null && !Objects.equals(request , "quit")) {
      String response = requestHandler.handleRequest(request);
      communicator.println(response);
      communicator.flush();
    }
    communicator.println("ok bye");
    communicator.flush();
    communicator.close();
  }

  public void stopThread(){
    communicator.close();
    this.stopped = true;
  }
}
