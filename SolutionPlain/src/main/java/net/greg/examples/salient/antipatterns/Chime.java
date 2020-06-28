package net.greg.examples.salient.antipatterns;

import java.time.LocalDateTime;

/**
 * The Singleton antipattern.
 */
public final class Chime {

  public static final Chime INSTANCE = new Chime();


  /**
   * Stops Reflection.
   */
  private Chime() {

    System.setSecurityManager(new SecurityManager());

    createdTime = LocalDateTime.now();

    System.err.println("\n\nChime(), Once ...'");
  }

  private LocalDateTime createdTime;
  public final String createdTime() { return createdTime + ""; }  // coercion


  /**
   * Stateless issuance of the current time back
   * to the dependent code that invoked this method.
   *
   * Appends an empty string coerces the
   * toString() method of LocalDateTime.
   */
  public final String currentTime() { return LocalDateTime.now() + ""; }


  /**
   * Lifecycle Consideration:
   *
   * This method is invoked during serialization, but we stop serialization
   * of this class because logically, the state of a ('Singleton') is invalid
   * across address spaces.
   *
   * @param ois ObjectInputStream
   * @throws RuntimeException
   */
  private void readObject(java.io.ObjectInputStream ois) throws RuntimeException {
    throw new RuntimeException("Opaque/vague");
  }

  /**
   * Lifecycle Consideration:
   *
   * This method is invoked during serialization, but we stop serialization
   * of this class because logically, the state of a ('Singleton') is invalid
   * across address spaces.
   *
   * @param oos ObjectOutputStream
   * @throws RuntimeException
   */
  private void writeObject(java.io.ObjectOutputStream oos) throws RuntimeException {
    throw new RuntimeException("Opaque/vague");
  }
}
