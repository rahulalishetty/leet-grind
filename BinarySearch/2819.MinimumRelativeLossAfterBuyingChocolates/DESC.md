# 2819. Minimum Relative Loss After Buying Chocolates

## Problem Description

You are given:

- An integer array `prices` representing chocolate prices.
- A 2D integer array `queries` where `queries[i] = [k_i, m_i]`.

Alice and Bob want to buy chocolates together.

For each query:

- Bob must select **exactly `m_i` chocolates**.
- Payment rules for each chocolate with price `p`:

```
If p <= k_i
    Bob pays p
Else
    Bob pays k_i
    Alice pays (p - k_i)
```

Bob wants to minimize his **relative loss**, defined as:

```
relative loss = b_i - a_i
```

Where:

```
b_i = total paid by Bob
a_i = total paid by Alice
```

For each query, return Bob's **minimum possible relative loss**.

---

# Example 1

Input

```
prices = [1,9,22,10,19]
queries = [[18,4],[5,2]]
```

Output

```
[34,-21]
```

Explanation

### Query 1

Bob selects chocolates:

```
[1,9,10,22]
```

Payments:

```
Bob   = 1 + 9 + 10 + 18 = 38
Alice = 0 + 0 + 0 + 4  = 4
```

Relative loss:

```
38 - 4 = 34
```

---

### Query 2

Bob selects:

```
[19,22]
```

Payments:

```
Bob   = 5 + 5 = 10
Alice = 14 + 17 = 31
```

Relative loss:

```
10 - 31 = -21
```

---

# Example 2

Input

```
prices = [1,5,4,3,7,11,9]
queries = [[5,4],[5,7],[7,3],[4,5]]
```

Output

```
[4,16,7,1]
```

Explanation

### Query 1

Chosen chocolates:

```
[1,3,9,11]
```

Payments:

```
Bob   = 1 + 3 + 5 + 5 = 14
Alice = 0 + 0 + 4 + 6 = 10
```

Relative loss:

```
14 - 10 = 4
```

---

### Query 2

Bob must select **all chocolates**.

Payments:

```
Bob   = 1 + 5 + 4 + 3 + 5 + 5 + 5 = 28
Alice = 0 + 0 + 0 + 0 + 2 + 6 + 4 = 12
```

Relative loss:

```
28 - 12 = 16
```

---

### Query 3

Chosen chocolates:

```
[1,3,11]
```

Payments:

```
Bob   = 1 + 3 + 7 = 11
Alice = 0 + 0 + 4 = 4
```

Relative loss:

```
11 - 4 = 7
```

---

### Query 4

Chosen chocolates:

```
[1,3,7,9,11]
```

Payments:

```
Bob   = 1 + 3 + 4 + 4 + 4 = 16
Alice = 0 + 0 + 3 + 5 + 7 = 15
```

Relative loss:

```
16 - 15 = 1
```

---

# Example 3

Input

```
prices = [5,6,7]
queries = [[10,1],[5,3],[3,3]]
```

Output

```
[5,12,0]
```

Explanation

### Query 1

Chosen chocolate:

```
[5]
```

Payments:

```
Bob   = 5
Alice = 0
```

Relative loss:

```
5
```

---

### Query 2

Bob selects all chocolates.

```
Bob   = 5 + 5 + 5 = 15
Alice = 0 + 1 + 2 = 3
```

Relative loss:

```
15 - 3 = 12
```

---

### Query 3

Bob selects all chocolates.

```
Bob   = 3 + 3 + 3 = 9
Alice = 2 + 3 + 4 = 9
```

Relative loss:

```
0
```

---

# Constraints

```
1 <= prices.length = n <= 10^5
```

```
1 <= prices[i] <= 10^9
```

```
1 <= queries.length <= 10^5
```

```
queries[i].length = 2
```

```
1 <= k_i <= 10^9
```

```
1 <= m_i <= n
```
