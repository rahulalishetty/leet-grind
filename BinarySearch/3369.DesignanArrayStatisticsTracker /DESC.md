# 3369. Design an Array Statistics Tracker

## Problem Description

Design a data structure that keeps track of values inserted into it and supports queries for **mean**, **median**, and **mode**.

Implement the `StatisticsTracker` class with the following methods.

---

## Class Definition

### `StatisticsTracker()`

Initializes the data structure with an **empty array**.

### `void addNumber(int number)`

Adds `number` to the data structure.

### `void removeFirstAddedNumber()`

Removes the **earliest inserted number** (FIFO order).

### `int getMean()`

Returns the **floored mean** of the numbers currently in the structure.

Mean formula:

```
mean = floor(sum_of_numbers / total_numbers)
```

### `int getMedian()`

Returns the **median** of the numbers.

Definition:

- Sort the array in **non-decreasing order**
- If there are two possible medians, return the **larger one**.

### `int getMode()`

Returns the **mode** (most frequent number).

If multiple numbers have the same highest frequency, return the **smallest one**.

---

# Example 1

### Input

```
["StatisticsTracker", "addNumber", "addNumber", "addNumber", "addNumber", "getMean", "getMedian", "getMode", "removeFirstAddedNumber", "getMode"]
[[], [4], [4], [2], [3], [], [], [], [], []]
```

### Output

```
[null, null, null, null, null, 3, 4, 4, null, 2]
```

### Explanation

```
StatisticsTracker statisticsTracker = new StatisticsTracker();

statisticsTracker.addNumber(4);
// [4]

statisticsTracker.addNumber(4);
// [4,4]

statisticsTracker.addNumber(2);
// [4,4,2]

statisticsTracker.addNumber(3);
// [4,4,2,3]

statisticsTracker.getMean();
// (4+4+2+3)/4 = 13/4 = 3

statisticsTracker.getMedian();
// sorted: [2,3,4,4] → median = 4

statisticsTracker.getMode();
// 4 appears twice → mode = 4

statisticsTracker.removeFirstAddedNumber();
// remove earliest → [4,2,3]

statisticsTracker.getMode();
// all freq=1 → smallest = 2
```

---

# Example 2

### Input

```
["StatisticsTracker", "addNumber", "addNumber", "getMean", "removeFirstAddedNumber", "addNumber", "addNumber", "removeFirstAddedNumber", "getMedian", "addNumber", "getMode"]
[[], [9], [5], [], [], [5], [6], [], [], [8], []]
```

### Output

```
[null, null, null, 7, null, null, null, null, 6, null, 5]
```

### Explanation

```
StatisticsTracker statisticsTracker = new StatisticsTracker();

statisticsTracker.addNumber(9);
// [9]

statisticsTracker.addNumber(5);
// [9,5]

statisticsTracker.getMean();
// (9+5)/2 = 7

statisticsTracker.removeFirstAddedNumber();
// remove 9 → [5]

statisticsTracker.addNumber(5);
// [5,5]

statisticsTracker.addNumber(6);
// [5,5,6]

statisticsTracker.removeFirstAddedNumber();
// remove first 5 → [5,6]

statisticsTracker.getMedian();
// sorted [5,6] → choose larger median → 6

statisticsTracker.addNumber(8);
// [5,6,8]

statisticsTracker.getMode();
// all freq=1 → smallest = 5
```

---

# Constraints

```
1 <= number <= 10^9
```

```
Total operations ≤ 10^5
```

Operations include:

```
addNumber
removeFirstAddedNumber
getMean
getMedian
getMode
```

Additional guarantees:

- `removeFirstAddedNumber`
- `getMean`
- `getMedian`
- `getMode`

will only be called **when at least one element exists** in the structure.

---

# Summary

The data structure must efficiently maintain:

- **Mean** → maintain running sum
- **Median** → maintain balanced ordering (typically heaps)
- **Mode** → track frequencies
- **FIFO removal** → maintain insertion order
