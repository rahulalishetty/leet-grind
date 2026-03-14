# 331. Verify Preorder Serialization of a Binary Tree — Solutions

## Approach 1: Iteration (Slot Counting)

### Intuition

A binary tree can be viewed as a collection of **slots** where nodes can be placed.

At the beginning:

```
slots = 1
```

This slot is for the **root node**.

Rules:

- Every node (number or `#`) **consumes one slot**
- A **non-null node** creates **two additional slots** for its children
- A **null node (`#`)** creates **no new slots**

If at any moment the number of slots becomes negative, the serialization is invalid.

At the end, all slots must be **exactly used up**.

---

### Algorithm

1. Initialize `slots = 1`
2. Split the string using commas
3. For each node:
   - Consume a slot: `slots -= 1`
   - If `slots < 0` → return `false`
   - If node is not `#`, add two slots: `slots += 2`
4. Serialization is valid if `slots == 0`

---

### Java Implementation

```java
class Solution {

  public boolean isValidSerialization(String preorder) {

    int slots = 1;

    for (String node : preorder.split(",")) {

      slots--;

      if (slots < 0) return false;

      if (!node.equals("#")) {
        slots += 2;
      }
    }

    return slots == 0;
  }
}
```

---

### Complexity Analysis

**Time Complexity**

```
O(N)
```

Where `N` is the length of the string.

**Space Complexity**

```
O(N)
```

Used for storing the split array.

---

# Approach 2: One-Pass Solution (Constant Space)

### Intuition

Instead of splitting the string into an array, we can process the **original string directly**.

Whenever we encounter a comma `,`, we process the preceding node.

Rules remain the same:

- Decrease slot by one
- If the node is not `#`, add two slots

The last node must be processed separately since it is not followed by a comma.

---

### Algorithm

1. Initialize `slots = 1`
2. Iterate through the string
3. When encountering `,`:
   - `slots -= 1`
   - If `slots < 0` → return `false`
   - If the previous character is not `#`, add `2` slots
4. Handle the final node
5. Return `slots == 0`

---

### Java Implementation

```java
class Solution {

  public boolean isValidSerialization(String preorder) {

    int slots = 1;

    int n = preorder.length();

    for (int i = 0; i < n; i++) {

      if (preorder.charAt(i) == ',') {

        slots--;

        if (slots < 0) return false;

        if (preorder.charAt(i - 1) != '#') {
          slots += 2;
        }
      }
    }

    slots = (preorder.charAt(n - 1) == '#') ? slots - 1 : slots + 1;

    return slots == 0;
  }
}
```

---

### Complexity Analysis

**Time Complexity**

```
O(N)
```

We scan the string exactly once.

**Space Complexity**

```
O(1)
```

Only a few variables are used.
