package net.greg.examples.salient;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.*;
import java.security.*;

import net.greg.examples.salient.antipatterns.Chime;


/**
 *
 *
 */
public final class Watch {

  /**
   * A GATE/CAPACITY value that is used for both
   * the CyclicBarrier, and for sizing the Executor
   * which populates that CyclicBarrier
   */
  private static int PARALLELISM_FACTOR =
    Integer.parseInt(
      System.getenv(
        "WATCH_FACTOR"));

  private static final CyclicBarrier BARRIER =
    new CyclicBarrier(PARALLELISM_FACTOR);

  /**
   * Maintain enough threads to support the given
   * parallelism level - use multiple queues to
   * reduce contention.
   *
   * The parallelism factor is the maximum number
   * of threads available-for/actively engaged-in
   * task processing.
   *
   * The thread-count is dynamic, nor is there any
   * guarantee of execution ordinality of submitted
   *                                         tasks.
   *
   * NB pool members are of type: ChimeAssailant
   */
  private static final ExecutorService POOL =
    Executors.newWorkStealingPool(2 * PARALLELISM_FACTOR);

  private static Chime now1 = Chime.INSTANCE;
  private static Chime now2 = Chime.INSTANCE;
  private static Chime now3 = Chime.INSTANCE;


  /**
   * Canonical entrypoint for a standalone Java app
   */
  public static void main(String[] anything) {

    long start = System.nanoTime();

    new Watch().loadPool().drainPool();

    System.err.println("\n diff " + ((System.nanoTime()-start)/1000000) + "ms. \n\n");
  }


  /**
   * Sequential invocations against the Chime 'Singleton'.
   */
  public static void inquire() {

    System.err.println("\n\nnow1.createdTime() " + now1.createdTime() + " <---");
    System.err.println("now1.currentTime() " + now1.currentTime());

    System.err.println("\nnow2.createdTime() " + now2.createdTime() + " <---");
    System.err.println("now2.currentTime() " + now2.currentTime());

    System.err.println("\nnow3.createdTime() " + now3.createdTime() + " <---");
    System.err.println("now3.currentTime() " + now3.currentTime() + "\n");
  }


  /**
   * Use the declared/defined ThreadPool instead.
   */
  public Watch loadPool() {

    for (int i = 0; i < PARALLELISM_FACTOR; i++) {

      POOL.execute(new Thread(new ChimeAssailant(i)));
    }

    return this;
  }


  /**
   * Threads that await the barrier becoming filled
   * to number of companion threads have reached the
   * barrier - as stipulated by the parallelism factor.
   */
  public static class ChimeAssailant implements Runnable {

    private int runID;

    public ChimeAssailant(int value) {

      System.err.println("\n\n  ChimeAssailant()");
      runID = value;
    }

    @Override
    public void run() {

    System.err.println("\n\n  ChimeAssailant().run()");

      try {

        System.out.println(
          "ChimeAssailant.runID " + runID + " waiting at Barrier");

        BARRIER.await();

        System.err.println(
          "ChimeAssailant.runID " + runID + " executes");

        inquire();
      }
      catch (Exception e) { e.printStackTrace(); }
    }
  }


  /**
   * Executor/TheadPool Lifecycle method which drains the pool.
   *s
   *  TODO:  make this a cannnical shutdown hook.
   */

  private void drainPool() {

    // Disable new tasks from being submitted
    POOL.shutdown();

    try {

      // Wait for existing tasks to terminate
      if ( ! POOL.awaitTermination(3, TimeUnit.SECONDS)) {

        // Cancel currently executing tasks
        POOL.shutdownNow();

        // Wait for tasks to respond to cancellation
        if ( ! POOL.awaitTermination(3, TimeUnit.SECONDS)) {
          System.err.println("Pool did not terminate");
        }
      }
    }
    catch (InterruptedException e) {

      // (Re-) Cancel if current thread also interrupted
      POOL.shutdownNow();

      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }

    System.err.println("\nPOOL Shutdown Completes\n");
  }

  /**
   * This anonymous block/method is not needed,
   * as the <code>Executor</code> instance does
   * an adequate self-cleanup.
   */
//  {
  //  Runtime.getRuntime().addShutdownHook(new Thread() {
    //  public void run() {

        /// System.err.println("Shutdown Hook starts");
        /// drainPool();
        /// System.err.println("Shutdown Hook completes");

        /// System.exit(0);
  //    }
//    });
  //}
}
