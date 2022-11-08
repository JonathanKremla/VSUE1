package dslab.mailbox.dmap;

import dslab.mailbox.ClientCommunicator;
import dslab.mailbox.dmtp.DmtpCommunicationThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class DmapListenerThread extends Thread {

  private ServerSocket serverSocket;
  private String users;
  private String domain;
  private boolean stopped = false;
  private ClientCommunicator communicator;
  private List<DmapCommunicationThread> threadPool = new ArrayList<>();

  public DmapListenerThread(ServerSocket serverSocket, String domain, String userConfig) {
    this.serverSocket = serverSocket;
    this.users = userConfig;
    this.domain = domain;
  }

  public void run() {
    while (!stopped) {
      communicator = new ClientCommunicator(serverSocket);
      if(!communicator.establishConnection()) break;
      var thread = new DmapCommunicationThread(communicator, users, domain);
      thread.start();
      threadPool.add(thread);
    }
    for(var thread : threadPool){
      thread.stopThread();
    }
  }

  public void stopThread(){
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
