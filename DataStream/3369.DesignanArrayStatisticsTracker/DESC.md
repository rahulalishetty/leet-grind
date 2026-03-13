# 3369. Design an Array Statistics Tracker

Design a data structure that keeps track of values and answers queries about **mean**, **median**, and **mode**.

---

# Problem Description

Implement the `StatisticsTracker` class.

## Constructor

```
StatisticsTracker()
```

Initializes the object with an **empty array**.

---

## Methods

### addNumber

```
void addNumber(int number)
```

Adds `number` to the data structure.

---

### removeFirstAddedNumber

```
void removeFirstAddedNumber()
```

Removes the **earliest added number** from the data structure.

---

### getMean

```
int getMean()
```

Returns the **floored mean** of the numbers.

Mean definition:

```
mean = floor(sum of all elements / number of elements)
```

---

### getMedian

```
int getMedian()
```

Returns the **median** of the numbers.

Median definition:

- Sort the numbers in **non‑decreasing order**
- If there is a single middle element → return it
- If there are **two middle elements**, return the **larger** one

---

### getMode

```
int getMode()
```

Returns the **mode** of the numbers.

Mode definition:

- The number that appears **most frequently**
- If multiple values share the same frequency → return the **smallest** value

---

# Example 1

## Input

```
["StatisticsTracker","addNumber","addNumber","addNumber","addNumber",
"getMean","getMedian","getMode","removeFirstAddedNumber","getMode"]

[[],[4],[4],[2],[3],[],[],[],[],[]]
```

## Output

```
[null,null,null,null,null,3,4,4,null,2]
```

## Explanation

```
StatisticsTracker tracker = new StatisticsTracker();
```

```
tracker.addNumber(4) → [4]
tracker.addNumber(4) → [4,4]
tracker.addNumber(2) → [4,4,2]
tracker.addNumber(3) → [4,4,2,3]
```

```
getMean()   → 3
getMedian() → 4
getMode()   → 4
```

```
removeFirstAddedNumber() → [4,2,3]
```

```
getMode() → 2
```

---

# Example 2

## Input

```
["StatisticsTracker","addNumber","addNumber","getMean",
"removeFirstAddedNumber","addNumber","addNumber",
"removeFirstAddedNumber","getMedian","addNumber","getMode"]

[[],[9],[5],[],[],[5],[6],[],[],[8],[]]
```

## Output

```
[null,null,null,7,null,null,null,null,6,null,5]
```

## Explanation

```
StatisticsTracker tracker = new StatisticsTracker();
```

```
addNumber(9) → [9]
addNumber(5) → [9,5]
```

```
getMean() → 7
```

```
removeFirstAddedNumber() → [5]
```

```
addNumber(5) → [5,5]
addNumber(6) → [5,5,6]
```

```
removeFirstAddedNumber() → [5,6]
```

```
getMedian() → 6
```

```
addNumber(8) → [5,6,8]
```

```
getMode() → 5
```

---

# Constraints

```
1 <= number <= 10^9
```

Operation limits:

```
At most 10^5 calls will be made in total to:
addNumber
removeFirstAddedNumber
getMean
getMedian
getMode
```

Additional guarantee:

```
removeFirstAddedNumber, getMean, getMedian, and getMode
will only be called when the data structure contains at least one element.
```
