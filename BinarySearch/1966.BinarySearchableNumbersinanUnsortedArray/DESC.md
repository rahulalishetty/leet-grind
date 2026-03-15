# 1966. Binary Searchable Numbers in an Unsorted Array

## Problem Description

Consider a function that implements an algorithm similar to **Binary Search**.

The function takes two inputs:

- `sequence`: a sequence of integers
- `target`: an integer value to search for

The purpose of the function is to determine whether the **target exists in the sequence**.

---

## Pseudocode

```
func(sequence, target)
  while sequence is not empty
    randomly choose an element from sequence as the pivot
    if pivot = target, return true
    else if pivot < target, remove pivot and all elements to its left from the sequence
    else, remove pivot and all elements to its right from the sequence
  end while
  return false
```

---

## Important Notes

- If the sequence is **sorted**, the algorithm behaves like standard binary search and works correctly.
- If the sequence is **not sorted**, the algorithm **may fail for some values**, depending on the pivot selections.
- However, for some values the algorithm will **always succeed**, regardless of how pivots are chosen.

Your task is to determine **how many values are guaranteed to be found** for **every possible pivot selection**.

---

## Problem Statement

Given an integer array `nums`:

- The array contains **unique numbers**
- The array **may or may not be sorted**

Return the **number of values that are guaranteed to be found** by the function for **all possible pivot choices**.

---

## Example 1

Input:

```
nums = [7]
```

Output:

```
1
```

Explanation:

Searching for value **7** is guaranteed to succeed.

Since the sequence contains only one element:

- 7 will always be selected as the pivot
- The pivot equals the target
- The function returns **true**.

---

## Example 2

Input:

```
nums = [-1,5,2]
```

Output:

```
1
```

Explanation:

### Searching for value `-1`

The algorithm will always find `-1` regardless of pivot selection.

Possible pivot scenarios:

- Pivot = `-1` → target found immediately.
- Pivot = `5` → elements `[5,2]` removed → sequence becomes `[-1]` → found.
- Pivot = `2` → remove `2` → sequence becomes `[-1,5]` → next pivot always leads to finding `-1`.

Therefore **-1 is guaranteed to be found**.

---

### Searching for value `5`

Not guaranteed.

If pivot `2` is chosen:

- `-1, 5, 2` are removed
- Sequence becomes empty
- Function returns **false**.

---

### Searching for value `2`

Not guaranteed.

If pivot `5` is chosen:

- `5` and `2` are removed
- Sequence becomes `[-1]`
- Target `2` is never found.

---

Therefore only **one number (`-1`) is guaranteed to be found**.

Result:

```
1
```

---

## Constraints

```
1 <= nums.length <= 10^5
-10^5 <= nums[i] <= 10^5
```

- All values in `nums` are **unique**.

---

## Follow-up

If `nums` **contains duplicates**, how would you modify the algorithm?
