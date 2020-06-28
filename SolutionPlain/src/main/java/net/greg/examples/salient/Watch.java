package net.greg.examples.salient;

import net.greg.examples.salient.antipatterns.Chime;


/**
 *
 *
 */
public final class Watch {

  private Chime now1;
  private Chime now2;
  private Chime now3;
  private Chime now4;
  private Chime now5;


  /**
   *
   */
  public void start() {

    System.err.println("\n\nnow1.createdTime() " + now1.createdTime());
    System.err.println("now1.currentTime() " + now1.currentTime());

    System.err.println("\nnow2.createdTime() " + now2.createdTime());
    System.err.println("now2.currentTime() " + now2.currentTime());

    System.err.println("\nnow3.createdTime() " + now3.createdTime());
    System.err.println("now3.currentTime() " + now3.currentTime());

    System.err.println("\nnow4.createdTime() " + now4.createdTime());
    System.err.println("now4.currentTime() " + now4.currentTime());

    System.err.println("\nnow5.createdTime() " + now5.createdTime());
    System.err.println("now5.currentTime() " + now5.currentTime());
  }

  public static void main(String [] any) {
    new Watch().set().start();
  }


  private Watch set() {

    try {

      now1 = Chime.INSTANCE;
      Thread.currentThread().sleep(1);

      now2 = Chime.INSTANCE;
      Thread.currentThread().sleep(1);

      now3 = Chime.INSTANCE;
      Thread.currentThread().sleep(1);

      now4 = Chime.INSTANCE;
      Thread.currentThread().sleep(1);

      now5 = Chime.INSTANCE;
    }
    catch (InterruptedException e) { e.printStackTrace(); }

    return this;
  }
}
