# 702. Search in a Sorted Array of Unknown Size

This is an **interactive problem**.

You have a **sorted array of unique elements** and an **unknown size**. You do not have direct access to the array, but you can access it through the `ArrayReader` interface.

You can call:

```
ArrayReader.get(i)
```

This method:

- Returns the value at index `i` (0-indexed) in the hidden array (`secret[i]`)
- Returns `2^31 - 1` if `i` is **out of bounds** of the array

You are also given an integer `target`.

Your task is to return the index `k` such that:

```
secret[k] == target
```

If the target does not exist in the array, return:

```
-1
```

Your algorithm must run in:

```
O(log n)
```

time complexity.

---

# Example 1

**Input**

```
secret = [-1,0,3,5,9,12]
target = 9
```

**Output**

```
4
```

**Explanation**

`9` exists in the array and its index is `4`.

---

# Example 2

**Input**

```
secret = [-1,0,3,5,9,12]
target = 2
```

**Output**

```
-1
```

**Explanation**

`2` does not exist in the array.

---

# Constraints

- `1 <= secret.length <= 10^4`
- `-10^4 <= secret[i], target <= 10^4`
- The array is **strictly increasing**
