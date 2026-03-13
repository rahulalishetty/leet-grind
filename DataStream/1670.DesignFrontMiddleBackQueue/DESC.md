# 1670. Design Front Middle Back Queue

Design a queue that supports **push** and **pop** operations in the **front**, **middle**, and **back**.

---

## Implement the `FrontMiddleBackQueue` Class

### Constructor

```
FrontMiddleBackQueue()
```

Initializes the queue.

---

### Methods

```
void pushFront(int val)
```

Adds `val` to the **front** of the queue.

```
void pushMiddle(int val)
```

Adds `val` to the **middle** of the queue.

```
void pushBack(int val)
```

Adds `val` to the **back** of the queue.

```
int popFront()
```

Removes the **front element** of the queue and returns it.

If the queue is empty, return:

```
-1
```

```
int popMiddle()
```

Removes the **middle element** of the queue and returns it.

If the queue is empty, return:

```
-1
```

```
int popBack()
```

Removes the **back element** of the queue and returns it.

If the queue is empty, return:

```
-1
```

---

## Important Rule

When there are **two possible middle positions**, the operation is performed on the **frontmost middle position**.

### Example

```
Push 6 into the middle of [1, 2, 3, 4, 5]
Result:
[1, 2, 6, 3, 4, 5]
```

```
Pop middle from [1, 2, 3, 4, 5, 6]
Returns:
3
Resulting queue:
[1, 2, 4, 5, 6]
```

---

# Example 1

### Input

```
["FrontMiddleBackQueue",
 "pushFront",
 "pushBack",
 "pushMiddle",
 "pushMiddle",
 "popFront",
 "popMiddle",
 "popMiddle",
 "popBack",
 "popFront"]

[[], [1], [2], [3], [4], [], [], [], [], []]
```

### Output

```
[null, null, null, null, null, 1, 3, 4, 2, -1]
```

---

# Explanation

```
FrontMiddleBackQueue q = new FrontMiddleBackQueue();
```

```
q.pushFront(1);
```

Queue:

```
[1]
```

```
q.pushBack(2);
```

Queue:

```
[1, 2]
```

```
q.pushMiddle(3);
```

Queue:

```
[1, 3, 2]
```

```
q.pushMiddle(4);
```

Queue:

```
[1, 4, 3, 2]
```

```
q.popFront();
```

Return:

```
1
```

Queue:

```
[4, 3, 2]
```

```
q.popMiddle();
```

Return:

```
3
```

Queue:

```
[4, 2]
```

```
q.popMiddle();
```

Return:

```
4
```

Queue:

```
[2]
```

```
q.popBack();
```

Return:

```
2
```

Queue:

```
[]
```

```
q.popFront();
```

Return:

```
-1
```

Queue remains:

```
[]
```

---

# Constraints

```
1 <= val <= 10^9
```

At most:

```
1000 calls
```

will be made to:

```
pushFront
pushMiddle
pushBack
popFront
popMiddle
popBack
```
