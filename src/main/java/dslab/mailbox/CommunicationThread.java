package dslab.mailbox;

import dslab.mailbox.dmap.DmapRequestHandler;

import java.net.SocketException;
import java.util.List;
import java.util.Objects;

public class CommunicationThread extends Thread {

  private ClientCommunicator communicator;
  private String users;
  private String domain;

  public CommunicationThread(ClientCommunicator communicator, String users, String domain){
    this.communicator = communicator;
    this.users = users;
    this.domain = domain;
  }

  public void run(){

    DmapRequestHandler requestHandler = new DmapRequestHandler(users, domain);
    String request;
    // read client requests
    while ((request = communicator.readLine()) != null && !Objects.equals(request , "quit")) {
      System.out.println("Client sent the following request: " + request);
      List<String> responses = requestHandler.handle(request);
      for(String response : responses){
        communicator.println(response);
      }
      communicator.flush();
    }
    communicator.println("ok bye");
    communicator.flush();
    communicator.close();
  }
}
