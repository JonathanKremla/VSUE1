package dslab.mailbox;

import java.net.ServerSocket;
import java.net.Socket;

public class ListenerThread extends Thread {

  private ServerSocket serverSocket;

  public ListenerThread(ServerSocket serverSocket) {
    this.serverSocket = serverSocket;
  }

  public void run() {

    while (true) {
      ClientCommunicator communicator = new ClientCommunicator(serverSocket);
      communicator.establishConnection();

      String request;
      // read client requests
      while ((request = communicator.readLine()) != null) {
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

        // print request
        communicator.println(response);
        communicator.flush();
      }
    }
  }
}
