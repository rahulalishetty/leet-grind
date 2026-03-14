# 2276. Count Integers in Intervals

## Problem Description

Given an empty set of intervals, implement a data structure that supports the following operations:

1. **Add an interval to the set of intervals**
2. **Count the number of integers present in at least one interval**

An interval `[left, right]` includes all integers `x` such that:

```
left ≤ x ≤ right
```

---

# Class Definition

Implement the `CountIntervals` class with the following methods:

### Constructor

```
CountIntervals()
```

Initializes the object with an empty set of intervals.

---

### Method: add

```
void add(int left, int right)
```

Adds the interval `[left, right]` to the set of intervals.

---

### Method: count

```
int count()
```

Returns the number of integers that are present in **at least one interval**.

---

# Example

## Input

```
["CountIntervals", "add", "add", "count", "add", "count"]
[[], [2, 3], [7, 10], [], [5, 8], []]
```

## Output

```
[null, null, null, 6, null, 8]
```

---

## Explanation

```
CountIntervals countIntervals = new CountIntervals();
```

Initialize the object with an empty set of intervals.

---

### Operation

```
countIntervals.add(2, 3)
```

Add interval `[2,3]`.

Covered integers:

```
2, 3
```

---

### Operation

```
countIntervals.add(7, 10)
```

Add interval `[7,10]`.

Covered integers:

```
7, 8, 9, 10
```

---

### Operation

```
countIntervals.count()
```

Total covered integers:

```
2, 3, 7, 8, 9, 10
```

Result:

```
6
```

---

### Operation

```
countIntervals.add(5, 8)
```

Add interval `[5,8]`.

Covered integers become:

```
2, 3
5, 6
7, 8
9, 10
```

Note: `7` and `8` appear in both intervals but are counted **once**.

---

### Operation

```
countIntervals.count()
```

Total unique integers covered:

```
2,3,5,6,7,8,9,10
```

Result:

```
8
```

---

# Constraints

```
1 ≤ left ≤ right ≤ 10^9
At most 10^5 calls will be made to add and count combined
At least one call will be made to count
```
