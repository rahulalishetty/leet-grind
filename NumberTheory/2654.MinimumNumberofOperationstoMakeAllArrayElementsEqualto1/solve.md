# 2654. Minimum Number of Operations to Make All Array Elements Equal to 1

## Approach: Greedy

### Intuition

First, check the **gcd of all numbers** in `nums`.

1. **If the gcd of the entire array is greater than 1**, it is impossible to make any element equal to `1`.

   Therefore:

   ```
   return -1
   ```

2. **If there is already a `1` in the array**:

   We can use that `1` to convert adjacent elements into `1` using the gcd operation.

   For each non‑1 element:

   ```
   gcd(x,1) = 1
   ```

   So every non‑1 element can become `1` in **one operation**.

   If `num1` is the number of `1`s:

   ```
   operations = n - num1
   ```

3. **Otherwise (no 1 exists yet)**:

   We must **create the first `1`**.

   To do that, find the **smallest subarray whose gcd equals `1`**.

   Suppose that subarray length is:

   ```
   minLen
   ```

   To reduce that subarray to `1`:

   ```
   operations = minLen - 1
   ```

   After obtaining one `1`, we convert the remaining elements:

   ```
   operations = n - 1
   ```

   Total:

   ```
   total operations = (minLen - 1) + (n - 1)
                    = minLen + n - 2
   ```

---

# Algorithm

1. Compute:
   - `num1` = number of `1`s
   - `g` = gcd of entire array

2. If `num1 > 0`:

   ```
   return n - num1
   ```

3. If `g > 1`:

   ```
   return -1
   ```

4. Otherwise:
   - Enumerate all intervals
   - Track gcd progressively
   - Find smallest interval with gcd = 1

5. Let its length be `minLen`.

   ```
   answer = minLen + n - 2
   ```

---

# Java Implementation

```java
class Solution {

    public int minOperations(int[] nums) {
        int n = nums.length;
        int num1 = 0;
        int g = 0;

        for (int x : nums) {
            if (x == 1) {
                num1++;
            }
            g = gcd(g, x);
        }

        if (num1 > 0) {
            return n - num1;
        }

        if (g > 1) {
            return -1;
        }

        int minLen = n;

        for (int i = 0; i < n; i++) {
            int currentGcd = 0;

            for (int j = i; j < n; j++) {
                currentGcd = gcd(currentGcd, nums[j]);

                if (currentGcd == 1) {
                    minLen = Math.min(minLen, j - i + 1);
                    break;
                }
            }
        }

        return minLen + n - 2;
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

# Complexity Analysis

Let:

- `n` = length of `nums`
- `M` = maximum value in `nums`

### Time Complexity

Enumerating all subarrays:

```
O(n²)
```

Each gcd computation takes:

```
O(log M)
```

So overall:

```
O(n² log M)
```

---

### Space Complexity

Only a few extra variables are used:

```
O(1)
```

constant auxiliary space.
