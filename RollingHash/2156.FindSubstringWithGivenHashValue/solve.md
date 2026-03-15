# 2156. Find Substring With Given Hash Value

## Problem Statement

For a string `s` of length `k`, the hash is defined as:

```text
hash(s, p, m) =
(val(s[0]) * p^0 + val(s[1]) * p^1 + ... + val(s[k - 1]) * p^(k - 1)) mod m
```

where:

- `val('a') = 1`
- `val('b') = 2`
- ...
- `val('z') = 26`

We are given:

- a string `s`
- integers `power`, `modulo`, `k`, and `hashValue`

We must return the **first substring of length `k`** whose hash equals `hashValue`.

The problem guarantees that at least one valid answer exists.

---

## Example 1

```text
Input:
s = "leetcode", power = 7, modulo = 20, k = 2, hashValue = 0

Output:
"ee"
```

Explanation:

```text
hash("ee", 7, 20)
= 5 * 7^0 + 5 * 7^1
= 5 + 35
= 40
40 mod 20 = 0
```

So `"ee"` is a valid substring of length `2`, and it is the first such substring.

---

## Example 2

```text
Input:
s = "fbxzaad", power = 31, modulo = 100, k = 3, hashValue = 32

Output:
"fbx"
```

Explanation:

```text
hash("fbx", 31, 100)
= 6 * 31^0 + 2 * 31^1 + 24 * 31^2
= 6 + 62 + 23064
= 23132
23132 mod 100 = 32
```

`"bxz"` also has hash `32`, but `"fbx"` appears earlier, so the answer is `"fbx"`.

---

# Core Difficulty

The hash is **directional**:

```text
val(s[0]) * p^0 + val(s[1]) * p^1 + ...
```

That means if we want to slide a length-`k` window from left to right in the usual way, the powers do not align naturally for easy removal.

The standard trick is:

- process the string **from right to left**
- maintain a rolling hash of the current window
- when a window hash matches `hashValue`, record its starting index
- keep going leftward so the last recorded index becomes the earliest valid substring

This is the key insight behind the optimal solution.

---

# Approach 1: Brute Force Recompute Hash for Every Substring

## Intuition

The most direct method is:

1. generate every substring of length `k`
2. compute its hash from scratch
3. return the first one that matches `hashValue`

This is simple but inefficient.

---

## Algorithm

For each start index `i` from `0` to `n - k`:

1. Compute:

```text
hash(s[i..i+k-1])
= val(s[i]) * p^0 + val(s[i+1]) * p^1 + ... + val(s[i+k-1]) * p^(k-1)
```

2. Take modulo at every step.
3. If hash matches `hashValue`, return this substring.

---

## Java Code

```java
class Solution {
    public String subStrHash(String s, int power, int modulo, int k, int hashValue) {
        int n = s.length();

        for (int start = 0; start <= n - k; start++) {
            long hash = 0;
            long pow = 1;

            for (int j = 0; j < k; j++) {
                int value = s.charAt(start + j) - 'a' + 1;
                hash = (hash + value * pow) % modulo;
                pow = (pow * power) % modulo;
            }

            if (hash == hashValue) {
                return s.substring(start, start + k);
            }
        }

        return "";
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O((n - k + 1) * k) = O(nk)
```

In the worst case, this is about `O(n^2)`.

### Space Complexity

```text
O(1)
```

excluding substring output.

---

## Verdict

This is fine for understanding, but too slow for the full constraints.

---

# Approach 2: Prefix Hash + Fast Substring Hash

## Intuition

A common thought is to use polynomial prefix hashing so substring hashes can be extracted in `O(1)`.

That works well for the usual form:

```text
s[0] * p^(k-1) + s[1] * p^(k-2) + ...
```

But here the problem defines the hash with powers increasing from **left to right** starting at `0`.

So while prefix hashes are possible, they are a little awkward because the substring hash must be normalized carefully.

This approach is workable, but the reverse rolling window is cleaner and more natural.

---

## Idea

We can define a prefix-style polynomial hash and derive each length-`k` substring hash using modular arithmetic.

However, because:

- `modulo` is not guaranteed to be prime
- modular inverses may not be available
- normalization becomes annoying

this is not the best practical path.

So this approach is more educational than recommended for this problem.

---

## Takeaway

Prefix hashing is conceptually related, but it is not the most elegant fit here.

The reverse rolling hash is the intended and superior solution.

---

# Approach 3: Rolling Hash from Right to Left

## Intuition

This is the optimal approach.

Suppose we want the hash of a substring:

```text
t = s[i..i+k-1]
```

Its hash is:

```text
val(t[0]) * p^0 + val(t[1]) * p^1 + ... + val(t[k-1]) * p^(k-1)
```

If we scan from right to left, we can maintain exactly this structure in a rolling way.

---

## Why Right-to-Left Works

Assume we currently know the hash of:

```text
s[i+1 .. i+k]
```

Now we want the hash of:

```text
s[i .. i+k-1]
```

If current hash is:

```text
H = val(s[i+1]) * p^0 + val(s[i+2]) * p^1 + ... + val(s[i+k]) * p^(k-1)
```

Then for the previous window we want:

```text
val(s[i]) * p^0 + val(s[i+1]) * p^1 + ... + val(s[i+k-1]) * p^(k-1)
```

We can update by:

1. multiplying current hash by `power`
2. adding the new left character
3. removing the contribution of the character that fell out on the right

That gives a natural rolling formula.

---

## Rolling Formula

Let `hash` represent the current length-`k` window as we move from right to left.

When extending one step left:

```text
hash = (hash * power + newChar - oldChar * power^k) mod modulo
```

More carefully in code:

```text
hash = (hash * power + value(leftChar)) % modulo
hash = (hash - value(removedChar) * power^k % modulo + modulo) % modulo
```

The extra `+ modulo` avoids negative results.

---

## Important Detail: Finding the First Substring

We scan from **right to left**.

Whenever current window hash equals `hashValue`, we store its start index.

Because we keep moving left, the **last stored index** will be the smallest one, which corresponds to the first substring in the string.

That matches the problem requirement exactly.

---

## Step-by-Step Example

### Example

```text
s = "leetcode"
power = 7
modulo = 20
k = 2
hashValue = 0
```

We scan from right to left, keeping a rolling hash for each length-2 window.

Eventually:

- `"de"`
- `"od"`
- `"co"`
- `"tc"`
- `"et"`
- `"ee"` ← hash becomes `0`
- `"le"`

When we see `"ee"`, we record its index.

If no earlier valid substring appears later in the reverse scan, that recorded index remains the answer.

---

# Optimal Algorithm

1. Precompute:

```text
power^k mod modulo
```

2. Initialize rolling hash to `0`.
3. Traverse `s` from `n - 1` down to `0`.
4. Add the current character to the rolling hash.
5. If window size exceeds `k`, remove the outgoing rightmost character.
6. Once the window size is exactly `k`, compare the hash with `hashValue`.
7. If equal, record the current index.
8. After traversal ends, return `s.substring(answerIndex, answerIndex + k)`.

---

## Java Code

```java
class Solution {
    public String subStrHash(String s, int power, int modulo, int k, int hashValue) {
        int n = s.length();
        long hash = 0;
        long powerK = 1;
        int answerIndex = 0;

        for (int i = 0; i < k; i++) {
            powerK = (powerK * power) % modulo;
        }

        for (int i = n - 1; i >= 0; i--) {
            int currentValue = s.charAt(i) - 'a' + 1;
            hash = (hash * power + currentValue) % modulo;

            if (i + k < n) {
                int outgoingValue = s.charAt(i + k) - 'a' + 1;
                hash = (hash - outgoingValue * powerK % modulo + modulo) % modulo;
            }

            if (i + k <= n && hash == hashValue) {
                answerIndex = i;
            }
        }

        return s.substring(answerIndex, answerIndex + k);
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n)
```

Each character enters and leaves the rolling window once.

### Space Complexity

```text
O(1)
```

Ignoring the returned substring.

---

## Verdict

This is the best solution:

- linear time
- constant extra space
- exact
- aligns directly with the hash definition

---

# Approach 4: Rolling Hash with Explicit Window Size Tracking

## Intuition

Some people find the compact optimal solution slightly magical.

A more verbose implementation keeps track of the current window length explicitly, which can be easier to reason about.

The logic is identical, but the code may feel more intuitive.

---

## Java Code

```java
class Solution {
    public String subStrHash(String s, int power, int modulo, int k, int hashValue) {
        int n = s.length();
        long hash = 0;
        long powerK = 1;
        int answerStart = 0;
        int windowSize = 0;

        for (int i = 0; i < k; i++) {
            powerK = (powerK * power) % modulo;
        }

        for (int i = n - 1; i >= 0; i--) {
            int added = s.charAt(i) - 'a' + 1;
            hash = (hash * power + added) % modulo;
            windowSize++;

            if (windowSize > k) {
                int removed = s.charAt(i + k) - 'a' + 1;
                hash = (hash - removed * powerK % modulo + modulo) % modulo;
                windowSize--;
            }

            if (windowSize == k && hash == hashValue) {
                answerStart = i;
            }
        }

        return s.substring(answerStart, answerStart + k);
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n)
```

### Space Complexity

```text
O(1)
```

---

# Why `powerK = power^k`, Not `power^(k-1)`?

This is a subtle point.

Suppose after multiplying by `power` and adding the new left character, the outgoing character has shifted to coefficient:

```text
p^k
```

So when removing that outgoing character, we subtract:

```text
outgoingValue * power^k
```

That is why the algorithm uses `power^k mod modulo`.

Many off-by-one bugs come from using `power^(k-1)` here.

---

# Formal Correctness Intuition

For a window `s[i..i+k-1]`, the maintained rolling hash is:

```text
val(s[i]) * p^0 + val(s[i+1]) * p^1 + ... + val(s[i+k-1]) * p^(k-1) mod modulo
```

When moving one step left to window `s[i-1..i+k-2]`:

1. all previous coefficients increase by one power after multiplying by `p`
2. the new left character gets coefficient `p^0`
3. the old rightmost character now incorrectly has coefficient `p^k`, so we subtract it

Thus the invariant is preserved for every window.

---

# Common Mistakes

## 1. Sliding Left to Right

This is the biggest trap.

The hash definition makes right-to-left rolling natural.
Trying to do it left-to-right usually becomes messy and error-prone.

---

## 2. Forgetting to Add `modulo` Before `% modulo`

In Java, `%` can produce negative values for negative inputs.

So always do:

```java
hash = (hash - value + modulo) % modulo;
```

or equivalently:

```java
hash = (hash - value % modulo + modulo) % modulo;
```

---

## 3. Using `int` for Intermediate Calculations

Since `power` and `modulo` can be up to `10^9`, intermediate products can overflow `int`.

Use `long` for:

- `hash`
- `powerK`
- multiplication expressions

---

## 4. Returning Immediately on First Match in Reverse Scan

If you return immediately while scanning from right to left, you will return the **rightmost** valid substring.

But the problem asks for the **first** valid substring, meaning the one with the smallest index.

So you must keep scanning and update the answer whenever a match is found.

---

## 5. Removing the Wrong Character

When the window start is at `i`, the removed character is at index:

```java
i + k
```

not `i + k - 1`.

That index is the character that belonged to the previous larger window and has now moved out.

---

# Dry Run

## Input

```text
s = "fbxzaad"
power = 31
modulo = 100
k = 3
hashValue = 32
```

We scan from right to left.

### Window `"aad"`

The rolling hash is built from the back.

### Window `"zaa"`

Update the hash.

### Window `"xza"`

Update the hash.

### Window `"bxz"`

Hash becomes `32`, so record its start.

### Window `"fbx"`

Hash also becomes `32`, so record this earlier start.

Since this index is smaller, it becomes the final answer.

Return:

```text
"fbx"
```

---

# Interview Framing

If asked in an interview, the clean thought process is:

1. The hash weights start from the left character with exponent `0`.
2. That makes ordinary left-to-right rolling awkward.
3. Reverse the traversal direction.
4. Maintain a rolling hash for windows of length `k`.
5. Record every match and return the leftmost one.

That demonstrates both pattern recognition and careful handling of rolling hash directionality.

---

# Final Recommended Solution

```java
class Solution {
    public String subStrHash(String s, int power, int modulo, int k, int hashValue) {
        int n = s.length();
        long currentHash = 0;
        long powerK = 1;
        int resultIndex = 0;

        for (int i = 0; i < k; i++) {
            powerK = (powerK * power) % modulo;
        }

        for (int i = n - 1; i >= 0; i--) {
            int currentCharValue = s.charAt(i) - 'a' + 1;
            currentHash = (currentHash * power + currentCharValue) % modulo;

            if (i + k < n) {
                int removedCharValue = s.charAt(i + k) - 'a' + 1;
                currentHash = (currentHash - removedCharValue * powerK % modulo + modulo) % modulo;
            }

            if (i + k <= n && currentHash == hashValue) {
                resultIndex = i;
            }
        }

        return s.substring(resultIndex, resultIndex + k);
    }
}
```

---

# Final Complexity

## Time Complexity

```text
O(n)
```

## Space Complexity

```text
O(1)
```

---

# Summary

This problem looks like a normal rolling hash problem, but the hash direction changes everything.

The decisive observation is:

- the substring hash is defined with powers increasing from left to right
- that makes **right-to-left sliding** the clean solution

So the optimal approach is a **reverse rolling hash** with window length `k`.

That gives:

- exact correctness
- linear performance
- constant extra space
