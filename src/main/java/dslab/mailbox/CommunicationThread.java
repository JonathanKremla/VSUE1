package dslab.mailbox;

import java.net.SocketException;
import java.util.Objects;

public class CommunicationThread extends Thread {

  private ClientCommunicator communicator;

  public CommunicationThread(ClientCommunicator communicator){
    this.communicator = communicator;
  }

  public void run(){

    String request;
    // read client requests
    while ((request = communicator.readLine()) != null && !Objects.equals(request , "stop")) {
      System.out.println("Client sent the following request: " + request);

      /*
       * check if request has the correct format: !ping
       * <client-name>
       */
      String[] parts = request.split("\\s");

      String response = "!error provided message does not fit the expected format: "
              + "!ping <client-name> or !stop <client-name>";

      if (parts.length == 2) {

        String clientName = parts[1];

        if (request.startsWith("!ping")) {
          response = "!pong " + clientName;
        } else if (request.startsWith("!stop")) {
          response = "!bye " + clientName;
        }
      }

      communicator.println(response);
      communicator.flush();
    }
    communicator.close();
  }
}
