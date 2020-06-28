package net.greg.examples.salient;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;


/**
 * https://mahmoudanouti.wordpress.com/2018/01/26/20-examples-of-using-javas-completablefuture/
 * https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html
 */
public class ForkJoinExecutorDemo {

  public static void main(String[] args) {

    new AsyncDemo().
      asyncCompletableFutureWithForkJoin().
      asyncCompletableFutureWithExecutorPool();
  }

  private ForkJoinExecutorDemo asyncCompletableFutureWithForkJoin() {

    System.err.println("\nasyncCompletableFutureWithForkJoin() begins\n");

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

    System.err.println("\nasyncCompletableFutureWithForkJoin() completes\n");

    return this;
  }

  private ForkJoinExecutorDemo asyncCompletableFutureWithExecutorPool() {

    System.err.println("\nasyncCompletableFutureWithExecutorPool() begins\n");

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

    System.err.println("\nasyncCompletableFutureWithExecutorPool() completes\n");

    return this;
  }
}
