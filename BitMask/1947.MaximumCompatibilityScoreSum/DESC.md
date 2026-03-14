# 1947. Maximum Compatibility Score Sum

## Problem Description

There is a survey containing **n questions**, where each answer is:

```
0 → No
1 → Yes
```

The survey was given to:

- `m` students
- `m` mentors

Students and mentors are both numbered from:

```
0 to m - 1
```

The answers are represented as:

```
students[i] → answers of student i
mentors[j] → answers of mentor j
```

Each student must be paired with exactly **one mentor**, and each mentor must be paired with exactly **one student**.

---

# Compatibility Score

The **compatibility score** between a student and mentor is defined as:

```
number of answers that match
```

Example:

```
student = [1,0,1]
mentor  = [0,0,1]
```

Matching answers:

```
question 2
question 3
```

Compatibility score:

```
2
```

---

# Objective

Assign students to mentors in a way that **maximizes the total compatibility score**.

Return the **maximum compatibility score sum**.

---

# Example 1

### Input

```
students = [[1,1,0],[1,0,1],[0,0,1]]
mentors  = [[1,0,0],[0,0,1],[1,1,0]]
```

### Output

```
8
```

### Explanation

Optimal assignment:

```
student 0 → mentor 2 (score = 3)
student 1 → mentor 0 (score = 2)
student 2 → mentor 1 (score = 3)
```

Total score:

```
3 + 2 + 3 = 8
```

---

# Example 2

### Input

```
students = [[0,0],[0,0],[0,0]]
mentors  = [[1,1],[1,1],[1,1]]
```

### Output

```
0
```

### Explanation

Every answer differs, so all compatibility scores are:

```
0
```

Total maximum score:

```
0
```

---

# Constraints

```
m == students.length == mentors.length
```

```
n == students[i].length == mentors[j].length
```

```
1 <= m, n <= 8
```

```
students[i][k] ∈ {0,1}
mentors[j][k] ∈ {0,1}
```

---

# Key Observations

This is a **maximum assignment problem**.

We want to match:

```
m students → m mentors
```

such that:

```
total compatibility score is maximized
```

Total possible assignments:

```
m!
```

Since:

```
m ≤ 8
```

we can efficiently solve the problem using:

```
Bitmask Dynamic Programming
Backtracking with pruning
```

---

# Compatibility Score Formula

For each student `i` and mentor `j`:

```
score(i,j) = number of k such that students[i][k] == mentors[j][k]
```

---

# Typical Techniques Used

This problem commonly uses:

```
Bitmask DP
Backtracking
Assignment optimization
State compression
```

---

# Summary

Goal:

```
Pair each student with exactly one mentor
Maximize total compatibility score
```

Key insight:

```
m ≤ 8
```

This allows efficient exploration of assignments using:

```
Bitmask Dynamic Programming
```
