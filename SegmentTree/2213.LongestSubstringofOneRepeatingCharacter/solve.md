# 2213. Longest Substring of One Repeating Character

## Problem Restatement

You are given:

- a string `s`
- a string `queryCharacters`
- an array `queryIndices`

For the `i-th` query:

```text
s[queryIndices[i]] = queryCharacters[i]
```

After each update, return the length of the **longest substring consisting of a single repeated character**.

---

## Key Constraints

```text
1 <= s.length <= 10^5
1 <= number of queries <= 10^5
```

This immediately tells us:

- recomputing the whole answer after every query in `O(n)` is too slow
- we need something around `O(log n)` per update

This is a classic signal for a **segment tree**.

---

# Core Insight

For any segment of the string, the information we really need is not the whole segment.
We only need enough information to merge two child segments and answer:

> what is the longest repeating-character substring in their union?

That can be done if each segment stores:

- its leftmost character
- its rightmost character
- the longest repeating prefix length
- the longest repeating suffix length
- the maximum repeating substring length anywhere inside

That is the standard segment-tree summary for this problem.

---

# Approach 1: Segment Tree with Custom Merge Information (Recommended)

## Idea

Build a segment tree over the string.

For each node representing segment `[l, r]`, store:

- `leftChar` → first character in the segment
- `rightChar` → last character in the segment
- `prefixLen` → length of longest prefix consisting of one repeated character
- `suffixLen` → length of longest suffix consisting of one repeated character
- `best` → longest repeating substring length anywhere in the segment
- `len` → total segment length

When one character changes, update the leaf and recompute upward.

The root’s `best` is the answer after each query.

---

## Why these fields are sufficient

Suppose we want to merge:

- left child segment `A`
- right child segment `B`

Then:

### 1. Best inside merged segment

The answer can come from:

- entirely inside `A`
- entirely inside `B`
- a substring crossing the middle

So:

```text
best = max(A.best, B.best)
```

If `A.rightChar == B.leftChar`, then a crossing block exists with length:

```text
A.suffixLen + B.prefixLen
```

So also compare with that.

---

### 2. Prefix of merged segment

The merged segment starts with `A.leftChar`.

Its repeating prefix is:

- all of `A.prefixLen` normally
- if all of `A` is one same character and `A.rightChar == B.leftChar`, then it can extend into `B`

---

### 3. Suffix of merged segment

Symmetric logic for the suffix.

---

## Node Merge Formula

If `left` and `right` are two child nodes, then:

- `leftChar = left.leftChar`
- `rightChar = right.rightChar`
- `best = max(left.best, right.best)`

If boundary chars match:

```text
best = max(best, left.suffixLen + right.prefixLen)
```

Prefix extension:

- start with `left.prefixLen`
- if left is entirely one repeated character and matches right’s left boundary, extend

Suffix extension is symmetric.

---

## Java Code

```java
class Solution {
    static class Node {
        int len;
        char leftChar, rightChar;
        int prefixLen, suffixLen, best;

        Node() {}

        Node(char c) {
            len = 1;
            leftChar = rightChar = c;
            prefixLen = suffixLen = best = 1;
        }
    }

    private Node[] tree;
    private char[] arr;

    public int[] longestRepeating(String s, String queryCharacters, int[] queryIndices) {
        int n = s.length();
        arr = s.toCharArray();
        tree = new Node[4 * n];

        build(1, 0, n - 1);

        int q = queryIndices.length;
        int[] ans = new int[q];

        for (int i = 0; i < q; i++) {
            int idx = queryIndices[i];
            char ch = queryCharacters.charAt(i);

            if (arr[idx] != ch) {
                arr[idx] = ch;
                update(1, 0, n - 1, idx);
            }

            ans[i] = tree[1].best;
        }

        return ans;
    }

    private void build(int node, int l, int r) {
        if (l == r) {
            tree[node] = new Node(arr[l]);
            return;
        }

        int mid = (l + r) >>> 1;
        build(node << 1, l, mid);
        build(node << 1 | 1, mid + 1, r);
        tree[node] = merge(tree[node << 1], tree[node << 1 | 1]);
    }

    private void update(int node, int l, int r, int idx) {
        if (l == r) {
            tree[node] = new Node(arr[idx]);
            return;
        }

        int mid = (l + r) >>> 1;
        if (idx <= mid) {
            update(node << 1, l, mid, idx);
        } else {
            update(node << 1 | 1, mid + 1, r, idx);
        }

        tree[node] = merge(tree[node << 1], tree[node << 1 | 1]);
    }

    private Node merge(Node a, Node b) {
        Node res = new Node();
        res.len = a.len + b.len;
        res.leftChar = a.leftChar;
        res.rightChar = b.rightChar;

        res.prefixLen = a.prefixLen;
        if (a.prefixLen == a.len && a.rightChar == b.leftChar) {
            res.prefixLen = a.len + b.prefixLen;
        }

        res.suffixLen = b.suffixLen;
        if (b.suffixLen == b.len && a.rightChar == b.leftChar) {
            res.suffixLen = b.len + a.suffixLen;
        }

        res.best = Math.max(a.best, b.best);
        if (a.rightChar == b.leftChar) {
            res.best = Math.max(res.best, a.suffixLen + b.prefixLen);
        }

        return res;
    }
}
```

---

## Complexity

Building the segment tree:

```text
O(n)
```

Each update:

```text
O(log n)
```

So total time for all queries:

```text
O((n + k) log n)
```

Space:

```text
O(n)
```

---

## Pros

- Standard optimal solution
- Efficient enough for `10^5` updates
- Elegant merge logic once understood

## Cons

- Merge logic is subtle
- Easy to make off-by-one mistakes

---

# Approach 2: Segment Tree with Explicit Interval Boundaries

## Idea

This is the same segment-tree solution, but some people prefer storing:

- leftmost run length
- rightmost run length
- answer
- boundary characters
- segment range `[l, r]`

Then merge uses range length directly.

It is not asymptotically different, just another implementation style.

---

## Java Code

```java
class Solution {
    static class Node {
        int l, r;
        int leftRun, rightRun, maxRun;
        char leftChar, rightChar;
    }

    private Node[] tree;
    private char[] s;

    public int[] longestRepeating(String str, String queryCharacters, int[] queryIndices) {
        int n = str.length();
        s = str.toCharArray();
        tree = new Node[4 * n];
        build(1, 0, n - 1);

        int[] ans = new int[queryIndices.length];
        for (int i = 0; i < queryIndices.length; i++) {
            int idx = queryIndices[i];
            char ch = queryCharacters.charAt(i);

            if (s[idx] != ch) {
                s[idx] = ch;
                update(1, idx);
            }

            ans[i] = tree[1].maxRun;
        }
        return ans;
    }

    private void build(int idx, int l, int r) {
        tree[idx] = new Node();
        tree[idx].l = l;
        tree[idx].r = r;

        if (l == r) {
            tree[idx].leftChar = tree[idx].rightChar = s[l];
            tree[idx].leftRun = tree[idx].rightRun = tree[idx].maxRun = 1;
            return;
        }

        int mid = (l + r) >>> 1;
        build(idx << 1, l, mid);
        build(idx << 1 | 1, mid + 1, r);
        pull(idx);
    }

    private void update(int idx, int pos) {
        Node cur = tree[idx];
        if (cur.l == cur.r) {
            cur.leftChar = cur.rightChar = s[pos];
            cur.leftRun = cur.rightRun = cur.maxRun = 1;
            return;
        }

        int mid = (cur.l + cur.r) >>> 1;
        if (pos <= mid) update(idx << 1, pos);
        else update(idx << 1 | 1, pos);

        pull(idx);
    }

    private void pull(int idx) {
        Node cur = tree[idx];
        Node left = tree[idx << 1];
        Node right = tree[idx << 1 | 1];

        cur.leftChar = left.leftChar;
        cur.rightChar = right.rightChar;

        cur.leftRun = left.leftRun;
        if (left.leftRun == left.r - left.l + 1 && left.rightChar == right.leftChar) {
            cur.leftRun = (left.r - left.l + 1) + right.leftRun;
        }

        cur.rightRun = right.rightRun;
        if (right.rightRun == right.r - right.l + 1 && left.rightChar == right.leftChar) {
            cur.rightRun = (right.r - right.l + 1) + left.rightRun;
        }

        cur.maxRun = Math.max(left.maxRun, right.maxRun);
        if (left.rightChar == right.leftChar) {
            cur.maxRun = Math.max(cur.maxRun, left.rightRun + right.leftRun);
        }
    }
}
```

---

## Complexity

Same as Approach 1:

```text
O((n + k) log n)
```

Space:

```text
O(n)
```

---

## Pros

- Same optimal complexity
- Some people find interval-aware merge easier to reason about

## Cons

- Still segment-tree heavy
- Slightly more verbose

---

# Approach 3: Maintain Runs in a TreeMap + Multiset of Run Lengths

## Idea

Instead of a segment tree, maintain the string as disjoint maximal runs of identical characters.

Example:

```text
aaabbccccd
```

becomes runs:

```text
[0,2]='a'
[3,4]='b'
[5,8]='c'
[9,9]='d'
```

For each update:

1. find the run containing the updated index
2. split it if needed
3. modify the character at that point
4. merge with neighboring runs if characters match
5. maintain a multiset of run lengths so the maximum is easy to query

This is a valid balanced-tree solution.

---

## Why it works

The answer depends only on maximal equal-character runs.

Each update changes only the local runs near the updated index.

So if we can maintain runs dynamically, we can also maintain the maximum run length.

---

## Java Code

```java
import java.util.*;

class Solution {
    static class Interval {
        int l, r;
        char c;

        Interval(int l, int r, char c) {
            this.l = l;
            this.r = r;
            this.c = c;
        }

        int len() {
            return r - l + 1;
        }
    }

    public int[] longestRepeating(String s, String queryCharacters, int[] queryIndices) {
        char[] arr = s.toCharArray();
        TreeMap<Integer, Interval> map = new TreeMap<>();
        TreeMap<Integer, Integer> freq = new TreeMap<>();

        // Build initial runs
        int n = arr.length;
        int start = 0;
        for (int i = 1; i <= n; i++) {
            if (i == n || arr[i] != arr[start]) {
                Interval in = new Interval(start, i - 1, arr[start]);
                map.put(start, in);
                addLen(freq, in.len(), 1);
                start = i;
            }
        }

        int[] ans = new int[queryIndices.length];

        for (int qi = 0; qi < queryIndices.length; qi++) {
            int idx = queryIndices[qi];
            char ch = queryCharacters.charAt(qi);

            if (arr[idx] == ch) {
                ans[qi] = freq.lastKey();
                continue;
            }

            Interval cur = map.floorEntry(idx).getValue();
            removeInterval(map, freq, cur);

            if (cur.l <= idx - 1) {
                addInterval(map, freq, new Interval(cur.l, idx - 1, cur.c));
            }
            if (idx + 1 <= cur.r) {
                addInterval(map, freq, new Interval(idx + 1, cur.r, cur.c));
            }

            Interval mid = new Interval(idx, idx, ch);
            arr[idx] = ch;

            Map.Entry<Integer, Interval> leftEntry = map.floorEntry(idx - 1);
            if (leftEntry != null) {
                Interval left = leftEntry.getValue();
                if (left.r == idx - 1 && left.c == ch) {
                    removeInterval(map, freq, left);
                    mid.l = left.l;
                }
            }

            Map.Entry<Integer, Interval> rightEntry = map.ceilingEntry(idx + 1);
            if (rightEntry != null) {
                Interval right = rightEntry.getValue();
                if (right.l == idx + 1 && right.c == ch) {
                    removeInterval(map, freq, right);
                    mid.r = right.r;
                }
            }

            addInterval(map, freq, mid);
            ans[qi] = freq.lastKey();
        }

        return ans;
    }

    private void addInterval(TreeMap<Integer, Interval> map, TreeMap<Integer, Integer> freq, Interval in) {
        map.put(in.l, in);
        addLen(freq, in.len(), 1);
    }

    private void removeInterval(TreeMap<Integer, Interval> map, TreeMap<Integer, Integer> freq, Interval in) {
        map.remove(in.l);
        addLen(freq, in.len(), -1);
    }

    private void addLen(TreeMap<Integer, Integer> freq, int len, int delta) {
        int v = freq.getOrDefault(len, 0) + delta;
        if (v == 0) freq.remove(len);
        else freq.put(len, v);
    }
}
```

---

## Complexity

Each query performs a constant number of `TreeMap` operations.

So each query is roughly:

```text
O(log n)
```

Total:

```text
O((n + k) log n)
```

Space:

```text
O(n)
```

---

## Pros

- Also optimal asymptotically
- Very interesting interval-maintenance approach

## Cons

- Much more implementation-heavy than segment tree
- Easier to get wrong on splits/merges

---

# Approach 4: Recompute Entire Longest Run After Every Query

## Idea

After each character update, scan the whole string and find the longest block of equal characters.

This is straightforward but far too slow.

---

## Java Code

```java
class Solution {
    public int[] longestRepeating(String s, String queryCharacters, int[] queryIndices) {
        char[] arr = s.toCharArray();
        int q = queryIndices.length;
        int[] ans = new int[q];

        for (int i = 0; i < q; i++) {
            arr[queryIndices[i]] = queryCharacters.charAt(i);
            ans[i] = longestRun(arr);
        }

        return ans;
    }

    private int longestRun(char[] arr) {
        int best = 1, cur = 1;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] == arr[i - 1]) {
                cur++;
            } else {
                cur = 1;
            }
            best = Math.max(best, cur);
        }
        return best;
    }
}
```

---

## Complexity

Each query scans the whole string:

```text
O(n)
```

So total:

```text
O(n * k)
```

This is too slow for `10^5`.

---

## Pros

- Very easy to understand

## Cons

- Completely impractical for the given constraints

---

# Deep Intuition

## Why local updates still need global structure

A query only changes one character, so it feels local.

But that one change can:

- split one run into two
- merge with the left neighbor
- merge with the right neighbor
- or merge all three into one longer run

So although the modification is local, the **global maximum** can change.

That is why we need a data structure that can update locally but answer globally.

---

## Why prefix/suffix summary is the right segment-tree abstraction

For any segment, if you want to merge it with a neighboring segment, the only cross-boundary repeating substring that matters is:

- suffix of left
- prefix of right

That is exactly why storing prefix length and suffix length is enough.

Everything else internal is already captured by the segment’s best value.

This is the same pattern used in several interval/string segment-tree problems.

---

## Why segment tree is usually the cleanest solution here

Compared with the run-maintenance `TreeMap` approach:

- segment tree is easier to reason about formally
- updates are always predictable `O(log n)`
- no complicated splitting/merging cases with intervals

That is why it is the most standard accepted approach.

---

# Correctness Sketch for Approach 1

We prove the segment tree solution is correct.

## Node invariant

For every segment-tree node representing interval `S`, the stored fields correctly describe:

- the first character in `S`
- the last character in `S`
- the longest repeating prefix in `S`
- the longest repeating suffix in `S`
- the longest repeating substring anywhere in `S`

## Base case

For a leaf node of length 1:

- first and last character are that character
- prefix, suffix, and best are all 1

So the invariant holds.

## Merge step

Suppose the invariant holds for left child `L` and right child `R`.

Then for the merged segment:

- the best substring is either entirely in `L`, entirely in `R`, or crosses the boundary
- a crossing substring exists only if `L.rightChar == R.leftChar`
- in that case, its length is `L.suffixLen + R.prefixLen`

Similarly:

- the merged prefix is either `L.prefixLen`, or all of `L` plus part of `R` if the whole left segment is one character and boundary chars match
- suffix is symmetric

Therefore the merge computes exactly the correct summary for the parent.

By induction, the root always stores the correct answer for the whole string.

Since point updates only modify one leaf and rebuild along its path, the root remains correct after every query.

---

# Example Walkthrough

## Example 1

```text
s = "babacc"
queryCharacters = "bcb"
queryIndices = [1,3,3]
```

### After query 1

Update index 1 to `'b'`:

```text
"bbbacc"
```

Runs:

```text
"bbb", "a", "cc"
```

Longest run = 3

### After query 2

Update index 3 to `'c'`:

```text
"bbbccc"
```

Runs:

```text
"bbb", "ccc"
```

Longest run = 3

### After query 3

Update index 3 to `'b'`:

```text
"bbbbcc"
```

Runs:

```text
"bbbb", "cc"
```

Longest run = 4

Answer:

```text
[3,3,4]
```

---

## Example 2

```text
s = "abyzz"
queryCharacters = "aa"
queryIndices = [2,1]
```

### After query 1

Index 2 becomes `'a'`:

```text
"abazz"
```

Longest run:

```text
"zz" -> 2
```

### After query 2

Index 1 becomes `'a'`:

```text
"aaazz"
```

Longest run:

```text
"aaa" -> 3
```

Answer:

```text
[2,3]
```

---

# Final Recommended Java Solution

This is the version I would submit.

```java
class Solution {
    static class Node {
        int len;
        char leftChar, rightChar;
        int prefixLen, suffixLen, best;

        Node() {}

        Node(char c) {
            len = 1;
            leftChar = rightChar = c;
            prefixLen = suffixLen = best = 1;
        }
    }

    private Node[] tree;
    private char[] arr;

    public int[] longestRepeating(String s, String queryCharacters, int[] queryIndices) {
        int n = s.length();
        arr = s.toCharArray();
        tree = new Node[4 * n];

        build(1, 0, n - 1);

        int q = queryIndices.length;
        int[] ans = new int[q];

        for (int i = 0; i < q; i++) {
            int idx = queryIndices[i];
            char ch = queryCharacters.charAt(i);

            if (arr[idx] != ch) {
                arr[idx] = ch;
                update(1, 0, n - 1, idx);
            }

            ans[i] = tree[1].best;
        }

        return ans;
    }

    private void build(int node, int l, int r) {
        if (l == r) {
            tree[node] = new Node(arr[l]);
            return;
        }

        int mid = (l + r) >>> 1;
        build(node << 1, l, mid);
        build(node << 1 | 1, mid + 1, r);
        tree[node] = merge(tree[node << 1], tree[node << 1 | 1]);
    }

    private void update(int node, int l, int r, int idx) {
        if (l == r) {
            tree[node] = new Node(arr[idx]);
            return;
        }

        int mid = (l + r) >>> 1;
        if (idx <= mid) {
            update(node << 1, l, mid, idx);
        } else {
            update(node << 1 | 1, mid + 1, r, idx);
        }

        tree[node] = merge(tree[node << 1], tree[node << 1 | 1]);
    }

    private Node merge(Node a, Node b) {
        Node res = new Node();
        res.len = a.len + b.len;
        res.leftChar = a.leftChar;
        res.rightChar = b.rightChar;

        res.prefixLen = a.prefixLen;
        if (a.prefixLen == a.len && a.rightChar == b.leftChar) {
            res.prefixLen = a.len + b.prefixLen;
        }

        res.suffixLen = b.suffixLen;
        if (b.suffixLen == b.len && a.rightChar == b.leftChar) {
            res.suffixLen = b.len + a.suffixLen;
        }

        res.best = Math.max(a.best, b.best);
        if (a.rightChar == b.leftChar) {
            res.best = Math.max(res.best, a.suffixLen + b.prefixLen);
        }

        return res;
    }
}
```

---

# Comparison of Approaches

| Approach   | Main Idea                                     |    Time Complexity | Space Complexity | Recommended |
| ---------- | --------------------------------------------- | -----------------: | ---------------: | ----------- |
| Approach 1 | Segment tree with prefix/suffix/best merge    | `O((n + k) log n)` |           `O(n)` | Yes         |
| Approach 2 | Segment tree with explicit intervals in nodes | `O((n + k) log n)` |           `O(n)` | Yes         |
| Approach 3 | TreeMap of runs + multiset of lengths         | `O((n + k) log n)` |           `O(n)` | Good        |
| Approach 4 | Recompute after each query                    |            `O(nk)` |           `O(n)` | No          |

---

# Pattern Recognition Takeaway

This problem is a classic example of:

- point updates on a string
- global query after every update
- answer depends on merging local segments

That strongly suggests:

- segment tree
- store enough summary information to merge two neighboring ranges

Whenever the answer to an interval can be reconstructed from left summary + right summary + boundary interaction, a segment tree is usually the right tool.

---

# Final Takeaway

The cleanest solution is:

1. build a segment tree over the string
2. each node stores:
   - left boundary char
   - right boundary char
   - longest repeating prefix
   - longest repeating suffix
   - longest repeating substring
3. after each point update, rebuild the affected path
4. read the answer from the root

That gives an efficient and robust `O(log n)` per query solution.
