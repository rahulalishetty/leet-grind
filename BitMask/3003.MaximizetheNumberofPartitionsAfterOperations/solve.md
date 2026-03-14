# 3003. Maximize the Number of Partitions After Operations

## Approach: Bitwise Operations + Preprocessing + Enumeration

### Intuition

From the problem statement, we know that we are allowed to **modify at most one character** in the string.

First, consider the case where **no modification is made**. We can traverse the string once and determine:

- how many partitions are formed
- the interval boundaries of each partition

This partitioning process always selects the **longest prefix containing at most `k` distinct characters**.

---

## Key Observation

Suppose we **modify the character at position `i`**.

Let the segment containing index `i` (in the original partitioning) be the **t-th segment**.

Important properties:

- All segments **before segment `t` remain unchanged**.
- All segments **after segment `t` also remain unchanged**.

This works because:

- The string splitting process is **deterministic**
- Splitting **from start → end** gives the same number of segments as **end → start**.

Therefore modifying index `i` only affects the **local region around that index**.

---

# Conceptual Division Around Index `i`

Treat position `i` as a boundary.

### Left Half

Substring:

```
0 → i-1
```

Partitioned **from start to end**.

Definitions:

- **Left Split** → the last segment touching position `i`
- **Prefix Split** → all segments before the left split

---

### Right Half

Substring:

```
i+1 → n-1
```

Partitioned **from end to start**.

Definitions:

- **Right Split** → the last segment touching position `i` when traversing backward
- **Suffix Split** → all segments after the right split

---

# Effect of Modifying Character `s[i]`

The modified character interacts only with:

- the **left split**
- the **right split**

This leads to **three possible cases**.

---

## Case 1 — All Three Merge

If the union of:

```
left split + right split + modified character
```

contains **≤ k distinct characters**, then they merge into **one segment**.

Contribution:

```
+1 partition
```

---

## Case 2 — Fully Split into Three Segments

If:

```
left distinct count = k
right distinct count = k
```

and together they contain **≤ 25 characters**, we can change `s[i]` to a character **not present in either side**.

This forces:

```
left split | new char | right split
```

into **three separate partitions**.

Contribution:

```
+3 partitions
```

---

## Case 3 — Default Case

All remaining situations contribute:

```
+2 partitions
```

---

# Preprocessing

To efficiently compute results for every index `i`, we precompute information about **left splits** and **right splits**.

We store:

```
left[i]  → information about split ending near i
right[i] → information about split starting near i
```

Each entry contains:

```
[0] number of prefix/suffix partitions
[1] character bitmask of the split
[2] number of distinct characters
```

---

# Bitmask Representation

Characters are represented using a **26-bit mask**.

Example:

```
a -> 1 << 0
b -> 1 << 1
c -> 1 << 2
...
```

Union of character sets:

```
maskA | maskB
```

Distinct character count:

```
Integer.bitCount(mask)
```

This allows **constant-time set operations**.

---

# Algorithm

### Step 1 — Compute Left Splits

Traverse the string from **left → right**.

Track:

- current mask
- distinct count
- number of segments

Whenever distinct characters exceed `k`, start a **new segment**.

Store results in `left[i]`.

---

### Step 2 — Compute Right Splits

Traverse the string from **right → left** using the same logic.

Store results in `right[i]`.

---

### Step 3 — Enumerate Modification Position

For every index `i`:

1. Combine prefix and suffix partition counts.
2. Evaluate the three cases.
3. Update the maximum partition count.

---

# Java Implementation

```java
public class Solution {

    public int maxPartitionsAfterOperations(String s, int k) {
        int n = s.length();
        int[][] left = new int[n][3];
        int[][] right = new int[n][3];

        int num = 0;
        int mask = 0;
        int count = 0;

        for (int i = 0; i < n - 1; i++) {
            int binary = 1 << (s.charAt(i) - 'a');

            if ((mask & binary) == 0) {
                count++;

                if (count <= k) {
                    mask |= binary;
                } else {
                    num++;
                    mask = binary;
                    count = 1;
                }
            }

            left[i + 1][0] = num;
            left[i + 1][1] = mask;
            left[i + 1][2] = count;
        }

        num = 0;
        mask = 0;
        count = 0;

        for (int i = n - 1; i > 0; i--) {
            int binary = 1 << (s.charAt(i) - 'a');

            if ((mask & binary) == 0) {
                count++;

                if (count <= k) {
                    mask |= binary;
                } else {
                    num++;
                    mask = binary;
                    count = 1;
                }
            }

            right[i - 1][0] = num;
            right[i - 1][1] = mask;
            right[i - 1][2] = count;
        }

        int maxVal = 0;

        for (int i = 0; i < n; i++) {
            int seg = left[i][0] + right[i][0] + 2;

            int totMask = left[i][1] | right[i][1];
            int totCount = Integer.bitCount(totMask);

            if (left[i][2] == k && right[i][2] == k && totCount < 26) {
                seg++;
            } else if (Math.min(totCount + 1, 26) <= k) {
                seg--;
            }

            maxVal = Math.max(maxVal, seg);
        }

        return maxVal;
    }
}
```

---

# Complexity Analysis

Let:

```
n = length of string
M = 26 (alphabet size)
```

### Time Complexity

```
O(M × n)
```

- Left preprocessing → `O(n)`
- Right preprocessing → `O(n)`
- Enumeration → `O(n)`
- Bit operations are constant.

---

### Space Complexity

```
O(n)
```

We maintain two arrays:

```
left[n][3]
right[n][3]
```

which store partition metadata.

---

# Key Idea

The key trick is realizing that **changing one character only affects its adjacent partitions**.

By precomputing prefix and suffix partition information using **bitmasks**, we can evaluate every possible modification in **constant time**.
