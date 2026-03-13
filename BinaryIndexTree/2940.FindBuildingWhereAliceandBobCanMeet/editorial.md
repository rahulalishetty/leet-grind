# Find Building Where Alice and Bob Can Meet — Detailed Approaches

## Overview

We are given:

- An integer array **heights**
- A list of queries **queries[i] = [ai, bi]**

For each query:

- Alice starts at building `ai`
- Bob starts at building `bi`

A person at building `i` can move to building `j` **only if**:

```
i < j AND heights[i] < heights[j]
```

The task is to find the **leftmost building index** where **both Alice and Bob can meet**.

If no such building exists, return **-1**.

---

# Approach 1: Monotonic Stack

## Intuition

First simplify the problem.

If queries contained only **one index**, the task becomes:

> Find the next building to the right that is taller than the current building.

This is exactly the **Next Greater Element** problem.

Using a **monotonic decreasing stack**, we can efficiently compute buildings to the right that are taller.

### Key Idea

While scanning from **right → left**:

- Maintain a stack of buildings with **strictly decreasing heights**
- Remove any building shorter than the current one
- The top of the stack becomes the **next taller building**

---

## Extending to Queries

Each query gives two buildings `a` and `b`.

We want the first building to the right that satisfies:

```
height > heights[a]
height > heights[b]
```

This means the building must be taller than:

```
max(heights[a], heights[b])
```

So each query reduces to finding the **first height greater than a threshold**.

---

## Algorithm

### Step 1 — Prepare Query Buckets

Create:

```
newQueries[index]
```

Each index stores queries whose **maximum index** is that index.

---

### Step 2 — Preprocess Queries

For each query:

1. Ensure `a <= b`
2. If:
   ```
   heights[b] > heights[a]
   ```
   then answer = `b`
3. Otherwise store the query for processing later.

---

### Step 3 — Traverse Heights Right → Left

For each building index:

1. Process queries attached to this index
2. Use **binary search** on the monotonic stack
3. Find first height greater than the query threshold

---

### Step 4 — Maintain Stack

While stack top height ≤ current height:

```
pop stack
```

Push current building.

---

## Implementation

```java
class Solution {

    public int[] leftmostBuildingQueries(int[] heights, int[][] queries) {
        List<Pair<Integer, Integer>> monoStack = new ArrayList<>();
        int[] result = new int[queries.length];
        Arrays.fill(result, -1);

        List<List<Pair<Integer, Integer>>> newQueries = new ArrayList<>(heights.length);

        for (int i = 0; i < heights.length; i++)
            newQueries.add(new ArrayList<>());

        for (int i = 0; i < queries.length; i++) {
            int a = queries[i][0];
            int b = queries[i][1];

            if (a > b) {
                int t = a;
                a = b;
                b = t;
            }

            if (heights[b] > heights[a] || a == b) {
                result[i] = b;
            } else {
                newQueries.get(b).add(new Pair<>(heights[a], i));
            }
        }

        for (int i = heights.length - 1; i >= 0; i--) {

            for (Pair<Integer, Integer> q : newQueries.get(i)) {
                int pos = search(q.getKey(), monoStack);
                if (pos >= 0)
                    result[q.getValue()] = monoStack.get(pos).getValue();
            }

            while (!monoStack.isEmpty() &&
                    monoStack.get(monoStack.size() - 1).getKey() <= heights[i]) {

                monoStack.remove(monoStack.size() - 1);
            }

            monoStack.add(new Pair<>(heights[i], i));
        }

        return result;
    }

    private int search(int height, List<Pair<Integer, Integer>> monoStack) {
        int left = 0;
        int right = monoStack.size() - 1;
        int ans = -1;

        while (left <= right) {
            int mid = (left + right) / 2;

            if (monoStack.get(mid).getKey() > height) {
                ans = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return ans;
    }
}
```

---

## Complexity Analysis

Let:

```
n = heights.length
q = number of queries
```

### Time Complexity

```
O(q log n + n)
```

Explanation:

- Binary search for each query
- Single traversal of heights

---

### Space Complexity

```
O(n + q)
```

Used by:

- monotonic stack
- query storage
- result array

---

# Approach 2: Priority Queue

## Intuition

Instead of answering each query independently, we process **buildings sequentially**.

For each building we ask:

> Can this building answer any pending queries?

To manage pending queries efficiently we use a **priority queue**.

Queries waiting for a taller building are stored with their **required height**.

Whenever we encounter a building taller than that requirement, we resolve the query.

---

## Algorithm

### Step 1 — Store Queries

Create:

```
storeQueries[index]
```

Each bucket contains queries whose **max index** equals that building.

---

### Step 2 — Preprocess Queries

For each query:

1. If Alice can reach Bob immediately → answer
2. If Bob can reach Alice → answer
3. If same building → answer
4. Otherwise store query for later

---

### Step 3 — Process Buildings Left → Right

For each building:

1. Resolve queries whose required height < current height
2. Add new queries whose max index equals current index

---

## Implementation

```java
class Solution {

    public int[] leftmostBuildingQueries(int[] heights, int[][] queries) {

        List<List<List<Integer>>> storeQueries = new ArrayList<>(heights.length);

        for (int i = 0; i < heights.length; i++)
            storeQueries.add(new ArrayList<>());

        PriorityQueue<List<Integer>> pq =
                new PriorityQueue<>((a,b)->a.get(0)-b.get(0));

        int[] result = new int[queries.length];
        Arrays.fill(result, -1);

        for (int i = 0; i < queries.length; i++) {

            int a = queries[i][0];
            int b = queries[i][1];

            if (a < b && heights[a] < heights[b])
                result[i] = b;

            else if (a > b && heights[a] > heights[b])
                result[i] = a;

            else if (a == b)
                result[i] = a;

            else {

                storeQueries.get(Math.max(a,b))
                        .add(Arrays.asList(
                                Math.max(heights[a], heights[b]),
                                i));
            }
        }

        for (int i = 0; i < heights.length; i++) {

            while (!pq.isEmpty() && pq.peek().get(0) < heights[i]) {
                result[pq.peek().get(1)] = i;
                pq.poll();
            }

            for (List<Integer> q : storeQueries.get(i))
                pq.offer(q);
        }

        return result;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(q log q + n)
```

Priority queue operations dominate.

---

### Space Complexity

```
O(n + q)
```

Used by:

- priority queue
- query storage
- result array

---

# Summary

| Approach        | Idea                                   | Time           | Space  |
| --------------- | -------------------------------------- | -------------- | ------ |
| Monotonic Stack | Next greater element + binary search   | O(q log n + n) | O(n+q) |
| Priority Queue  | Process queries while scanning heights | O(q log q + n) | O(n+q) |

---

# Key Insight

Both approaches rely on the same principle:

> The meeting building must be taller than both starting buildings.

So every query reduces to:

```
find first building to the right with height >
max(heights[a], heights[b])
```
