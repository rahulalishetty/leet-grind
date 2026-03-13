# 1352. Product of the Last K Numbers

Design an algorithm that accepts a stream of integers and retrieves the **product of the last k integers** of the stream.

---

## Implement the `ProductOfNumbers` class

### Constructor

```
ProductOfNumbers()
```

Initializes the object with an empty stream.

### Methods

```
void add(int num)
```

Appends the integer `num` to the stream.

```
int getProduct(int k)
```

Returns the **product of the last `k` numbers** in the current list.

You can assume that the list always contains **at least `k` numbers** when `getProduct` is called.

---

## Important Notes

The test cases are generated such that:

- The product of any contiguous sequence of numbers **fits into a 32‑bit integer**.
- No overflow will occur.

---

# Example

### Input

```
["ProductOfNumbers","add","add","add","add","add","getProduct","getProduct","getProduct","add","getProduct"]
[[],[3],[0],[2],[5],[4],[2],[3],[4],[8],[2]]
```

### Output

```
[null,null,null,null,null,null,20,40,0,null,32]
```

### Explanation

```
ProductOfNumbers productOfNumbers = new ProductOfNumbers();

productOfNumbers.add(3);        // [3]
productOfNumbers.add(0);        // [3,0]
productOfNumbers.add(2);        // [3,0,2]
productOfNumbers.add(5);        // [3,0,2,5]
productOfNumbers.add(4);        // [3,0,2,5,4]

productOfNumbers.getProduct(2); // return 20
                                // 5 * 4 = 20

productOfNumbers.getProduct(3); // return 40
                                // 2 * 5 * 4 = 40

productOfNumbers.getProduct(4); // return 0
                                // 0 * 2 * 5 * 4 = 0

productOfNumbers.add(8);        // [3,0,2,5,4,8]

productOfNumbers.getProduct(2); // return 32
                                // 4 * 8 = 32
```

---

# Constraints

```
0 <= num <= 100
1 <= k <= 4 * 10^4
```

- At most **4 × 10⁴** calls will be made to `add` and `getProduct`.
- The product of the stream at any time fits in a **32‑bit integer**.

---

# Follow‑Up

Can you implement both:

```
add()
getProduct()
```

so that they run in:

```
O(1) time complexity
```

instead of:

```
O(k)
```
