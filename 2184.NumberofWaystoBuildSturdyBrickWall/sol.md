# Sturdy Brick Wall Problem — Detailed Explanation

## Problem Statement

You are given:

- `height` → number of rows in the wall
- `width` → width of the wall
- `bricks[]` → array of brick widths (each brick has height 1)

You have **infinite supply** of each brick type.

### Rules

1. Each row must sum exactly to **`width`**.
2. Bricks **cannot be rotated**.
3. A **sturdy wall** requires that **no vertical cracks align between adjacent rows**, except at the wall boundaries.
4. Return the number of ways to build such a wall.
5. Since the answer may be large, return it **modulo `10^9 + 7`**.

---

# Key Insight

Instead of building the entire wall directly, we analyze the problem **row by row**.

Each row creates **internal vertical crack positions** where bricks meet.

Example:

```
width = 6
row = [2,1,3]
```

Brick layout:

```
|--2--|--1--|----3----|
```

Internal joins occur at:

```
2, 3
```

These crack positions must **not match** the cracks of adjacent rows.

---

# Row Representation

We represent each row by a **bitmask of crack positions**.

Example:

```
width = 6
cracks at positions 2 and 3
```

Mask:

```
001100
```

Bit `i` indicates a crack at position `i`.

Wall boundaries (`0` and `width`) are ignored.

---

# Step 1 — Generate All Valid Row Patterns

We generate all sequences of bricks whose total width equals `width`.

For every brick placed:

```
next_position = current_position + brick_width
```

If the brick does **not reach the wall end**, mark a crack in the mask.

Example:

```
width = 4
bricks = [1,2]
```

Possible rows:

```
1+1+1+1  -> cracks {1,2,3}
1+1+2    -> cracks {1,2}
1+2+1    -> cracks {1,3}
2+1+1    -> cracks {2,3}
2+2      -> cracks {2}
```

Each becomes a bitmask pattern.

---

# Step 2 — Compute Compatible Rows

Two rows are compatible if they **share no crack positions**.

Condition:

```
(maskA & maskB) == 0
```

Example:

```
Row A cracks: {1,3}
Row B cracks: {2}
```

Compatible.

But

```
Row A cracks: {1,3}
Row B cracks: {1}
```

Not compatible.

We build a **compatibility graph** between row patterns.

---

# Step 3 — Dynamic Programming on Height

Let:

```
patterns = list of all row masks
R = number of patterns
```

Define:

```
dp[i] = number of ways to build wall ending with row pattern i
```

### Base Case

Height = 1

```
dp[i] = 1 for all patterns
```

### Transition

For each new row:

```
dp_next[i] += dp[j]
```

Where `pattern[i]` is compatible with `pattern[j]`.

Repeat this for all rows up to `height`.

---

# Final Answer

After processing `height` rows:

```
answer = sum(dp[i]) % MOD
```

---

# Java Implementation

```java
import java.util.*;

class Solution {

    static final int MOD = 1_000_000_007;

    public int buildWall(int height, int width, int[] bricks) {

        List<Integer> patterns = new ArrayList<>();
        generatePatterns(width, bricks, 0, 0, patterns);

        int m = patterns.size();

        List<Integer>[] compat = new ArrayList[m];

        for(int i=0;i<m;i++)
            compat[i] = new ArrayList<>();

        for(int i=0;i<m;i++)
        {
            for(int j=0;j<m;j++)
            {
                if((patterns.get(i) & patterns.get(j)) == 0)
                    compat[i].add(j);
            }
        }

        long[] dp = new long[m];
        Arrays.fill(dp,1);

        for(int h=2;h<=height;h++)
        {
            long[] next = new long[m];

            for(int i=0;i<m;i++)
            {
                long ways = 0;

                for(int j : compat[i])
                {
                    ways = (ways + dp[j]) % MOD;
                }

                next[i] = ways;
            }

            dp = next;
        }

        long ans = 0;

        for(long v : dp)
            ans = (ans + v) % MOD;

        return (int)ans;
    }

    private void generatePatterns(int width,int[] bricks,int pos,int mask,List<Integer> patterns)
    {
        if(pos == width)
        {
            patterns.add(mask);
            return;
        }

        for(int brick : bricks)
        {
            int next = pos + brick;

            if(next > width) continue;

            int nextMask = mask;

            if(next < width)
                nextMask |= (1 << next);

            generatePatterns(width,bricks,next,nextMask,patterns);
        }
    }
}
```

---

# Complexity Analysis

Let:

```
R = number of valid row patterns
```

### Pattern Generation

```
O(R)
```

### Compatibility Graph

```
O(R^2)
```

### DP over Wall Height

```
O(height * R^2)
```

Using adjacency lists:

```
O(height * edges)
```

### Space Complexity

```
O(R^2) for compatibility
O(R) for DP
```

---

# Example

```
height = 2
width = 3
bricks = [1,2]
```

Valid rows:

```
1+1+1 -> cracks {1,2}
1+2   -> cracks {1}
2+1   -> cracks {2}
```

Compatibility:

```
{1} <-> {2}
```

Valid walls:

```
Row1: {1}
Row2: {2}

Row1: {2}
Row2: {1}
```

Answer:

```
2
```

---

# Summary

Key reductions:

1. Convert row layouts into **crack masks**
2. Precompute **row compatibility**
3. Use **DP across height**

This transforms a structural wall-building problem into a **graph DP problem over row states**.
