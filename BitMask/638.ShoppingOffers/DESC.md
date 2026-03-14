# 638. Shopping Offers

## Problem Description

In the **LeetCode Store**, there are **n different items** available for purchase.

Each item has:

- a **regular price**
- possible **special bundle offers**

Your goal is to purchase exactly the items listed in **needs** at the **minimum possible price**.

---

## Inputs

### `price`

An integer array where:

```
price[i]
```

represents the price of the **i-th item**.

---

### `needs`

An integer array where:

```
needs[i]
```

represents how many units of the **i-th item** you want to buy.

---

### `special`

A list of special offers.

Each offer is represented as an array of size:

```
n + 1
```

Where:

```
special[i][j]
```

is the number of units of item `j` included in the offer.

The last element:

```
special[i][n]
```

is the **total price of that special offer**.

---

## Rules

- You must buy **exactly the quantities listed in `needs`**
- You **cannot buy extra items**, even if the offer is cheaper
- You may use **each special offer multiple times**
- The goal is to **minimize the total cost**

---

# Example 1

### Input

```
price = [2,5]
special = [[3,0,5],[1,2,10]]
needs = [3,2]
```

### Output

```
14
```

### Explanation

Items:

- A costs `$2`
- B costs `$5`

Special offers:

```
Offer 1 → 3A + 0B for $5
Offer 2 → 1A + 2B for $10
```

Required items:

```
3A and 2B
```

Optimal purchase:

```
Use Offer 2 → 1A + 2B for $10
Buy remaining 2A individually → $4
```

Total cost:

```
10 + 4 = 14
```

---

# Example 2

### Input

```
price = [2,3,4]
special = [[1,1,0,4],[2,2,1,9]]
needs = [1,2,1]
```

### Output

```
11
```

### Explanation

Item prices:

```
A = $2
B = $3
C = $4
```

Offers:

```
Offer 1 → 1A + 1B for $4
Offer 2 → 2A + 2B + 1C for $9
```

Required:

```
1A, 2B, 1C
```

Best option:

```
Use Offer 1 → 1A + 1B = $4
Buy remaining 1B → $3
Buy 1C → $4
```

Total:

```
4 + 3 + 4 = 11
```

Offer 2 cannot be used because it exceeds the required items.

---

# Constraints

```
n == price.length == needs.length
```

```
1 <= n <= 6
```

```
0 <= price[i], needs[i] <= 10
```

```
1 <= special.length <= 100
```

```
special[i].length == n + 1
```

```
0 <= special[i][j] <= 50
```

At least **one item count in each offer is non-zero**.

---
