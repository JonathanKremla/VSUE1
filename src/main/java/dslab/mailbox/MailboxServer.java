package dslab.mailbox;

import at.ac.tuwien.dsg.orvell.StopShellException;
import dslab.ComponentFactory;
import dslab.mailbox.dmap.DmapListenerThread;
import dslab.mailbox.dmtp.DmtpListenerThread;
import dslab.shell.IShell;
import dslab.util.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.Set;

public class MailboxServer implements IMailboxServer, Runnable {

  private InputStream in;
  private PrintStream out;
  private Config config;
  private ServerSocket dmapSocket;
  private ServerSocket dmtpSocket;
  private DmapListenerThread dmapListenerThread;
  private DmtpListenerThread dmtpListenerThread;
  private final String domain;
  private final int tcpDmapPort;
  private final int tcpDmtpPort;
  private final String users;

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
    MessageStorage.loadUsers(new Config(users));
    createDmapListenerThread();
    createDmtpListenerThread();
    System.out.println("Server is up!");

    try {
      IShell shell = ComponentFactory.createMailboxShell("shell-mailbox", System.in, System.out);
      shell.run();
    }
    catch (Exception e){
      shutdown();
    }
    shutdown();

  }

  @Override
  public void shutdown() {
    dmapListenerThread.stopThread();
    dmtpListenerThread.stopThread();
  }

  private void createDmapListenerThread(){
    try {
      dmapSocket = new ServerSocket(tcpDmapPort);
      dmapListenerThread = new DmapListenerThread(dmapSocket, domain, users);
      dmapListenerThread.start();
    }
    catch (IOException e){
      System.err.println(e.getMessage());
    }
  }

  private void createDmtpListenerThread(){
    try {
      dmtpSocket = new ServerSocket(tcpDmtpPort);
      dmtpListenerThread = new DmtpListenerThread(dmtpSocket, domain, users);
      dmtpListenerThread.start();
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















