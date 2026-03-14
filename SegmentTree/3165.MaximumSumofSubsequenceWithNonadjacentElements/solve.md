# Maximum Sum of Subsequence With Non-adjacent Elements — Segment Tree DP Approach

## Intuition

The problem asks for the **maximum sum of a subsequence with no two adjacent elements**, and after each update query, we must report the new maximum.

For a single array (no updates), this is classic Dynamic Programming:

```
dp[i] = max(dp[i-1], dp[i-2] + nums[i])
```

But here we have **multiple update queries**.

If we recompute the full DP after every update:

```
Time per query = O(n)
Total = O(n × q)
```

This is too slow.

So we need a faster approach.

---

# Key Observation

When we split the array into segments (using a segment tree), the only possible conflict happens at the boundary:

- the **last element of the left segment**
- the **first element of the right segment**

Therefore, instead of storing just sums in each node, we store **boundary DP states**.

---

# What Each Segment Tree Node Stores

Each node stores **four DP states**.

```
dp[0] → first not taken, last not taken
dp[1] → first not taken, last taken
dp[2] → first taken, last not taken
dp[3] → first taken, last taken
```

Each value represents the **maximum subsequence sum inside that segment under that boundary condition**.

This information is sufficient to merge two segments safely.

---

# Base Case (Leaf Node)

For a single element `x`:

```
Not take → sum = 0
Take → sum = x
```

So the DP values are:

```
dp[0] = 0
dp[1] = -INF
dp[2] = -INF
dp[3] = x
```

States `dp[1]` and `dp[2]` are invalid because a single element cannot have mismatched boundaries.

---

# Merge Logic

To merge **left** and **right** segments:

1. Try all 4 states from the left segment
2. Try all 4 states from the right segment

Skip the combination if:

```
left.last == 1 AND right.first == 1
```

This would select adjacent elements, which is not allowed.

Otherwise:

```
newFirst = left.first
newLast = right.last
newSum = leftSum + rightSum
```

Update the corresponding new DP state with the maximum value.

This works because:

- adjacency inside segments is already handled
- only boundary adjacency must be checked during merge

Each merge checks **16 combinations**, which is constant time.

---

# Handling Updates

For each query:

```
index, newValue
```

Steps:

1. Update the corresponding leaf node
2. Recompute ancestors using the merge operation
3. The root now contains the DP states for the entire array

The answer for the array is:

```
max(tree[0][0..3])
```

---

# Time Complexity

```
Build → O(n)
Each update → O(log n)
Each merge → O(1)
```

Total complexity:

```
O(n + q log n)
```

This is efficient enough for the problem constraints.

---

# Why This Works

Normal DP is sequential.

To make it compatible with a segment tree, we convert DP into **composable boundary states**.

By tracking:

- whether the first element of a segment is selected
- whether the last element of a segment is selected

we can merge segments safely while maintaining correctness.

---

# Final Summary

We use a **segment tree where each node stores four boundary DP states**, allowing segments to be merged in constant time while enforcing the non‑adjacent constraint. This enables updates to be processed in **O(log n)** time.

---

# Java Implementation

```java
class Solution {
    public class TreeSegment{
        long[][] tree;
        TreeSegment(int n){
            tree = new long[n*4][4];
        }

        public void build(int ind, int lft, int rgt, int[] arr){
            if(lft == rgt){
                tree[ind][0] = 0;
                tree[ind][1] = Long.MIN_VALUE;
                tree[ind][2] = Long.MIN_VALUE;
                tree[ind][3] = arr[lft];
                return;
            }

            int mid = (lft+rgt)/2;
            build(2*ind+1, lft, mid, arr);
            build(2*ind+2, mid+1, rgt, arr);
            merge(ind);
        }

        public void merge(int ind){
            int lft = 2*ind+1;
            int rgt = 2*ind+2;

            for(int i = 0;i < 4;i++) tree[ind][i] = Long.MIN_VALUE;

            for(int lf = 0;lf < 2;lf++){
                for(int ll = 0;ll < 2;ll++){
                    int lftSeg = lf*2+ll;
                    if(tree[lft][lftSeg] == Long.MIN_VALUE) continue;

                    for(int rf = 0;rf < 2;rf++){
                        for(int rl = 0;rl < 2;rl++){
                            int rgtSeg = rf*2+rl;
                            if(tree[rgt][rgtSeg] == Long.MIN_VALUE) continue;

                            if(ll == 1 && rf == 1) continue;

                            int newLft = lf;
                            int newRgt = rl;
                            int newInd = newLft*2 + newRgt;

                            tree[ind][newInd] = Math.max(
                                tree[ind][newInd],
                                tree[lft][lftSeg] + tree[rgt][rgtSeg]
                            );
                        }
                    }
                }
            }
        }

        public void queryPos(int ind, int tarInd, int lft, int rgt, int value){
            if(lft == rgt){
                tree[ind][3] = value;
                return;
            }

            int mid = (lft+rgt)/2;

            if(tarInd <= mid){
                queryPos(2*ind+1, tarInd, lft, mid, value);
            }
            else{
                queryPos(2*ind+2, tarInd, mid+1, rgt, value);
            }

            merge(ind);
        }
    }

    public int maximumSumSubsequence(int[] nums, int[][] queries) {
        int len = nums.length;
        TreeSegment tree = new TreeSegment(len);
        int MOD = (int)1e9+7;
        long ans = 0;

        tree.build(0, 0, len-1, nums);

        for(int[] query : queries){
            tree.queryPos(0, query[0], 0, len-1, query[1]);

            long max = Long.MIN_VALUE;
            for(long ele: tree.tree[0]){
                max = Math.max(max, ele);
            }

            ans = (ans + max) % MOD;
        }

        return (int) ans;
    }
}
```
