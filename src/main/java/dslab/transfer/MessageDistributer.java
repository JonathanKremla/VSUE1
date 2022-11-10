package dslab.transfer;

import dslab.mailbox.ClientCommunicator;
import dslab.mailbox.Email;
import dslab.util.Config;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class MessageDistributer {
  private LinkedList<EmailDomainTuple> list = new LinkedList<>();
  private Socket mailboxSocket;
  private PrintWriter mailboxOut;
  private BufferedReader mailboxIn;
  private Config domainConfig = new Config("domains");
  private int capacity = 2;

  public void distribute(Email email, List<String> domains) throws InterruptedException {
    synchronized (this) {
      while (list.size() == capacity)
        wait();
      list.add(new EmailDomainTuple(email, domains));
      notify();
    }

  }

  public void forward() throws InterruptedException {
    while (true) {
      synchronized (this) {
        while (list.size() == 0)
          wait();
        var toSend = list.removeFirst();
        List<String> domains = toSend.getSecond();
        Email email = toSend.getFirst();
        for(String domain : domains){
          try{
            int ip = domainConfig.getInt(domain);
            establishClientConnection(ip);
            sendMail(email);
          }
          catch (Exception e){
            e.printStackTrace();
          }
        }

        notify();
      }
    }
  }

  private void establishClientConnection(int ip){
    try {
      mailboxSocket = new Socket("localhost", ip);
      mailboxOut = new PrintWriter(mailboxSocket.getOutputStream());
      mailboxIn = new BufferedReader(new InputStreamReader(mailboxSocket.getInputStream()));
    }
    catch (IOException e){
      e.printStackTrace();
    }

  }

  private void sendMail(Email email){
    try {
      mailboxOut.println("begin");
      mailboxOut.flush();
      System.err.print(mailboxIn.readLine());
      mailboxOut.println("to " + email.getTo());
      mailboxOut.flush();
      System.err.println(mailboxIn.readLine());
      mailboxOut.println("from " + email.getFrom());
      mailboxOut.flush();
      System.err.println(mailboxIn.readLine());
      mailboxOut.println("subject " + email.getSubject());
      mailboxOut.flush();
      System.err.println(mailboxIn.readLine());
      mailboxOut.println("data " + email.getData());
      mailboxOut.flush();
      System.err.println(mailboxIn.readLine());
      mailboxOut.println("send");
      mailboxOut.flush();
      System.err.println(mailboxIn.readLine());
      mailboxOut.close();
      mailboxIn.close();
    }
    catch (Exception e){
      e.printStackTrace();
    }

  }


}
