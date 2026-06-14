# Intuition Behind the `findMedianSortedArrays` Logic

This is a divide-and-conquer solution for the classic problem:

> Find the median of two sorted arrays without actually merging them.

The key insight is that **finding the median is equivalent to finding the k-th smallest element in the union of the two arrays**.

## Step 1: Think in Terms of the k-th Element

Suppose:

```js
A = [1, 3, 5];
B = [2, 4, 6];
```

Combined sorted order:

```js
[1, 2, 3, 4, 5, 6];
```

Median requires:

```js
k = 2 and k = 3
```

(0-indexed)

So instead of merging everything, the algorithm repeatedly eliminates elements that cannot possibly be the k-th element.

---

## Step 2: The Important Observation

Consider the middle elements of both arrays.

```js
aIndex = middle of A
bIndex = middle of B
```

Example:

```js
A = [1, 3, 5, 7, 9] ^ 5;

B = [2, 4, 6, 8, 10] ^ 6;
```

Here:

```js
aIndex = 2;
bIndex = 2;
```

### What Does `aIndex + bIndex` Mean?

It roughly tells us how many elements lie before these middle elements.

For example:

```js
A: [1,3,5,...]
      ^
index=2
```

There are 2 elements before 5.

Similarly:

```js
B: [2,4,6,...]
      ^
index=2
```

There are 2 elements before 6.

So together:

```js
aIndex + bIndex = 4
```

meaning at least 4 elements are before the larger of the two middle values.

This helps determine whether the k-th element lies:

- in the left side
- in the right side

---

## Case 1: `aIndex + bIndex < k`

Example:

```js
k = 7
aIndex+bIndex = 4
```

The k-th element is farther to the right.

So we need to discard some elements from the left.

Suppose:

```js
aValue = 8;
bValue = 5;
```

Since:

```js
bValue < aValue;
```

every element in B's left half is definitely smaller than 8.

Those elements cannot possibly contain the k-th element because we already know the target is farther right.

So we safely remove:

```js
B[bStart ... bIndex]
```

and recurse on:

```js
bIndex+1 ... bEnd
```

### Why Is That Safe?

If:

```js
aValue > bValue;
```

then everything before `bValue` is guaranteed to be among the smallest elements.

Since:

```js
aIndex + bIndex < k;
```

we know the k-th element is beyond them.

Thus they can be discarded.

---

## Case 2: `aIndex + bIndex >= k`

Example:

```js
k = 3
aIndex+bIndex = 6
```

Now the k-th element lies somewhere on the left.

We need to throw away elements from the right.

Suppose:

```js
aValue = 8;
bValue = 5;
```

Since:

```js
aValue > bValue;
```

the right half of A contains values that are too large.

The k-th element must be before them.

So discard:

```js
A[aIndex ... aEnd]
```

which becomes:

```js
aStart ... aIndex-1
```

---

## Visual Intuition

Suppose:

```js
A = [1, 3, 5, 7, 9];
B = [2, 4, 6, 8, 10];
```

Searching for:

```js
k = 7;
```

Combined order:

```js
[1,2,3,4,5,6,7,8,9,10]
               ^
```

Middle elements:

```js
5 and 6
```

Their positions imply:

```js
aIndex+bIndex = 4
```

Since:

```js
4 < 7;
```

the answer is to the right.

Smaller middle:

```js
5;
```

Therefore all elements before and including 5 can be discarded.

The search space shrinks dramatically.

---

## Why the Strange Base Cases?

Consider:

```js
A segment becomes empty.
```

Example:

```js
A exhausted
B = [10,20,30,40]
```

Now the answer must be inside B.

The code:

```js
return B[k - aStart];
```

adjusts for how many elements of A have already been discarded.

Example:

```js
Original k = 5
```

Suppose:

```js
aStart = 3;
```

meaning 3 elements from A have already been removed.

Therefore the desired element is:

```js
B[k - 3];
```

---

## The Real Intuition

The algorithm repeatedly asks:

> Is the k-th element before or after the two middle elements?

It uses:

```js
aIndex + bIndex < k;
```

to answer that question.

Then:

- If the answer is to the right → remove a left half.
- If the answer is to the left → remove a right half.

And it always removes the half that is guaranteed not to contain the answer by comparing:

```js
aValue;
vs;
bValue;
```

So each recursive call throws away roughly half of one array.

The search space keeps shrinking until one array disappears, at which point the answer is directly available in the other array.

### Complexity

- Merge approach: **O(m + n)**
- Divide-and-conquer approach: **O(log m + log n)** (logarithmic elimination of the search space)

```js
var findMedianSortedArrays = function (A, B) {
  var na = A.length,
    nb = B.length;
  var n = na + nb;

  function solve(A, B, k, aStart, aEnd, bStart, bEnd) {
    // If the segment of one array is empty, it means we have passed all
    // its element, just return the corresponding element in the other array.
    if (aEnd < aStart) {
      return B[k - aStart];
    }
    if (bEnd < bStart) {
      return A[k - bStart];
    }

    // Get the middle indexes and middle values of A and B.
    var aIndex = Math.floor((aStart + aEnd) / 2),
      bIndex = Math.floor((bStart + bEnd) / 2);
    var aValue = A[aIndex],
      bValue = B[bIndex];

    // If k is in the right half of A + B, remove the smaller left half.
    if (aIndex + bIndex < k) {
      if (aValue > bValue) {
        return solve(A, B, k, aStart, aEnd, bIndex + 1, bEnd);
      } else {
        return solve(A, B, k, aIndex + 1, aEnd, bStart, bEnd);
      }
    }
    // Otherwise, remove the larger right half.
    else {
      if (aValue > bValue) {
        return solve(A, B, k, aStart, aIndex - 1, bStart, bEnd);
      } else {
        return solve(A, B, k, aStart, aEnd, bStart, bIndex - 1);
      }
    }
  }

  if (n % 2 == 1) {
    return solve(A, B, Math.floor(n / 2), 0, na - 1, 0, nb - 1);
  } else {
    return (solve(A, B, Math.floor(n / 2 - 1), 0, na - 1, 0, nb - 1) + solve(A, B, Math.floor(n / 2), 0, na - 1, 0, nb - 1)) / 2.0;
  }
};
```
