# Kth Largest Element in a Stream — Approaches

## Overview

Imagine a university admissions office wants to keep track of the **k‑th highest test scores** from applicants in real time.
This allows them to dynamically determine the cut‑off score as new applications arrive.

To achieve this we implement a class:

```
KthLargest
```

Responsibilities:

- Initialize using an existing stream of numbers.
- Continuously return the **k‑th largest element** after new numbers are added.

Methods:

```
KthLargest(int k, int[] nums)
add(int val)
```

---

# Approach 1: Maintain Sorted List

## Intuition

If we maintain the stream **sorted in ascending order**, then:

```
k-th largest element = element at index (size - k)
```

Therefore we:

1. Keep all elements in a sorted list.
2. Insert new elements in sorted order.
3. Directly access the k‑th largest element.

Binary search helps find the insertion index efficiently.

---

## Algorithm

### Constructor

1. Store `k`
2. Create list `stream`
3. Add all numbers from `nums`
4. Sort `stream`

### add(val)

1. Use binary search to find correct insertion index
2. Insert value into list
3. Return element at

```
stream.size() - k
```

---

## Implementation

```java
class KthLargest {

    List<Integer> stream;
    int k;

    public KthLargest(int k, int[] nums) {
        stream = new ArrayList<Integer>(nums.length);
        this.k = k;

        for (int num : nums) {
            stream.add(num);
        }

        Collections.sort(stream);
    }

    public int add(int val) {
        int index = getIndex(val);
        stream.add(index, val);
        return stream.get(stream.size() - k);
    }

    private int getIndex(int val) {

        int left = 0;
        int right = stream.size() - 1;

        while (left <= right) {

            int mid = (left + right) / 2;
            int midElement = stream.get(mid);

            if (midElement == val) return mid;

            if (midElement > val) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }
}
```

---

## Complexity Analysis

Let:

```
M = initial stream size
N = number of add() calls
```

### Time Complexity

Constructor:

```
O(M log M)
```

Add operation:

```
Binary search → O(log(M+N))
Insertion → O(M+N)
```

Total:

```
O(N² + N*M)
```

The quadratic insertion cost dominates.

---

### Space Complexity

```
O(M + N)
```

All elements must be stored.

---

# Approach 2: Heap (Optimal)

## Intuition

Sorting the entire stream is unnecessary.

We only need the **k largest elements**.

Key idea:

```
Maintain a min‑heap of size k
```

Properties:

- Heap contains **k largest elements**
- Smallest element in heap = **k‑th largest element**

---

## Example

Stream:

```
[0,4,6,9], k = 3
```

k largest elements:

```
[4,6,9]
```

Heap top:

```
4
```

If new element arrives:

### Case 1 — Smaller value

```
val ≤ 4
```

Heap unchanged.

### Case 2 — Larger value

```
val > 4
```

Add value and remove smallest.

---

## Algorithm

### Constructor

1. Initialize min‑heap
2. Store `k`
3. Insert all initial numbers using `add()`

### add(val)

1. If heap size < k → add value
2. Else if val > heap.peek()

```
insert val
remove smallest
```

3. Return heap.peek()

---

## Implementation

```java
class KthLargest {

    PriorityQueue<Integer> minHeap;
    int k;

    public KthLargest(int k, int[] nums) {

        minHeap = new PriorityQueue<>();
        this.k = k;

        for (int num : nums) {
            add(num);
        }
    }

    public int add(int val) {

        if (minHeap.size() < k || minHeap.peek() < val) {

            minHeap.add(val);

            if (minHeap.size() > k) {
                minHeap.remove();
            }
        }

        return minHeap.peek();
    }
}
```

---

# Complexity Analysis

Let:

```
M = initial numbers
N = add() calls
```

### Time Complexity

Each heap operation:

```
O(log k)
```

Constructor:

```
O(M log k)
```

Add operations:

```
O(N log k)
```

Total:

```
O((M + N) log k)
```

---

### Space Complexity

```
O(k)
```

Only the **k largest elements** are stored.

---

# Key Insight

Maintaining only the **k largest elements** is enough.

Min‑heap provides:

```
Fast updates
Efficient memory usage
Direct access to k‑th largest
```
