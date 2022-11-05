package dslab.mailbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Communication Interfaces with Client through socket
 */
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
    }
    catch (SocketException e) {
      // when the socket is closed, the I/O methods of the Socket will throw a SocketException
      // almost all SocketException cases indicate that the socket was closed
      System.out.println("SocketException while handling socket: " + e.getMessage());

    }
    catch (IOException e) {
      // you should properly handle all other exceptions
      throw new UncheckedIOException(e);
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
    catch (NullPointerException e){
      return null;
    }
  }

  public void println(String line){
    writer.println(line);
  }

  public void flush(){
    writer.flush();
  }

  public void close(){
    try {
      socket.close();
      reader.close();
      writer.close();
    } catch (IOException e){
      System.err.println(e.getMessage());
    }
  }
}
