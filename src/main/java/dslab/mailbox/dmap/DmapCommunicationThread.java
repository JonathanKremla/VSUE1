package dslab.mailbox.dmap;

import dslab.mailbox.ClientCommunicator;

import java.util.List;
import java.util.Objects;

public class DmapCommunicationThread extends Thread {

  private ClientCommunicator communicator;
  private String users;
  private String domain;
  private boolean stopped = false;

  public DmapCommunicationThread(ClientCommunicator communicator, String users, String domain){
    this.communicator = communicator;
    this.users = users;
    this.domain = domain;
  }

  public void run(){

    DmapRequestHandler dmapRequestHandler = new DmapRequestHandler(users, domain);
    String request;
    communicator.println("ok DMAP");
    communicator.flush();
    // read client requests
    while (!stopped && (request = communicator.readLine()) != null && !Objects.equals(request , "quit")) {
      List<String> responses = dmapRequestHandler.handle(request);
      for(String response : responses){
        communicator.println(response);
      }
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
