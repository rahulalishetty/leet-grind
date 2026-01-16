class Reentrants {
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition canH = lock.newCondition();
  private final Condition canO = lock.newCondition();

  private int hInMolecule = 0;
  private int oInMolecule = 0;
  private int bonded = 0; // how many have printed in current molecule

  public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
    lock.lock();
    try {
      while (hInMolecule == 2)
        canH.await();
      // wait until oxygen slot exists or molecule state permits
      while (bonded == 3)
        canH.await();

      hInMolecule++;
      releaseHydrogen.run();
      bonded++;

      if (bonded == 3)
        resetMolecule();
      signalNext();
    } finally {
      lock.unlock();
    }
  }

  public void oxygen(Runnable releaseOxygen) throws InterruptedException {
    lock.lock();
    try {
      while (oInMolecule == 1)
        canO.await();
      while (bonded == 3)
        canO.await();

      oInMolecule++;
      releaseOxygen.run();
      bonded++;

      if (bonded == 3)
        resetMolecule();
      signalNext();
    } finally {
      lock.unlock();
    }
  }

  private void resetMolecule() {
    hInMolecule = 0;
    oInMolecule = 0;
    bonded = 0;
  }

  private void signalNext() {
    canH.signalAll();
    canO.signalAll();
  }
}
