# 526. Beautiful Arrangement — Approaches

## Approach 1: Brute Force (Time Limit Exceeded)

### Algorithm

In the brute force method, we generate **all permutations** of numbers from `1` to `N`.

After generating a permutation, we check if it satisfies the **beautiful arrangement condition**:

For every position `i` (1-indexed):

- `perm[i] % i == 0`
- OR `i % perm[i] == 0`

If the condition holds for all positions, we increment the count.

### Implementation (Java)

```java
public class Solution {

    int count = 0;

    public int countArrangement(int N) {
        int[] nums = new int[N];
        for (int i = 1; i <= N; i++) nums[i - 1] = i;
        permute(nums, 0);
        return count;
    }

    public void permute(int[] nums, int l) {
        if (l == nums.length - 1) {
            int i;
            for (i = 1; i <= nums.length; i++) {
                if (nums[i - 1] % i != 0 && i % nums[i - 1] != 0) break;
            }
            if (i == nums.length + 1) {
                count++;
            }
        }
        for (int i = l; i < nums.length; i++) {
            swap(nums, i, l);
            permute(nums, l + 1);
            swap(nums, i, l);
        }
    }

    public void swap(int[] nums, int x, int y) {
        int temp = nums[x];
        nums[x] = nums[y];
        nums[y] = temp;
    }
}
```

### Complexity Analysis

**Time Complexity**

```
O(n!)
```

**Space Complexity**

```
O(n)
```

---

## Approach 2: Better Brute Force (Accepted)

### Algorithm

Instead of generating the full permutation first, we check the divisibility condition **while constructing the permutation**.

If the condition fails, we stop exploring that branch.

### Implementation (Java)

```java
public class Solution {

    int count = 0;

    public int countArrangement(int N) {
        int[] nums = new int[N];
        for (int i = 1; i <= N; i++) nums[i - 1] = i;
        permute(nums, 0);
        return count;
    }

    public void permute(int[] nums, int l) {

        if (l == nums.length) {
            count++;
        }

        for (int i = l; i < nums.length; i++) {

            swap(nums, i, l);

            if (nums[l] % (l + 1) == 0 || (l + 1) % nums[l] == 0)
                permute(nums, l + 1);

            swap(nums, i, l);
        }
    }

    public void swap(int[] nums, int x, int y) {
        int temp = nums[x];
        nums[x] = nums[y];
        nums[y] = temp;
    }
}
```

### Complexity Analysis

**Time Complexity**

```
O(k)
```

Where `k` is the number of valid permutations explored.

**Space Complexity**

```
O(n)
```

---

## Approach 3: Backtracking (Optimal)

### Algorithm

Instead of generating permutations explicitly, we build them **position by position**.

For each position we try numbers that:

- are not already used
- satisfy the divisibility rule

We maintain a `visited` array.

### Implementation (Java)

```java
public class Solution {

    int count = 0;

    public int countArrangement(int N) {
        boolean[] visited = new boolean[N + 1];
        calculate(N, 1, visited);
        return count;
    }

    public void calculate(int N, int pos, boolean[] visited) {

        if (pos > N) {
            count++;
            return;
        }

        for (int i = 1; i <= N; i++) {

            if (!visited[i] && (pos % i == 0 || i % pos == 0)) {

                visited[i] = true;

                calculate(N, pos + 1, visited);

                visited[i] = false;
            }
        }
    }
}
```

### Complexity Analysis

**Time Complexity**

```
O(k)
```

Where `k` is the number of valid arrangements.

**Space Complexity**

```
O(n)
```

---

# Summary

| Approach           | Idea                               | Time Complexity | Space Complexity |
| ------------------ | ---------------------------------- | --------------- | ---------------- |
| Brute Force        | Generate all permutations          | O(n!)           | O(n)             |
| Better Brute Force | Early pruning                      | O(k)            | O(n)             |
| Backtracking       | Place numbers position by position | O(k)            | O(n)             |

---

# Key Insight

The most important optimization is **pruning invalid permutations early**.

By validating the divisibility rule **during construction**, we dramatically reduce the search space compared to generating all permutations first.
