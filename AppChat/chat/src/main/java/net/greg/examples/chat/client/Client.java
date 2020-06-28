package net.greg.examples.chat.client;

import java.io.*;
import java.net.*;

import java.time.*;
import java.time.format.*;


// https://www.geeksforgeeks.org/multi-threaded-chat-application-set-1/
// https://www.geeksforgeeks.org/multi-threaded-chat-application-set-2
public final class Client {

  private static final int SERVER_PORT = 1234;
  private static final String LOCALHOST = "localhost";

  private static final String EXPLICIT = "uuuu.MM.dd.HH.mm.ss";


  // ßecause of visibility concerns (nested-classes-as-threads), a try/catch
  // clause is not feasible :: "referents of inner classes must be final
  // or effectively final"
  public static void main(String args[]) throws UnknownHostException, IOException {

    // getting localhost IP
    InetAddress ip = InetAddress.getByName(LOCALHOST);

    // establish the connection
    Socket socket = new Socket(ip, SERVER_PORT);

    // obtain input and output streams
    DataInputStream inputStream =
      new DataInputStream(socket.getInputStream());

    DataOutputStream outputStream =
      new DataOutputStream(socket.getOutputStream());

    Thread messageSender = new Thread(new Runnable() {

      @Override
      public void run() {

        final int LIMIT = 3;
        int ndx = 0;

        while (true) {

          try {

            Thread.sleep(2_000);

            if (ndx++ == LIMIT) {
              outputStream.writeUTF("BYE");
            }
            else {
              outputStream.writeUTF(
                ZonedDateTime.now(
                  ZoneId.systemDefault()).
                    format(
                      DateTimeFormatter.ofPattern(
                        EXPLICIT)));
            }
          }
          catch (SocketException e) {
            // fail-fast
            e.printStackTrace();
            System.exit(0);
          }
          catch (IOException e) {
            e.printStackTrace();
          }
          catch(InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    });

    // readMessage thread
    Thread messageReader = new Thread(new Runnable() {

      @Override
      public void run() {

        while (true) {

          try {
            // or, do stuff ...
            String message = inputStream.readUTF();

            System.err.println("\nClient reads a response: " + message);
          }
          catch(EOFException e) {
            // swallow, otherwise cascading false errors emanate
            System.exit(0);
          }
          catch (SocketException e) {
            // fail-fast
            e.printStackTrace();
            System.exit(0);
          }
          catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    });

    // consider a threadpool (is its overhead warranted?)
    messageSender.start();
    messageReader.start();
  }


  static {

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.err.println("\nClient Shutdown Hook via Lambda expression starts");

      try {

        new DataOutputStream(
          new Socket(
            InetAddress.getByName(LOCALHOST),
              SERVER_PORT).
                getOutputStream()).
                  writeUTF("BYE");

        System.err.println("\nClient Shutdown Hook via Lambda expression finishes");
      }
      catch(UnknownHostException e) { e.printStackTrace(); }
      catch (IOException e) { e.printStackTrace(); }
    }));


    Runtime.getRuntime().addShutdownHook(new Thread() {

      public void run() {

        System.err.println("\nClient Shutdown Hook Nested Thread starts");

        try {

          new DataOutputStream(
            new Socket(
              InetAddress.getByName(LOCALHOST),
              SERVER_PORT).
                getOutputStream()).
                  writeUTF("BYE");

          System.err.println("\nClient Shutdown Hook Nested Thread finishes");

        }
        catch(UnknownHostException e) { e.printStackTrace(); }
        catch(IOException e) { e.printStackTrace(); }
      }
    });
  }
}
