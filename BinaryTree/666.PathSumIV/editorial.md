# Path Sum IV – Detailed Approaches

## Overview

We are given a tree represented as a list of **three‑digit numbers**.

Each number encodes a node using the following format:

| Digit    | Meaning                              |
| -------- | ------------------------------------ |
| Hundreds | Node depth (1–4)                     |
| Tens     | Node position within the level (1–8) |
| Units    | Node value (0–9)                     |

Example:

```
215
```

means:

```
depth = 2
position = 1
value = 5
```

The goal is to **compute the sum of all root‑to‑leaf path sums**.

To traverse the tree we can use:

- **Depth First Search (DFS)**
- **Breadth First Search (BFS)**

---

# Approach 1 — Depth First Search

## Intuition

DFS explores one path completely before moving to another.

Since we want **root → leaf paths**, DFS naturally matches this structure.

Important observations:

- If a node is at depth `d`, its children are at depth `d + 1`.
- If a node is at position `p`, then:
  - Left child position = `2*p - 1`
  - Right child position = `2*p`

Thus child coordinates become:

```
left  = (level + 1) * 10 + position * 2 - 1
right = (level + 1) * 10 + position * 2
```

Each node can be uniquely identified using:

```
(depth * 10 + position)
```

We store these coordinates in a **HashMap**.

Key:

```
depth*10 + position
```

Value:

```
node value
```

---

## Algorithm

### Main Function — `pathSum(nums)`

1. Create a hashmap `map`.
2. For every number in `nums`:

```
key   = num / 10
value = num % 10
map[key] = value
```

3. Call DFS:

```
dfs(nums[0] / 10, 0)
```

4. Return result.

---

### DFS Function — `dfs(rootCoordinates, preSum)`

1. Extract level and position:

```
level = root / 10
pos   = root % 10
```

2. Calculate children:

```
left  = (level + 1) * 10 + pos * 2 - 1
right = (level + 1) * 10 + pos * 2
```

3. Update running sum:

```
currSum = preSum + nodeValue
```

4. If node is a leaf:

```
return currSum
```

5. Otherwise recursively explore children.

---

## Implementation (Java)

```java
class Solution {

    Map<Integer, Integer> map = new HashMap<>();

    public int pathSum(int[] nums) {

        for (int num : nums) {
            int key = num / 10;
            int value = num % 10;
            map.put(key, value);
        }

        return dfs(nums[0] / 10, 0);
    }

    private int dfs(int root, int preSum) {

        int level = root / 10;
        int pos = root % 10;

        int left = (level + 1) * 10 + pos * 2 - 1;
        int right = (level + 1) * 10 + pos * 2;

        int currSum = preSum + map.get(root);

        if (!map.containsKey(left) && !map.containsKey(right)) {
            return currSum;
        }

        int leftSum = map.containsKey(left) ? dfs(left, currSum) : 0;
        int rightSum = map.containsKey(right) ? dfs(right, currSum) : 0;

        return leftSum + rightSum;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(n)
```

Each node is visited once.

### Space Complexity

```
O(n)
```

- HashMap storage
- Recursion stack

---

# Approach 2 — Breadth First Search

## Intuition

BFS processes nodes **level by level**.

We maintain a queue containing:

```
(nodeCoordinates, currentPathSum)
```

Whenever we reach a **leaf node**, we add its path sum to the total.

---

## Algorithm

1. Build hashmap from input.
2. Initialize queue.
3. Insert root node.
4. While queue is not empty:

- Dequeue node
- Compute children
- If leaf → add path sum
- Otherwise enqueue children

---

## Implementation (Java)

```java
class Solution {

    public int pathSum(int[] nums) {

        Map<Integer, Integer> map = new HashMap<>();

        for (int element : nums) {
            int coordinates = element / 10;
            int value = element % 10;
            map.put(coordinates, value);
        }

        Queue<Pair<Integer, Integer>> q = new LinkedList<>();
        int totalSum = 0;

        int rootCoordinates = nums[0] / 10;

        q.add(new Pair<>(rootCoordinates, map.get(rootCoordinates)));

        while (!q.isEmpty()) {

            Pair<Integer, Integer> current = q.poll();

            int coordinates = current.getKey();
            int currentSum = current.getValue();

            int level = coordinates / 10;
            int position = coordinates % 10;

            int left = (level + 1) * 10 + position * 2 - 1;
            int right = (level + 1) * 10 + position * 2;

            if (!map.containsKey(left) && !map.containsKey(right)) {
                totalSum += currentSum;
            }

            if (map.containsKey(left)) {
                q.add(new Pair<>(left, currentSum + map.get(left)));
            }

            if (map.containsKey(right)) {
                q.add(new Pair<>(right, currentSum + map.get(right)));
            }
        }

        return totalSum;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(n)
```

Every node is processed exactly once.

### Space Complexity

```
O(n)
```

- HashMap storage
- Queue storage
