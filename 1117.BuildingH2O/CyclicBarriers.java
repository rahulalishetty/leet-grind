class CyclicBarriers {
  private final Semaphore hSem = new Semaphore(2);
  private final Semaphore oSem = new Semaphore(1);
  private final CyclicBarrier barrier = new CyclicBarrier(3, () -> {
    // This runs after 3 threads reach the barrier (i.e., one molecule formed)
    hSem.release(2);
    oSem.release(1);
  });

  public CyclicBarriers() {
  }

  public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
    hSem.acquire();
    releaseHydrogen.run(); // prints "H"
    awaitBarrier();
  }

  public void oxygen(Runnable releaseOxygen) throws InterruptedException {
    oSem.acquire();
    releaseOxygen.run(); // prints "O"
    awaitBarrier();
  }

  private void awaitBarrier() throws InterruptedException {
    try {
      barrier.await();
    } catch (BrokenBarrierException e) {
      // In LeetCode-style environments you can treat this as unrecoverable
      throw new RuntimeException(e);
    }
  }
}
