package net.greg.examples.chat.server;

import java.io.*;
import java.util.*;
import java.net.*;

import net.greg.examples.chat.client.ClientProxy;


// https://www.geeksforgeeks.org/multi-threaded-chat-application-set-1/
// https://www.geeksforgeeks.org/multi-threaded-chat-application-set-2/
public final class Server {

  private static List<ClientProxy> requests = new ArrayList();

  public static final List<ClientProxy> getRequests() {
    return requests;
  }

  private static int clientID = 0;

  private static final int SERVER_PORT = 1234;


  public static void main(String[] args) throws IOException {

    System.err.println("\n\nServer awaits ...\n");

    Server.accept();
  }

  private static void accept() throws IOException {

    ServerSocket ss =
      new ServerSocket(SERVER_PORT);

    Socket socket;

    // poll
    while (true) {

      try {

        socket = ss.accept();

        System.err.println(
          "\n\nHappens Only ONCE Per Client Connection: " +
          socket + "\n\n");

        // obtain new input/output streams
        DataInputStream inputStream =
          new DataInputStream(socket.getInputStream());

        DataOutputStream outputStream =
          new DataOutputStream(socket.getOutputStream());

        // Create a new proxy object to handle this request.
        ClientProxy proxy =
          new ClientProxy(
            socket,
            "client " + clientID,
            inputStream, outputStream);

        // Create a new Thread with this object.
        Thread worker = new Thread(proxy);

        requests.add(proxy);

        System.err.println(
          "requests.size(): " + requests.size());

        worker.start();

        clientID++;
      }
      catch(EOFException e) {
        // swallow
      }
    }
  }
}
