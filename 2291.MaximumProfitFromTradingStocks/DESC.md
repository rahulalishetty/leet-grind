# 2291. Maximum Profit From Trading Stocks

## Problem Description

You are given two **0-indexed integer arrays** of the same length:

- `present`
- `future`

Where:

- `present[i]` represents the **current price** of the _i-th stock_.
- `future[i]` represents the **price of the same stock one year later**.

You may **buy each stock at most once**.

You are also given an integer:

```
budget
```

which represents the amount of money you currently have.

Your goal is to determine the **maximum profit** you can achieve by buying stocks now and selling them after one year.

Return the **maximum profit** that can be obtained.

---

# Example 1

### Input

```
present = [5,4,6,2,3]
future  = [8,5,4,3,5]
budget  = 10
```

### Output

```
6
```

### Explanation

One optimal strategy:

Buy the following stocks:

```
Stock 0 → cost 5
Stock 3 → cost 2
Stock 4 → cost 3
```

Total purchase cost:

```
5 + 2 + 3 = 10
```

Next year sell them for:

```
8 + 3 + 5 = 16
```

Profit:

```
16 - 10 = 6
```

This is the **maximum possible profit**.

---

# Example 2

### Input

```
present = [2,2,5]
future  = [3,4,10]
budget  = 6
```

### Output

```
5
```

### Explanation

The optimal strategy:

```
Buy stock 2
cost = 5
sell next year = 10
profit = 5
```

No better combination exists within the budget.

---

# Example 3

### Input

```
present = [3,3,12]
future  = [0,3,15]
budget  = 10
```

### Output

```
0
```

### Explanation

One possible action:

```
Buy stock 1
cost = 3
sell next year = 3
profit = 0
```

Other stocks either exceed the budget or produce negative profit.

Thus the maximum achievable profit is **0**.

---

# Constraints

```
n == present.length == future.length
1 <= n <= 1000
0 <= present[i], future[i] <= 100
0 <= budget <= 1000
```
