# 352. Data Stream as Disjoint Intervals

Given a data stream input of non-negative integers:

```
a1, a2, ..., an
```

Summarize the numbers seen so far as a list of **disjoint intervals**.

---

# Class to Implement

Implement the **SummaryRanges** class.

### Constructor

```
SummaryRanges()
```

Initializes the object with an **empty data stream**.

---

### Method

```
void addNum(int value)
```

Adds the integer `value` to the data stream.

---

### Method

```
int[][] getIntervals()
```

Returns the summary of integers seen so far as a list of **disjoint intervals**:

```
[start_i, end_i]
```

Requirements:

- Intervals must be **sorted by start**
- Intervals must be **non-overlapping**
- Adjacent numbers should be merged into a single interval

---

# Example

## Input

```
["SummaryRanges", "addNum", "getIntervals", "addNum", "getIntervals",
 "addNum", "getIntervals", "addNum", "getIntervals", "addNum", "getIntervals"]

[[], [1], [], [3], [], [7], [], [2], [], [6], []]
```

## Output

```
[null, null, [[1,1]], null, [[1,1],[3,3]], null,
 [[1,1],[3,3],[7,7]], null, [[1,3],[7,7]], null, [[1,3],[6,7]]]
```

---

# Explanation

```
SummaryRanges summaryRanges = new SummaryRanges();
```

### Step 1

```
addNum(1)
stream = [1]
```

```
getIntervals()
→ [[1,1]]
```

---

### Step 2

```
addNum(3)
stream = [1,3]
```

```
getIntervals()
→ [[1,1],[3,3]]
```

---

### Step 3

```
addNum(7)
stream = [1,3,7]
```

```
getIntervals()
→ [[1,1],[3,3],[7,7]]
```

---

### Step 4

```
addNum(2)
stream = [1,2,3,7]
```

Intervals merge:

```
[1,2,3] → [1,3]
```

```
getIntervals()
→ [[1,3],[7,7]]
```

---

### Step 5

```
addNum(6)
stream = [1,2,3,6,7]
```

Intervals merge:

```
[6,7] → [6,7]
```

```
getIntervals()
→ [[1,3],[6,7]]
```

---

# Constraints

```
0 <= value <= 10^4
At most 3 * 10^4 calls to addNum and getIntervals
At most 10^2 calls to getIntervals
```

---

# Follow-up

What if:

- There are **lots of merges**
- The number of **disjoint intervals is small**
- But the **data stream size is large**

Design a solution that efficiently handles:

```
frequent merging operations
```
