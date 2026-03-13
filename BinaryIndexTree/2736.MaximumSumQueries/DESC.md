# 2736. Maximum Sum Queries

## Problem Statement

You are given two **0-indexed integer arrays** `nums1` and `nums2`, each of length `n`, and a **1-indexed 2D array** `queries` where:

```
queries[i] = [xi, yi]
```

For the **i-th query**, find the **maximum value of**:

```
nums1[j] + nums2[j]
```

among all indices `j` (`0 <= j < n`) such that:

```
nums1[j] >= xi
nums2[j] >= yi
```

If no such index exists, return `-1`.

Return an array `answer` where:

```
answer[i] = result for the i-th query
```

---

# Example 1

### Input

```
nums1 = [4,3,1,2]
nums2 = [2,4,9,5]
queries = [[4,1],[1,3],[2,5]]
```

### Output

```
[6,10,7]
```

### Explanation

**Query 1**

```
xi = 4, yi = 1
```

Valid indices:

```
j = 0
nums1[0] = 4 >= 4
nums2[0] = 2 >= 1
```

Sum:

```
4 + 2 = 6
```

Maximum = **6**

---

**Query 2**

```
xi = 1, yi = 3
```

Valid indices:

```
j = 2
nums1[2] = 1 >= 1
nums2[2] = 9 >= 3
```

Sum:

```
1 + 9 = 10
```

Maximum = **10**

---

**Query 3**

```
xi = 2, yi = 5
```

Valid index:

```
j = 3
nums1[3] = 2 >= 2
nums2[3] = 5 >= 5
```

Sum:

```
2 + 5 = 7
```

Maximum = **7**

Final Answer:

```
[6,10,7]
```

---

# Example 2

### Input

```
nums1 = [3,2,5]
nums2 = [2,3,4]
queries = [[4,4],[3,2],[1,1]]
```

### Output

```
[9,9,9]
```

### Explanation

Index `j = 2` works for all queries:

```
nums1[2] = 5
nums2[2] = 4
sum = 9
```

Thus the answer for all queries is:

```
[9,9,9]
```

---

# Example 3

### Input

```
nums1 = [2,1]
nums2 = [2,3]
queries = [[3,3]]
```

### Output

```
[-1]
```

### Explanation

No index satisfies:

```
nums1[j] >= 3 AND nums2[j] >= 3
```

Therefore the answer is:

```
[-1]
```

---

# Constraints

```
nums1.length == nums2.length
n == nums1.length
1 <= n <= 10^5

1 <= nums1[i], nums2[i] <= 10^9

1 <= queries.length <= 10^5
queries[i].length == 2

xi == queries[i][0]
yi == queries[i][1]

1 <= xi, yi <= 10^9
```
