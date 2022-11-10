package dslab.transfer.dmtp;

import dslab.mailbox.ClientCommunicator;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class DmtpListenerThread extends Thread{


  private ServerSocket serverSocket;
  private boolean stopped = false;
  private List<DmtpCommunicationThread> threadPool = new ArrayList<>();

  public DmtpListenerThread(ServerSocket serverSocket) {
    this.serverSocket = serverSocket;
  }

  public void run() {
    while (!stopped) {
      ClientCommunicator communicator = new ClientCommunicator(serverSocket);
      if (!communicator.establishConnection()) {
        break;
      }
      var thread = new DmtpCommunicationThread(communicator);
      thread.start();
      threadPool.add(thread);
    }
    for (var thread : threadPool) {
      thread.stopThread();
    }
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

