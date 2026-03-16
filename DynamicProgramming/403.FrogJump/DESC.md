# 403. Frog Jump

## Problem Statement

A frog is crossing a river. The river is divided into units, and at each unit there may or may not be a stone.

The frog can **only land on stones** and must **not jump into the water**.

You are given a list of stone positions in **sorted ascending order**.

```
stones[i] = position of the i-th stone
```

The frog starts on the **first stone**.

---

## Jump Rules

- The frog's **first jump must be exactly 1 unit**.
- If the frog's last jump was **k units**, then the next jump must be:

```
k - 1
k
k + 1
```

- The frog can **only jump forward**.
- The frog must land **exactly on another stone**.

---

## Goal

Determine whether the frog can **reach the last stone**.

Return:

- `true` → if the frog can reach the last stone
- `false` → otherwise

---

## Example 1

### Input

```
stones = [0,1,3,5,6,8,12,17]
```

### Output

```
true
```

### Explanation

One valid sequence of jumps:

```
0 → 1   (jump 1)
1 → 3   (jump 2)
3 → 5   (jump 2)
5 → 8   (jump 3)
8 → 12  (jump 4)
12 → 17 (jump 5)
```

The frog successfully reaches the last stone.

---

## Example 2

### Input

```
stones = [0,1,2,3,4,8,9,11]
```

### Output

```
false
```

### Explanation

The gap between stones `4` and `8` is too large to reach with allowed jump sizes.

Thus, the frog cannot reach the final stone.

---

## Constraints

- `2 <= stones.length <= 2000`
- `0 <= stones[i] <= 2^31 - 1`
- `stones[0] == 0`
- `stones` is sorted in **strictly increasing order**
