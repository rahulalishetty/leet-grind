class Semaphores {
  private final Semaphore oReady = new Semaphore(1);
  private final Semaphore hReady = new Semaphore(0);
  private final Semaphore hDone = new Semaphore(0);

  public void oxygen(Runnable releaseOxygen) throws InterruptedException {
    oReady.acquire(); // only one oxygen per molecule
    releaseOxygen.run(); // print O
    hReady.release(2); // allow two hydrogens
    hDone.acquire(2); // wait until both hydrogens printed
    oReady.release(); // next molecule oxygen allowed
  }

  public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
    hReady.acquire(); // wait until oxygen opens gate
    releaseHydrogen.run(); // print H
    hDone.release(); // tell oxygen one hydrogen is done
  }
}
