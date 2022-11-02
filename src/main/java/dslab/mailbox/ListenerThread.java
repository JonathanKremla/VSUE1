package dslab.mailbox;

import java.io.IOException;
import java.net.ServerSocket;

public class ListenerThread extends Thread {

  private ServerSocket serverSocket;

  public ListenerThread(ServerSocket serverSocket) {
    this.serverSocket = serverSocket;
  }

  public void run() {
    while (true) {
      ClientCommunicator communicator = new ClientCommunicator(serverSocket);
      communicator.establishConnection();
      new CommunicationThread(communicator).start();
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
