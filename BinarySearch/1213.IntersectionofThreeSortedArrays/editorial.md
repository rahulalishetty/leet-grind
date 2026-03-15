# 1213. Intersection of Three Sorted Arrays — Approaches

## Approach 1: Brute Force with HashMap

### Intuition

One of the most straightforward approaches is counting the **frequency of each element** appearing in the three arrays.

Since all three arrays are **strictly increasing**, we know:

- No element appears more than once in the same array.
- Therefore, if a number appears **three times total**, it must appear in **all three arrays**.

We use a **map (counter)** to track how many times each number appears.

---

### Algorithm

1. Create a **map `counter`** to store frequency counts.
2. Iterate through `arr1`, `arr2`, and `arr3` and update the frequency.
3. Iterate through the map and select numbers with frequency **equal to 3**.
4. Add them to the result list.

---

### Implementation

```java
class Solution {

    public List<Integer> arraysIntersection(
        int[] arr1,
        int[] arr2,
        int[] arr3
    ) {
        List<Integer> ans = new ArrayList<>();

        // TreeMap keeps keys sorted
        Map<Integer, Integer> counter = new TreeMap<>();

        for (Integer e : arr1) {
            counter.put(e, counter.getOrDefault(e, 0) + 1);
        }
        for (Integer e : arr2) {
            counter.put(e, counter.getOrDefault(e, 0) + 1);
        }
        for (Integer e : arr3) {
            counter.put(e, counter.getOrDefault(e, 0) + 1);
        }

        for (Integer item : counter.keySet()) {
            if (counter.get(item) == 3) {
                ans.add(item);
            }
        }

        return ans;
    }
}
```

---

### Complexity Analysis

**Time Complexity**

```
O(n)
```

Where `n` is the total number of elements across all arrays.

**Space Complexity**

```
O(n)
```

Because the hashmap stores the elements from the arrays.

---

# Approach 2: Three Pointers

### Intuition

The previous approach **does not use the fact that arrays are sorted**.

Since the arrays are sorted, we can iterate through them **simultaneously using three pointers**.

Pointers:

```
p1 → arr1
p2 → arr2
p3 → arr3
```

At each step:

- If all three values match → store it.
- Otherwise, move the pointer pointing to the **smallest value**.

This works because:

- If a value is smaller than the others, it cannot appear later in the other arrays.

---

### Algorithm

1. Initialize three pointers:

```
p1 = p2 = p3 = 0
```

2. While all pointers are within bounds:

- If:

```
arr1[p1] == arr2[p2] == arr3[p3]
```

Add to result and move all pointers.

- Otherwise:

Move the pointer with the **smallest value**.

---

### Implementation

```java
class Solution {

    public List<Integer> arraysIntersection(
        int[] arr1,
        int[] arr2,
        int[] arr3
    ) {
        List<Integer> ans = new ArrayList<>();

        int p1 = 0, p2 = 0, p3 = 0;

        while (p1 < arr1.length && p2 < arr2.length && p3 < arr3.length) {

            if (arr1[p1] == arr2[p2] && arr2[p2] == arr3[p3]) {
                ans.add(arr1[p1]);
                p1++;
                p2++;
                p3++;
            }
            else {
                if (arr1[p1] < arr2[p2]) {
                    p1++;
                }
                else if (arr2[p2] < arr3[p3]) {
                    p2++;
                }
                else {
                    p3++;
                }
            }
        }

        return ans;
    }
}
```

---

### Complexity Analysis

**Time Complexity**

```
O(n)
```

Where `n` is the total number of elements across the arrays.

Each pointer moves at most once across its array.

**Space Complexity**

```
O(1)
```

Only constant extra space is used.
