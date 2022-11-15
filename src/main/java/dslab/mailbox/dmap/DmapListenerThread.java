package dslab.mailbox.dmap;

import dslab.mailbox.ClientCommunicator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementation of the Thread which listens for new DMAP connections,
 * establishes new Connections and then passes the Communication to the {@link DmapCommunicationThread}
 * and resumes listening.
 *
 * This Threads Lifespan is as long as the Applications Lifespan
 */
public class DmapListenerThread extends Thread {

  private final ServerSocket serverSocket;
  private final String users;
  private final ExecutorService executor = Executors.newCachedThreadPool();
  private final Log LOG = LogFactory.getLog(DmapListenerThread.class);
  private boolean stopped = false;

  public DmapListenerThread(ServerSocket serverSocket, String userConfig) {
    this.serverSocket = serverSocket;
    this.users = userConfig;
  }

  public void run() {
    while (!stopped) {
      ClientCommunicator communicator = new ClientCommunicator(serverSocket);
      if (!communicator.establishConnection()) {
        break;
      }
      executor.execute(new DmapCommunicationThread(communicator, users));
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
        LOG.error("Error while closing server socket: " + e.getMessage());
      }
    }

  }
}
