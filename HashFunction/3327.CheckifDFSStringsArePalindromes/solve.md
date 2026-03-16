# 3327. Check if DFS Strings Are Palindromes

## Problem Restatement

You are given:

- a rooted tree with nodes `0 ... n - 1`
- `parent[i]` gives the parent of node `i`
- a string `s` where `s[i]` is the character of node `i`

For any node `x`, define a DFS traversal:

1. visit all children of `x` in increasing order
2. after all children are processed, append `s[x]` to the DFS string

Let the produced string for subtree `x` be `dfsStr(x)`.

We must return a boolean array `answer` where:

- `answer[x] = true` if `dfsStr(x)` is a palindrome
- otherwise `false`

---

# Key Observation

For a node `x`, the DFS string is exactly the **postorder string of the subtree rooted at `x`**.

So the task is:

> For every node, determine whether the postorder string of its subtree is a palindrome.

A naive solution would generate the full string for each node independently, but that would be far too slow for `n <= 10^5`.

We need to reuse structure across subtrees.

---

# Structural Insight

If we do **one global DFS from root 0** in the exact required child order, and append characters in postorder, then:

- every subtree of node `x` contributes a **contiguous segment** in that global DFS string

This is the crucial insight.

That means the problem becomes:

1. Build one global postorder DFS string
2. For each node, find the substring interval corresponding to its subtree
3. Check if that substring is a palindrome

So the problem reduces from a tree problem to a **substring palindrome query** problem.

---

# Approach 1: Naive DFS Per Node

## Intuition

For each node `x`:

- run the DFS rooted at `x`
- build the string explicitly
- check whether it is a palindrome

This directly matches the statement.

---

## Why It Works

The DFS definition is followed exactly.

So the generated string is correct, and checking palindrome on that string gives the correct answer.

---

## Why It Is Too Slow

Suppose the tree is a chain of length `n`.

Then:

- subtree of node `0` has size `n`
- subtree of node `1` has size `n - 1`
- subtree of node `2` has size `n - 2`
- ...

Total work becomes:

```text
n + (n - 1) + (n - 2) + ... + 1 = O(n^2)
```

With `n = 10^5`, this is impossible.

---

## Java Code

```java
import java.util.*;

class Solution {
    public boolean[] findAnswer(int[] parent, String s) {
        int n = parent.length;
        List<Integer>[] children = new ArrayList[n];
        for (int i = 0; i < n; i++) children[i] = new ArrayList<>();

        for (int i = 1; i < n; i++) {
            children[parent[i]].add(i);
        }

        for (int i = 0; i < n; i++) {
            Collections.sort(children[i]);
        }

        boolean[] ans = new boolean[n];

        for (int i = 0; i < n; i++) {
            StringBuilder sb = new StringBuilder();
            dfsBuild(i, children, s, sb);
            ans[i] = isPalindrome(sb);
        }

        return ans;
    }

    private void dfsBuild(int node, List<Integer>[] children, String s, StringBuilder sb) {
        for (int child : children[node]) {
            dfsBuild(child, children, s, sb);
        }
        sb.append(s.charAt(node));
    }

    private boolean isPalindrome(StringBuilder sb) {
        int l = 0, r = sb.length() - 1;
        while (l < r) {
            if (sb.charAt(l) != sb.charAt(r)) return false;
            l++;
            r--;
        }
        return true;
    }
}
```

---

## Complexity

### Time

- Building the string for one node costs `O(size of subtree)`
- Doing that for all nodes costs `O(n^2)` in the worst case

### Space

- `O(n)` for recursion/string in worst case

---

## Verdict

This is useful only as a baseline and for understanding.

---

# Approach 2: One Global DFS + Direct Substring Check

## Intuition

Instead of running DFS from every node separately, run **one DFS from the root**.

During that DFS:

- append characters in postorder
- record the substring interval `[start[x], end[x]]` for each subtree

Then for every node:

- the DFS string of its subtree is exactly `global[start[x] ... end[x]]`

Now we only need to check whether each such substring is a palindrome.

---

## Why Each Subtree Forms a Contiguous Interval

Postorder DFS processes a subtree completely before returning to its parent.

So all characters contributed by a subtree are appended consecutively.

That is why every subtree corresponds to one continuous segment in the global postorder string.

---

## Example

Suppose:

```text
parent = [-1,0,0,1,1,2]
s = "aababa"
```

Tree:

```text
0(a)
├── 1(a)
│   ├── 3(a)
│   └── 4(b)
└── 2(b)
    └── 5(a)
```

Global postorder DFS string:

- subtree(3) -> "a"
- subtree(4) -> "b"
- then node 1 -> "a"
- subtree(5) -> "a"
- then node 2 -> "b"
- then node 0 -> "a"

So global string is:

```text
"abaaba"
```

Now:

- subtree of 1 = `"aba"` = contiguous segment
- subtree of 2 = `"ab"` = contiguous segment
- subtree of 0 = full string

This is exactly what we need.

---

## Algorithm

1. Build adjacency list of children
2. DFS from root in increasing child order
3. Before exploring a node, remember current length of global string as `start[node]`
4. After exploring all children, append `s[node]`
5. The final index after append is `end[node]`
6. For each node, check whether substring `global[start[node] ... end[node]]` is palindrome

---

## Java Code

```java
import java.util.*;

class Solution {
    private List<Integer>[] children;
    private int[] start;
    private int[] end;
    private String s;
    private StringBuilder postorder;

    public boolean[] findAnswer(int[] parent, String s) {
        int n = parent.length;
        this.s = s;
        children = new ArrayList[n];
        for (int i = 0; i < n; i++) children[i] = new ArrayList<>();

        for (int i = 1; i < n; i++) {
            children[parent[i]].add(i);
        }

        for (int i = 0; i < n; i++) {
            Collections.sort(children[i]);
        }

        start = new int[n];
        end = new int[n];
        postorder = new StringBuilder();

        dfs(0);

        boolean[] ans = new boolean[n];
        for (int i = 0; i < n; i++) {
            ans[i] = isPalindrome(postorder, start[i], end[i]);
        }
        return ans;
    }

    private void dfs(int node) {
        start[node] = postorder.length();
        for (int child : children[node]) {
            dfs(child);
        }
        postorder.append(s.charAt(node));
        end[node] = postorder.length() - 1;
    }

    private boolean isPalindrome(StringBuilder sb, int l, int r) {
        while (l < r) {
            if (sb.charAt(l) != sb.charAt(r)) return false;
            l++;
            r--;
        }
        return true;
    }
}
```

---

## Complexity

### Time

- One DFS: `O(n)`
- Palindrome check for each node scans substring length
- Worst case total: `O(n^2)`

### Space

- `O(n)`

---

## Verdict

This is much better conceptually than Approach 1 because it discovers the interval property.

But direct substring checking is still too slow.

We now need a faster palindrome query structure.

---

# Approach 3: One Global DFS + Rolling Hash

## Intuition

We keep the same interval trick from Approach 2.

The only improvement is:

> Instead of checking each substring character-by-character, answer palindrome queries in `O(1)` using hashing.

This gives an efficient full solution.

---

## Core Idea

A string segment is a palindrome if it is equal to its reverse.

So for each subtree interval `[l, r]` in the global postorder string:

- compare hash of `postorder[l..r]`
- with hash of the corresponding reversed segment

If the hashes match, we treat it as palindrome.

With polynomial rolling hash, substring hashes can be computed in `O(1)` after `O(n)` preprocessing.

---

## Step-by-Step Plan

### Step 1: Build the global postorder DFS string

As before.

### Step 2: Record subtree intervals

For each node:

- `start[node]`
- `end[node]`

### Step 3: Build prefix hashes

For:

- the original postorder string
- the reversed postorder string

### Step 4: Query each subtree

For interval `[l, r]` in original string:

- corresponding reversed interval becomes `[n - 1 - r, n - 1 - l]`
- compare hashes

---

## Rolling Hash Formula

For string `str`:

```text
prefix[i+1] = prefix[i] * BASE + value(str[i])
```

Then substring hash for `[l, r]` is:

```text
prefix[r+1] - prefix[l] * powBase[r-l+1]
```

This can be computed in constant time.

---

## Why Reverse Mapping Works

If original string length is `n`, then character at index `i` maps in reversed string to:

```text
n - 1 - i
```

So substring `[l, r]` in original corresponds to reversed segment:

```text
[n - 1 - r, n - 1 - l]
```

If those two hashes are equal, then substring equals its reverse.

---

## Java Code

```java
import java.util.*;

class Solution {
    private List<Integer>[] children;
    private int[] start;
    private int[] end;
    private String s;
    private StringBuilder postorder;

    private static final long BASE = 911382323L;
    private static final long MOD = 1_000_000_007L;

    public boolean[] findAnswer(int[] parent, String s) {
        int n = parent.length;
        this.s = s;

        children = new ArrayList[n];
        for (int i = 0; i < n; i++) children[i] = new ArrayList<>();

        for (int i = 1; i < n; i++) {
            children[parent[i]].add(i);
        }

        for (int i = 0; i < n; i++) {
            Collections.sort(children[i]);
        }

        start = new int[n];
        end = new int[n];
        postorder = new StringBuilder();

        dfs(0);

        String str = postorder.toString();
        String rev = new StringBuilder(str).reverse().toString();

        long[] pow = new long[n + 1];
        long[] hash1 = new long[n + 1];
        long[] hash2 = new long[n + 1];

        pow[0] = 1;
        for (int i = 0; i < n; i++) {
            pow[i + 1] = (pow[i] * BASE) % MOD;
            hash1[i + 1] = (hash1[i] * BASE + str.charAt(i)) % MOD;
            hash2[i + 1] = (hash2[i] * BASE + rev.charAt(i)) % MOD;
        }

        boolean[] ans = new boolean[n];
        for (int i = 0; i < n; i++) {
            int l = start[i], r = end[i];
            long forwardHash = getHash(hash1, pow, l, r);

            int rl = n - 1 - r;
            int rr = n - 1 - l;
            long reverseHash = getHash(hash2, pow, rl, rr);

            ans[i] = forwardHash == reverseHash;
        }

        return ans;
    }

    private void dfs(int node) {
        start[node] = postorder.length();
        for (int child : children[node]) {
            dfs(child);
        }
        postorder.append(s.charAt(node));
        end[node] = postorder.length() - 1;
    }

    private long getHash(long[] prefix, long[] pow, int l, int r) {
        long res = (prefix[r + 1] - (prefix[l] * pow[r - l + 1]) % MOD + MOD) % MOD;
        return res;
    }
}
```

---

## Complexity

### Time

- Build tree: `O(n)`
- DFS once: `O(n)`
- Build prefix hashes: `O(n)`
- Answer all queries: `O(n)`

Total:

```text
O(n)
```

### Space

- adjacency + arrays + hash arrays: `O(n)`

---

## Practical Note About Hash Collisions

Rolling hash is extremely fast and usually accepted in practice.

But it is probabilistic:

- two different strings can theoretically have the same hash

This is rare, but possible.

To reduce risk, one may use:

- double hashing
- or another deterministic approach

That brings us to the next method.

---

# Approach 4: One Global DFS + Manacher's Algorithm

## Intuition

Approach 3 is fast but probabilistic.

Can we solve it in linear time **deterministically**?

Yes, with **Manacher’s algorithm**.

Manacher preprocesses a string and answers:

> what is the longest palindrome centered at each position?

From that information, we can test whether any substring is a palindrome in `O(1)`.

---

## Why Manacher Fits Here

Again, after one global DFS, each subtree corresponds to a substring interval.

So this becomes:

- preprocess the whole postorder string with Manacher
- for each subtree interval `[l, r]`, check in `O(1)` whether that interval is palindrome

---

## Refresher: Manacher Transformation

To unify odd/even palindromes, transform string:

```text
"abaaba"
```

into:

```text
^#a#b#a#a#b#a#$
```

Then compute array `p[i]`:

- radius of palindrome centered at `i`

Using this, any original substring can be tested.

---

## Substring Palindrome Check

For original substring `[l, r]`:

- its center in transformed string is `l + r + 2`
- its length is `r - l + 1`

The substring is palindrome iff:

```text
p[center] >= length
```

That is the key query formula.

---

## Java Code

```java
import java.util.*;

class Solution {
    private List<Integer>[] children;
    private int[] start;
    private int[] end;
    private String s;
    private StringBuilder postorder;

    public boolean[] findAnswer(int[] parent, String s) {
        int n = parent.length;
        this.s = s;

        children = new ArrayList[n];
        for (int i = 0; i < n; i++) children[i] = new ArrayList<>();

        for (int i = 1; i < n; i++) {
            children[parent[i]].add(i);
        }

        for (int i = 0; i < n; i++) {
            Collections.sort(children[i]);
        }

        start = new int[n];
        end = new int[n];
        postorder = new StringBuilder();

        dfs(0);

        String str = postorder.toString();
        int[] p = manacher(str);

        boolean[] ans = new boolean[n];
        for (int i = 0; i < n; i++) {
            int l = start[i];
            int r = end[i];
            int len = r - l + 1;

            // center in transformed string ^#c#c#...#$
            int center = l + r + 2;
            ans[i] = p[center] >= len;
        }

        return ans;
    }

    private void dfs(int node) {
        start[node] = postorder.length();
        for (int child : children[node]) {
            dfs(child);
        }
        postorder.append(s.charAt(node));
        end[node] = postorder.length() - 1;
    }

    private int[] manacher(String s) {
        StringBuilder t = new StringBuilder();
        t.append('^');
        for (int i = 0; i < s.length(); i++) {
            t.append('#');
            t.append(s.charAt(i));
        }
        t.append("#$");

        int m = t.length();
        int[] p = new int[m];
        int center = 0, right = 0;

        for (int i = 1; i < m - 1; i++) {
            int mirror = 2 * center - i;
            if (i < right) {
                p[i] = Math.min(right - i, p[mirror]);
            }

            while (t.charAt(i + 1 + p[i]) == t.charAt(i - 1 - p[i])) {
                p[i]++;
            }

            if (i + p[i] > right) {
                center = i;
                right = i + p[i];
            }
        }

        return p;
    }
}
```

---

## Complexity

### Time

- Build tree: `O(n)`
- DFS: `O(n)`
- Manacher: `O(n)`
- All queries: `O(n)`

Total:

```text
O(n)
```

### Space

- `O(n)`

---

## Why This Is Excellent

This is:

- linear time
- deterministic
- elegant once you know Manacher

The only downside is implementation complexity. Many candidates are much more comfortable with rolling hash than with Manacher.

---

# Approach 5: Iterative DFS Version for Stack Safety

## Motivation

In Java, recursion depth can be risky for trees of height up to `10^5`.

A chain-shaped tree can cause stack overflow.

So for production robustness, it is often better to compute the global postorder string iteratively.

We can combine:

- iterative DFS for interval generation
- rolling hash or Manacher for palindrome testing

Below is an iterative + rolling hash version.

---

## Java Code

```java
import java.util.*;

class Solution {
    private static final long BASE = 911382323L;
    private static final long MOD = 1_000_000_007L;

    public boolean[] findAnswer(int[] parent, String s) {
        int n = parent.length;

        List<Integer>[] children = new ArrayList[n];
        for (int i = 0; i < n; i++) children[i] = new ArrayList<>();
        for (int i = 1; i < n; i++) children[parent[i]].add(i);
        for (int i = 0; i < n; i++) Collections.sort(children[i]);

        int[] start = new int[n];
        int[] end = new int[n];
        StringBuilder post = new StringBuilder();

        // iterative postorder: [node, state]
        // state 0 = entering, state 1 = exiting
        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{0, 0});

        while (!stack.isEmpty()) {
            int[] cur = stack.pop();
            int node = cur[0], state = cur[1];

            if (state == 0) {
                start[node] = post.length();
                stack.push(new int[]{node, 1});

                List<Integer> list = children[node];
                for (int i = list.size() - 1; i >= 0; i--) {
                    stack.push(new int[]{list.get(i), 0});
                }
            } else {
                post.append(s.charAt(node));
                end[node] = post.length() - 1;
            }
        }

        String str = post.toString();
        String rev = new StringBuilder(str).reverse().toString();

        long[] pow = new long[n + 1];
        long[] h1 = new long[n + 1];
        long[] h2 = new long[n + 1];

        pow[0] = 1;
        for (int i = 0; i < n; i++) {
            pow[i + 1] = (pow[i] * BASE) % MOD;
            h1[i + 1] = (h1[i] * BASE + str.charAt(i)) % MOD;
            h2[i + 1] = (h2[i] * BASE + rev.charAt(i)) % MOD;
        }

        boolean[] ans = new boolean[n];
        for (int i = 0; i < n; i++) {
            int l = start[i], r = end[i];
            long a = getHash(h1, pow, l, r);

            int rl = n - 1 - r;
            int rr = n - 1 - l;
            long b = getHash(h2, pow, rl, rr);

            ans[i] = a == b;
        }

        return ans;
    }

    private long getHash(long[] prefix, long[] pow, int l, int r) {
        return (prefix[r + 1] - (prefix[l] * pow[r - l + 1]) % MOD + MOD) % MOD;
    }
}
```

---

## Complexity

Still:

- Time: `O(n)`
- Space: `O(n)`

But it avoids recursion overflow.

---

# Comparing the Approaches

## Approach 1: Naive DFS per node

- easiest to understand
- far too slow: `O(n^2)`

## Approach 2: One DFS + direct substring palindrome check

- important conceptual improvement
- still too slow in worst case: `O(n^2)`

## Approach 3: One DFS + rolling hash

- efficient: `O(n)`
- simple enough for interviews
- tiny theoretical collision risk

## Approach 4: One DFS + Manacher

- efficient: `O(n)`
- deterministic
- more advanced and trickier to implement

## Approach 5: Iterative DFS + rolling hash

- efficient: `O(n)`
- stack-safe in Java
- very practical

---

# Which Approach Should You Prefer?

## For learning the problem

Start with:

- Approach 1 to understand the DFS string
- then immediately observe the interval property from Approach 2

That is the conceptual breakthrough.

## For interviews

Approach 3 is usually the best balance:

- elegant
- fast
- easier than Manacher

## For maximum rigor

Approach 4 is the strongest fully deterministic linear-time method.

## For Java robustness

Approach 5 is great because it avoids recursion-depth issues.

---

# Final Recommended Explanation

A strong interview-quality explanation is:

1. Run one global postorder DFS from the root
2. Every subtree’s DFS string becomes a contiguous interval in that global string
3. Reduce the task to substring palindrome queries
4. Solve those queries in `O(1)` each after preprocessing with rolling hash or Manacher

That gives overall `O(n)`.

---

# Final Complexity Summary

## Naive

- Time: `O(n^2)`
- Space: `O(n)`

## Global DFS + direct check

- Time: `O(n^2)`
- Space: `O(n)`

## Global DFS + rolling hash

- Time: `O(n)`
- Space: `O(n)`

## Global DFS + Manacher

- Time: `O(n)`
- Space: `O(n)`

## Iterative DFS + rolling hash

- Time: `O(n)`
- Space: `O(n)`

---

# Final Takeaway

The heart of the problem is not palindrome checking itself.

The real insight is:

> the DFS string of every subtree appears as one contiguous segment in the global postorder traversal string.

Once you see that, the tree problem becomes a substring-query problem.

And once it becomes a substring-query problem, standard string tools such as:

- rolling hash
- Manacher’s algorithm

solve it efficiently.
