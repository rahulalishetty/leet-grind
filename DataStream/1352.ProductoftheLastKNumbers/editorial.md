# Product of the Last K Numbers — Prefix Product Approach

## Intuition

We need to implement a `ProductOfNumbers` class initialized with an empty integer stream that supports two operations:

- `add(int num)`: Add `num` to the stream.
- `getProduct(int k)`: Return the product of the last `k` integers in the stream.

The challenge comes from the constraints:

```
k ≤ 4 * 10^4
stream size ≤ 4 * 10^4
```

A brute force solution would compute the product by multiplying the last `k` numbers every time a query is made.
That would take:

```
O(k)
```

per query, which is too slow.

---

# Key Idea: Prefix Product

This problem is analogous to the **prefix sum technique**.

### Prefix Sum Example

For sums, we store:

```
prefixSum[i] = sum of elements from index 0 to i
```

Then the sum of the last `k` elements can be computed as:

```
prefixSum[n] - prefixSum[n - k]
```

in constant time.

---

# Prefix Product Concept

Instead of prefix sums, we store **prefix products**.

```
prefixProduct[i] = product of elements from index 0 to i
```

Then the product of the last `k` numbers is:

```
prefixProduct[n] / prefixProduct[n - k]
```

This allows us to answer each query in:

```
O(1)
```

time.

---

# Handling the Zero Problem

Zero breaks prefix products.

Example:

```
3, 4, 0, 2, 5
```

Any product that includes the `0` becomes:

```
0
```

Also division becomes invalid.

### Solution

Whenever we encounter `0`:

- Reset the prefix product list.
- Start fresh with `[1]`.

This ensures calculations remain correct.

---

# Algorithm

## Constructor

```
ProductOfNumbers()
```

1. Initialize `prefixProduct` with `[1]`.
2. Initialize `size = 0`.

---

## add(num)

If `num == 0`:

```
reset prefixProduct = [1]
size = 0
```

Otherwise:

```
prefixProduct.append(prefixProduct[size] * num)
size++
```

---

## getProduct(k)

If:

```
k > size
```

then a `0` occurred in the last `k` elements → return `0`.

Otherwise compute:

```
prefixProduct[size] / prefixProduct[size - k]
```

---

# Implementation

```java
class ProductOfNumbers {

    // Stores cumulative product of the stream
    private ArrayList<Integer> prefixProduct = new ArrayList<>();
    private int size = 0;

    public ProductOfNumbers() {
        // Initialize the product list with 1
        this.prefixProduct.add(1);
        this.size = 0;
    }

    public void add(int num) {

        if (num == 0) {

            // Reset on zero
            this.prefixProduct = new ArrayList<>();
            this.prefixProduct.add(1);
            this.size = 0;

        } else {

            this.prefixProduct.add(
                this.prefixProduct.get(size) * num
            );

            this.size++;
        }
    }

    public int getProduct(int k) {

        if (k > this.size)
            return 0;

        return (
            this.prefixProduct.get(this.size) /
            this.prefixProduct.get(this.size - k)
        );
    }
}
```

---

# Complexity Analysis

Let:

```
n = number of add operations
```

---

## Time Complexity

### add()

Appending to list or resetting list:

```
O(1)
```

### getProduct()

Accessing list and division:

```
O(1)
```

### Total Time

For `n` operations:

```
O(n)
```

---

## Space Complexity

The `prefixProduct` array grows linearly when no `0` appears.

Worst case:

```
O(n)
```

---

# Key Insight

Instead of recomputing products repeatedly:

```
store cumulative products
```

Then compute ranges with:

```
division of prefix products
```

This converts the problem from:

```
O(k) per query
```

to

```
O(1) per query
```
