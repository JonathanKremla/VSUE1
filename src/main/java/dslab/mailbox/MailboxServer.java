package dslab.mailbox;

import dslab.ComponentFactory;
import dslab.util.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;

public class MailboxServer implements IMailboxServer, Runnable {

  private InputStream in;
  private PrintStream out;
  private Config config;
  private ServerSocket socket;
  private String domain;
  private int tcpDmapPort;
  private int tcpDmtpPort;
  private String users;

  /**
   * Creates a new server instance.
   *
   * @param componentId the id of the component that corresponds to the Config resource
   * @param config      the component config
   * @param in          the input stream to read console input from
   * @param out         the output stream to write console output to
   */
  public MailboxServer(String componentId, Config config, InputStream in, PrintStream out) {
    this.in = in;
    this.out = out;
    this.config = config;
    domain = config.getString("domain");
    users = config.getString("users.config");
    tcpDmapPort = config.getInt("dmap.tcp.port");
    tcpDmtpPort = config.getInt("dmtp.tcp.port");
  }

  @Override
  public void run() {
    createListenerThread();
    System.out.println("Server is up!");

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    try {
      // read commands from the console
      reader.readLine();
    } catch (IOException e) {
      // IOException from System.in is very very unlikely (or impossible)
      // and cannot be handled
    }

    // close socket and listening thread
    close();
  }

  public void close(){
    if (socket != null) {
      try {
        socket.close();
      } catch (IOException e) {
        System.err.println("Error while closing server socket: " + e.getMessage());
      }
    }
  }

  @Override
  public void shutdown() {
    // TODO
  }

  private void createListenerThread(){
    try {
      socket = new ServerSocket(tcpDmapPort);
      new ListenerThread(socket, domain, users).start();
    }
    catch (IOException e){
      System.err.println(e.getMessage());
    }
  }

  public static void main(String[] args) throws Exception {
    IMailboxServer server = ComponentFactory.createMailboxServer(args[0], System.in, System.out);
    server.run();
  }
}















