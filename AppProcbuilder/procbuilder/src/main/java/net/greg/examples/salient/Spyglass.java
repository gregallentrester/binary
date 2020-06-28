package net.greg.examples.salient;

import java.io.*;


public final class Spyglass {

  private static final String COOR =
    System.getProperty("user.home");

  public static void main(String[] args) {

    new Spyglass().status().secrets();
  }


  public final Spyglass status() {

    String[] command = { "vault", "status" };
    BufferedReader reader = null;

    ProcessBuilder pb = new ProcessBuilder(command);

    pb.directory(new File(COOR + "/stage/procbuilder"));

    try {
      Process process = pb.start();

      reader =
        new BufferedReader(
          new InputStreamReader(
            process.getInputStream()));

      String line;
      while ((line = reader.readLine()) != null) {
        System.err.println(line);
      }

      System.out.println(
        "\nExit Status " + process.waitFor() + "\n  ...  ...  ... \n\n");
    }
    catch (Throwable e) {
      e.printStackTrace();
    }
    finally {
      try {
        reader.close();
      }
      catch (Throwable e) { e.printStackTrace(); }
    }

    return this;
  }

  public final Spyglass secrets() {

    String[] command = { "vault", "secrets", "list" };
    BufferedReader reader = null;

    ProcessBuilder pb = new ProcessBuilder(command);
    pb.directory(new File(COOR + "/stage/procbuilder"));

    try {
      Process process = pb.start();

      reader =
        new BufferedReader(
          new InputStreamReader(
            process.getInputStream()));

      String line;
      while ((line = reader.readLine()) != null) {
        System.err.println(line);
      }

      System.out.println("\nExit secrets " + process.waitFor() + "\n  ...  ...  ... \n\n");
    }
    catch (Throwable e) {
      e.printStackTrace();
    }
    finally {
      try { reader.close(); }
      catch (Throwable e) { e.printStackTrace(); }
    }

    return this;
  }
}
