class CompletableFutureSolution {
  private final CompletableFuture<Void> firstDone = new CompletableFuture<>();
  private final CompletableFuture<Void> secondDone = new CompletableFuture<>();

  public void first(Runnable printFirst) {
    printFirst.run();
    firstDone.complete(null);
  }

  public void second(Runnable printSecond) {
    firstDone.join();
    printSecond.run();
    secondDone.complete(null);
  }

  public void third(Runnable printThird) {
    secondDone.join();
    printThird.run();
  }
}
