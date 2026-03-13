# Data Stream as Disjoint Intervals — Approaches

## Approach 1: Save all values in an ordered set

### Intuition

The question asks to combine consecutive values into intervals.
For example:

1, 2, 3, 4 → interval [1,4]

If the data is sorted, we can easily iterate through it to detect consecutive numbers and build intervals.

Therefore we need a data structure that:

- Maintains sorted order
- Supports efficient insertion

In **Java**, `TreeSet` satisfies both requirements.

- Insert: `O(log n)`
- Ordered iteration

Equivalent structures:

- Python → `SortedList`
- C++ → `std::set`

---

### Algorithm

Initialize:

```
TreeSet values
```

#### addNum(value)

Simply insert value into the TreeSet.

```
values.add(value)
```

If duplicates are allowed in the language implementation, check existence before inserting.

---

#### getIntervals()

Steps:

1. If the set is empty → return empty array.
2. Iterate through sorted values.
3. Build intervals based on adjacency.

Variables:

```
left  = start of interval
right = end of interval
```

Rules:

- If starting → `left = right = value`
- If `value == right + 1` → extend interval
- Otherwise → store previous interval and start new one

---

### Implementation

```java
class SummaryRanges {
    private Set<Integer> values;

    public SummaryRanges() {
        values = new TreeSet<>();
    }

    public void addNum(int value) {
       values.add(value);
    }

    public int[][] getIntervals() {
        if (values.isEmpty()) {
            return new int[0][2];
        }

        List<int[]> intervals = new ArrayList<>();
        int left = -1, right = -1;

        for (Integer value : values) {
            if (left < 0) {
                left = right = value;
            } else if (value == right + 1) {
                right = value;
            } else {
                intervals.add(new int[] {left, right});
                left = right = value;
            }
        }

        intervals.add(new int[] {left, right});
        return intervals.toArray(new int[0][]);
    }
}
```

---

### Complexity Analysis

Let **N** be the number of calls to `addNum`.

Time Complexity:

```
addNum      O(log N)
getIntervals O(N)
```

Space Complexity:

```
O(N)
```

Space is required to store all numbers in the `TreeSet`.

---

# Approach 2: Maintain intervals directly using ordered map

## Intuition

Instead of storing every number and recomputing intervals every time, we directly maintain intervals.

Each interval is stored as:

```
[left, right]
```

Using:

```
TreeMap<left, right>
```

This keeps intervals sorted by their start value.

Equivalent structures:

- Python → `SortedDict`
- C++ → `std::map`

---

## Cases when inserting a number

### Case 1 — Extend left interval

If there exists:

```
[x, value-1]
```

Then the new interval becomes:

```
[x, value]
```

---

### Case 2 — Extend right interval

If there exists:

```
[value+1, y]
```

Then the new interval becomes:

```
[value, y]
```

---

### Case 3 — Merge both sides

If both exist:

```
[x, value-1] and [value+1, y]
```

Then merge:

```
[x, y]
```

---

### Trivial cases

1. Value already inside an interval → do nothing.
2. No adjacent intervals → create `[value,value]`.

---

## Algorithm

Initialize:

```
TreeMap intervals
```

### addNum(value)

Steps:

1. Find interval with largest left ≤ value.
2. Check merge with left interval.
3. Check merge with right interval.
4. Insert or merge accordingly.

---

### getIntervals()

Simply iterate through `TreeMap` entries.

---

## Implementation

```java
class SummaryRanges {
    private TreeMap<Integer, Integer> intervals;

    public SummaryRanges() {
        intervals = new TreeMap<>();
    }

    public void addNum(int value) {

        Map.Entry<Integer,Integer> smallEntry = intervals.floorEntry(value);

        int left = value;
        int right = value;

        if (smallEntry != null) {
            int previous = smallEntry.getValue();

            if (previous >= value) {
                return;
            }

            if (previous == value - 1) {
                left = smallEntry.getKey();
            }
        }

        Map.Entry<Integer,Integer> maxEntry = intervals.higherEntry(value);

        if (maxEntry != null && maxEntry.getKey() == value + 1) {
            right = maxEntry.getValue();
            intervals.remove(value + 1);
        }

        intervals.put(left, right);
    }

    public int[][] getIntervals() {

        int[][] answer = new int[intervals.size()][2];

        int ind = 0;

        for (Map.Entry<Integer,Integer> entry : intervals.entrySet()) {
            answer[ind][0] = entry.getKey();
            answer[ind][1] = entry.getValue();
            ind++;
        }

        return answer;
    }
}
```

---

## Complexity Analysis

Let **N** be number of insert operations.

Time Complexity:

```
addNum      O(log N)
getIntervals O(N)
```

Operations in TreeMap:

- Insert → `log N`
- Delete → `log N`
- Search → `log N`

---

Space Complexity

```
O(N)
```

Stores all disjoint intervals.
