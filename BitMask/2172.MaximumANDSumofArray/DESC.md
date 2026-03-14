# 2172. Maximum AND Sum of Array

## Problem Statement

You are given:

- an integer array `nums` of length `n`
- an integer `numSlots`

There are `numSlots` slots numbered from `1` to `numSlots`.

You must place all numbers from `nums` into these slots under the following rule:

- each slot can contain **at most two numbers**

The **AND sum** of a placement is defined as:

- for every number placed in a slot, compute
  `number AND slotNumber`
- sum all those values

Return the **maximum possible AND sum**.

---

## Example 1

**Input**

```text
nums = [1,2,3,4,5,6], numSlots = 3
```

**Output**

```text
9
```

**Explanation**

One optimal placement is:

- slot `1` -> `[1, 4]`
- slot `2` -> `[2, 6]`
- slot `3` -> `[3, 5]`

AND sum:

```text
(1 AND 1) + (4 AND 1) + (2 AND 2) + (6 AND 2) + (3 AND 3) + (5 AND 3)
= 1 + 0 + 2 + 2 + 3 + 1
= 9
```

---

## Example 2

**Input**

```text
nums = [1,3,10,4,7,1], numSlots = 9
```

**Output**

```text
24
```

**Explanation**

One optimal placement is:

- slot `1` -> `[1, 1]`
- slot `3` -> `[3]`
- slot `4` -> `[4]`
- slot `7` -> `[7]`
- slot `9` -> `[10]`

AND sum:

```text
(1 AND 1) + (1 AND 1) + (3 AND 3) + (4 AND 4) + (7 AND 7) + (10 AND 9)
= 1 + 1 + 3 + 4 + 7 + 8
= 24
```

Slots `2`, `5`, `6`, and `8` are empty, which is allowed.

---

## Constraints

```text
n == nums.length
1 <= numSlots <= 9
1 <= n <= 2 * numSlots
1 <= nums[i] <= 15
```
