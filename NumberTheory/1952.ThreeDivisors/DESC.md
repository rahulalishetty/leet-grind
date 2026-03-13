# 1952. Three Divisors

## Problem Statement

Given an integer **n**, return **true** if `n` has **exactly three positive divisors**. Otherwise, return **false**.

An integer **m** is a divisor of `n` if there exists an integer `k` such that:

```
n = k * m
```

---

## Examples

### Example 1

**Input**

```
n = 2
```

**Output**

```
false
```

**Explanation**

The divisors of `2` are:

```
1, 2
```

There are only **two divisors**, so the answer is **false**.

---

### Example 2

**Input**

```
n = 4
```

**Output**

```
true
```

**Explanation**

The divisors of `4` are:

```
1, 2, 4
```

There are exactly **three divisors**, so the answer is **true**.

---

## Constraints

```
1 <= n <= 10^4
```

---

## Java Function Signature

```java
class Solution {
    public boolean isThree(int n) {

    }
}
```
