package dslab.mailbox.dmap;

import dslab.mailbox.ClientCommunicator;
import dslab.util.ThreadFactoryImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class DmapListenerThread extends Thread {

  private ServerSocket serverSocket;
  private String users;
  private String domain;
  private boolean stopped = false;
  private ExecutorService executor = Executors.newCachedThreadPool();
  private Logger logger = Logger.getLogger(this.getClass().getName());

  public DmapListenerThread(ServerSocket serverSocket, String domain, String userConfig) {
    this.serverSocket = serverSocket;
    this.users = userConfig;
    this.domain = domain;
  }

  public void run() {
    while (!stopped) {
      ClientCommunicator communicator = new ClientCommunicator(serverSocket);
      if (!communicator.establishConnection()) {
        break;
      }
      executor.execute(new DmapCommunicationThread(communicator, users, domain));
    }
    executor.shutdown();
  }

  public void stopThread() {
    close();
    executor.shutdownNow();
    this.stopped = true;
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
