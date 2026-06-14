# 1820. Maximum Number of Accepted Invitations

There are **m boys** and **n girls** in a class attending an upcoming party.

You are given an **m x n integer matrix `grid`**, where:

```
grid[i][j] == 1
```

means:

> the **i-th boy can invite the j-th girl** to the party.

Rules:

- Each **boy can invite at most one girl**.
- Each **girl can accept at most one invitation**.
- A girl can only accept an invitation from a boy if `grid[i][j] == 1`.

Your task is to return:

```
The maximum possible number of accepted invitations.
```

---

# Example 1

Input

```
grid = [[1,1,1],
        [1,0,1],
        [0,0,1]]
```

Output

```
3
```

Explanation

The invitations can be arranged as:

- Boy 1 → Girl 2
- Boy 2 → Girl 1
- Boy 3 → Girl 3

Total accepted invitations = **3**.

---

# Example 2

Input

```
grid = [[1,0,1,0],
        [1,0,0,0],
        [0,0,1,0],
        [1,1,1,0]]
```

Output

```
3
```

Explanation

Possible invitations:

- Boy 1 → Girl 3
- Boy 2 → Girl 1
- Boy 4 → Girl 2

Boy 3 invites no one.

Total accepted invitations = **3**.

---

# Constraints

```
grid.length == m
grid[i].length == n

1 <= m, n <= 200
grid[i][j] is either 0 or 1
```
