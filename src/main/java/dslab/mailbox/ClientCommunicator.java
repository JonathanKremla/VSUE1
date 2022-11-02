package dslab.mailbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientCommunicator{

  private ServerSocket serverSocket;
  private Socket socket;
  private BufferedReader reader;
  private PrintWriter writer;

  public ClientCommunicator(ServerSocket serverSocket) {
    this.serverSocket = serverSocket;
  }


  public void establishConnection() {
    try {
      socket = serverSocket.accept();
      reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      writer = new PrintWriter(socket.getOutputStream());
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  public String readLine(){
    try {
      return reader.readLine();
    }
    catch (IOException e){
      System.err.println(e.getMessage());
      return null;
    }
  }

  public void println(String line){
    writer.println(line);
  }

  public void flush(){
    writer.flush();
  }
}
