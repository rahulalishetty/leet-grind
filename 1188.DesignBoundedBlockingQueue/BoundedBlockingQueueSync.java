class BoundedBlockingQueueSync {
  private final int capacity;
  private final Deque<Integer> q = new ArrayDeque<>();

  public BoundedBlockingQueueSync(int capacity) {
    if (capacity <= 0)
      throw new IllegalArgumentException("capacity must be > 0");
    this.capacity = capacity;
  }

  public synchronized void enqueue(int element) throws InterruptedException {
    while (q.size() == capacity) {
      wait();
    }
    q.addFirst(element);
    notifyAll(); // both producers/consumers could be waiting
  }

  public synchronized int dequeue() throws InterruptedException {
    while (q.isEmpty()) {
      wait();
    }
    int val = q.removeLast();
    notifyAll();
    return val;
  }

  public synchronized int size() {
    return q.size();
  }
}
