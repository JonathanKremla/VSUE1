package dslab.transfer;

import dslab.util.Config;
import dslab.util.datastructures.DataQueue;
import dslab.util.datastructures.Email;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.MissingResourceException;
import java.util.logging.Logger;

public class MessageDistributer {
  private DataQueue queue = new DataQueue(3);
  private Socket mailboxSocket;
  private PrintWriter mailboxOut;
  private BufferedReader mailboxIn;
  private Config domainConfig = new Config("domains");
  private boolean stopped = false;
  private final Logger logger = Logger.getLogger(MessageDistributer.class.getName());

  public void distribute(Email email) throws InterruptedException {
    logger.info("distribute: " + email.toString());
    while (queue.isFull()) {
      try {
        queue.waitOnFull();
      } catch (InterruptedException e) {
        break;
      }
      if (stopped) {
        break;
      }
    }
    queue.add(email);
    logger.info("in distribute list after fill: " + queue.peek());
    queue.notifyAllForEmpty();
  }

  public void forward() {
    while (!stopped) {
      if (queue.isEmpty()) {
        try {
          queue.waitOnEmpty();
        } catch (InterruptedException e) {
          break;
        }
      }
      if (stopped) {
        break;
      }
      logger.info("forward after wait: " + queue.peek());
      Email toSend = queue.poll();
      queue.notifyAllForFull();
      logger.info("state of Message Distributer :" + toSend);
      for (String domain : toSend.getDomains()) {
        if (!establishClientConnection(domain)) {
          sendFailureMail(toSend.getFrom());
        }
        else {
          sendMail(toSend);
        }
      }
      logger.info("finished sending all emails");
    }
  }

  private boolean establishClientConnection(String domain) {
    int ip;
    try {
      ip = domainConfig.getInt(domain);
    } catch (MissingResourceException e) {
      return false;
    }
    try {
      logger.info("establishConnection: " + domain);
      mailboxSocket = new Socket("localhost", ip);
      mailboxOut = new PrintWriter(mailboxSocket.getOutputStream());
      mailboxIn = new BufferedReader(new InputStreamReader(mailboxSocket.getInputStream()));
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  private void sendMail(Email email) {
    try {
      logger.info("sendMail: " + email.toString());
      mailboxOut.println("begin");
      mailboxOut.flush();
      logger.info(mailboxIn.readLine());
      mailboxOut.println("to " + email.getTo());
      mailboxOut.flush();
      logger.info(mailboxIn.readLine());
      mailboxOut.println("from " + email.getFrom());
      mailboxOut.flush();
      logger.info(mailboxIn.readLine());
      mailboxOut.println("subject " + email.getSubject());
      mailboxOut.flush();
      logger.info(mailboxIn.readLine());
      mailboxOut.println("data " + email.getData());
      mailboxOut.flush();
      logger.info(mailboxIn.readLine());
      mailboxOut.println("send");
      mailboxOut.flush();
      logger.info(mailboxIn.readLine());
      mailboxOut.close();
      mailboxIn.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void stopThread() {
    logger.info("stopThread()");
    this.stopped = true;
    queue.notifyAllForFull();
    queue.notifyAllForEmpty();
    try {
      mailboxSocket.close();
      mailboxIn.close();
      mailboxOut.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void sendFailureMail(String from) {
    String domain = from.split("@")[1];
    if(establishClientConnection(domain)) {
      //TODO change maybe
      sendMail(new Email("mailer@[127.0.0.1]", from, "Failed to send Email", "Failed to send Email"));
    }

  }


}
