# 2301. Match Substring After Replacement

## Problem Statement

You are given:

- a string `s`
- a string `sub`
- a 2D character array `mappings`

Each mapping:

```text
[old, new]
```

means:

- a character `old` in `sub` may be replaced with `new`
- each character position in `sub` can be replaced at most once
- replacements are optional

Return `true` if it is possible to transform `sub` so that it becomes a substring of `s`, otherwise return `false`.

A substring is a contiguous non-empty sequence of characters.

---

## Example 1

```text
Input:
s = "fool3e7bar"
sub = "leet"
mappings = [["e","3"],["t","7"],["t","8"]]

Output:
true
```

Explanation:

We can replace:

- first `'e'` with `'3'`
- `'t'` with `'7'`

Then:

```text
sub -> "l3e7"
```

and `"l3e7"` is a substring of `s`.

---

## Example 2

```text
Input:
s = "fooleetbar"
sub = "f00l"
mappings = [["o","0"]]

Output:
false
```

Explanation:

We cannot replace `'0'` with `'o'`.

So `"f00l"` cannot be turned into a substring of `"fooleetbar"`.

---

## Example 3

```text
Input:
s = "Fool33tbaR"
sub = "leetd"
mappings = [["e","3"],["t","7"],["t","8"],["d","b"],["p","b"]]

Output:
true
```

Explanation:

We can replace:

- both `'e'` characters with `'3'`
- `'d'` with `'b'`

Then:

```text
sub -> "l33tb"
```

and `"l33tb"` is a substring of `s`.

---

## Constraints

- `1 <= sub.length <= s.length <= 5000`
- `0 <= mappings.length <= 1000`
- `mappings[i].length == 2`
- `old != new`
- `s` and `sub` consist of uppercase letters, lowercase letters, and digits

---

# Core Insight

A position `sub[j]` can match `s[i + j]` if either:

1. they are already equal, or
2. `sub[j]` is allowed to be replaced by `s[i + j]`

So for a fixed starting position `i` in `s`, we only need to check:

```text
for every j in [0, sub.length - 1]:
    sub[j] == s[i+j]
    OR
    mapping allows sub[j] -> s[i+j]
```

That is the whole matching rule.

So the problem becomes:

> Is there any starting index `i` in `s` such that every aligned character pair is compatible?

This naturally gives a sliding-window style brute-force solution.

Then we can improve the compatibility lookup, and finally discuss stronger pattern-matching styles.

---

# Character Compatibility Model

It is useful to define:

```text
matches(a, b) = true
```

if character `a` from `sub` can match character `b` from `s`.

That means:

```text
a == b
OR
(a -> b) exists in mappings
```

This is directional.

Very important:

```text
a -> b
```

does **not** imply:

```text
b -> a
```

This asymmetry is the main subtlety of the problem.

---

# Approach 1: Brute Force Over Every Start Position

## Intuition

Try to align `sub` with every possible substring of `s` of the same length.

If at some alignment every character position is compatible, return `true`.

Otherwise return `false`.

This is the most direct solution, and with constraints up to `5000`, it is actually acceptable if implemented carefully.

---

## Algorithm

1. Preprocess mappings into a fast lookup structure
2. For each starting position `start` from `0` to `s.length - sub.length`
3. Check each position `j` in `sub`
4. If every `sub[j]` can match `s[start + j]`, return `true`
5. If no start works, return `false`

---

## Java Code

```java
import java.util.*;

class Solution {
    public boolean matchReplacement(String s, String sub, char[][] mappings) {
        boolean[][] allowed = new boolean[128][128];

        for (char[] map : mappings) {
            allowed[map[0]][map[1]] = true;
        }

        int n = s.length();
        int m = sub.length();

        for (int start = 0; start <= n - m; start++) {
            boolean ok = true;

            for (int j = 0; j < m; j++) {
                char a = sub.charAt(j);
                char b = s.charAt(start + j);

                if (a != b && !allowed[a][b]) {
                    ok = false;
                    break;
                }
            }

            if (ok) {
                return true;
            }
        }

        return false;
    }
}
```

---

## Complexity Analysis

Let:

- `n = s.length()`
- `m = sub.length()`

### Time Complexity

For each of the `n - m + 1` starting positions, we may compare up to `m` characters:

```text
O((n - m + 1) * m)
```

Worst case:

```text
O(n * m)
```

Since both can be up to `5000`, this can approach `25,000,000` character checks, which is still usually manageable in Java.

### Space Complexity

```text
O(1)
```

if we treat the `128 x 128` table as constant-sized.

---

## Verdict

This is a strong practical solution and often enough.

---

# Approach 2: Brute Force With HashSet-Based Mapping Lookup

## Intuition

Instead of a fixed 2D boolean table, we can store mappings using sets.

For each source character from `sub`, store the set of target characters it may become.

This is more flexible conceptually, though the boolean table is faster and simpler when the alphabet is small.

---

## Java Code

```java
import java.util.*;

class Solution {
    public boolean matchReplacement(String s, String sub, char[][] mappings) {
        Map<Character, Set<Character>> map = new HashMap<>();

        for (char[] pair : mappings) {
            map.computeIfAbsent(pair[0], k -> new HashSet<>()).add(pair[1]);
        }

        int n = s.length();
        int m = sub.length();

        for (int start = 0; start <= n - m; start++) {
            boolean ok = true;

            for (int j = 0; j < m; j++) {
                char a = sub.charAt(j);
                char b = s.charAt(start + j);

                if (a == b) {
                    continue;
                }

                if (!map.containsKey(a) || !map.get(a).contains(b)) {
                    ok = false;
                    break;
                }
            }

            if (ok) {
                return true;
            }
        }

        return false;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Still:

```text
O(n * m)
```

Expected set lookups are `O(1)` average.

### Space Complexity

```text
O(number of mappings)
```

---

## Verdict

Correct and clear, but the 2D boolean table is better here.

---

# Approach 3: Modified String Matching (KMP-Style Compatibility Matching)

## Intuition

This problem resembles substring matching, but equality is replaced by compatibility:

```text
sub[j] matches s[i+j]
```

if:

```text
sub[j] == s[i+j] OR allowed[sub[j]][s[i+j]]
```

This makes us wonder whether we can use KMP.

However, ordinary KMP relies on equality being transitive and self-consistent within the pattern, while here compatibility is directional between pattern and text characters.

That means standard KMP prefix-function reasoning does **not** transfer cleanly in the usual way.

So while the problem looks like string matching, the compatibility rule breaks the assumptions that make vanilla KMP straightforward.

This is an important skeptical checkpoint.

---

## Why Standard KMP Is Tricky Here

KMP preprocessing compares pattern with itself to build failure transitions.

But here our match relation is not ordinary equality. It depends on whether a pattern character can transform into a text character.

That does not naturally define the same symmetric relation among pattern characters themselves.

So a direct KMP adaptation is not obvious or robust.

---

## Verdict

Interesting idea, but not the best path for this problem.

---

# Approach 4: Bitset Optimization

## Intuition

Because the alphabet is small and the string length is moderate, we can think about a bitset-based acceleration.

For each possible character `c` in `sub`, build a bitset of text positions where `c` can match the text character at that position.

Then simulate the substring alignment using shifts and AND operations.

This can reduce constant factors in languages with strong bitset support.

But for this problem size, this is unnecessary unless you are specifically optimizing low-level performance.

---

## Conceptual Sketch

For each pattern position `j`, define a bitset:

```text
B_j = positions i in s where sub[j] can match s[i]
```

Then a valid full alignment of `sub` starting at `start` requires:

```text
start ∈ B_0
start+1 ∈ B_1
start+2 ∈ B_2
...
```

So with bitset shifts:

```text
B_0 & (B_1 >> 1) & (B_2 >> 2) & ...
```

Any set bit indicates a valid alignment.

---

## Verdict

Elegant, but not the most practical Java interview solution.

---

# Approach 5: Rolling Hash / Rabin–Karp Style Thinking

## Intuition

One may wonder whether rolling hash helps.

Normally Rabin–Karp is useful when exact equality of substrings matters.

Here, however, every position may allow multiple target characters depending on mappings.

So there is no single fixed transformed version of `sub` to hash.

In fact, `sub` may represent many possible strings under replacements.

That makes hashing much less natural.

---

## Verdict

Not a good fit.

---

# Best Practical Solution

Because:

- `s.length <= 5000`
- `sub.length <= 5000`
- alphabet is tiny
- mappings are easy to preprocess

the best practical solution is:

## brute force over every start position

plus

## O(1) compatibility lookup using a boolean table

It is simple, reliable, and efficient enough.

---

# Why the Brute Force Is Actually Fine Here

At first glance, `O(n * m)` may look large.

But with maximum values:

```text
5000 * 5000 = 25,000,000
```

This is not outrageous for simple constant-time checks in Java, especially with early breaks on mismatches.

So this is one of those cases where the simplest correct algorithm is already strong enough.

That is worth recognizing.

---

# Common Mistakes

## 1. Treating mappings as bidirectional

If mapping contains:

```text
['o', '0']
```

that only allows:

```text
'o' -> '0'
```

not:

```text
'0' -> 'o'
```

This is the most common bug.

---

## 2. Forgetting that exact equality is always allowed

Even without any mapping, `sub[j]` can match `s[i+j]` when the characters are already equal.

---

## 3. Applying one replacement to all identical characters globally

Replacements happen per character position in `sub`, independently.

So one `'e'` in `sub` can be replaced while another identical `'e'` may remain unchanged.

---

## 4. Overengineering with KMP or hashing without validating assumptions

This problem looks like classical substring matching, but the directed compatibility relation changes the structure.

A direct scan is cleaner.

---

# Final Recommended Solution

Use a compatibility lookup table and brute-force every possible alignment.

---

## Clean Final Java Solution

```java
class Solution {
    public boolean matchReplacement(String s, String sub, char[][] mappings) {
        boolean[][] allowed = new boolean[128][128];

        for (char[] pair : mappings) {
            allowed[pair[0]][pair[1]] = true;
        }

        int n = s.length();
        int m = sub.length();

        for (int start = 0; start <= n - m; start++) {
            boolean ok = true;

            for (int j = 0; j < m; j++) {
                char from = sub.charAt(j);
                char to = s.charAt(start + j);

                if (from != to && !allowed[from][to]) {
                    ok = false;
                    break;
                }
            }

            if (ok) {
                return true;
            }
        }

        return false;
    }
}
```

---

# Complexity Summary

## Boolean-table brute force

- Time: `O(n * m)`
- Space: `O(1)` with fixed alphabet table

## HashSet mapping brute force

- Time: `O(n * m)`
- Space: `O(number of mappings)`

## Bitset idea

- Better constant factors in some environments
- More complex than necessary here

## KMP / Rabin–Karp style ideas

- Not the natural fit because matching is directional compatibility, not ordinary equality

---

# Interview Summary

The key is to define compatibility correctly:

```text
sub[j] matches s[i+j]
iff
sub[j] == s[i+j]
or
sub[j] can be replaced by s[i+j]
```

Then simply test every possible alignment of `sub` inside `s`.

Because the constraints are moderate and the alphabet is small, a direct `O(n * m)` scan with `O(1)` lookup is the cleanest and most practical solution.
