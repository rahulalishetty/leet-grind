# 1461. Check If a String Contains All Binary Codes of Size K — Approaches

## Overview

We introduce two different approaches to solve this problem:

1. **Using a Set**
2. **Using Rolling Hash (Bit Manipulation)**

The second approach improves performance by avoiding substring creation and using bitwise operations.

---

# Approach 1: Using a Set

## Problem Reminder

Return **true** if **every binary code of length `k`** appears as a substring of `s`. Otherwise return **false**.

Since each position can be either:

```
0 or 1
```

The total number of binary codes of length `k` is:

```
2^k
```

In Java we compute this as:

```
1 << k
```

We iterate through every substring of length `k` and store it in a **HashSet** to avoid duplicates.

When the number of unique substrings reaches `2^k`, we know all codes exist.

---

## Java Implementation

```java
class Solution {
    public boolean hasAllCodes(String s, int k) {
        int need = 1 << k;
        Set<String> got = new HashSet<>();

        for (int i = k; i <= s.length(); i++) {
            String sub = s.substring(i - k, i);

            if (!got.contains(sub)) {
                got.add(sub);
                need--;

                if (need == 0) {
                    return true;
                }
            }
        }

        return false;
    }
}
```

---

## Python Short Version

```python
class Solution:
    def hasAllCodes(self, s: str, k: int) -> bool:
        got = {s[i - k : i] for i in range(k, len(s) + 1)}
        return len(got) == 1 << k
```

---

## Complexity Analysis

Let `N` be the length of `s`.

### Time Complexity

```
O(N * K)
```

We iterate through the string and each substring creation takes `O(K)`.

### Space Complexity

```
O(N * K)
```

The set may store up to `N` substrings of length `K`.

---

# Approach 2: Rolling Hash with Bit Manipulation

The first approach creates substrings repeatedly, which is expensive.

Instead we compute a **rolling hash** for each substring of length `k`.

---

## Key Insight

Every binary string of length `k` can be interpreted as a **binary number**.

Example:

```
"110" -> 6
"101" -> 5
```

All binary numbers of length `k` lie in the range:

```
0 → 2^k - 1
```

So we can use an array of size:

```
2^k
```

to track which binary codes appear.

---

## Rolling Hash Idea

Suppose we know the hash of:

```
"110"
```

Now we want the hash of the next substring:

```
"101"
```

Steps:

1. Shift left

```
110 << 1 = 1100
```

2. Remove the leftmost bit using mask

```
1100 & 111 = 100
```

3. Add the new bit

```
100 | 1 = 101
```

Formula:

```
newHash = ((oldHash << 1) & mask) | newBit
```

Where:

```
mask = (1 << k) - 1
```

This keeps only the last `k` bits.

---

## Java Implementation

```java
class Solution {
    public boolean hasAllCodes(String s, int k) {

        int need = 1 << k;
        boolean[] seen = new boolean[need];

        int mask = need - 1;
        int hash = 0;

        for (int i = 0; i < s.length(); i++) {

            hash = ((hash << 1) & mask) | (s.charAt(i) - '0');

            if (i >= k - 1 && !seen[hash]) {
                seen[hash] = true;
                need--;

                if (need == 0) {
                    return true;
                }
            }
        }

        return false;
    }
}
```

---

## Complexity Analysis

Let `N` be the length of `s`.

### Time Complexity

```
O(N)
```

Each substring hash is computed in constant time using bitwise operations.

### Space Complexity

```
O(2^k)
```

We store all possible binary codes of length `k`.

---

# Comparison of Approaches

| Approach     | Time Complexity | Space Complexity | Notes            |
| ------------ | --------------- | ---------------- | ---------------- |
| Set          | O(NK)           | O(NK)            | Easy but slower  |
| Rolling Hash | O(N)            | O(2^k)           | Optimal solution |

---

# Key Takeaway

When the input string consists of **binary characters**, we can treat substrings as **binary numbers** and use **bit manipulation**.

This allows:

- constant time substring hashing
- no substring creation
- optimal performance

Rolling hash with bitwise operations is the **best solution** for this problem.
