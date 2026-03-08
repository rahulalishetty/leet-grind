# 1230. Toss Strange Coins

## Problem Description

You have some coins. The **i-th coin** has a probability `prob[i]` of landing **heads** when tossed.

You will toss **every coin exactly once**.

Return the **probability that the number of coins showing heads equals `target`**.

Answers will be accepted if they are within **10^-5** of the correct answer.

---

# Examples

## Example 1

**Input**

```
prob = [0.4]
target = 1
```

**Output**

```
0.40000
```

---

## Example 2

**Input**

```
prob = [0.5,0.5,0.5,0.5,0.5]
target = 0
```

**Output**

```
0.03125
```

---

# Constraints

```
1 <= prob.length <= 1000
0 <= prob[i] <= 1
0 <= target <= prob.length
```

---

# Notes

- Each coin toss is **independent**.
- The goal is to compute the probability that **exactly `target` coins show heads**.
- This problem is commonly solved using **dynamic programming on probabilities**.
