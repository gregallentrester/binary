package net.greg.examples.salient;

import java.util.concurrent.*;


/**
 * Compare
 */
public class AsyncCompare {

  public static void main(String[] args) {

    new AsyncCompare().
      asyncCFLeveragingForkJoin().
      asyncCFLeveragingExecutorPool();
  }

  private AsyncCompare asyncCFLeveragingForkJoin() {

    System.err.println(
      GREEN + "\nasyncCFLeveragingForkJoin() begins\n" + RESET);

    CompletableFuture<Void> cf =
      CompletableFuture.runAsync(() -> {
        System.out.println(
          "  Running, in thread: " +
          Thread.currentThread().getName());
      });

    cf.join(); //waits until task completes

    System.out.println(
      "  main exiting, thread: " +
      Thread.currentThread().getName());

    System.err.println(
      GREEN + "\nasyncCFLeveragingForkJoin() completes"  + RESET +
      "\n          ...  ...  ...\n\n");

    return this;
  }

  private AsyncCompare asyncCFLeveragingExecutorPool() {

    System.err.println(
      GREEN + "\nasyncCFLeveragingExecutorPool() begins\n" + RESET);

    final ExecutorService executor =
      Executors.newFixedThreadPool(3, new ThreadFactory() {

        int count = 1;

        @Override
        public Thread newThread(Runnable runnable) {
          return new Thread(runnable, "custom-executor-" + count++);
        }
    });

    CompletableFuture<Void> cf =
      CompletableFuture.runAsync(() -> {
        System.out.println(
          "  Running, in thread: " +
          Thread.currentThread().getName());
      }, executor);

    cf.join(); //waits until task completes

    System.out.println(
      "  main exiting, thread: " +
      Thread.currentThread().getName());

    // Disable new tasks from being submitted
    executor.shutdown();

    try {

      // Wait for existing tasks to terminate
      if ( ! executor.awaitTermination(30, TimeUnit.SECONDS)) {

        // Cancel currently executing tasks
        executor.shutdownNow();

        // Wait for tasks to respond to cancellation
        if ( ! executor.awaitTermination(30, TimeUnit.SECONDS)) {
          System.err.println("Pool did not terminate");
        }
      }
    }
    catch (InterruptedException e) {

      // (Re-) Cancel if current thread also interrupted
      executor.shutdownNow();

      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }

    System.err.println(
      GREEN + "\nasyncCFLeveragingExecutorPool() completes" + RESET +
      "\n          ...  ...  ...\n\n");

    return this;
  }

  public static final String RESET = "\u001B[0m";
  // public static final String GREEN = "\u001B[32m";
  public static final String GREEN = "\u001B[28m";
}
