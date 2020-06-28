package net.greg.examples.salient;

import java.util.concurrent.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.CommandLineRunner;

/**
 * Compare two ways to leverage an <i>Async</i>
 * <code>CompletableFuture</code>:
 *
 * <ul style="list-style: none;">
 *   <li> Fork-Join (default)
 *
 *   <li> Thread Pool (via an added parameter overloading
 *        any <code>CompletableFuture</code> method with
 *        the <i>Async</i> prefix
 * </ul>
 *
 * The two demo methods return the owning class - this allows
 * them to be chained to single application reference (instance
 * variable) which exemplifies the <i>ßuilder Design Pattern</i>.
 */
@SpringBootApplication
public final class App implements CommandLineRunner {

  public void run(String...im_tech_debt) { }

  public static void main(String [] any) {


  }

  public static final String RESET = "\u001B[0m";
  public static final String GREEN = "\u001B[32m";
}
