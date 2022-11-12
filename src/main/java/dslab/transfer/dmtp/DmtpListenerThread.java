package dslab.transfer.dmtp;

import dslab.mailbox.ClientCommunicator;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DmtpListenerThread extends Thread {


  private ServerSocket serverSocket;
  private boolean stopped = false;
  private ExecutorService executor = Executors.newCachedThreadPool();
  private ClientCommunicator communicator;

  public DmtpListenerThread(ServerSocket serverSocket) {
    this.serverSocket = serverSocket;
    Thread.currentThread().setName("DmtpListenerThread");
  }

  public void run() {
    while (!stopped) {
      communicator = new ClientCommunicator(serverSocket);
      if (!communicator.establishConnection()) {
        break;
      }
      executor.execute(new DmtpCommunicationThread(communicator));
    }
    executor.shutdownNow();
  }

  public void stopThread() {
    close();
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

