# 2355. Maximum Number of Books You Can Take

## Problem Description

You are given a **0-indexed integer array `books`** of length `n`, where:

```
books[i]
```

denotes the **number of books on the i-th shelf** of a bookshelf.

You are going to take books from a **contiguous section of the bookshelf** spanning from:

```
l to r
```

where:

```
0 ≤ l ≤ r < n
```

### Constraint on Books Taken

For each index:

```
l ≤ i < r
```

the number of books taken must satisfy:

```
books taken from shelf i < books taken from shelf i + 1
```

In other words, the number of books taken from the shelves must form a **strictly increasing sequence** from left to right.

### Goal

Return the **maximum number of books** you can take from the bookshelf while satisfying the constraint.

---

# Example 1

## Input

```
books = [8,5,2,7,9]
```

## Output

```
19
```

## Explanation

Take books from shelves:

- Take **1** book from shelf `1`
- Take **2** books from shelf `2`
- Take **7** books from shelf `3`
- Take **9** books from shelf `4`

Total books taken:

```
1 + 2 + 7 + 9 = 19
```

It can be proven that **19 is the maximum number of books** you can take.

---

# Example 2

## Input

```
books = [7,0,3,4,5]
```

## Output

```
12
```

## Explanation

Take books from shelves:

- Take **3** books from shelf `2`
- Take **4** books from shelf `3`
- Take **5** books from shelf `4`

Total books taken:

```
3 + 4 + 5 = 12
```

It can be proven that **12 is the maximum number of books** you can take.

---

# Example 3

## Input

```
books = [8,2,3,7,3,4,0,1,4,3]
```

## Output

```
13
```

## Explanation

Take books from shelves:

- Take **1** book from shelf `0`
- Take **2** books from shelf `1`
- Take **3** books from shelf `2`
- Take **7** books from shelf `3`

Total books taken:

```
1 + 2 + 3 + 7 = 13
```

It can be proven that **13 is the maximum number of books** you can take.

---

# Constraints

```
1 ≤ books.length ≤ 10^5
0 ≤ books[i] ≤ 10^5
```
