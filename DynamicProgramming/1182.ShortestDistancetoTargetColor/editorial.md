# 1182. Shortest Distance to Target Color — Detailed Notes

This document converts the provided explanation into a detailed Markdown note.

---

# Problem Recap

You are given:

- an array `colors`, where each value is one of:
  - `1`
  - `2`
  - `3`
- a list of queries, where each query is:
  - `[i, c]`

For each query, you must return the shortest distance between index `i` and any position in `colors` whose value is `c`.

Distance is absolute difference in indices:

```text
|i - j|
```

If the target color `c` does not appear anywhere in the array, return:

```text
-1
```

---

# Example

## Input

```text
colors  = [1,1,2,1,3,2,2,3,3]
queries = [[1,3],[2,2],[6,1]]
```

## Output

```text
[3,0,3]
```

## Explanation

### Query 1: `[1, 3]`

At index `1`, we want the nearest `3`.

Occurrences of `3` are at indices:

```text
4, 7, 8
```

Nearest is `4`, so distance is:

```text
|1 - 4| = 3
```

---

### Query 2: `[2, 2]`

At index `2`, we want color `2`.

`colors[2]` is already `2`, so distance is:

```text
0
```

---

### Query 3: `[6, 1]`

Occurrences of `1` are at indices:

```text
0, 1, 3
```

Nearest to index `6` is `3`, so distance is:

```text
|6 - 3| = 3
```

---

# Key Observations

1. There are only **three possible colors**, which is a very useful constraint.
2. The number of queries can be large, up to `5 * 10^4`.
3. A naive approach of scanning the entire array for every query would be too slow.

This suggests preprocessing.

The provided discussion covers two strong approaches:

1. **Binary Search on stored positions**
2. **Pre-computed nearest distances**

---

# Approach 1: Binary Search

## Intuition

For each query `[i, c]`, the most obvious approach is:

1. find every occurrence of color `c`
2. compute the distance from `i` to each occurrence
3. return the minimum

That works, but it is wasteful.

If we do that from scratch for every query, we repeatedly scan parts of the array we do not care about.

So instead, we preprocess the positions of each color.

Because the colors array is traversed from left to right, the indices we store for each color are naturally sorted.

That immediately suggests binary search.

---

## Main Idea

Create a mapping:

```text
color -> sorted list of indices where that color appears
```

For example, for:

```text
colors = [1,1,2,1,3,2,2,3,3]
```

we would store:

```text
1 -> [0, 1, 3]
2 -> [2, 5, 6]
3 -> [4, 7, 8]
```

Now for a query `[i, c]`, instead of scanning the entire array:

- look up the sorted list for color `c`
- use binary search to find the insertion position of `i`
- the nearest occurrence is either:
  - the first index greater than or equal to `i`
  - or the previous index just before it

So the answer is the smaller of those two distances.

---

## Why Binary Search Works

Suppose the target list is:

```text
[4, 7, 8]
```

and the query index is:

```text
i = 6
```

If we binary search for `6`, we find that `6` would be inserted between `4` and `7`.

So the nearest occurrence of the color must be either:

- `4`
- or `7`

No other value can be closer, because all earlier values are even farther left and all later values are even farther right.

That is the core reason binary search solves each query efficiently.

---

## Algorithm

### Preprocessing

1. Create a hashmap:
   - key = color
   - value = sorted list of indices where that color occurs
2. Traverse the array `colors`
3. Append each index to the corresponding list

### Query Answering

For each query `[i, c]`:

1. If `c` does not exist in the hashmap, return `-1`
2. Otherwise, let `indexList` be the sorted positions of color `c`
3. Use binary search to find the insertion position of `i`
4. Handle three cases:
   - `i` is before all positions
   - `i` is after all positions
   - `i` lies between two positions
5. Return the nearest distance

---

## Java Implementation

```java
class Solution {
    public List<Integer> shortestDistanceColor(int[] colors, int[][] queries) {
        // initialization
        List<Integer> queryResults = new ArrayList<>();
        Map<Integer, List<Integer>> hashmap = new HashMap<>();

        for (int i = 0; i < colors.length; i++) {
            hashmap.putIfAbsent(colors[i], new ArrayList<Integer>());
            hashmap.get(colors[i]).add(i);
        }

        // for each query, apply binary search
        for (int i = 0; i < queries.length; i++) {
            int target = queries[i][0], color = queries[i][1];
            if (!hashmap.containsKey(color)) {
                queryResults.add(-1);
                continue;
            }

            List<Integer> indexList = hashmap.get(color);
            int insert = Collections.binarySearch(indexList, target);

            // convert the result from Collections.binarySearch
            if (insert < 0) {
                insert = (insert + 1) * -1;
            }

            if (insert == 0) {
                queryResults.add(indexList.get(insert) - target);
            } else if (insert == indexList.size()) {
                queryResults.add(target - indexList.get(insert - 1));
            } else {
                int leftNearest = target - indexList.get(insert - 1);
                int rightNearest = indexList.get(insert) - target;
                queryResults.add(Math.min(leftNearest, rightNearest));
            }
        }
        return queryResults;
    }
}
```

---

## Understanding `Collections.binarySearch`

In Java, `Collections.binarySearch(list, target)` behaves like this:

- if `target` is found, it returns its index
- if `target` is not found, it returns a negative value encoding the insertion point

Specifically:

```text
return value = -(insertionPoint) - 1
```

So to recover the insertion point:

```java
if (insert < 0) {
    insert = (insert + 1) * -1;
}
```

This gives the first index at which `target` could be inserted while preserving sorted order.

---

## Complexity Analysis

Let:

- `N` = length of `colors`
- `Q` = number of queries

### Time Complexity

Building the hashmap of positions takes:

```text
O(N)
```

Each query performs one binary search over a list of positions, which takes:

```text
O(log N)
```

So all queries take:

```text
O(Q log N)
```

Total:

```text
O(N + Q log N)
```

---

### Space Complexity

We store every index exactly once across the hashmap lists:

```text
O(N)
```

---

## Strengths of This Approach

- straightforward
- efficient enough for the problem constraints
- answers each query in logarithmic time
- easy to reason about

---

# Approach 2: Pre-computed Distances

## Intuition

Instead of answering each query with binary search, we can do more preprocessing and make each query answerable in constant time.

The idea is:

> For every index `i` and every color `c`, precompute the shortest distance from `i` to color `c`.

Then each query becomes just a lookup.

Since there are only **three colors**, this is very manageable.

---

## Core Idea

For each index `i` and each color `c`, the nearest occurrence of `c` can be either:

- somewhere to the **left**
- somewhere to the **right**

So the shortest distance is:

```text
min(distance to nearest c on left, distance to nearest c on right)
```

That suggests two passes:

1. left-to-right pass
2. right-to-left pass

---

## Why Two Passes Are Enough

Suppose color `c` appears at indices:

```text
i < j
```

with no occurrence of `c` between them.

Then for any index `k` between `i` and `j`:

- the nearest `c` on the left is at distance `k - i`
- the nearest `c` on the right is at distance `j - k`

So if we process left-to-right and right-to-left, we can fill in exactly these nearest distances.

---

## Data Structure

We maintain a table:

```text
distance[color][index]
```

Since colors are only `1`, `2`, and `3`, we can use 3 rows.

So:

- `distance[0][i]` → shortest distance from index `i` to color `1`
- `distance[1][i]` → shortest distance from index `i` to color `2`
- `distance[2][i]` → shortest distance from index `i` to color `3`

Initialize all values to `-1`.

Then fill them by scanning from both directions.

---

## Left-to-Right Pass

For each color, track the latest seen occurrence while scanning from left to right.

When we encounter a color again, we can fill the distances for the stretch since the last occurrence.

Conceptually, this computes distance to the nearest occurrence on the left.

---

## Right-to-Left Pass

Similarly, scan from right to left.

Now we compute distance to the nearest occurrence on the right.

If the right-side distance is smaller than the currently stored value, update it.

At the end, each cell contains the shortest distance overall.

---

## Java Implementation

```java
class Solution {
    public List<Integer> shortestDistanceColor(int[] colors, int[][] queries) {
        // initializations
        int n = colors.length;
        int[] rightmost = {0, 0, 0};
        int[] leftmost = {n - 1, n - 1, n - 1};

        int[][] distance = new int[3][n];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < n; j++) {
                distance[i][j] = -1;
            }
        }

        // looking forward
        for (int i = 0; i < n; i++) {
            int color = colors[i] - 1;
            for (int j = rightmost[color]; j < i + 1; j++) {
                distance[color][j] = i - j;
            }
            rightmost[color] = i + 1;
        }

        // looking backward
        for (int i = n - 1; i > -1; i--) {
            int color = colors[i] - 1;
            for (int j = leftmost[color]; j > i - 1; j--) {
                if (distance[color][j] == -1 || distance[color][j] > j - i) {
                    distance[color][j] = j - i;
                }
            }
            leftmost[color] = i - 1;
        }

        List<Integer> queryResults = new ArrayList<>();
        for (int i = 0; i < queries.length; i++) {
            queryResults.add(distance[queries[i][1] - 1][queries[i][0]]);
        }
        return queryResults;
    }
}
```

---

## How to Understand the Precomputed Table

Once preprocessing is complete, `distance[c - 1][i]` directly stores:

> the shortest distance from index `i` to color `c`

So if a query is:

```text
[i, c]
```

the answer is simply:

```java
distance[c - 1][i]
```

No searching is needed anymore.

---

## Complexity Analysis

Let:

- `N` = length of `colors`
- `Q` = number of queries

### Time Complexity

The distance table has only `3 x N` size, and both preprocessing passes together are linear in `N`.

So preprocessing takes:

```text
O(N)
```

Each query is answered in:

```text
O(1)
```

So all queries take:

```text
O(Q)
```

Total:

```text
O(N + Q)
```

---

### Space Complexity

We store:

- two arrays of size 3
- a `3 x N` distance table

So total extra space is:

```text
O(N)
```

---

## Strengths of This Approach

- optimal query time: `O(1)`
- total time `O(N + Q)`
- works especially well because there are only **3 colors**

This is usually the best approach for this particular problem.

---

# Comparing the Two Approaches

| Approach               | Main Idea                                      | Preprocessing |  Per Query |       Total Time |  Space |
| ---------------------- | ---------------------------------------------- | ------------: | ---------: | ---------------: | -----: |
| Binary Search          | Store positions of each color, search nearest  |        `O(N)` | `O(log N)` | `O(N + Q log N)` | `O(N)` |
| Pre-computed Distances | Fill distance table for all indices and colors |        `O(N)` |     `O(1)` |       `O(N + Q)` | `O(N)` |

---

# Which Approach Should You Prefer?

## Binary Search Approach

Prefer this when:

- you want a very standard preprocessing + query solution
- the number of colors is not necessarily tiny
- you already have sorted position lists

It is elegant and easy to generalize.

---

## Pre-computed Distance Approach

Prefer this here because:

- there are only three colors
- preprocessing is easy
- queries become constant-time lookups
- total complexity is optimal for the given constraints

This is usually the strongest solution for this exact problem.

---

# Key Takeaways

## 1. The color domain is tiny

The fact that colors are only `1`, `2`, and `3` is the key simplifying factor.

## 2. Repeated queries strongly suggest preprocessing

When many queries are asked on the same underlying array, preprocessing is usually worthwhile.

## 3. Binary search is natural once occurrences are stored

Sorted occurrence lists let you find the nearest index efficiently.

## 4. Two-direction scanning is a common pattern

Many nearest-distance problems can be solved by combining:

- nearest from the left
- nearest from the right

## 5. Constant-time query answering is often worth extra preprocessing

That is exactly what the pre-computed approach achieves.

---

# Final Insight

This problem is fundamentally about answering many nearest-occurrence queries efficiently.

There are two strong ways to think about it:

- **store occurrence positions and search on demand**
- **precompute all answers in advance**

Because there are only three colors, the second strategy is especially powerful here, giving:

```text
O(N + Q)
```

overall time with simple constant-time query answering.
