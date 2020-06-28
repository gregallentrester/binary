package net.greg.examples.chat.client;

import java.io.*;
import java.util.*;
import java.net.*;

import net.greg.examples.chat.server.Server;


// https://www.geeksforgeeks.org/multi-threaded-chat-application-set-1/
// https://www.geeksforgeeks.org/multi-threaded-chat-application-set-2
public final class ClientProxy implements Runnable {

  private static final String BYE = "bye";
  private static final String DELIMITER = ",port=";
  private static final String COMMA = ",";


  private String name;
  private final DataInputStream inputStream;
  private final DataOutputStream outputStream;
  private Socket socket;
  private boolean isloggedin;


  public ClientProxy(
      Socket socketA, String nameA,
      DataInputStream inputStreamA,
      DataOutputStream outputStreamA) {

    inputStream = inputStreamA;
    outputStream = outputStreamA;
    name = nameA;
    socket = socketA;
    isloggedin = true;
  }

  @Override
  public void run() {

    String payload;

    while (true) {

      try {

        payload = inputStream.readUTF();

        if (payload.equalsIgnoreCase(BYE)){

          isloggedin = false;
          socket.close();
          break;
        }

        String meta = socket.toString();

        meta =
          meta.substring(
            meta.indexOf(DELIMITER)+6);

        meta =
          meta.substring(0,
            meta.lastIndexOf(COMMA));

// Directed to server's standard-error stream
System.err.println("\n     server socket: " + socket);
System.out.println("client socket meta: " + meta);
System.err.println("           payload: " + payload);

        // search for the recipient in the connected devices list.
        for (ClientProxy registrant : Server.getRequests()) {

          // if the registrant is found, write on its output stream
          if (registrant.isloggedin) {

            registrant.outputStream.writeUTF(
              "\nClientProxy:: \nReports Server echos to Client " + name +
              "\nwhich originates at port " + meta +
              ":\n" + payload);
            break;
          }
        }
      }
      catch(EOFException e) {
        // swallow, otherwise cascading false errors emanate
      }
      catch (SocketException e) {
        // swallow ? e.printStackTrace();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      catch (Throwable e) {
        System.err.println("Proxy-THROWABLE");
        e.printStackTrace();
      }
    }

    try {
      inputStream.close();
      outputStream.close();
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }
}
