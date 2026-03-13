# 2748. Number of Beautiful Pairs

## Problem Restatement

We are given an integer array `nums`.

A pair of indices `(i, j)` is called **beautiful** if:

- `0 <= i < j < nums.length`
- the **first digit** of `nums[i]`
- and the **last digit** of `nums[j]`

are **coprime**.

That means:

```text
gcd(firstDigit(nums[i]), lastDigit(nums[j])) == 1
```

We need to count the total number of such pairs.

---

## Key Observations

For each pair `(i, j)`:

- only the **first digit** of `nums[i]` matters
- only the **last digit** of `nums[j]` matters

The rest of the digits do not affect the answer.

Also, the first digit is always in:

```text
1 to 9
```

and the last digit is also in:

```text
1 to 9
```

because:

```text
nums[i] % 10 != 0
```

So the problem operates over a tiny digit space.

That opens the door to both a simple brute-force solution and a more optimized counting solution.

---

# Approach 1 — Brute Force Over All Pairs

## Intuition

The most direct method is:

1. try every pair `(i, j)` with `i < j`
2. compute:
   - `firstDigit(nums[i])`
   - `lastDigit(nums[j])`
3. check if those two digits are coprime
4. count the pair if they are

Since:

```text
nums.length <= 100
```

there are at most:

```text
100 * 99 / 2 = 4950
```

pairs

So brute force is absolutely fine.

---

## Algorithm

1. Initialize `count = 0`
2. For each `i` from `0` to `n - 1`
3. For each `j` from `i + 1` to `n - 1`
4. Extract:
   - `a = firstDigit(nums[i])`
   - `b = lastDigit(nums[j])`
5. If `gcd(a, b) == 1`, increment count
6. Return count

---

## Java Code

```java
class Solution {
    public int countBeautifulPairs(int[] nums) {
        int n = nums.length;
        int count = 0;

        for (int i = 0; i < n; i++) {
            int first = getFirstDigit(nums[i]);

            for (int j = i + 1; j < n; j++) {
                int last = nums[j] % 10;

                if (gcd(first, last) == 1) {
                    count++;
                }
            }
        }

        return count;
    }

    private int getFirstDigit(int x) {
        while (x >= 10) {
            x /= 10;
        }
        return x;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}
```

---

## Complexity Analysis

Let `n = nums.length`.

### Time Complexity

There are `O(n^2)` pairs.

For each pair:

- first digit extraction is at most a few divisions
- gcd is on digits `1..9`, so constant time

Overall:

```text
O(n^2)
```

### Space Complexity

```text
O(1)
```

---

# Approach 2 — Prefix Counting by First Digits

## Intuition

Instead of checking all earlier indices one by one for each `j`, we can maintain how many times each **first digit** has appeared so far.

Why this works:

For a fixed `j`, we only care about previous `i < j`.

If we know how many previous numbers have first digit:

- `1`
- `2`
- ...
- `9`

then for the current `lastDigit(nums[j])`, we can quickly count how many of those first digits are coprime with it.

Since the digit range is only `1..9`, this becomes very efficient.

---

## Algorithm

1. Maintain an array `freq[10]`
   - `freq[d]` = how many previous numbers had first digit `d`
2. Traverse `nums` from left to right
3. For current number:
   - `last = nums[j] % 10`
   - for each digit `d` from `1` to `9`:
     - if `gcd(d, last) == 1`, add `freq[d]` to answer
4. Then insert the current number’s first digit into `freq`
5. Return answer

This respects the order `i < j` naturally because we only count previously seen numbers.

---

## Java Code

```java
class Solution {
    public int countBeautifulPairs(int[] nums) {
        int[] freq = new int[10];
        int ans = 0;

        for (int num : nums) {
            int last = num % 10;

            for (int d = 1; d <= 9; d++) {
                if (gcd(d, last) == 1) {
                    ans += freq[d];
                }
            }

            int first = getFirstDigit(num);
            freq[first]++;
        }

        return ans;
    }

    private int getFirstDigit(int x) {
        while (x >= 10) {
            x /= 10;
        }
        return x;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}
```

---

## Complexity Analysis

For each element, we loop over digits `1..9`, which is constant.

### Time Complexity

```text
O(n * 9) = O(n)
```

### Space Complexity

```text
O(1)
```

because `freq` has fixed size `10`.

---

# Approach 3 — Precompute Coprime Digit Pairs

## Intuition

In Approach 2, we still compute `gcd(d, last)` repeatedly.

But the digit space is tiny:

- `d` is from `1` to `9`
- `last` is from `1` to `9`

So we can precompute once whether two digits are coprime.

Then each query becomes an `O(1)` table lookup.

This makes the solution cleaner and avoids repeated gcd computation.

---

## Algorithm

1. Build a boolean table:

```text
coprime[a][b] = true if gcd(a, b) == 1
```

for `1 <= a, b <= 9`

2. Maintain `freq[firstDigit]` for previously seen numbers
3. For each current number:
   - get its last digit
   - sum all `freq[d]` where `coprime[d][last]` is true
4. Add current first digit to `freq`
5. Return answer

---

## Java Code

```java
class Solution {
    public int countBeautifulPairs(int[] nums) {
        boolean[][] coprime = buildCoprimeTable();
        int[] freq = new int[10];
        int ans = 0;

        for (int num : nums) {
            int last = num % 10;

            for (int d = 1; d <= 9; d++) {
                if (coprime[d][last]) {
                    ans += freq[d];
                }
            }

            int first = getFirstDigit(num);
            freq[first]++;
        }

        return ans;
    }

    private boolean[][] buildCoprimeTable() {
        boolean[][] table = new boolean[10][10];

        for (int a = 1; a <= 9; a++) {
            for (int b = 1; b <= 9; b++) {
                table[a][b] = gcd(a, b) == 1;
            }
        }

        return table;
    }

    private int getFirstDigit(int x) {
        while (x >= 10) {
            x /= 10;
        }
        return x;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}
```

---

## Complexity Analysis

The precomputation is constant because the digit range is fixed.

### Time Complexity

```text
O(n)
```

### Space Complexity

```text
O(1)
```

---

# Approach 4 — Brute Force with Pre-Extracted First and Last Digits

## Intuition

Another small improvement to brute force is to precompute:

- first digit of every number
- last digit of every number

Then each pair check becomes a simple gcd call.

This does not improve asymptotic complexity, but it makes the pair loop cleaner.

---

## Java Code

```java
class Solution {
    public int countBeautifulPairs(int[] nums) {
        int n = nums.length;
        int[] first = new int[n];
        int[] last = new int[n];

        for (int i = 0; i < n; i++) {
            first[i] = getFirstDigit(nums[i]);
            last[i] = nums[i] % 10;
        }

        int ans = 0;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (gcd(first[i], last[j]) == 1) {
                    ans++;
                }
            }
        }

        return ans;
    }

    private int getFirstDigit(int x) {
        while (x >= 10) {
            x /= 10;
        }
        return x;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n^2)
```

### Space Complexity

```text
O(n)
```

for the precomputed arrays.

---

# Correctness Reasoning

## Claim 1

A pair `(i, j)` is beautiful exactly when:

```text
gcd(firstDigit(nums[i]), lastDigit(nums[j])) == 1
```

### Why?

That is literally the problem definition.

So any correct algorithm must count exactly those pairs.

---

## Claim 2

In the prefix-frequency approach, when processing index `j`, `freq[d]` equals the number of earlier indices `i < j` whose first digit is `d`.

### Proof

We process the array left to right.

After finishing index `j - 1`, every prior element has already contributed its first digit into `freq`, and the current index `j` has not yet been inserted.

So `freq[d]` counts exactly the valid earlier choices for `i`.

Proved.

---

## Claim 3

For a fixed `j`, summing `freq[d]` over all digits `d` coprime with `lastDigit(nums[j])` gives exactly the number of beautiful pairs ending at `j`.

### Proof

Each earlier index `i < j` contributes exactly one first digit `d = firstDigit(nums[i])`.

The pair `(i, j)` is beautiful if and only if `d` is coprime with `lastDigit(nums[j])`.

So the number of beautiful pairs ending at `j` is exactly the sum of frequencies of such digits.

Proved.

---

## Therefore

Adding this count for every index `j` gives the total number of beautiful pairs.

---

# Worked Examples

## Example 1

```text
nums = [2, 5, 1, 4]
```

First digits:

```text
[2, 5, 1, 4]
```

Last digits:

```text
[2, 5, 1, 4]
```

Check all pairs:

- `(0,1)` -> gcd(2,5)=1 -> beautiful
- `(0,2)` -> gcd(2,1)=1 -> beautiful
- `(0,3)` -> gcd(2,4)=2 -> not beautiful
- `(1,2)` -> gcd(5,1)=1 -> beautiful
- `(1,3)` -> gcd(5,4)=1 -> beautiful
- `(2,3)` -> gcd(1,4)=1 -> beautiful

Total:

```text
5
```

---

## Example 2

```text
nums = [11, 21, 12]
```

First digits:

```text
[1, 2, 1]
```

Last digits:

```text
[1, 1, 2]
```

Pairs:

- `(0,1)` -> gcd(1,1)=1 -> beautiful
- `(0,2)` -> gcd(1,2)=1 -> beautiful
- `(1,2)` -> gcd(2,2)=2 -> not beautiful

Total:

```text
2
```

---

# Edge Cases

## 1. First digit is 1

If the first digit is `1`, it is coprime with every last digit from `1` to `9`.

So such numbers are especially “powerful” as left endpoints.

---

## 2. Last digit is 1

If the last digit is `1`, then every earlier first digit forms a beautiful pair with it.

Because:

```text
gcd(d, 1) = 1
```

for every digit `d`.

---

## 3. Very small array

If `nums.length = 2`, there is only one possible pair to check.

---

## 4. Repeated numbers

No problem. Pairs are based on indices, not distinct values.

---

# Comparison of Approaches

## Approach 1 — Plain brute force

Pros:

- simplest
- easiest to derive

Cons:

- `O(n^2)`

Still fully acceptable because `n <= 100`.

---

## Approach 2 — Prefix first-digit frequency

Pros:

- elegant
- linear time
- uses the tiny digit range well

Cons:

- a bit more abstract than brute force

This is the recommended approach.

---

## Approach 3 — Prefix frequency + coprime table

Pros:

- fastest clean version
- removes repeated gcd calls
- very neat for interviews

Cons:

- slightly more setup

---

## Approach 4 — Brute force with precomputation

Pros:

- keeps brute force simple
- separates digit extraction from pair checking

Cons:

- still `O(n^2)`

---

# Final Recommended Java Solution

```java
class Solution {
    public int countBeautifulPairs(int[] nums) {
        int[] freq = new int[10];
        int ans = 0;

        for (int num : nums) {
            int last = num % 10;

            for (int d = 1; d <= 9; d++) {
                if (gcd(d, last) == 1) {
                    ans += freq[d];
                }
            }

            int first = getFirstDigit(num);
            freq[first]++;
        }

        return ans;
    }

    private int getFirstDigit(int x) {
        while (x >= 10) {
            x /= 10;
        }
        return x;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}
```

---

# Complexity Summary

## Approach 1

```text
Time:  O(n^2)
Space: O(1)
```

## Approach 2

```text
Time:  O(n)
Space: O(1)
```

## Approach 3

```text
Time:  O(n)
Space: O(1)
```

## Approach 4

```text
Time:  O(n^2)
Space: O(n)
```

---

# Final Takeaway

The crucial simplification is that only digits matter:

- first digit of the left number
- last digit of the right number

Because both are only in the range `1..9`, we can solve the problem much faster than checking all pairs explicitly.

The cleanest optimized strategy is:
