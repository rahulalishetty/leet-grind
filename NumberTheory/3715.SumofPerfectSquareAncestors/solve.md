# 3715. Sum of Perfect Square Ancestors — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public long sumOfAncestors(int n, int[][] edges, int[] nums) {

    }
}
```

---

# Problem Restatement

We are given:

- a rooted tree with root `0`
- `nums[i]` assigned to each node `i`

For each node `i`, define:

```text
t_i = number of ancestors a of i such that nums[i] * nums[a] is a perfect square
```

We need:

```text
sum of t_i over all nodes i from 1 to n-1
```

---

# Core Insight

The condition:

```text
nums[i] * nums[a] is a perfect square
```

has a classic number-theory characterization.

A number is a perfect square iff every prime exponent in its factorization is even.

So if we write each value in terms of its **square-free kernel**:

- remove all prime factors with even exponent
- keep only primes with odd exponent

then two numbers `x` and `y` satisfy:

```text
x * y is a perfect square
```

iff their square-free kernels are equal.

That is the decisive simplification.

---

# Square-Free Kernel

Example:

```text
12 = 2^2 * 3   -> kernel = 3
18 = 2 * 3^2   -> kernel = 2
8  = 2^3       -> kernel = 2
2  = 2         -> kernel = 2
```

Notice:

```text
8 * 2 = 16
```

is a perfect square, and both have the same kernel `2`.

So the original condition becomes:

> For node `i`, count how many ancestors have the same square-free kernel as `nums[i]`.

That turns the tree problem into a path-frequency problem.

---

# Tree Reformulation

Let:

```text
kernel[i] = squareFreeKernel(nums[i])
```

Then while traversing from the root to a node, if we maintain counts of kernels currently present on the root-to-parent path, the contribution of node `i` is simply:

```text
count[kernel[i]]
```

among current ancestors.

So we can solve the problem with one DFS:

1. enter node
2. answer contribution using current ancestor counts
3. add this node’s kernel
4. recurse to children
5. remove this node’s kernel on backtracking

This is exactly the standard “frequency on root-to-current path” pattern.

---

# Approach 1 — DFS With Square-Free Kernel Frequency Map (Recommended)

## Idea

### Step 1

Precompute smallest prime factors up to `10^5`, because:

```text
nums[i] <= 10^5
```

### Step 2

Compute each node’s square-free kernel.

### Step 3

Build the tree adjacency list.

### Step 4

Run DFS from root `0`, maintaining:

```text
freq[kernel] = how many ancestors on current root-to-node path have this kernel
```

At node `u`:

- contribution is `freq[kernel[u]]`
- then add `kernel[u]` before visiting children
- remove it after finishing children

That yields the total answer in linear time after preprocessing.

---

## Why this works

A node only cares about its ancestors.

During DFS, the recursion stack exactly matches the current ancestor chain.

So the frequency map always represents the correct ancestor multiset for the current node.

And because the perfect-square condition is equivalent to kernel equality, the count lookup gives `t_i` directly.

---

## Java Code

```java
import java.util.*;

class Solution {
    public long sumOfAncestors(int n, int[][] edges, int[] nums) {
        int maxVal = 100000;
        int[] spf = smallestPrimeFactor(maxVal);

        int[] kernel = new int[n];
        for (int i = 0; i < n; i++) {
            kernel[i] = squareFreeKernel(nums[i], spf);
        }

        List<Integer>[] graph = new ArrayList[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        for (int[] e : edges) {
            int u = e[0], v = e[1];
            graph[u].add(v);
            graph[v].add(u);
        }

        Map<Integer, Integer> freq = new HashMap<>();
        return dfs(0, -1, graph, kernel, freq);
    }

    private long dfs(int u, int parent, List<Integer>[] graph, int[] kernel, Map<Integer, Integer> freq) {
        long ans = freq.getOrDefault(kernel[u], 0);

        freq.put(kernel[u], freq.getOrDefault(kernel[u], 0) + 1);

        for (int v : graph[u]) {
            if (v == parent) continue;
            ans += dfs(v, u, graph, kernel, freq);
        }

        int c = freq.get(kernel[u]) - 1;
        if (c == 0) freq.remove(kernel[u]);
        else freq.put(kernel[u], c);

        return ans;
    }

    private int[] smallestPrimeFactor(int n) {
        int[] spf = new int[n + 1];
        for (int i = 0; i <= n; i++) spf[i] = i;
        for (int i = 2; i * i <= n; i++) {
            if (spf[i] != i) continue;
            for (int j = i * i; j <= n; j += i) {
                if (spf[j] == j) spf[j] = i;
            }
        }
        return spf;
    }

    private int squareFreeKernel(int x, int[] spf) {
        int result = 1;
        while (x > 1) {
            int p = spf[x];
            int cnt = 0;
            while (x % p == 0) {
                x /= p;
                cnt ^= 1; // only parity matters
            }
            if (cnt == 1) result *= p;
        }
        return result;
    }
}
```

---

## Complexity

Let:

- `n` = number of nodes
- `A` = max value in nums, here at most `10^5`

Then:

- SPF preprocessing: `O(A log log A)` or close to linear
- kernel computation for all nodes: about `O(n log A)`
- DFS traversal: `O(n)`

So overall:

```text
Time:  O(A log log A + n log A)
Space: O(A + n)
```

This is efficient for the constraints.

---

# Approach 2 — DFS With Compressed Kernel IDs + Array Frequency

## Idea

The previous solution uses a `HashMap<Integer, Integer>` for frequencies.

But square-free kernels are at most `10^5`, and often fewer distinct kernels appear in the input.

So we can compress all node kernels to dense IDs:

```text
0 .. m-1
```

Then use a plain `int[] freq` instead of a hash map.

This is a small optimization, but it makes the solution faster and more memory-predictable.

---

## Java Code

```java
import java.util.*;

class Solution {
    public long sumOfAncestors(int n, int[][] edges, int[] nums) {
        int maxVal = 100000;
        int[] spf = smallestPrimeFactor(maxVal);

        int[] rawKernel = new int[n];
        for (int i = 0; i < n; i++) {
            rawKernel[i] = squareFreeKernel(nums[i], spf);
        }

        Map<Integer, Integer> id = new HashMap<>();
        int[] kernel = new int[n];
        int nextId = 0;
        for (int i = 0; i < n; i++) {
            if (!id.containsKey(rawKernel[i])) {
                id.put(rawKernel[i], nextId++);
            }
            kernel[i] = id.get(rawKernel[i]);
        }

        List<Integer>[] graph = new ArrayList[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        for (int[] e : edges) {
            int u = e[0], v = e[1];
            graph[u].add(v);
            graph[v].add(u);
        }

        int[] freq = new int[nextId];
        return dfs(0, -1, graph, kernel, freq);
    }

    private long dfs(int u, int parent, List<Integer>[] graph, int[] kernel, int[] freq) {
        long ans = freq[kernel[u]];
        freq[kernel[u]]++;

        for (int v : graph[u]) {
            if (v == parent) continue;
            ans += dfs(v, u, graph, kernel, freq);
        }

        freq[kernel[u]]--;
        return ans;
    }

    private int[] smallestPrimeFactor(int n) {
        int[] spf = new int[n + 1];
        for (int i = 0; i <= n; i++) spf[i] = i;
        for (int i = 2; i * i <= n; i++) {
            if (spf[i] != i) continue;
            for (int j = i * i; j <= n; j += i) {
                if (spf[j] == j) spf[j] = i;
            }
        }
        return spf;
    }

    private int squareFreeKernel(int x, int[] spf) {
        int result = 1;
        while (x > 1) {
            int p = spf[x];
            int parity = 0;
            while (x % p == 0) {
                x /= p;
                parity ^= 1;
            }
            if (parity == 1) result *= p;
        }
        return result;
    }
}
```

---

## Complexity

Same asymptotic complexity as Approach 1:

```text
Time:  O(A log log A + n log A)
Space: O(A + n)
```

but slightly better constants.

---

# Approach 3 — Naive Ancestor Walk Per Node (Too Slow)

## Idea

For each node, walk up its ancestors to the root.

For each ancestor, check whether:

```text
nums[i] * nums[ancestor]
```

is a perfect square.

This is straightforward but far too slow in the worst case.

---

## Java Code

```java
import java.util.*;

class Solution {
    public long sumOfAncestors(int n, int[][] edges, int[] nums) {
        List<Integer>[] graph = new ArrayList[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        for (int[] e : edges) {
            graph[e[0]].add(e[1]);
            graph[e[1]].add(e[0]);
        }

        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        buildParent(0, -1, graph, parent);

        long ans = 0;
        for (int i = 1; i < n; i++) {
            int cur = parent[i];
            while (cur != -1) {
                long prod = 1L * nums[i] * nums[cur];
                if (isPerfectSquare(prod)) ans++;
                cur = parent[cur];
            }
        }

        return ans;
    }

    private void buildParent(int u, int p, List<Integer>[] graph, int[] parent) {
        parent[u] = p;
        for (int v : graph[u]) {
            if (v != p) buildParent(v, u, graph, parent);
        }
    }

    private boolean isPerfectSquare(long x) {
        long r = (long) Math.sqrt(x);
        while (r * r < x) r++;
        while (r * r > x) r--;
        return r * r == x;
    }
}
```

---

## Why it fails

In a chain tree, each node has `O(n)` ancestors, so total work becomes:

```text
O(n^2)
```

which is too slow for:

```text
n <= 10^5
```

---

# Detailed Walkthrough

## Example 1

```text
n = 3
edges = [[0,1],[1,2]]
nums = [2,8,2]
```

Square-free kernels:

- `2` -> `2`
- `8 = 2^3` -> `2`
- `2` -> `2`

All three nodes have the same kernel `2`.

Now DFS:

- root `0`: no ancestors, contributes `0`; freq[2] becomes 1
- node `1`: one ancestor with kernel `2`, contributes `1`; freq[2] becomes 2
- node `2`: two ancestors with kernel `2`, contributes `2`

Total:

```text
1 + 2 = 3
```

---

## Example 2

```text
nums = [1,2,4]
```

Kernels:

- `1` -> `1`
- `2` -> `2`
- `4 = 2^2` -> `1`

Tree:

- node `1` has ancestor `0`
  - kernels `2` and `1` differ -> no contribution
- node `2` has ancestor `0`
  - kernels `1` and `1` match -> contribution `1`

Total:

```text
1
```

---

## Example 3

```text
nums = [1,2,9,4]
```

Kernels:

- `1` -> `1`
- `2` -> `2`
- `9 = 3^2` -> `1`
- `4 = 2^2` -> `1`

Now:

- node `1`: ancestor kernel `1`, node kernel `2` -> no match
- node `2`: ancestor kernel `1`, node kernel `1` -> one match
- node `3`: ancestors are node 1(kernel 2), node 0(kernel 1)
  - node 3 kernel is `1`
  - matches only node 0

Total:

```text
2
```

---

# Why Kernel Equality Is Equivalent to Perfect-Square Product

Let:

```text
x = ∏ p_i^(a_i)
y = ∏ p_i^(b_i)
```

Then:

```text
x * y = ∏ p_i^(a_i + b_i)
```

This is a perfect square iff every exponent `a_i + b_i` is even.

That happens exactly when:

```text
a_i mod 2 = b_i mod 2
```

for every prime `p_i`.

So `x` and `y` must have exactly the same set of primes with odd exponent parity.

That is precisely the square-free kernel.

So:

```text
x * y is a perfect square  <=>  kernel(x) = kernel(y)
```

---

# Common Pitfalls

## 1. Checking perfect squares by multiplying every ancestor pair directly

That is too slow and unnecessary.

---

## 2. Confusing full factorization with parity of exponents

Only odd/even parity matters.

---

## 3. Forgetting to backtrack DFS frequency counts

The frequency structure must represent only the current ancestor path.

---

## 4. Using recursion without care on very deep trees

In Java, an iterative DFS may be safer if stack depth is a concern.
The recursive version is clearer, but for adversarial chain trees recursion depth may be risky.

---

# Best Approach

## Recommended: Square-free kernel + DFS ancestor-frequency counting

This is the cleanest solution because:

- the number-theory condition collapses to equality of kernels
- ancestor queries become path-frequency counting
- one DFS solves the entire problem

---

# Iterative DFS Variant (Safer in Java)

Because `n` can be `10^5`, a deeply skewed tree may overflow recursion in Java.

So here is a safer iterative version of the recommended method.

```java
import java.util.*;

class Solution {
    public long sumOfAncestors(int n, int[][] edges, int[] nums) {
        int maxVal = 100000;
        int[] spf = smallestPrimeFactor(maxVal);

        int[] rawKernel = new int[n];
        for (int i = 0; i < n; i++) {
            rawKernel[i] = squareFreeKernel(nums[i], spf);
        }

        Map<Integer, Integer> id = new HashMap<>();
        int[] kernel = new int[n];
        int nextId = 0;
        for (int i = 0; i < n; i++) {
            if (!id.containsKey(rawKernel[i])) {
                id.put(rawKernel[i], nextId++);
            }
            kernel[i] = id.get(rawKernel[i]);
        }

        List<Integer>[] graph = new ArrayList[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        for (int[] e : edges) {
            int u = e[0], v = e[1];
            graph[u].add(v);
            graph[v].add(u);
        }

        int[] freq = new int[nextId];
        long ans = 0;

        // stack entries: {node, parent, state}
        // state 0 = enter, state 1 = exit
        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{0, -1, 0});

        while (!stack.isEmpty()) {
            int[] cur = stack.pop();
            int u = cur[0], parent = cur[1], state = cur[2];

            if (state == 0) {
                ans += freq[kernel[u]];
                freq[kernel[u]]++;

                stack.push(new int[]{u, parent, 1});
                List<Integer> adj = graph[u];
                for (int i = adj.size() - 1; i >= 0; i--) {
                    int v = adj.get(i);
                    if (v != parent) {
                        stack.push(new int[]{v, u, 0});
                    }
                }
            } else {
                freq[kernel[u]]--;
            }
        }

        return ans;
    }

    private int[] smallestPrimeFactor(int n) {
        int[] spf = new int[n + 1];
        for (int i = 0; i <= n; i++) spf[i] = i;
        for (int i = 2; i * i <= n; i++) {
            if (spf[i] != i) continue;
            for (int j = i * i; j <= n; j += i) {
                if (spf[j] == j) spf[j] = i;
            }
        }
        return spf;
    }

    private int squareFreeKernel(int x, int[] spf) {
        int result = 1;
        while (x > 1) {
            int p = spf[x];
            int parity = 0;
            while (x % p == 0) {
                x /= p;
                parity ^= 1;
            }
            if (parity == 1) result *= p;
        }
        return result;
    }
}
```

---

# Complexity Summary

```text
Time:  O(A log log A + n log A)
Space: O(A + n)
```

with:

```text
A = 10^5
```

This is fully efficient for the constraints.

---

# Final Takeaway

The crucial trick is:

- a product is a perfect square iff prime exponents are all even
- therefore two numbers form a perfect-square product iff their square-free kernels are equal

After that, the problem is no longer about multiplication at all.
It becomes:

> for each node, count ancestors on the current root path having the same kernel

That is exactly what a DFS with a frequency map solves.
