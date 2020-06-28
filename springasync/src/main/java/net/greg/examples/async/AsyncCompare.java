package net.greg.examples.async;

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
 * them to be coupled to single application reference (instance
 * variable) which exemplifies the <i>ßuilder Design Pattern</i>.
 */
@SpringBootApplication
public final class AsyncCompare implements CommandLineRunner {

  public void run(String...im_tech_debt) { }


  /**
   * Canonical entry for a standalone app.
   */
  public static void main(String [] any) {

    System.err.println("\nApp ßegins");

    new AsyncCompare().
      asyncForkJoin().
      asyncExecutorPool();

    System.err.println("App Completes\n\n");
  }


  private AsyncCompare asyncForkJoin() {

    System.err.println(
      GREEN + "\n  asyncForkJoin() begins\n" + RESET);

    CompletableFuture<Void> cf =
      CompletableFuture.runAsync(() -> {
        System.out.println(
          "    Active Thread: " +
          Thread.currentThread().getName());
      });

    cf.join(); //waits until task completes

    System.out.println(
      "    Active Thread Exits: " +
      Thread.currentThread().getName());

    System.err.println(
      GREEN + "\n  asyncForkJoin() completes"  + RESET +
      "\n        ...  ...  ...\n\n");

    return this;
  }

  private AsyncCompare asyncExecutorPool() {

    System.err.println(
      GREEN + "\n  asyncExecutorPool() begins\n" + RESET);

    final ExecutorService fixedThreadPool =
      Executors.newFixedThreadPool(3, new ThreadFactory() {

        int count = 1;

        @Override
        public Thread newThread(Runnable runnable) {
          return new Thread(runnable, "FixedThreadPool-executor-" + count++);
        }
    });

    CompletableFuture<Void> cf =
      CompletableFuture.runAsync(() -> {
        System.out.println(
          "    Active Thread: " +
          Thread.currentThread().getName());
      }, fixedThreadPool);

    cf.join(); //waits until task completes

    System.out.println(
      "    Active Thread Exits: " +
      Thread.currentThread().getName());

    // Disable new tasks from being submitted
    fixedThreadPool.shutdown();

    try {

      // Wait for existing tasks to terminate
      if ( ! fixedThreadPool.awaitTermination(30, TimeUnit.SECONDS)) {

        // Cancel currently executing tasks
        fixedThreadPool.shutdownNow();

        // Wait for tasks to respond to cancellation
        if ( ! fixedThreadPool.awaitTermination(30, TimeUnit.SECONDS)) {
          System.err.println("    Pool did not terminate");
        }
      }
    }
    catch (InterruptedException e) {

      // (Re-) Cancel if current thread also interrupted
      fixedThreadPool.shutdownNow();

      // This call preserves the interrupt status of the current thread,
      // and facilitates the thread's shutdown while also reporting
      // that a task was in an incomplete/indeterminate state
      Thread.currentThread().interrupt();
    }

    System.err.println(
      GREEN + "\n  asyncExecutorPool() completes" + RESET +
      "\n        ...  ...  ...\n\n");

    return this;
  }

  public static final String RESET = "\u001B[0m";
  public static final String GREEN = "\u001B[32m";
}
