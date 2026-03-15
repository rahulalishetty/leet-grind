# 3155. Maximum Number of Upgradable Servers

## Problem Description

You have **n data centers** and need to upgrade their servers.

You are given four arrays:

- `count`
- `upgrade`
- `sell`
- `money`

All arrays have length `n`.

For each data center `i`:

- `count[i]` → Number of servers in the data center
- `upgrade[i]` → Cost of upgrading a single server
- `sell[i]` → Money received by selling one server
- `money[i]` → Initial money available

Your task is to determine the **maximum number of servers that can be upgraded** for each data center.

Important constraint:

> Money earned from one data center **cannot be used for another**.

Return an array:

```
answer[i] = maximum number of servers that can be upgraded in data center i
```

---

# Example 1

### Input

```
count   = [4,3]
upgrade = [3,5]
sell    = [4,2]
money   = [8,9]
```

### Output

```
[3,2]
```

### Explanation

**Data Center 1**

If we sell one server:

```
money = 8 + 4 = 12
```

Remaining servers:

```
3
```

Cost to upgrade:

```
3 * 3 = 9
```

Since `12 ≥ 9`, we can upgrade **3 servers**.

---

**Data Center 2**

If we sell one server:

```
money = 9 + 2 = 11
```

Remaining servers:

```
2
```

Cost to upgrade:

```
2 * 5 = 10
```

Since `11 ≥ 10`, we can upgrade **2 servers**.

---

# Example 2

### Input

```
count   = [1]
upgrade = [2]
sell    = [1]
money   = [1]
```

### Output

```
[0]
```

### Explanation

We have one server.

Even if we sell it:

```
money = 1 + 1 = 2
```

There would be **no servers left to upgrade**, so the answer is **0**.

---

# Constraints

```
1 <= count.length == upgrade.length == sell.length == money.length <= 10^5
```

```
1 <= count[i], upgrade[i], sell[i], money[i] <= 10^5
```
