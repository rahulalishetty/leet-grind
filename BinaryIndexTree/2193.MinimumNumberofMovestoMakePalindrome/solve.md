# 2193. Minimum Number of Moves to Make Palindrome

## Problem Restatement

We are given a string `s` of lowercase English letters.

In one move, we may swap any **adjacent** characters.

We need the **minimum number of adjacent swaps** required to transform `s` into **some palindrome**.

It is guaranteed that such a transformation is always possible.

---

## Core Insight

Because only **adjacent swaps** are allowed, moving a character by `k` positions costs exactly `k` moves.

So the problem is not just about whether a palindrome exists. It is about forming one with the **fewest total adjacent shifts**.

There are two major ways to think about this problem:

1. **Greedy simulation**:
   - repeatedly match characters from both ends
   - bubble matching characters into place
2. **Permutation / inversion view**:
   - decide each character’s target position in a palindrome
   - count how many adjacent swaps are needed to realize that target ordering

The greedy method is the most standard and easiest to derive.
The inversion-based method is more advanced and elegant.

---

# Approach 1 — Greedy Two-Pointer Simulation

## Intuition

We try to build the palindrome from the outside inward.

Suppose we are currently working on substring:

```text
s[left...right]
```

We want the character at `left` to be matched with the same character somewhere near `right`.

### Case 1: A matching character exists on the right side

Search from `right` backward until we find a position `k` such that:

```text
s[k] == s[left]
```

If `k != left`, then this character can pair with `s[left]`.

Now we bring `s[k]` to position `right` using adjacent swaps:

```text
... x a b c ...
        ^
        k
```

Bubble it rightward one step at a time.
Each swap costs `1`.

Then we have fixed both ends and continue with the inside substring.

---

### Case 2: No matching character exists

If the search stops at `k == left`, then `s[left]` is the odd-frequency character that must eventually go into the middle of the palindrome.

In that case, we swap it one step right:

```text
swap(s[left], s[left + 1])
```

This moves the unmatched character toward the center.

We do **not** shrink the window yet, because the left boundary still needs a valid matched pair.

This greedy process is optimal because:

- if a matching character exists, pairing it now is always best
- if no match exists, that character must be the center character, so moving it inward is unavoidable

---

## Step-by-Step Example

### Example: `s = "aabb"`

Start:

```text
a a b b
^     ^
l     r
```

Match `s[l] = 'a'` from the right:

- scan from right: `b`, `b`, `a`
- found at index 1

Bubble it to the end:

```text
a a b b -> a b a b -> a b b a
```

Cost = 2

Now inside substring is `"bb"` which is already matched.

Total = 2

---

## Java Code

```java
class Solution {
    public int minMovesToMakePalindrome(String s) {
        char[] arr = s.toCharArray();
        int left = 0, right = arr.length - 1;
        int moves = 0;

        while (left < right) {
            int k = right;

            while (k > left && arr[k] != arr[left]) {
                k--;
            }

            if (k == left) {
                // arr[left] is the middle character
                swap(arr, left, left + 1);
                moves++;
            } else {
                while (k < right) {
                    swap(arr, k, k + 1);
                    moves++;
                    k++;
                }
                left++;
                right--;
            }
        }

        return moves;
    }

    private void swap(char[] arr, int i, int j) {
        char temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
```

---

## Complexity Analysis

Let `n = s.length()`.

### Time Complexity

In the worst case:

- for each left boundary, we may scan from the right
- and perform bubbling swaps

So worst-case time is:

```text
O(n^2)
```

Given:

```text
n <= 2000
```

this is acceptable.

### Space Complexity

```text
O(n)
```

for the mutable character array.

---

# Approach 2 — Greedy with Mutable String/List Operations

## Intuition

This is the same greedy logic as Approach 1, but written with a mutable list-like structure.

Instead of manual array swaps, one can think in terms of:

- finding the matching partner
- removing it
- reinserting it at the appropriate edge
- counting how many adjacent swaps that corresponds to

This approach is conceptually nice, but arrays are usually simpler and faster in Java.

---

## Java Code

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public int minMovesToMakePalindrome(String s) {
        List<Character> chars = new ArrayList<>();
        for (char c : s.toCharArray()) {
            chars.add(c);
        }

        int moves = 0;

        while (chars.size() > 1) {
            int i = chars.size() - 1;

            while (i > 0 && chars.get(i) != chars.get(0)) {
                i--;
            }

            if (i == 0) {
                // unmatched middle character
                moves += chars.size() / 2;
                chars.remove(0);
            } else {
                moves += chars.size() - 1 - i;
                chars.remove(i);
                chars.remove(0);
            }
        }

        return moves;
    }
}
```

---

## Why this works

If the matching partner is found at position `i` in the current list, then moving it to the back costs:

```text
(size - 1 - i)
```

adjacent swaps.

Then both matched endpoints are removed from further consideration.

If no partner exists, that front character must be the center character, and moving it to the middle costs:

```text
size / 2
```

adjacent swaps.

---

## Complexity Analysis

### Time Complexity

List removals can be linear, and we do this repeatedly:

```text
O(n^2)
```

### Space Complexity

```text
O(n)
```

---

# Approach 3 — Build Target Palindrome Order + Count Inversions

## Intuition

Adjacent swaps needed to transform one ordering into another equals the number of **inversions** between them.

So another way to solve the problem is:

1. decide where each occurrence of each character should go in the final palindrome
2. transform the problem into counting inversions of target positions

This is more advanced but very elegant.

---

## High-Level Idea

Suppose the string is:

```text
m a m a d
```

A valid palindrome arrangement is:

```text
m a d a m
```

Each original character occurrence can be mapped to a target palindrome position.

Then the minimum adjacent swaps needed is exactly the inversion count of that target-position sequence.

This can be computed efficiently with a Fenwick Tree.

---

## How to Build Target Positions

For each character, collect all its occurrence indices.

If a character occurs multiple times, then:

- its first occurrence should pair with its last occurrence
- second with second-last
- and so on

These pairs should be placed symmetrically in the palindrome.

If one character has odd count, one occurrence goes to the center.

After assigning target positions to all original occurrences, we obtain an array `target[]`.

Then the answer is the inversion count of `target`.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minMovesToMakePalindrome(String s) {
        int n = s.length();

        Map<Character, Deque<Integer>> positions = new HashMap<>();
        for (int i = 0; i < n; i++) {
            positions.computeIfAbsent(s.charAt(i), k -> new ArrayDeque<>()).add(i);
        }

        int[] target = new int[n];
        int left = 0, right = n - 1;

        // We assign target positions by pairing from both ends.
        for (char ch = 'a'; ch <= 'z'; ch++) {
            Deque<Integer> dq = positions.get(ch);
            if (dq == null) continue;

            while (dq.size() >= 2) {
                int first = dq.pollFirst();
                int last = dq.pollLast();
                target[first] = left++;
                target[last] = right--;
            }

            if (!dq.isEmpty()) {
                target[dq.pollFirst()] = n / 2;
            }
        }

        // Count inversions in target[]
        Fenwick bit = new Fenwick(n);
        long inv = 0;

        for (int i = n - 1; i >= 0; i--) {
            inv += bit.query(target[i]);
            bit.add(target[i] + 1, 1);
        }

        return (int) inv;
    }

    static class Fenwick {
        int[] tree;

        Fenwick(int n) {
            tree = new int[n + 2];
        }

        void add(int index, int delta) {
            while (index < tree.length) {
                tree[index] += delta;
                index += index & -index;
            }
        }

        int query(int index) {
            int sum = 0;
            while (index > 0) {
                sum += tree[index];
                index -= index & -index;
            }
            return sum;
        }
    }
}
```

---

## Important Note

This inversion-based formulation is elegant, but it is easier to make implementation mistakes in assigning target positions correctly.

For interviews and reliability, the greedy solution is often the safest choice.

---

## Complexity Analysis

### Time Complexity

- building target mapping: `O(n)`
- Fenwick inversion counting: `O(n log n)`

Overall:

```text
O(n log n)
```

### Space Complexity

```text
O(n)
```

---

# Approach 4 — Recursive / Backtracking Thinking (Why Not Use It)

## Intuition

One might think of recursively trying all valid ways to pair characters and forming a palindrome with minimum cost.

This is a natural first thought, but it is completely impractical.

Why?

Because:

- there may be many repeated characters
- many possible palindrome targets
- branching factor is large

This leads to exponential behavior.

So although it is conceptually valid, it is not useful for the given constraints.

---

# Why the Greedy Approach Is Correct

## Greedy Claim 1

If `s[left]` has a matching character at some position `k > left`, then using the **rightmost such match** and bubbling it to `right` is optimal.

### Reason

Any palindrome must pair `s[left]` with some identical character on the right side.

To bring a partner to the right boundary, every adjacent swap moves it one step closer.

Choosing the rightmost available match minimizes the number of swaps needed to place the pair.

So this local choice is optimal.

---

## Greedy Claim 2

If no matching character exists for `s[left]` on the right side, then `s[left]` must be the center character.

### Reason

A palindrome has at most one odd-frequency character, and that character must occupy the middle position.

If `s[left]` has no partner in the remaining substring, then it is exactly that middle character.

So moving it inward one step at a time is unavoidable.

---

## Greedy Claim 3

Once an endpoint pair is fixed, the remaining problem is the same problem on the inner substring.

### Reason

After placing matching characters at both ends, those positions are finalized and independent of the rest.

What remains is to minimize swaps for the substring inside them.

So the problem has optimal substructure.

---

# Worked Examples

## Example 1

```text
s = "aabb"
```

Start:

```text
a a b b
```

Match first `'a'` with the `'a'` at index 1.

Bubble it right:

```text
a a b b
a b a b
a b b a
```

Moves = 2

Now remaining middle is `"bb"`, already valid.

Answer:

```text
2
```

---

## Example 2

```text
s = "letelt"
```

Start:

```text
l e t e l t
```

Take left `'l'`. Matching `'l'` exists near the right.

By bubbling and repeating, one minimum path is:

```text
letelt -> letetl -> lettel
```

Total moves:

```text
2
```

---

# Edge Cases

## 1. Already a palindrome

Example:

```text
"racecar"
```

No swaps needed.

Answer:

```text
0
```

---

## 2. One unmatched middle character

Example:

```text
"mamad"
```

Character `'d'` ends up in the middle.

The greedy algorithm naturally handles this by repeatedly moving the unmatched character inward.

---

## 3. All characters identical

Example:

```text
"aaaa"
```

Already a palindrome.

Answer:

```text
0
```

---

## 4. Short strings

- length 1 -> answer `0`
- length 2:
  - `"aa"` -> `0`
  - `"ab"` is impossible, but problem guarantees valid input

---

# Comparison of Approaches

## Approach 1 — Greedy two-pointer with swaps

Pros:

- standard solution
- easy to derive
- very reliable
- simple to explain

Cons:

- `O(n^2)`

This is the recommended interview solution.

---

## Approach 2 — Greedy with mutable list

Pros:

- same intuition as Approach 1
- sometimes conceptually simpler

Cons:

- list operations can obscure the actual swap process

---

## Approach 3 — Target permutation + inversions

Pros:

- asymptotically faster
- elegant
- connects to inversion counting

Cons:

- harder to derive
- more error-prone to implement

---

## Approach 4 — Backtracking

Pros:

- conceptually exhaustive

Cons:

- infeasible

---

# Final Recommended Java Solution

```java
class Solution {
    public int minMovesToMakePalindrome(String s) {
        char[] arr = s.toCharArray();
        int left = 0, right = arr.length - 1;
        int moves = 0;

        while (left < right) {
            int k = right;

            while (k > left && arr[k] != arr[left]) {
                k--;
            }

            if (k == left) {
                swap(arr, left, left + 1);
                moves++;
            } else {
                while (k < right) {
                    swap(arr, k, k + 1);
                    moves++;
                    k++;
                }
                left++;
                right--;
            }
        }

        return moves;
    }

    private void swap(char[] arr, int i, int j) {
        char temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
```

---

# Complexity Summary

## Approach 1

```text
Time:  O(n^2)
Space: O(n)
```

## Approach 2

```text
Time:  O(n^2)
Space: O(n)
```

## Approach 3

```text
Time:  O(n log n)
Space: O(n)
```

## Approach 4

```text
Time:  Exponential
Space: Exponential / recursion-heavy
```

---

# Final Takeaway

The key greedy idea is:

- try to match the leftmost character with a partner from the right
- if a partner exists, bubble it to the end
- if no partner exists, that character must be the palindrome center, so move it inward

That gives a clean and reliable minimum-swap solution.

For stronger optimization, the problem can also be seen as an inversion-counting problem after assigning characters to target palindrome positions.
