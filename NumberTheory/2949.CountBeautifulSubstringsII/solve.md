# 2949. Count Beautiful Substrings II — Java Summary

## Method Signature

For this problem, the correct Java method signature is:

```java
class Solution {
    public long beautifulSubstrings(String s, int k) {

    }
}
```

The pasted signature

```java
public long countPaths(int n, int[][] edges)
```

belongs to a different tree problem and is not the correct signature for 2949.

---

# Problem Restatement

We are given:

- a string `s`
- an integer `k`

A substring is **beautiful** if:

1. the number of vowels equals the number of consonants
2. `(vowels * consonants) % k == 0`

We need to count all non-empty beautiful substrings.

---

# Core Observation

If a substring is beautiful, then:

```text
vowels = consonants = x
```

So its length is:

```text
2x
```

And the divisibility condition becomes:

```text
x * x % k == 0
```

So for a substring to be beautiful:

- its vowel/consonant balance must be zero
- its half-length `x` must satisfy `x² % k == 0`

This is the key reduction.

---

# Prefix Balance Reformulation

Define a prefix balance:

- `+1` for a vowel
- `-1` for a consonant

Let:

```text
pref[i] = balance of first i characters
```

Then a substring `s[l..r]` has equal vowels and consonants iff:

```text
pref[r + 1] = pref[l]
```

So condition 1 becomes a standard prefix-equality condition.

Now let substring length be:

```text
len = (r - l + 1)
```

Since equal vowels and consonants implies even length:

```text
len = 2x
```

Then:

```text
x = len / 2
```

We need:

```text
(len / 2)^2 % k == 0
```

---

# Important Number Theory Reduction

We want:

```text
x^2 % k == 0
```

Let `L` be the smallest positive integer such that:

```text
L^2 % k == 0
```

Then `x^2 % k == 0` iff:

```text
x % L == 0
```

So the substring condition becomes:

- equal balance
- half-length divisible by `L`

Equivalently:

```text
len % (2L) == 0
```

This lets us convert the problem into counting prefix pairs with:

1. same balance
2. indices congruent modulo `2L`

---

# How to Compute L

Factorize:

```text
k = p1^a1 * p2^a2 * ... * pm^am
```

For `x^2` to be divisible by `k`, `x` must contain at least:

```text
ceil(a1/2), ceil(a2/2), ..., ceil(am/2)
```

copies of those primes.

So:

```text
L = p1^ceil(a1/2) * p2^ceil(a2/2) * ... * pm^ceil(am/2)
```

Then a substring is beautiful iff:

- prefix balances are equal
- its length is divisible by:

```text
modLen = 2 * L
```

---

# Approach 1 — Prefix Balance + HashMap by (balance, index mod modLen) (Recommended)

## Idea

We scan prefix positions `i = 0..n`.

For each prefix index `i`, define:

- `balance = pref[i]`
- `rem = i % modLen`

A substring between prefix positions `j` and `i` is beautiful iff:

- `pref[i] == pref[j]`
- `(i - j) % modLen == 0`

That means:

- same balance
- same remainder modulo `modLen`

So we count how many earlier prefixes had the same pair:

```text
(balance, remainder)
```

This is the cleanest solution.

---

## Java Code

```java
import java.util.*;

class Solution {
    public long beautifulSubstrings(String s, int k) {
        int need = minHalfLength(k);
        int modLen = 2 * need;

        Map<Long, Long> freq = new HashMap<>();
        long ans = 0;

        int balance = 0;

        // prefix index 0
        long key0 = encode(balance, 0);
        freq.put(key0, 1L);

        for (int i = 1; i <= s.length(); i++) {
            char ch = s.charAt(i - 1);
            if (isVowel(ch)) {
                balance++;
            } else {
                balance--;
            }

            int rem = i % modLen;
            long key = encode(balance, rem);

            ans += freq.getOrDefault(key, 0L);
            freq.put(key, freq.getOrDefault(key, 0L) + 1);
        }

        return ans;
    }

    private boolean isVowel(char ch) {
        return ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u';
    }

    private int minHalfLength(int k) {
        int x = k;
        int res = 1;

        for (int p = 2; p * p <= x; p++) {
            if (x % p != 0) continue;

            int cnt = 0;
            while (x % p == 0) {
                x /= p;
                cnt++;
            }

            for (int i = 0; i < (cnt + 1) / 2; i++) {
                res *= p;
            }
        }

        if (x > 1) {
            res *= x;
        }

        return res;
    }

    private long encode(int balance, int rem) {
        // shift balance to avoid collisions
        return (((long) balance) << 32) ^ (rem & 0xffffffffL);
    }
}
```

---

## Why this works

For a substring from `l` to `r`, let:

```text
j = l
i = r + 1
```

Then:

- equal vowels and consonants means `pref[i] == pref[j]`
- length divisibility means `(i - j) % modLen == 0`
- which means `i % modLen == j % modLen`

So every beautiful substring corresponds exactly to a pair of equal states:

```text
(balance, prefixIndex mod modLen)
```

That is why the hash map counting is correct.

---

## Complexity

Let `n = s.length()`.

- computing `L`: `O(sqrt(k))`
- scanning string: `O(n)`

So:

```text
Time:  O(n + sqrt(k))
Space: O(n)
```

This is efficient for `n <= 5 * 10^4`.

---

# Approach 2 — Group Prefix Indices by Balance, Then Count Matching Mod Classes

## Idea

Another way to see the same logic:

1. build all prefix balances
2. group prefix indices by balance
3. within each balance group, count how many indices have the same remainder modulo `2L`
4. for each remainder class with count `c`, add:

```text
c * (c - 1) / 2
```

This is mathematically equivalent to Approach 1, just done in two phases instead of online counting.

---

## Java Code

```java
import java.util.*;

class Solution {
    public long beautifulSubstrings(String s, int k) {
        int need = minHalfLength(k);
        int modLen = 2 * need;

        Map<Integer, List<Integer>> groups = new HashMap<>();
        int balance = 0;

        groups.computeIfAbsent(0, z -> new ArrayList<>()).add(0);

        for (int i = 1; i <= s.length(); i++) {
            char ch = s.charAt(i - 1);
            if (isVowel(ch)) {
                balance++;
            } else {
                balance--;
            }
            groups.computeIfAbsent(balance, z -> new ArrayList<>()).add(i);
        }

        long ans = 0;

        for (List<Integer> list : groups.values()) {
            Map<Integer, Integer> remFreq = new HashMap<>();

            for (int idx : list) {
                int rem = idx % modLen;
                remFreq.put(rem, remFreq.getOrDefault(rem, 0) + 1);
            }

            for (int c : remFreq.values()) {
                ans += (long) c * (c - 1) / 2;
            }
        }

        return ans;
    }

    private boolean isVowel(char ch) {
        return ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u';
    }

    private int minHalfLength(int k) {
        int x = k;
        int res = 1;

        for (int p = 2; p * p <= x; p++) {
            if (x % p != 0) continue;

            int cnt = 0;
            while (x % p == 0) {
                x /= p;
                cnt++;
            }

            for (int i = 0; i < (cnt + 1) / 2; i++) {
                res *= p;
            }
        }

        if (x > 1) {
            res *= x;
        }

        return res;
    }
}
```

---

## Complexity

Still:

```text
Time:  O(n + sqrt(k))
Space: O(n)
```

This is also good, though the online hash-counting version is more direct.

---

# Approach 3 — Brute Force With Prefix Vowel Counts (Too Slow)

## Idea

We can precompute prefix vowel counts, then try every substring:

- get vowels and consonants in `O(1)`
- test both conditions

This works logically, but not for the constraints.

---

## Java Code

```java
class Solution {
    public long beautifulSubstrings(String s, int k) {
        int n = s.length();
        int[] prefVowel = new int[n + 1];

        for (int i = 0; i < n; i++) {
            prefVowel[i + 1] = prefVowel[i] + (isVowel(s.charAt(i)) ? 1 : 0);
        }

        long ans = 0;

        for (int l = 0; l < n; l++) {
            for (int r = l; r < n; r++) {
                int len = r - l + 1;
                int vowels = prefVowel[r + 1] - prefVowel[l];
                int consonants = len - vowels;

                if (vowels == consonants && ((long) vowels * consonants) % k == 0) {
                    ans++;
                }
            }
        }

        return ans;
    }

    private boolean isVowel(char ch) {
        return ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u';
    }
}
```

---

## Why it fails

This takes:

```text
O(n^2)
```

which is too slow for:

```text
n <= 5 * 10^4
```

So this is only useful for intuition.

---

# Detailed Walkthrough

## Example 2

```text
s = "abba"
k = 1
```

Since `k = 1`, we need:

```text
x^2 % 1 == 0
```

which is always true.

So only the equality condition matters:

```text
vowels == consonants
```

Build prefix balance (`+1` vowel, `-1` consonant):

- prefix 0: `0`
- after `a`: `1`
- after `b`: `0`
- after `b`: `-1`
- after `a`: `0`

So balances are:

```text
[0, 1, 0, -1, 0]
```

Now equal-balance pairs are:

- `(0,2)` -> substring `"ab"`
- `(2,4)` -> substring `"ba"`
- `(0,4)` -> substring `"abba"`

Total:

```text
3
```

which matches the example.

---

# Why the Modulus Is `2L`, Not `L`

Let a beautiful substring have:

```text
vowels = consonants = x
```

Then substring length is:

```text
2x
```

And the divisibility condition depends on `x`.

If `x` must be divisible by `L`, then length must be divisible by:

```text
2L
```

Since substring length is a difference of prefix indices, we need:

```text
(i - j) % (2L) == 0
```

That is why we track prefix indices modulo `2L`.

---

# Important Correctness Argument

A substring is beautiful iff:

1. equal vowels and consonants
   `=>` prefix balances are equal
2. `(vowels * consonants) % k == 0`

Since equal counts imply:

```text
vowels = consonants = x
```

condition 2 becomes:

```text
x^2 % k == 0
```

By number theory, this is equivalent to:

```text
x % L == 0
```

for the minimal `L` derived from the prime factorization of `k`.

Since substring length is `2x`, that becomes:

```text
length % (2L) == 0
```

So beautiful substrings correspond exactly to prefix pairs with:

- equal balance
- equal prefix index modulo `2L`

That proves the algorithm.

---

# Common Pitfalls

## 1. Using the wrong method signature

For this problem, the correct signature is:

```java
public long beautifulSubstrings(String s, int k)
```

not `countPaths`.

---

## 2. Forgetting that equal vowels/consonants implies even length

Beautiful substrings must always have even length.

---

## 3. Testing `(vowels * consonants) % k == 0` directly for every substring

That leads to quadratic work.

The number theory reduction is essential.

---

## 4. Missing the `x^2 % k == 0` transformation

Once `vowels == consonants == x`, this simplification is what makes the problem manageable.

---

# Best Approach

## Recommended: Prefix balance + hash map on `(balance, index mod 2L)`

This is the cleanest and fastest solution.

It combines:

- prefix sums / balance trick
- number theory reduction of the divisibility condition
- hash counting of equal states

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    public long beautifulSubstrings(String s, int k) {
        int need = minHalfLength(k);
        int modLen = 2 * need;

        Map<Long, Long> freq = new HashMap<>();
        long ans = 0;

        int balance = 0;

        long key0 = encode(balance, 0);
        freq.put(key0, 1L);

        for (int i = 1; i <= s.length(); i++) {
            char ch = s.charAt(i - 1);
            if (isVowel(ch)) {
                balance++;
            } else {
                balance--;
            }

            int rem = i % modLen;
            long key = encode(balance, rem);

            ans += freq.getOrDefault(key, 0L);
            freq.put(key, freq.getOrDefault(key, 0L) + 1);
        }

        return ans;
    }

    private boolean isVowel(char ch) {
        return ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u';
    }

    private int minHalfLength(int k) {
        int x = k;
        int res = 1;

        for (int p = 2; p * p <= x; p++) {
            if (x % p != 0) continue;

            int cnt = 0;
            while (x % p == 0) {
                x /= p;
                cnt++;
            }

            for (int i = 0; i < (cnt + 1) / 2; i++) {
                res *= p;
            }
        }

        if (x > 1) {
            res *= x;
        }

        return res;
    }

    private long encode(int balance, int rem) {
        return (((long) balance) << 32) ^ (rem & 0xffffffffL);
    }
}
```

---

# Complexity Summary

```text
Time:  O(n + sqrt(k))
Space: O(n)
```

This fits comfortably for:

```text
s.length <= 5 * 10^4
k <= 1000
```

---

# Final Takeaway

The main trick is to combine:

1. **prefix balance** for the condition `vowels == consonants`
2. **prime-factor reduction of k** for the condition `(vowels * consonants) % k == 0`

That turns the problem into counting equal prefix states under a carefully chosen modulus.
