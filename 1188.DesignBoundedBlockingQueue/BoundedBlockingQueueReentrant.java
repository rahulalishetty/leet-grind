class BoundedBlockingQueueReentrant {
  private final int capacity;
  private final Deque<Integer> q = new ArrayDeque<>();
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition notFull = lock.newCondition();
  private final Condition notEmpty = lock.newCondition();

  public BoundedBlockingQueue(int capacity) {
    if (capacity <= 0)
      throw new IllegalArgumentException("capacity must be > 0");
    this.capacity = capacity;
  }

  public void enqueue(int element) throws InterruptedException {
    lock.lock();
    try {
      while (q.size() == capacity) {
        notFull.await(); // releases lock, blocks, reacquires before returning
      }
      q.addFirst(element); // "front" as requested
      notEmpty.signal(); // wake one consumer
    } finally {
      lock.unlock();
    }
  }

  public int dequeue() throws InterruptedException {
    lock.lock();
    try {
      while (q.isEmpty()) {
        notEmpty.await();
      }
      int val = q.removeLast(); // "rear" as requested
      notFull.signal(); // wake one producer
      return val;
    } finally {
      lock.unlock();
    }
  }

  public int size() {
    lock.lock();
    try {
      return q.size();
    } finally {
      lock.unlock();
    }
  }
}
