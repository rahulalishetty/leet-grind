# 2947. Count Beautiful Substrings I

## Problem Restatement

We are given:

- a string `s`
- an integer `k`

A substring is **beautiful** if:

1. the number of vowels equals the number of consonants
2. `(vowels * consonants) % k == 0`

We need to return the number of **non-empty beautiful substrings**.

---

## Key Observations

Let a substring have:

- `v` vowels
- `c` consonants

The first condition says:

```text
v = c
```

So the substring length must be even:

```text
length = v + c = 2v
```

Now the second condition becomes:

```text
(v * c) % k == 0
```

Since `v = c`, this reduces to:

```text
v^2 % k == 0
```

If the substring length is `L = 2v`, then:

```text
(L / 2)^2 % k == 0
```

So a substring is beautiful iff:

1. vowels = consonants
2. `(length / 2)^2` is divisible by `k`

This is the central reduction.

---

# Approach 1 — Brute Force Enumeration

## Intuition

Check every substring.

For each substring:

- count vowels
- count consonants
- if they are equal, check divisibility

This is the most direct approach.

Because `n <= 1000`, brute force is feasible, though not optimal.

---

## Algorithm

For each starting index `i`:

- initialize `vowels = 0`, `consonants = 0`
- extend substring to each `j >= i`
- update counts
- if `vowels == consonants` and `(vowels * consonants) % k == 0`, increment answer

---

## Java Code

```java
class Solution {
    public int beautifulSubstrings(String s, int k) {
        int n = s.length();
        int ans = 0;

        for (int i = 0; i < n; i++) {
            int vowels = 0;
            int consonants = 0;

            for (int j = i; j < n; j++) {
                char ch = s.charAt(j);
                if (isVowel(ch)) {
                    vowels++;
                } else {
                    consonants++;
                }

                if (vowels == consonants && (vowels * consonants) % k == 0) {
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

## Complexity Analysis

Let `n = s.length()`.

### Time Complexity

There are `O(n^2)` substrings.

Each substring update is `O(1)` because we extend incrementally.

So total:

```text
O(n^2)
```

### Space Complexity

```text
O(1)
```

---

# Approach 2 — Prefix Balance + Length Check

## Intuition

Instead of separately counting vowels and consonants for each substring, we can convert the string into a balance array:

- vowel -> `+1`
- consonant -> `-1`

Then for a substring:

```text
vowels == consonants
```

iff its balance sum is zero.

That means if `prefix[i]` is the balance up to index `i`, then substring `(l..r)` has equal vowels and consonants iff:

```text
prefix[r + 1] == prefix[l]
```

So we reduce the problem to:

- equal prefix balance
- valid length divisibility condition

This avoids tracking both counts explicitly.

---

## Important Length Condition

Suppose substring length is `L`.

Since vowels = consonants, we have:

```text
v = c = L / 2
```

Then beauty requires:

```text
(L / 2)^2 % k == 0
```

So once balance is zero, we only need to check whether the substring length satisfies this arithmetic condition.

---

## Algorithm

1. Build prefix balance:
   - vowel => `+1`
   - consonant => `-1`
2. Enumerate all substring endpoints using prefix indices:
   - for all `i < j`
   - if `prefix[i] == prefix[j]`, substring `s[i..j-1]` has equal vowels and consonants
   - length = `j - i`
   - check whether `((length / 2) * (length / 2)) % k == 0`
3. Count such substrings

---

## Java Code

```java
class Solution {
    public int beautifulSubstrings(String s, int k) {
        int n = s.length();
        int[] prefix = new int[n + 1];

        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + (isVowel(s.charAt(i)) ? 1 : -1);
        }

        int ans = 0;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j <= n; j++) {
                if (prefix[i] == prefix[j]) {
                    int len = j - i;
                    int half = len / 2;
                    if ((half * half) % k == 0) {
                        ans++;
                    }
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

## Complexity Analysis

### Time Complexity

Two nested loops over prefix indices:

```text
O(n^2)
```

### Space Complexity

Prefix array:

```text
O(n)
```

---

# Approach 3 — Prefix Balance + Modular Grouping

## Intuition

Approach 2 still checks all pairs of equal balances, which is already acceptable for `n <= 1000`.

But we can tighten the logic by observing that not every even length works.

We need:

```text
(length / 2)^2 % k == 0
```

Instead of checking this every time from scratch, we can characterize which lengths are valid.

---

## Deriving the Valid Length Pattern

Let:

```text
m = length / 2
```

We need:

```text
m^2 % k == 0
```

That means `m` must be divisible by a certain minimum number derived from `k`.

Let that number be `need`.

Then:

```text
m % need == 0
```

So:

```text
length = 2m
```

must satisfy:

```text
length % (2 * need) == 0
```

This lets us transform the length condition into a modular condition.

---

## How to Compute `need`

Prime-factorize `k`:

```text
k = p1^a1 * p2^a2 * ...
```

For `m^2` to be divisible by `k`, `m` must contain each prime `pi` to exponent at least:

```text
ceil(ai / 2)
```

So:

```text
need = product of pi^(ceil(ai / 2))
```

Then valid lengths are exactly multiples of:

```text
period = 2 * need
```

---

## Final Prefix Trick

A substring `s[l..r]` is beautiful iff:

1. prefix balance at `l` and `r+1` is the same
2. its length `(r-l+1)` is divisible by `period`

In prefix-index language, for indices `i < j`:

- `prefix[i] == prefix[j]`
- `(j - i) % period == 0`

So while scanning prefix indices, we can group by:

- balance value
- index modulo `period`

If two prefix positions have the same:

- balance
- modulo class

then the substring between them is beautiful.

This gives a much cleaner counting approach.

---

## Algorithm

1. Compute `need`
2. Set `period = 2 * need`
3. Build prefix balance array on the fly
4. Maintain a frequency map keyed by:
   - `(balance, index % period)`
5. For each prefix position `i`:
   - all earlier positions with same key form beautiful substrings ending here
   - add their count to answer
   - increment current key frequency

---

## Java Code

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    public int beautifulSubstrings(String s, int k) {
        int need = getNeed(k);
        int period = 2 * need;

        Map<String, Integer> freq = new HashMap<>();
        int balance = 0;
        int ans = 0;

        // prefix index 0
        String firstKey = balance + "#" + (0 % period);
        freq.put(firstKey, 1);

        for (int i = 1; i <= s.length(); i++) {
            char ch = s.charAt(i - 1);
            balance += isVowel(ch) ? 1 : -1;

            int mod = i % period;
            String key = balance + "#" + mod;

            ans += freq.getOrDefault(key, 0);
            freq.put(key, freq.getOrDefault(key, 0) + 1);
        }

        return ans;
    }

    private int getNeed(int k) {
        int need = 1;
        int x = k;

        for (int p = 2; p * p <= x; p++) {
            int count = 0;
            while (x % p == 0) {
                x /= p;
                count++;
            }
            if (count > 0) {
                int times = (count + 1) / 2; // ceil(count / 2)
                for (int i = 0; i < times; i++) {
                    need *= p;
                }
            }
        }

        if (x > 1) {
            need *= x;
        }

        return need;
    }

    private boolean isVowel(char ch) {
        return ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u';
    }
}
```

---

## Complexity Analysis

Let `n = s.length()`.

### Time Complexity

- factorizing `k`: `O(sqrt(k))`
- one pass through string: `O(n)`

Overall:

```text
O(n + sqrt(k))
```

### Space Complexity

Hash map stores prefix states:

```text
O(n)
```

---

# Why Approach 3 Works

## Condition 1: Equal vowels and consonants

Using vowel = `+1`, consonant = `-1`, a substring has equal counts exactly when its sum is `0`.

That is equivalent to equal prefix balances at both ends.

---

## Condition 2: Product divisible by `k`

If the substring is balanced, then:

```text
v = c = length / 2
```

So:

```text
v * c = (length / 2)^2
```

We need:

```text
(length / 2)^2 % k == 0
```

By construction of `need`, this is equivalent to:

```text
length % (2 * need) == 0
```

So we only need prefix pairs with:

- same balance
- same prefix index modulo `period = 2 * need`

That is exactly what the map counts.

---

# Worked Example

## Example 1

```text
s = "baeyh", k = 2
```

### Step 1: Compute period

We need:

```text
m^2 % 2 == 0
```

So `m` must be divisible by `2`.

Thus:

```text
need = 2
period = 4
```

### Step 2: Balance encoding

- `b` -> -1
- `a` -> +1
- `e` -> +1
- `y` -> -1
- `h` -> -1

Prefix balances:

```text
index:   0  1  2  3  4  5
balance: 0 -1  0  1  0 -1
```

Now look for prefix pairs with:

- same balance
- same index mod 4

That yields exactly the two valid substrings:

- `"baey"`
- `"aeyh"`

Answer = `2`

---

# Comparison of Approaches

## Approach 1 — Brute Force Counts

Good for:

- first understanding
- direct implementation

Pros:

- simple
- intuitive

Cons:

- less elegant
- repeats work

---

## Approach 2 — Prefix Balance + Pair Check

Good for:

- learning the balance reduction
- cleaner than raw brute force

Pros:

- nice mathematical simplification
- still easy to implement

Cons:

- still `O(n^2)`

---

## Approach 3 — Prefix Balance + Modular Grouping

Good for:

- best optimized solution
- strongest interview solution

Pros:

- very efficient
- elegant use of number theory + prefix sums

Cons:

- hardest to derive

---

# Final Recommended Solution

For interviews and solid understanding, remember **Approach 3**.

It combines:

- prefix-balance equality
- divisibility reduction
- modular grouping

---

## Final Recommended Java Code

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    public int beautifulSubstrings(String s, int k) {
        int need = getNeed(k);
        int period = 2 * need;

        Map<String, Integer> freq = new HashMap<>();
        int balance = 0;
        int ans = 0;

        freq.put("0#0", 1);

        for (int i = 1; i <= s.length(); i++) {
            balance += isVowel(s.charAt(i - 1)) ? 1 : -1;
            String key = balance + "#" + (i % period);

            ans += freq.getOrDefault(key, 0);
            freq.put(key, freq.getOrDefault(key, 0) + 1);
        }

        return ans;
    }

    private int getNeed(int k) {
        int need = 1;
        int x = k;

        for (int p = 2; p * p <= x; p++) {
            int cnt = 0;
            while (x % p == 0) {
                x /= p;
                cnt++;
            }
            for (int i = 0; i < (cnt + 1) / 2; i++) {
                need *= p;
            }
        }

        if (x > 1) {
            need *= x;
        }

        return need;
    }

    private boolean isVowel(char ch) {
        return ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u';
    }
}
```

---

# Summary

A substring is beautiful if:

1. vowels = consonants
2. `(length / 2)^2 % k == 0`

Using prefix balance:

- same balance => equal vowels and consonants

Using number theory:

- valid substring lengths must be multiples of `2 * need`

So count prefix pairs with same:

- balance
- prefix index modulo `2 * need`

---

## Complexity Summary

### Approach 1

```text
Time:  O(n^2)
Space: O(1)
```

### Approach 2

```text
Time:  O(n^2)
Space: O(n)
```

### Approach 3

```text
Time:  O(n + sqrt(k))
Space: O(n)
```
