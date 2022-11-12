package dslab.mailbox.dmtp;

import dslab.mailbox.ClientCommunicator;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DmtpListenerThread extends Thread {
  private ServerSocket serverSocket;
  private String users;
  private String domain;
  private boolean stopped = false;
  private ClientCommunicator communicator;
  private ExecutorService executor = Executors.newCachedThreadPool();

  public DmtpListenerThread(ServerSocket serverSocket, String domain, String userConfig) {
    this.serverSocket = serverSocket;
    this.users = userConfig;
    this.domain = domain;
    Thread.currentThread().setName("Listener Thread");
  }

  public void run() {
    while (!stopped) {
      communicator = new ClientCommunicator(serverSocket);
      if (!communicator.establishConnection()) {
        break;
      }
      executor.execute(new DmtpCommunicationThread(communicator, users, domain));
    }
    executor.shutdownNow();
  }

  public void stopThread() {
    close();
    this.stopped = true;
    executor.shutdownNow();
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
