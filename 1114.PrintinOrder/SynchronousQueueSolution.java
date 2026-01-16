class SynchronousQueueSolution {
  private final SynchronousQueue<Integer> q12 = new SynchronousQueue<>();
  private final SynchronousQueue<Integer> q23 = new SynchronousQueue<>();

  public void first(Runnable printFirst) throws InterruptedException {
    printFirst.run();
    q12.put(1);
  }

  public void second(Runnable printSecond) throws InterruptedException {
    q12.take();
    printSecond.run();
    q23.put(1);
  }

  public void third(Runnable printThird) throws InterruptedException {
    q23.take();
    printThird.run();
  }
}
