# 638. Shopping Offers — Approaches

## Approach 1: Using Recursion

### Key Observations

Before discussing the algorithm, note the following:

1. Whenever we apply a **special offer**, we must **update the needs list** by subtracting the items included in the offer.
2. An offer can only be applied if **all required quantities in the offer are ≤ current needs**.
3. If any item in the offer exceeds the required quantity, that offer **cannot be used**.

---

## Algorithm

We define a recursive function:

```
shopping(price, special, needs)
```

This function returns the **minimum cost** required to satisfy the given `needs`.

### Steps

1. **Base Cost (No Offer)**

Compute the cost of buying all items individually:

```
res = dot(needs, price)
```

2. **Try Each Offer**

For each offer in `special`:

3. **Clone Current Needs**

Create a copy of `needs` so the original list is not modified.

4. **Apply Offer**

For each item type:

```
diff = clone[i] - offer[i]
```

If `diff < 0`, the offer cannot be applied.

Otherwise update:

```
clone[i] = diff
```

5. **Recursive Cost Calculation**

If the offer was valid:

```
res = min(res, offer_price + shopping(price, special, clone))
```

6. Return `res`.

---

## Java Implementation

```java
public class Solution {

    public int shoppingOffers(List<Integer> price,
                              List<List<Integer>> special,
                              List<Integer> needs) {
        return shopping(price, special, needs);
    }

    public int shopping(List<Integer> price,
                        List<List<Integer>> special,
                        List<Integer> needs) {

        int j = 0;
        int res = dot(needs, price);

        for (List<Integer> s : special) {

            ArrayList<Integer> clone = new ArrayList<>(needs);

            for (j = 0; j < needs.size(); j++) {
                int diff = clone.get(j) - s.get(j);

                if (diff < 0)
                    break;

                clone.set(j, diff);
            }

            if (j == needs.size()) {
                res = Math.min(res,
                        s.get(j) + shopping(price, special, clone));
            }
        }

        return res;
    }

    public int dot(List<Integer> a, List<Integer> b) {
        int sum = 0;
        for (int i = 0; i < a.size(); i++) {
            sum += a.get(i) * b.get(i);
        }
        return sum;
    }
}
```

---

## Approach 2: Recursion + Memoization

### Intuition

The same `needs` state can be reached through **different sequences of offers**.

Example:

```
Offer A → Offer B
Offer B → Offer A
```

Both lead to the same `needs` state.

Without memoization, recursion recomputes the same state multiple times.

To avoid this, we use a **HashMap**:

```
Map<needs, minimum_cost>
```

If a `needs` configuration has already been computed, we return the stored result.

---

## Algorithm

1. Use a `HashMap` to cache results.
2. Before recursion:

```
if map contains needs:
    return cached value
```

3. Compute cost exactly as in the recursive solution.
4. Store result in map:

```
map.put(needs, res)
```

5. Return result.

---

## Java Implementation

```java
public class Solution {

    public int shoppingOffers(List<Integer> price,
                              List<List<Integer>> special,
                              List<Integer> needs) {

        Map<List<Integer>, Integer> map = new HashMap<>();
        return shopping(price, special, needs, map);
    }

    public int shopping(List<Integer> price,
                        List<List<Integer>> special,
                        List<Integer> needs,
                        Map<List<Integer>, Integer> map) {

        if (map.containsKey(needs))
            return map.get(needs);

        int j = 0;
        int res = dot(needs, price);

        for (List<Integer> s : special) {

            ArrayList<Integer> clone = new ArrayList<>(needs);

            for (j = 0; j < needs.size(); j++) {
                int diff = clone.get(j) - s.get(j);

                if (diff < 0)
                    break;

                clone.set(j, diff);
            }

            if (j == needs.size()) {
                res = Math.min(res,
                        s.get(j) + shopping(price, special, clone, map));
            }
        }

        map.put(needs, res);
        return res;
    }

    public int dot(List<Integer> a, List<Integer> b) {
        int sum = 0;
        for (int i = 0; i < a.size(); i++) {
            sum += a.get(i) * b.get(i);
        }
        return sum;
    }
}
```

---

## Complexity Analysis

### Without Memoization

Time complexity is **exponential** because every offer combination is explored.

```
O(s^n)
```

Where:

- `s` = number of offers
- `n` = depth of recursion

---

### With Memoization

Each unique `needs` state is solved once.

Maximum states:

```
(need_1 + 1) * (need_2 + 1) * ... * (need_n + 1)
```

Given constraints:

```
needs[i] ≤ 10
n ≤ 6
```

Worst-case states:

```
11^6 ≈ 1.7 million
```

But actual states are far fewer because offers reduce needs quickly.

---

## Key Insight

The important optimization is recognizing that:

```
needs = state
```

If the same state appears again, the cost will always be identical.

Therefore:

```
memoization drastically reduces repeated work.
```
