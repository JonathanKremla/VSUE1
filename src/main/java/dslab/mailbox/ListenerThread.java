package dslab.mailbox;

import java.io.IOException;
import java.net.ServerSocket;

public class ListenerThread extends Thread {

  private ServerSocket serverSocket;
  private String users;
  private String domain;

  public ListenerThread(ServerSocket serverSocket, String domain, String userConfig) {
    this.serverSocket = serverSocket;
    this.users = userConfig;
    this.domain = domain;
  }

  public void run() {
    while (true) {
      ClientCommunicator communicator = new ClientCommunicator(serverSocket);
      communicator.establishConnection();
      new CommunicationThread(communicator, users, domain).start();
    }

  }


  public void close() {
    if (serverSocket != null) {
      try {
        serverSocket.close();
      } catch (IOException e) {
        System.err.println("Error while closing server socket: " + e.getMessage());
      }
    }

  }
}
