# 2197. Replace Non-Coprime Numbers in Array — Intuition and Stack Solution

## Intuition

Since the problem statement asserts (without proof) that **any order of replacing adjacent non‑coprime numbers leads to the same final result**, we can process the array **from left to right**.

A convenient way to simulate this process is using a **stack**.

### Key Idea

While traversing the array:

1. Maintain a stack (or list) of processed numbers.
2. For each new number `num`:
   - Compare it with the element at the top of the stack.
   - If they are **non‑coprime** (`gcd > 1`):
     - Replace both with their **LCM**.
     - Remove the top element.
     - Continue checking with the new top of the stack.
   - Stop once the top element is **coprime** with the current number.
3. Push the resulting value into the stack.

The stack eventually contains the **final modified array**.

The elements in the stack from **bottom to top** form the final answer.

---

# Java Implementation

```java
class Solution {

    public List<Integer> replaceNonCoprimes(int[] nums) {
        List<Integer> ans = new ArrayList<>();

        for (int num : nums) {
            while (!ans.isEmpty()) {
                int last = ans.get(ans.size() - 1);
                int g = gcd(last, num);

                if (g > 1) {
                    num = (last / g) * num; // compute LCM
                    ans.remove(ans.size() - 1);
                } else {
                    break;
                }
            }

            ans.add(num);
        }

        return ans;
    }

    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
}
```

---

# Complexity Analysis

Let:

```
n = nums.length
C = max(nums[i])
```

## Time Complexity

```
O(n log C)
```

Explanation:

- Each number is pushed onto the stack at most once.
- During merging, we may compute GCD multiple times.
- Each GCD computation costs:

```
O(log C)
```

Thus the overall complexity becomes:

```
O(n log C)
```

---

## Space Complexity

```
O(1)
```

Explanation:

No additional auxiliary space is used beyond the stack storing the result.
