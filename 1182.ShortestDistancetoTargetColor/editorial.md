# Shortest Distance to Target Color — Detailed Summary of 2 Approaches

## Problem context

We are given:

- an array `colors`
- a list of queries, where each query is:

```text
[i, c]
```

For each query, we need the shortest distance from index `i` to any occurrence of color `c` in `colors`.

If color `c` does not appear in the array at all, the answer is:

```text
-1
```

The typical LeetCode version of this problem has colors from `{1, 2, 3}`.

---

# Core task for one query

For a query:

```text
[i, c]
```

we want:

```text
min(|i - j|) for all j such that colors[j] == c
```

So the problem becomes:

- find all indices where color `c` occurs
- choose the one closest to `i`

The two main approaches below optimize this in different ways.

---

# Approach 1: Binary Search

## Intuition

A direct approach would be:

- for each query `(i, c)`
- scan the whole array
- compute distance to every index where `colors[index] == c`
- keep the minimum

That is too slow because each query would cost `O(N)`.

A better idea is to preprocess the array once.

Since colors only take a small number of values, we can collect the indices for each color into separate lists.

For example, if:

```text
colors = [1, 1, 2, 1, 3, 2, 2, 3, 3]
```

then we might store:

```text
1 -> [0, 1, 3]
2 -> [2, 5, 6]
3 -> [4, 7, 8]
```

These lists are automatically sorted because we traverse the array from left to right.

Now for a query `(i, c)`, instead of scanning the whole array, we only look at the sorted list of positions for color `c`.

Since that list is sorted, the closest occurrence to `i` can be found using **binary search**.

---

## Why binary search works

Suppose the sorted index list for color `c` is:

```text
[2, 5, 9, 14]
```

and the query asks for distance from:

```text
i = 8
```

The closest occurrence must be either:

- the first index `>= 8`
- or the one just before it

In this example:

- right candidate = `9`
- left candidate = `5`

So we only need to check those two positions.

This is exactly what binary search gives us efficiently.

---

## Step-by-step algorithm

### Preprocessing

Create a hashmap:

```text
color -> list of indices where that color appears
```

Iterate through `colors` and append each index into the corresponding list.

### Answering a query `(i, c)`

1. If `c` is not in the map, return `-1`.
2. Otherwise retrieve the sorted list of indices for `c`.
3. Perform binary search for `i` in that list.
4. Handle three cases:
   - insertion position is `0` → closest is the first element
   - insertion position is at the end → closest is the last element
   - otherwise compare the element on the left and the one on the right

---

## Java implementation

```java
import java.util.*;

class Solution {
    public List<Integer> shortestDistanceColor(int[] colors, int[][] queries) {
        List<Integer> queryResults = new ArrayList<>();
        Map<Integer, List<Integer>> hashmap = new HashMap<>();

        // Build color -> sorted list of indices
        for (int i = 0; i < colors.length; i++) {
            hashmap.putIfAbsent(colors[i], new ArrayList<Integer>());
            hashmap.get(colors[i]).add(i);
        }

        // Answer each query with binary search
        for (int i = 0; i < queries.length; i++) {
            int target = queries[i][0];
            int color = queries[i][1];

            if (!hashmap.containsKey(color)) {
                queryResults.add(-1);
                continue;
            }

            List<Integer> indexList = hashmap.get(color);
            int insert = Collections.binarySearch(indexList, target);

            // Convert Java's negative result into insertion index
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

Java’s `Collections.binarySearch(list, key)` behaves as follows:

- if `key` is found, it returns its index
- if `key` is not found, it returns:

```text
-(insertion_point) - 1
```

So to recover the insertion position, use:

```java
if (insert < 0) {
    insert = -insert - 1;
}
```

The provided code uses an equivalent transformation:

```java
insert = (insert + 1) * -1;
```

Both are correct.

---

## Dry run for binary search approach

Suppose:

```text
colors = [1, 1, 2, 1, 3, 2, 2, 3, 3]
```

Map becomes:

```text
1 -> [0, 1, 3]
2 -> [2, 5, 6]
3 -> [4, 7, 8]
```

Now query:

```text
[4, 2]
```

We need the shortest distance from index `4` to color `2`.

Index list for color `2`:

```text
[2, 5, 6]
```

Binary search insertion position for `4` is between `2` and `5`.

So nearest candidates are:

- left = `2`, distance = `4 - 2 = 2`
- right = `5`, distance = `5 - 4 = 1`

Answer:

```text
1
```

---

## Complexity analysis

Let:

- `N = colors.length`
- `Q = queries.length`

### Time complexity

Building the map takes:

```text
O(N)
```

Each query performs one binary search on a list of positions, which costs:

```text
O(log N)
```

So total query time is:

```text
O(Q log N)
```

Overall:

```text
O(N + Q log N)
```

### Space complexity

We store every index exactly once across the lists:

```text
O(N)
```

---

# Approach 2: Pre-computed Distances

## Intuition

Instead of answering each query with binary search, we can precompute the shortest distance from every index to every color.

Then each query becomes a direct lookup in `O(1)` time.

Since there are only 3 colors, this is feasible.

The idea is:

For each index `i` and each target color `c`, the nearest occurrence of `c` could be:

- somewhere on the left of `i`
- somewhere on the right of `i`

So the shortest distance is:

```text
min(distance to nearest c on left, distance to nearest c on right)
```

We can compute these efficiently by scanning:

1. left to right
2. right to left

---

## Key observation

Suppose color `c` appears at indices:

```text
i < j
```

and there is no other `c` between them.

Then for any index `k` between `i` and `j`:

- nearest `c` on the left is at distance `k - i`
- nearest `c` on the right is at distance `j - k`

So a forward pass can fill distances to the nearest occurrence on the left, and a backward pass can update with the nearest occurrence on the right.

---

## Data structure

We build a matrix:

```text
distance[color][index]
```

where:

- `color` is `0, 1, 2` corresponding to actual colors `1, 2, 3`
- `distance[color][index]` = shortest distance from `index` to that color
- if unreachable, keep `-1`

Because only 3 colors exist, this matrix has size:

```text
3 x N
```

---

## Forward pass meaning

As we move left to right:

- whenever we encounter a color `c` at position `i`
- we fill/update distances for that color from the last occurrence up to `i`

This effectively records nearest occurrences on the left.

---

## Backward pass meaning

As we move right to left:

- whenever we encounter a color `c` at position `i`
- we update distances for that color from the last seen position on the right back to `i`

If the right-side distance is better than the current stored left-side distance, replace it.

This combines left and right nearest distances into the final answer.

---

## Java implementation

```java
import java.util.*;

class Solution {
    public List<Integer> shortestDistanceColor(int[] colors, int[][] queries) {
        int n = colors.length;
        int[] rightmost = {0, 0, 0};
        int[] leftmost = {n - 1, n - 1, n - 1};

        int[][] distance = new int[3][n];

        for (int i = 0; i < 3; i++) {
            Arrays.fill(distance[i], -1);
        }

        // Looking forward: nearest color on the left
        for (int i = 0; i < n; i++) {
            int color = colors[i] - 1;
            for (int j = rightmost[color]; j < i + 1; j++) {
                distance[color][j] = i - j;
            }
            rightmost[color] = i + 1;
        }

        // Looking backward: nearest color on the right
        for (int i = n - 1; i >= 0; i--) {
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

## Why query answering becomes `O(1)`

Once `distance[color][index]` is completely filled, each query:

```text
[i, c]
```

can be answered by directly reading:

```text
distance[c - 1][i]
```

So no search is needed during query time.

---

## Dry run for precomputed approach

Suppose:

```text
colors = [1, 2, 3, 2, 1]
```

We want distances to color `2`.

### Forward pass

Nearest `2` on the left:

- index 0 → none yet → `-1`
- index 1 → distance `0`
- index 2 → nearest left `2` at 1 → distance `1`
- index 3 → distance `0`
- index 4 → nearest left `2` at 3 → distance `1`

Intermediate row for color `2`:

```text
[-1, 0, 1, 0, 1]
```

### Backward pass

Nearest `2` on the right may improve values:

- index 4 → right `2` at 3 → distance `1`
- index 2 → right `2` at 3 → distance `1`
- index 0 → right `2` at 1 → distance `1`

Final distances to color `2`:

```text
[1, 0, 1, 0, 1]
```

Now a query like:

```text
[0, 2]
```

is answered immediately as:

```text
1
```

---

# Alternative cleaner precompute idea

The provided precompute solution is clever, but there is also a more standard way to explain the same concept.

For each of the 3 colors:

1. sweep left to right, storing nearest seen occurrence
2. sweep right to left, updating with nearest seen occurrence on the right

That version is often easier to reason about.

### Cleaner Java version

```java
import java.util.*;

class Solution {
    public List<Integer> shortestDistanceColor(int[] colors, int[][] queries) {
        int n = colors.length;
        int[][] dist = new int[4][n]; // use colors 1..3 directly

        for (int c = 1; c <= 3; c++) {
            Arrays.fill(dist[c], Integer.MAX_VALUE / 2);

            int last = -1;
            for (int i = 0; i < n; i++) {
                if (colors[i] == c) {
                    last = i;
                }
                if (last != -1) {
                    dist[c][i] = i - last;
                }
            }

            last = -1;
            for (int i = n - 1; i >= 0; i--) {
                if (colors[i] == c) {
                    last = i;
                }
                if (last != -1) {
                    dist[c][i] = Math.min(dist[c][i], last - i);
                }
            }
        }

        List<Integer> ans = new ArrayList<>();
        for (int[] q : queries) {
            int i = q[0], c = q[1];
            ans.add(dist[c][i] >= Integer.MAX_VALUE / 4 ? -1 : dist[c][i]);
        }

        return ans;
    }
}
```

This has the same complexity and is often easier to present in an interview.

---

# Comparing the two approaches

## Approach 1: Binary Search

### Pros

- conceptually simple
- good when colors can be many or queries are moderate
- uses standard “index list + binary search” pattern

### Cons

- each query still costs `O(log N)`

---

## Approach 2: Pre-computed Distances

### Pros

- each query becomes `O(1)`
- excellent when there are many queries
- especially attractive because colors are limited to 3

### Cons

- requires more preprocessing logic
- depends on the fact that the color domain is very small

---

# Complexity comparison

Let:

- `N = colors.length`
- `Q = queries.length`

## Binary search approach

### Time

```text
O(N + Q log N)
```

### Space

```text
O(N)
```

---

## Precomputed approach

### Time

```text
O(N + Q)
```

because preprocessing is linear and each query is constant-time.

### Space

```text
O(N)
```

more precisely `O(3N)`, which is still `O(N)`.

---

# When to prefer which approach

## Use binary search when:

- you want a very standard, reusable solution
- the number of distinct colors may not be tiny
- you are comfortable with ordered index lists

## Use precomputation when:

- the color set is tiny and fixed
- there are lots of queries
- you want optimal query speed

For this specific problem, the precomputed solution is especially strong because there are only 3 possible colors.

---

# Common mistakes

## 1. Scanning the whole array for every query

That gives:

```text
O(N * Q)
```

which is too slow for larger inputs.

---

## 2. Forgetting that the index lists are sorted

In the binary search approach, sorted order is what makes logarithmic query time possible.

---

## 3. Mishandling Java’s `Collections.binarySearch`

Remember:

- if not found, it returns a negative value
- you must convert it to insertion index correctly

---

## 4. Off-by-one errors with color indexing

In the precompute approach, colors are typically `1, 2, 3`, but arrays are `0`-indexed.

So many implementations map:

```text
color -> color - 1
```

Be consistent.

---

## 5. Forgetting the `-1` case

If a color does not exist in the array, the answer must be:

```text
-1
```

---

# Final takeaway

Both approaches rely on preprocessing.

## Binary search approach

Store the sorted positions of each color and answer each query by finding the nearest position with binary search.

## Precomputed approach

Store the answer for every `(index, color)` pair in advance, then each query becomes a direct lookup.

## Final complexity summary

### Approach 1: Binary Search

```text
Time:  O(N + Q log N)
Space: O(N)
```

### Approach 2: Pre-computed Distances

```text
Time:  O(N + Q)
Space: O(N)
```

For this problem specifically, the **precomputed distance approach** is usually the strongest final solution because the number of colors is fixed and very small.
