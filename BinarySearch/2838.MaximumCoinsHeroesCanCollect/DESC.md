# 2838. Maximum Coins Heroes Can Collect

## Problem Description

There is a battle where **n heroes** fight **m monsters**.

You are given:

- `heroes` → array of hero powers (length `n`)
- `monsters` → array of monster powers (length `m`)
- `coins` → array where `coins[i]` is the reward for defeating monster `i`

All arrays are **1-indexed conceptually** (but implemented normally in code).

---

## Rules

A hero can defeat a monster if:

```
monsters[j] <= heroes[i]
```

Important properties:

- A hero's power **does not decrease** after defeating monsters.
- A hero may defeat **multiple monsters**.
- Multiple heroes can defeat the **same monster**.
- Each monster can be defeated **only once per hero**.

---

## Goal

For each hero `i`, compute the **maximum coins** they can collect.

Return an array:

```
ans[i] = maximum coins hero i can collect
```

---

# Example 1

Input

```
heroes   = [1,4,2]
monsters = [1,1,5,2,3]
coins    = [2,3,4,5,6]
```

Output

```
[5,16,10]
```

### Explanation

Hero 1 (power = 1)

Defeats monsters:

```
[1,2]
```

Coins:

```
2 + 3 = 5
```

---

Hero 2 (power = 4)

Defeats monsters:

```
[1,2,4,5]
```

Coins:

```
2 + 3 + 5 + 6 = 16
```

---

Hero 3 (power = 2)

Defeats monsters:

```
[1,2,4]
```

Coins:

```
2 + 3 + 5 = 10
```

Answer:

```
[5,16,10]
```

---

# Example 2

Input

```
heroes   = [5]
monsters = [2,3,1,2]
coins    = [10,6,5,2]
```

Output

```
[23]
```

Explanation

The hero can defeat **all monsters**.

Coins:

```
10 + 6 + 5 + 2 = 23
```

---

# Example 3

Input

```
heroes   = [4,4]
monsters = [5,7,8]
coins    = [1,1,1]
```

Output

```
[0,0]
```

Explanation

All monsters have power greater than heroes.

So no hero can defeat any monster.

---

# Constraints

```
1 <= n == heroes.length <= 10^5
1 <= m == monsters.length <= 10^5
```

```
coins.length == m
```

```
1 <= heroes[i], monsters[i], coins[i] <= 10^9
```
