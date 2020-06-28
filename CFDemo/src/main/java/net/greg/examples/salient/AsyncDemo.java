package net.greg.examples.salient;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;


/**
 * https://mahmoudanouti.wordpress.com/2018/01/26/20-examples-of-using-javas-completablefuture/
 * https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html
 */
public class AsyncDemo {

  private AsyncDemo completedFuture() {

    CompletableFuture<String> cf =
      CompletableFuture.completedFuture("message");

    System.err.println("cf.isDone() " + cf.isDone());
    System.err.println("message " + cf.getNow(null));

    return this;
  }

  /**
   * 1. A CompletableFuture is executes asynchronously
   *    when the method  ends with the keyword Async
   *
   * 2. When no Executor is specified, ForkJoinPool
   *    (which uses daemon threads) executes Runnable.
   *
   *    This is specific to CompletableFuture.
   *    Other implementations of CompletionStage can override.
   */
  private AsyncDemo runAsync() {

    final int WALLTIME = latency();
    final int JITTER = 1;

    System.err.println("\n\nrunAsync() begins");

    CompletableFuture<Void> cf =
      CompletableFuture.runAsync(() -> {

        System.err.println("\n   Lambda waits " + WALLTIME + " ms.");

        pause(WALLTIME);

        System.err.println("\n   Lambda Done ");
    });

    System.err.println("\n cf.isDone() " + cf.isDone());

    while ( ! cf.isDone()) {

      pause(1);

      System.out.println(" .");
    }

    pause(WALLTIME + JITTER);

    System.err.println("\n cf.isDone() " + cf.isDone());

    System.err.println("\nrunAsync() completes \n");

    return this;
  }

  /**
   * Take a completed CompletableFuture, which returns
   * "message", then apply an uppercase function.
   *
   * Behavioral keywords in <code>thenApply()</code>:
   *
   * <ul>
   *  <li>
   *      <code><b>then</b></code> - this stage happens when the current stage
   *      completes w/o error.
   *      In this case, the current stage has already completed with the value
   *      “message”.
   *  </li>
   *  <li>
   *    <code><b>Apply</b></code>, the returned stage applies a function on
   *    the result of the previous stage.
   *  </li>
   * </ul>
   *
   * The execution of the function is blocking - <code>getNow()</code> can
   * be reached only when the uppercase operation has completed.
   */
  private AsyncDemo thenApply() {

    System.err.println("\nthenApply() begins\n");

    CompletableFuture<String> cf =
      CompletableFuture.completedFuture("message").
        thenApply(any -> {
          return any.toUpperCase();
    });

    System.err.println("  MESSAGE == " + cf.getNow(null));

    System.err.println("\nthenApply() completes\n");

    return this;
  }


  /**
   * Asynchronously apply a function on a previous stage by adding the
   * <code>Async</code> suffix to the method <code>thenApply()</code>
   * the chained CompletableFuture executes asynchronously
   * (using ForkJoinPool.commonPool()).
   */
  private AsyncDemo thenApplyAsync() {

    System.err.println("\nthenApplyAsync() begins\n");

    final int WALLTIME = latency();

    CompletableFuture<String> cf =
      CompletableFuture.completedFuture("message").
        thenApplyAsync(any -> {

          System.err.println(
            "\n  CompletableFuture waits " + WALLTIME + " ms.");

          pause(WALLTIME);

          return any.toUpperCase();
    });

    System.err.println("  cf.getNow(null) " + cf.getNow(null));

    System.err.println("\n  MESSAGE == " + cf.join() + " - via cf.join()");

    System.err.println("\nthenApplyAsync() completes\n");

    return this;
  }

  /**
   * Use a custom Executor to asynchronously apply a function on previous stage.
   * A fixed thread pool is used to apply the uppercase conversion function.
   *
   * The <i>Executor</i> in this method shuts down an <code>ExecutorService</code>
   * in two phases:
   *
   * (1) Calls <code>shutdown()</code> to reject incoming tasks
   * (2) Calls <code>shutdownNow()</code>, if necessary, cancel lingering tasks
   */
  private AsyncDemo thenApplyAsyncWithExecutor() {

    final int WALLTIME = latency();

    final ExecutorService executor =
      Executors.newFixedThreadPool(3, new ThreadFactory() {

        int count = 1;

        @Override
        public Thread newThread(Runnable runnable) {
          return new Thread(runnable, "custom-executor-" + count++);
        }
    });

    System.err.println("\nthenApplyAsyncWithExecutor() begins\n");

    CompletableFuture<String> cf =
      CompletableFuture.completedFuture("message").
        thenApplyAsync(any -> {

          System.err.println(
            "  Thread.currentThread().getName().startsWith(\"custom-executor-\") " +
            Thread.currentThread().getName().startsWith("custom-executor-"));

          System.err.println(
            "  Thread.currentThread().isDaemon() " +
            Thread.currentThread().isDaemon());

          System.err.println(
            "\n  CompletableFuture waits " + WALLTIME + " ms.");

          pause(latency());

          return any.toUpperCase();
    },
    executor);

    System.err.println("  cf.getNow(null) " + cf.getNow(null));

    System.err.println("  MESSAGE == " + cf.join());

    // Disable new tasks from being submitted
    executor.shutdown();

    try {

      // Wait for existing tasks to terminate
      if ( ! executor.awaitTermination(30, TimeUnit.SECONDS)) {

      // Cancel currently executing tasks
      executor.shutdownNow();

      // Wait for tasks to respond to cancellation
      if ( ! executor.awaitTermination(30, TimeUnit.SECONDS))
         System.err.println("Pool did not terminate");
      }
    }
    catch (InterruptedException e) {

      // (Re-) Cancel if current thread also interrupted
      executor.shutdownNow();

      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }

    System.err.println("\nthenApplyAsyncWithExecutor() completes\n");

    return this;
  }

  /**
   * Consuming result of previous stage.
   *
   * If the next stage accepts the result of the current stage, but does not
   * need to return a value in the computation (i.e. its return type is void),
   * then instead of applying a function, it can accept a <i>Consumer</i> --
   * hence the method:  <code>thenAccept()</code> -- the <i>Consumer</i> executes
   * synchronously, so we don’t need to join on the returned <code>CompletableFuture</code>
   */
  private AsyncDemo thenAccept() {

    System.err.println("\nthenAccept() begins\n");

    StringBuilder result = new StringBuilder();

    CompletableFuture.completedFuture("message").
      thenAccept(any -> result.append(any));

    System.err.println("  Empty result? " + (result.length() == 0));

    System.err.println("\nthenAccept() completes\n");

    return this;
  }

  /**
   * Use the async version of <code>thenAccept()</code> -
   * <code>thenAcceptAsync()</code> - to make the chained
   * <code>CompletableFuture</code> execute asynchronously.
   */
  private AsyncDemo thenAcceptAsync() {

    System.err.println("\nthenAcceptAsync() begins\n");

    StringBuilder result = new StringBuilder();

    CompletableFuture<Void> cf =
      CompletableFuture.completedFuture("message").
        thenAcceptAsync(any -> result.append(any));

    cf.join();

    System.err.println("  Empty result? " + (result.length() == 0));

    System.err.println("\nthenAcceptAsync() completes\n");

    return this;
  }

  /**
   * Chain a completed <code>CompletableFuture</code> to the
   * <code>thenApplyAsync()</code> method which returns a new
   * <code>CompletableFuture</code>.
   *
   * Delay the async task using <code>delayedExecutor(timeout, timeUnit)</code>.
   *
   * Create a separate “handler” stage, <code>exceptionHandler</code>,
   * that returns another message "cancellation message".
   *
   * Explicitly complete the second stage with an exception using
   * <code>completeExceptionally(RuntimeException)</code>
   *
   * This does two things:
   *
   * <ul>
   *   <li>
   *      Makes the <code>join()</code> method on the stage which is doing the
   *      uppercase operation, throw a <code>CompletionException</code> instead
   *      having that <code>join()</code> wait for 1 second.
   *   </li>
   *   <li>
   *      It will also trigger the handler stage.
   *   </li>
   * </ul>
   */
  private AsyncDemo completeExceptionally() {

    System.err.println("\ncompleteExceptionally() begins\n");

    Executor delayedExecutor =
      CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS);

    CompletableFuture<String> cf =
      CompletableFuture.completedFuture("message").
        thenApplyAsync(String::toUpperCase, delayedExecutor);

    CompletableFuture<String> exceptionHandler =
      cf.handle((s, th) -> { return (th != null) ? "  cancellation message" : ""; });

    cf.completeExceptionally(
      new RuntimeException("  completed exceptionally"));

    System.err.println(
      "  Was not completed exceptionally? " +
      cf.isCompletedExceptionally());

    try {

      cf.join();
      System.err.println("  cf.join() should have thrown an exception");
    }
    catch(CompletionException e) {
      System.err.println(
        "  completed exceptionally " +
        e.getCause().getMessage());
    }

    System.err.println(
      "  cancellation message " +
      exceptionHandler.join());

    System.err.println("\ncompleteExceptionally() completes\n");

    return this;
  }

  /**
   * Similar to an exceptional completion, cancelling a computation via
   * the <code>cancel(boolean mayInterruptIfRunning)</code> method from
   * the <code>Future</code> <i>Interface</i> can be called.
   *
   * For <code>CompletableFuture</code>, the <code>boolean</code> parameter
   * is not used because the implementation does not use interrupts to do
   * the cancellation.
   *
   * Instead, the <code>cancel()</code> method is used as an equivalent
   * to <code>completeExceptionally(new CancellationException())</code>.
   */
  private AsyncDemo cancellation() {

    System.err.println("\n cancellation() begins\n");

    CompletableFuture<String> cf = CompletableFuture.completedFuture("message").thenApplyAsync(String::toUpperCase,
            CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
    CompletableFuture<String> cf2 = cf.exceptionally(throwable -> "canceled message");

    System.err.println(
      "  Was not canceled " +
      cf.cancel(true));

    System.err.println(
      "  Was not completed exceptionally " +
      cf.isCompletedExceptionally());

    System.err.println(
      "  canceled message " +
      cf2.join());

    System.err.println("\n cancellation() completes\n");

    return this;
  }

  /**
   * Apply a function to result of either of two completed stages.
   *
   * The <code>CompletableFuture</code> applies a function to the
   * result from either of two previous stages (no order guarantees).
   *
   * One stage applies an uppercase conversion to the original string,
   * Another stage applies a lowercase conversion to the original string.
   *
   * An example of two possible outcomes:
   *
   * delayedLowerCase walltime 9
   * delayedUpperCase walltime 14
   * cf2.join().endsWith("from applyToEither()") true
   * original MeSSage
   * cf1.get() message
   * cf2.get() message from applyToEither()
   *
   * delayedLowerCase walltime 12
   * delayedUpperCase walltime 10
   * cf2.join().endsWith("from applyToEither()") true
   * original MeSSage
   * cf1.get() message
   * cf2.get() MESSAGE from applyToEither()
   */
  private AsyncDemo applyToEither() {

    System.err.println("\napplyToEither() begins\n");

    String original = "MeSSage";

    CompletableFuture<String> cf1 =
      CompletableFuture.completedFuture(original).
        thenApplyAsync(s -> delayedLowerCase(s));

    CompletableFuture<String> cf2 =
      cf1.applyToEither(
        CompletableFuture.completedFuture(original).
          thenApplyAsync(
            s -> delayedUpperCase(s)),
            s -> s + " from applyToEither()");

    System.err.println(
      "  cf2.join().endsWith(\"from applyToEither()\") " +
      cf2.join().endsWith("from applyToEither()"));

    try {

      System.err.println("\n  original " + original);
      System.err.println("  cf1.get() " + cf1.get());
      System.err.println("  cf2.get() " + cf2.get());
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
    catch (ExecutionException e) {
      e.printStackTrace();
    }

    System.err.println("\napplyToEither() completes\n");

    return this;
  }

  /**
   * Uses a <i>Consumer</i> instead of a <i>function</i> because the
   * dependent <code>CompletableFuture<code> is of type <code>Void</code>.
   */
  private AsyncDemo acceptEither() {

    System.err.println("\nacceptEither() begins\n");

    String original = "Message";

    StringBuffer result = new StringBuffer();  //thread-safe

    CompletableFuture<Void> cf =
      CompletableFuture.completedFuture(original).
        thenApplyAsync(s -> delayedUpperCase(s)).
          acceptEither(
            CompletableFuture.completedFuture(original).
              thenApplyAsync(s -> delayedLowerCase(s)),
            s -> result.append(s).append("acceptEither"));

    cf.join();

    System.err.println(
      "  Result was empty " +
      result.toString().endsWith("acceptEither"));

    try {

      System.err.println("\n  original " + original);
      System.err.println("  cf.get() " + cf.get());
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
    catch (ExecutionException e) {
      e.printStackTrace();
    }

    System.err.println("\nacceptEither() completes\n");

    return this;
  }

  /**
   * Running a <code>Runnable</code> upon completion of two stages
   * shows how the dependent <code>CompletableFuture</code> that
   * executes a <code>Runnable</code> is triggered upon both stages'
   * completion.
   *
   * Stages run synchronously.
   */
  private AsyncDemo runAfterBoth() {

    System.err.println("\nrunAfterBoth() begins\n");

    String original = "Message";

    StringBuilder result = new StringBuilder();

    CompletableFuture.completedFuture(original).
      thenApply(String::toUpperCase).
        runAfterBoth(
          CompletableFuture.completedFuture(original).
            thenApply(String::toLowerCase),
          () -> result.append("DONE"));

    System.err.println("  result " + result + "\n");

    System.err.println("  Result was empty " + (result.length() == 0));

    System.err.println("\nrunAfterBoth() completes\n");

    return this;
  }

  /**
   * Accepting results of both stages in a <i>BiConsumer</i>.
   * Instead of executing a <code>Runnable</code> upon completion of both stages,
   * using <i>BiConsumer</i> allows processing of their results if needed.
   */
  private AsyncDemo thenAcceptBoth() {

    System.err.println("\n thenAcceptBoth() begins\n");

    String original = "Message";

    StringBuilder result = new StringBuilder();

    CompletableFuture.completedFuture(original).
      thenApply(String::toUpperCase).
        thenAcceptBoth(
          CompletableFuture.completedFuture(original).
            thenApply(String::toLowerCase),
          (s1, s2) -> result.append(s1 + s2));

    System.err.println("  MESSAGEmessage == " + result.toString());

    System.err.println("\n thenAcceptBoth() completes\n");

    return this;
  }

  /**
   * Applying a <i>BiFunction</i> on results of both stages.
   *
   * If the dependent <code>CompletableFuture</code> is intended to combine
   * the results of two previous <code>CompletableFuture</code>s by applying
   * a function on them and returning a result, then we can use the method
   * <code>thenCombine()</code>.
   *
   * The entire pipeline is synchronous so <code>getNow()</code>
   * at the end would retrieve the final result - the concatenation
   * of uppercase and lowercase outcomes.
   */
  private AsyncDemo thenCombine() {

    System.err.println("\n thenCombine() begins\n");

    String original = "Message";

    CompletableFuture<String> cf =
      CompletableFuture.completedFuture(original).
        thenApply(s -> delayedLowerCase(s)).
          thenCombine(CompletableFuture.completedFuture(original).
            thenApply(s -> delayedUpperCase(s)),
          (s1, s2) -> s1 + s2);

    System.err.println("MESSAGEmessage == " + cf.getNow(null));

    System.err.println("\n thenCombine() completes\n");

    return this;
  }

  /**
   * Asynchronously applying a <i>BiFunction</i> on results of both stages.
   *
   * Since the two stages upon which <code>CompletableFuture<code> depends
   * upon both run asynchronously, the <code>thenCombine()</code> method
   * executes asynchronously, even though it lacks the <code>Async</code>
   *                                                               suffix.
   *
   * It's documented in the <code>CompletableFuture</code> class' Javadoc:
   *
   * <blockquote>
   *   “Actions supplied for dependent completions of non-async
   *    methods may be performed by the thread that completes
   *    the current <code>CompletableFuture</code>, or by any
   *    other caller of a completion method.”
   * </blockquote>
   *
   * So, we need to <code>join()</code> on the combining
   * <code>CompletableFuture</code> to wait for the result.
   */
  private AsyncDemo thenCombineAsync() {

    System.err.println("\nthenCombineAsync() begins\n");

    String original = "Message";

    CompletableFuture<String> cf =
      CompletableFuture.completedFuture(original).
        thenApplyAsync(s -> delayedLowerCase(s)).
          thenCombine(CompletableFuture.completedFuture(original).
            thenApplyAsync(s -> delayedUpperCase(s)),
            (s1, s2) -> s1 + s2);

    System.err.println("  messageMESSAGE == " + cf.join());

    System.err.println("\nthenCombineAsync() completes\n");

    return this;
  }

  /**
   *  Composing CompletableFutures.
   *
   * We can use composition using thenCompose() to accomplish the same computation done in the previous two examples. This method waits for the first stage (which applies an uppercase conversion) to complete. Its result is passed to the specified Function which returns a CompletableFuture, whose result will be the result of the returned CompletableFuture. In this case, the Function takes the uppercase string (upper), and returns a CompletableFuture that converts the original string to lowercase and then appends it to upper.
   */
  private AsyncDemo thenCompose() {

    System.err.println("\nthenCompose() begins\n");

    String original = "Message";

    CompletableFuture<String> cf =
      CompletableFuture.completedFuture(original).
        thenApply(s -> delayedUpperCase(s)).
          thenCompose(upper ->
            CompletableFuture.completedFuture(original).
              thenApply(s -> delayedLowerCase(s)).
                thenApply(s -> upper + s));

    System.err.println("  MESSAGEmessage " + cf.join());

    System.err.println("\nthenCompose() completes\n");

    return this;
  }

  /**
   * Creating a stage that completes when any of several stages completes.
   *
   * Create a <code>CompletableFuture</code> that completes when any of
   * several <code>CompletableFuture</code>s completes, with the same result.
   *
   * Several stages are created - each converts a string from a list to uppercase.
   *
   * Because all of the <code>CompletableFuture</code>s execute synchronously
   * (using <code>thenApply()</code> method), the <code>CompletableFuture</code>
   * returned from the <code>anyOf()</code> method would execute immediately,
   * since by the time it is invoked, all stages are completed.
   *
   * We then use the <code>whenComplete(BiConsumer * action)</code>, which
   * processes the result (asserting that the result is uppercase).
   */
  private AsyncDemo anyOf() {

    System.err.println("\nanyOf() begins\n");

    StringBuilder result = new StringBuilder();

    List<String> messages = Arrays.asList("a", "b", "c");

    List<CompletableFuture<String>> futures =
      messages.stream().
        map(msg ->
          CompletableFuture.completedFuture(msg).
            thenApply(s -> delayedUpperCase(s))).
              collect(Collectors.toList());

    CompletableFuture.anyOf(
      futures.toArray(new CompletableFuture[futures.size()])).
        whenComplete((res, th) -> {
          if (th == null) {
            System.err.println(isUpperCase((String) res));
            result.append(res);
          }
    });

    System.err.println(
      "  Result was empty " +
      (result.length() > 0));

    System.err.println("\nanyOf() completes\n");

    return this;
  }

  /**
   * Creating a stage that completes when all stages complete.
   *
   * Create a <code>CompletableFuture</code> that completes when all of
   * several <code>CompletableFutures</code> completes, in a synchronous
   *                                                            fashion.
   *
   * A list of strings is provided where each element is converted to uppercase.
   *
   */
  private AsyncDemo allOf() {

    System.err.println("\nallOf() begins\n");

    StringBuilder result = new StringBuilder();

    List<String> messages = Arrays.asList("a", "b", "c");

    List<CompletableFuture<String>> futures =
      messages.stream().
        map(msg -> CompletableFuture.completedFuture(msg).
          thenApply(s -> delayedUpperCase(s))).
            collect(Collectors.toList());

    CompletableFuture.allOf(
      futures.toArray(new CompletableFuture[futures.size()])).
        whenComplete((v, th) -> {
          futures.forEach(cf -> System.err.println("  " + isUpperCase(cf.getNow(null))));
          result.append("  DONE");
    });

    System.err.println("  Result was empty " + (result.length() == 0));

    System.err.println("\nallOf() completes\n");

    return this;
  }

  /**
   * Creating a stage that completes asynchronously when all stages complete.
   *
   * By switching to the <code>thenApplyAsync()</code> mdthod in the individual
   * <code>CompletableFuture</code>s, the stage returned by <code>allOf()</code>
   * gets executed by one of the common pool threads that completed the stages.
   *
   * Call <code>join()</code> on it to wait for its completion.
   */
  private AsyncDemo allOfAsync() {

    System.err.println("\nallOfAsync() begins\n");

    StringBuilder result = new StringBuilder();
    List<String> messages = Arrays.asList("a", "b", "c");

    List<CompletableFuture<String>> futures =
      messages.stream().
        map(msg -> CompletableFuture.completedFuture(msg).
          thenApplyAsync(s -> delayedUpperCase(s))).
            collect(Collectors.toList());

    CompletableFuture<Void> allOf =
      CompletableFuture.allOf(
        futures.toArray(new CompletableFuture[futures.size()])).
          whenComplete((v, th) -> {
            futures.forEach(cf ->
              System.err.println(
                "  " +
                isUpperCase(cf.getNow(null))));

        result.append("  DONE");
      });

    allOf.join();

    System.err.println("  Result was empty " + (result.length() > 0));

    System.err.println("\nallOfAsync() completes\n");

    return this;
  }


  public static void main(String[] args) {

    new AsyncDemo().
      // completedFuture().
      // runAsync().
      // thenApply().
      // thenApplyAsync().
      // thenApplyAsyncWithExecutor().

      // thenAccept().
      // thenAcceptAsync().
      // completeExceptionally().
      // cancellation().
      // applyToEither().

      // acceptEither().
      // runAfterBoth().
      // thenAcceptBoth().
      // thenCombineAsync().
      // thenCompose().

      // anyOf().
      // allOf().
      // allOfAsync().
      // TODO carsInventory(). modeling().
      interestingFork().
      interestingPool();
  }

  private AsyncDemo interestingFork() {

    System.err.println("\ninteresting() begins\n");

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

    System.err.println("\ninteresting() completes\n");

    return this;
  }

  private AsyncDemo interestingPool() {

    System.err.println("\ninterestingPool() begins\n");

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

    System.err.println("\ninterestingPool() completes\n");

    return this;
  }


  /**
   * utilitarian.
   */
  private String delayedUpperCase(String value) {
    int walltime = latency();

    System.err.println(
      "  delayedUpperCase walltime " + walltime);

    pause(walltime);
    return value.toUpperCase();
  }

  /**
   * utilitarian.
   */
  private String delayedLowerCase(String value) {
    int walltime = latency();

    System.err.println(
      "  delayedLowerCase walltime " + walltime);

    pause(walltime);
    return value.toLowerCase();
  }

  /**
   * utilitarian.
   */
  private void pause(int value) {
    try { Thread.currentThread().sleep(value); }
    catch (InterruptedException e) { e.printStackTrace(); }
  }

  /**
   * utilitarian.
   */
  private int latency() {
    return new Random().nextInt(10)+5; // 6+
  }

  /**
   * utilitarian.
   */
  private boolean isUpperCase(String value) {
    char[] model = value.toCharArray();
    for (int i=0; i < model.length; i++){
      if ( ! Character.isUpperCase(model[i])) { return false; }
    }
    return true;
  }
}
