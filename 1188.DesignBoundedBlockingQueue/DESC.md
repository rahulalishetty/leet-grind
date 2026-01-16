# 1188. Design Bounded Blocking Queue

Implement a thread-safe bounded blocking queue with the following methods:

- **`BoundedBlockingQueue(int capacity)`**: Initializes the queue with a maximum capacity.
- **`void enqueue(int element)`**: Adds an element to the front of the queue. If the queue is full, the calling thread is blocked until the queue is no longer full.
- **`int dequeue()`**: Removes and returns the element at the rear of the queue. If the queue is empty, the calling thread is blocked until the queue is no longer empty.
- **`int size()`**: Returns the number of elements currently in the queue.

Your implementation will be tested using multiple threads simultaneously. Each thread will either be a producer thread (calling `enqueue`) or a consumer thread (calling `dequeue`). The `size` method will be called after every test case.

**Note**: Do not use built-in implementations of bounded blocking queues, as they are not acceptable in an interview setting.

---

## Example 1

**Input**:

```
1
1
["BoundedBlockingQueue","enqueue","dequeue","dequeue","enqueue","enqueue","enqueue","enqueue","dequeue"]
[[2],[1],[],[],[0],[2],[3],[4],[]]
```

**Output**:

```
[1,0,2,2]
```

**Explanation**:

- Number of producer threads = 1
- Number of consumer threads = 1

```java
BoundedBlockingQueue queue = new BoundedBlockingQueue(2); // Initialize the queue with capacity = 2.

queue.enqueue(1); // Producer thread enqueues 1.
queue.dequeue(); // Consumer thread dequeues and returns 1.
queue.dequeue(); // Consumer thread is blocked as the queue is empty.
queue.enqueue(0); // Producer enqueues 0, unblocking the consumer thread, which returns 0.
queue.enqueue(2); // Producer enqueues 2.
queue.enqueue(3); // Producer enqueues 3.
queue.enqueue(4); // Producer is blocked as the queue is full.
queue.dequeue(); // Consumer dequeues 2, unblocking the producer thread, which enqueues 4.
queue.size(); // 2 elements remain in the queue.
```

---

## Example 2

**Input**:

```
3
4
["BoundedBlockingQueue","enqueue","enqueue","enqueue","dequeue","dequeue","dequeue","enqueue"]
[[3],[1],[0],[2],[],[],[],[3]]
```

**Output**:

```
[1,0,2,1]
```

**Explanation**:

- Number of producer threads = 3
- Number of consumer threads = 4

```java
BoundedBlockingQueue queue = new BoundedBlockingQueue(3); // Initialize the queue with capacity = 3.

queue.enqueue(1); // Producer thread P1 enqueues 1.
queue.enqueue(0); // Producer thread P2 enqueues 0.
queue.enqueue(2); // Producer thread P3 enqueues 2.
queue.dequeue(); // Consumer thread C1 dequeues.
queue.dequeue(); // Consumer thread C2 dequeues.
queue.dequeue(); // Consumer thread C3 dequeues.
queue.enqueue(3); // A producer thread enqueues 3.
queue.size(); // 1 element remains in the queue.
```

Since there are multiple producer and consumer threads, the thread scheduling by the operating system may vary. Therefore, any of the following outputs will be accepted: `[1,0,2]`, `[1,2,0]`, `[0,1,2]`, `[0,2,1]`, `[2,0,1]`, or `[2,1,0]`.

---

## Constraints

- `1 <= Number of Producers <= 8`
- `1 <= Number of Consumers <= 8`
- `1 <= size <= 30`
- `0 <= element <= 20`
- The number of calls to `enqueue` is greater than or equal to the number of calls to `dequeue`.
- At most 40 calls will be made to `enqueue`, `dequeue`, and `size`.
