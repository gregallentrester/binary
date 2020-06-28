package net.greg.examples.salient.antipatterns;

import java.time.LocalDateTime;


/**
 * The Singleton antipattern!
 */
public final class Chime {

  /**
   * The Java JSL states that a constructor cannot publish
   * an instance of a class until all <code>final</code>
   * instance (and static/class) attributes are reified.
   *
   * This language construct - <code>final</code> - is
   * the key to affording this Singleton class thread-safe
   * instantiation.
   */
  public static final Chime INSTANCE = new Chime();

  /**
   * The following statement in this method disables Reflection:
   *
   *  <blockquote>
   *    System.setSecurityManager(
   *      new SecurityManager());
   *  </blockquote>
   *
   * ... but it also disables key permissions.
   *
   * For this application, the above statement affects
   * certain programmatic Threading permissions/capabilities.
   *
   * Specifically, the app loses the (vital) ability to
   * preserve the current Thread-of-execution's interrupt
   * status, which is accomplished via the following
   * canonical, static method:
   *
   *  <blockquote>
   *    Thread.currentThread().interrupt();
   *  </blockquote>
   *
   * When needed, the rescission of this permission
   * can be reversed/overridden by affordance of an
   * entry in this canonical file:
   *
   *  <code>$HOME/.java.policy</code>
   *
   * The other permissions/capabilites export the
   * JVM's metadata, and can be reversed, too.
   *
   * To programmatically (at will) reinstate the
   * capability to restore a Thread's interrupt
   * status, add the next statement to this file:
   *
   *   <code>$HOME/.java.policy</code>
   *
   *  The statement is:
   *
   *  <blockquote>
   *    grant {
   *      permission java.lang.RuntimePermission "modifyThread";
   *    };
   *  </blockquote>
   *
   * @see Watch#drainPool*/

  private Chime() {

    createdTime = LocalDateTime.now();

    System.setSecurityManager(new SecurityManager());

  }

  private LocalDateTime createdTime;
  public final String createdTime() {
    return createdTime + ""; // NB coercion
  }

  /**
   * Stateless (not stored here) issuance of the current time
   * back to the dependent code that invoked this method.
   *
   * Appends an empty string, which coerces the <code>toString()</code>
   * method of <code>LocalDateTime</code> instance to be evaluated.
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
