# 1492. The kth Factor of n — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int kthFactor(int n, int k) {

    }
}
```

---

# Problem Restatement

We are given two positive integers:

- `n`
- `k`

A factor of `n` is an integer `i` such that:

```text
n % i == 0
```

If we sort all factors of `n` in ascending order, we need to return the `k`th factor.

If `n` has fewer than `k` factors, return:

```text
-1
```

---

# Core Idea

The straightforward way is to test every integer from `1` to `n`.

Whenever `i` divides `n`, it is a factor.

That works, but the follow-up asks whether we can do better than `O(n)`.

The key observation is:

> Factors come in pairs.

If `i` divides `n`, then:

```text
n / i
```

is also a factor.

For example, for `n = 12`:

```text
1 ↔ 12
2 ↔ 6
3 ↔ 4
```

So we only need to search up to:

```text
sqrt(n)
```

That reduces the work significantly.

---

# Approach 1 — Brute Force Scan from 1 to n

## Idea

Check every integer `i` from `1` to `n`.

Each time `i` is a factor, decrement `k`.

When `k` becomes `0`, return `i`.

If the loop ends first, return `-1`.

---

## Java Code

```java
class Solution {
    public int kthFactor(int n, int k) {
        for (int i = 1; i <= n; i++) {
            if (n % i == 0) {
                k--;
                if (k == 0) {
                    return i;
                }
            }
        }
        return -1;
    }
}
```

---

## Complexity

```text
Time:  O(n)
Space: O(1)
```

This is simple and correct, but not optimal.

---

# Approach 2 — Factor Pair Method Using sqrt(n) (Recommended)

## Idea

Instead of scanning all the way to `n`, scan only from:

```text
1 to sqrt(n)
```

Whenever `i` divides `n`, there are potentially two factors:

- `i`
- `n / i`

The smaller factors appear first in ascending order.
The larger paired factors appear later.

So we can do this in two phases:

### Phase 1

Collect the smaller factors in ascending order while scanning upward.

If the `k`th factor is among them, return immediately.

### Phase 2

If not found yet, traverse the paired larger factors in reverse order.

That produces the remaining factors in ascending order overall.

Be careful with perfect squares, because when:

```text
i * i == n
```

the pair is the same number and must not be counted twice.

---

## Why this works

If `i` is a factor and `i <= sqrt(n)`, then `n / i >= sqrt(n)`.

So scanning upward gives the left half of the sorted factors, and reversing the stored values gives the right half in ascending order.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int kthFactor(int n, int k) {
        List<Integer> large = new ArrayList<>();

        for (int i = 1; i * i <= n; i++) {
            if (n % i == 0) {
                k--;
                if (k == 0) {
                    return i;
                }

                if (i != n / i) {
                    large.add(n / i);
                }
            }
        }

        for (int i = large.size() - 1; i >= 0; i--) {
            k--;
            if (k == 0) {
                return large.get(i);
            }
        }

        return -1;
    }
}
```

---

## Complexity

```text
Time:  O(sqrt(n))
Space: O(sqrt(n))
```

This is the best practical solution for this problem.

---

# Approach 3 — Two-Pass sqrt(n) Without Storing All Large Factors

## Idea

We can avoid extra storage.

### First pass

Count small factors up to `sqrt(n)`.

### Second pass

Walk backward from `sqrt(n)` to `1` and use the paired factors.

Again, skip the duplicated middle factor when `n` is a perfect square.

This saves the extra list, though the code becomes slightly less direct.

---

## Java Code

```java
class Solution {
    public int kthFactor(int n, int k) {
        int root = (int) Math.sqrt(n);

        for (int i = 1; i <= root; i++) {
            if (n % i == 0) {
                k--;
                if (k == 0) {
                    return i;
                }
            }
        }

        for (int i = root; i >= 1; i--) {
            if (n % i == 0) {
                int other = n / i;

                if (other == i) {
                    continue;
                }

                k--;
                if (k == 0) {
                    return other;
                }
            }
        }

        return -1;
    }
}
```

---

## Complexity

```text
Time:  O(sqrt(n))
Space: O(1)
```

This is slightly more space-efficient than Approach 2.

---

# Approach 4 — Generate All Factors, Sort, Then Pick

## Idea

Collect all factors, sort them, and return the `k`th if it exists.

This is valid, but sorting is unnecessary because factor pairs already give order more efficiently.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int kthFactor(int n, int k) {
        List<Integer> factors = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            if (n % i == 0) {
                factors.add(i);
            }
        }

        if (k > factors.size()) {
            return -1;
        }

        return factors.get(k - 1);
    }
}
```

---

## Complexity

```text
Time:  O(n)
Space: O(number of factors)
```

Correct, but weaker than the sqrt-based methods.

---

# Detailed Walkthrough

## Example 1

```text
n = 12, k = 3
```

Factors of `12` are:

```text
1, 2, 3, 4, 6, 12
```

The 3rd factor is:

```text
3
```

Using the sqrt approach:

- `i = 1` → factor → k becomes 2
- `i = 2` → factor → k becomes 1
- `i = 3` → factor → k becomes 0 → return `3`

---

## Example 2

```text
n = 7, k = 2
```

Factors are:

```text
1, 7
```

The 2nd factor is:

```text
7
```

Using factor pairs:

- small side gives `1`
- large side gives `7`

So answer is `7`.

---

## Example 3

```text
n = 4, k = 4
```

Factors are:

```text
1, 2, 4
```

There are only 3 factors.

So the answer is:

```text
-1
```

---

# Important Edge Case: Perfect Squares

Suppose:

```text
n = 16
```

Then factors include:

```text
1, 2, 4, 8, 16
```

Notice that `4` pairs with itself because:

```text
4 * 4 = 16
```

So when using factor pairs, do not count `4` twice.

That is why we check:

```java
if (i != n / i)
```

before adding the paired large factor.

---

# Common Pitfalls

## 1. Off-by-one in k

The problem asks for the `k`th factor in 1-based indexing.

So when using a list:

```java
return list.get(k - 1);
```

---

## 2. Double-counting the square root

For perfect squares, the middle factor appears only once.

---

## 3. Sorting unnecessarily

When using the sqrt method, you can produce the factor order without sorting.

---

## 4. Using O(n) when the follow-up wants better

The brute-force approach is acceptable for correctness, but the sqrt approach is the intended optimization.

---

# Best Approach

## Recommended: Factor pair scan up to sqrt(n)

This is the best balance of:

- simplicity
- correctness
- efficiency

It improves time complexity from:

```text
O(n)
```

to:

```text
O(sqrt(n))
```

which directly answers the follow-up.

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    public int kthFactor(int n, int k) {
        List<Integer> large = new ArrayList<>();

        for (int i = 1; i * i <= n; i++) {
            if (n % i == 0) {
                k--;
                if (k == 0) {
                    return i;
                }

                if (i != n / i) {
                    large.add(n / i);
                }
            }
        }

        for (int i = large.size() - 1; i >= 0; i--) {
            k--;
            if (k == 0) {
                return large.get(i);
            }
        }

        return -1;
    }
}
```

---

# Complexity Summary

```text
Time:  O(sqrt(n))
Space: O(sqrt(n))
```

---

# Final Takeaway

The whole problem becomes easy once you use the fact that divisors come in pairs.

Instead of checking every number up to `n`, check only up to `sqrt(n)`, and reconstruct the full sorted factor list from:

- the small factors directly
- the paired large factors in reverse order
