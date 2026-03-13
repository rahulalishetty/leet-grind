# 3187. Peaks in Array

## Problem Statement

A **peak** in an array `arr` is an element that is **strictly greater than both its previous and next elements**.

You are given:

- An integer array `nums`
- A 2D array `queries`

You must process two types of queries.

---

## Query Types

### Type 1 — Count Peaks

```
queries[i] = [1, li, ri]
```

Determine the **number of peak elements** in the subarray:

```
nums[li..ri]
```

---

### Type 2 — Update Element

```
queries[i] = [2, indexi, vali]
```

Update the array:

```
nums[indexi] = vali
```

---

## Important Notes

- The **first and last element of an array (or subarray)** cannot be a peak.
- Only elements that have **both neighbors inside the considered range** can be peaks.

---

# Example 1

### Input

```
nums = [3,1,4,2,5]
queries = [[2,3,4],[1,0,4]]
```

### Output

```
[0]
```

### Explanation

1. Query `[2,3,4]`
   - Update `nums[3] = 4`
   - Array becomes:

   ```
   [3,1,4,4,5]
   ```

2. Query `[1,0,4]`
   - Subarray = `[3,1,4,4,5]`
   - No element is strictly greater than both neighbors

Result:

```
0 peaks
```

---

# Example 2

### Input

```
nums = [4,1,4,2,1,5]
queries = [[2,2,4],[1,0,2],[1,0,4]]
```

### Output

```
[0,1]
```

### Explanation

1. Query `[2,2,4]`
   - `nums[2]` already equals `4`
   - Array remains:

   ```
   [4,1,4,2,1,5]
   ```

2. Query `[1,0,2]`
   - Subarray = `[4,1,4]`
   - The middle element `1` is not greater than neighbors
   - Peaks = `0`

3. Query `[1,0,4]`
   - Subarray = `[4,1,4,2,1]`
   - The second `4` is greater than `1` and `2`
   - Peaks = `1`

Result:

```
[0,1]
```

---

# Constraints

```
3 <= nums.length <= 10^5
1 <= nums[i] <= 10^5
1 <= queries.length <= 10^5
```

Query format:

```
queries[i][0] == 1 or 2
```

For **type 1 queries**:

```
0 <= li <= ri <= nums.length - 1
```

For **type 2 queries**:

```
0 <= indexi <= nums.length - 1
1 <= vali <= 10^5
```
