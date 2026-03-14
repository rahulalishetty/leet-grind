# Convert Sorted List to Binary Search Tree — Detailed Approaches

## Problem Goal

Given a **sorted singly linked list**, convert it into a **height-balanced Binary Search Tree (BST)**.

A height-balanced BST ensures:

```
|height(left) - height(right)| ≤ 1
```

Because the list is **sorted**, it can directly correspond to the **inorder traversal of a BST**.

---

# Approach 1: Recursion (Find Middle Node)

## Intuition

To construct a balanced BST:

- The **middle element** becomes the root.
- Elements **left of the middle** form the left subtree.
- Elements **right of the middle** form the right subtree.

Since this is a **linked list**, we cannot directly index elements.
Instead we use the **fast and slow pointer technique** to find the middle node.

### Two Pointer Technique

```
slow_ptr → moves 1 step
fast_ptr → moves 2 steps
```

When `fast_ptr` reaches the end, `slow_ptr` is at the middle.

---

## Algorithm

1. Find the middle node of the linked list.
2. Disconnect the left half.
3. Create a BST node using the middle value.
4. Recursively build:
   - Left subtree using the left half
   - Right subtree using the right half

---

## Java Implementation

```java
class Solution {

    private ListNode findMiddleElement(ListNode head) {

        ListNode prevPtr = null;
        ListNode slowPtr = head;
        ListNode fastPtr = head;

        while (fastPtr != null && fastPtr.next != null) {
            prevPtr = slowPtr;
            slowPtr = slowPtr.next;
            fastPtr = fastPtr.next.next;
        }

        if (prevPtr != null) {
            prevPtr.next = null;
        }

        return slowPtr;
    }

    public TreeNode sortedListToBST(ListNode head) {

        if (head == null) {
            return null;
        }

        ListNode mid = findMiddleElement(head);

        TreeNode node = new TreeNode(mid.val);

        if (head == mid) {
            return node;
        }

        node.left = sortedListToBST(head);
        node.right = sortedListToBST(mid.next);

        return node;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(N log N)
```

Each recursive step requires finding the middle element.

Work per level:

```
N/2 + 2*(N/4) + 4*(N/8) ...
```

Which results in:

```
O(N log N)
```

### Space Complexity

```
O(log N)
```

Due to recursion stack for a balanced BST.

---

# Approach 2: Convert Linked List to Array

## Intuition

Accessing the middle of a linked list is expensive.

If we **convert the list to an array first**, then:

```
middle index access → O(1)
```

This reduces the total time complexity.

This is a **time–space tradeoff**.

---

## Algorithm

1. Convert linked list → array.
2. Use classic **sorted array → BST** approach.
3. Recursively build BST using middle element.

---

## Java Implementation

```java
class Solution {

    private List<Integer> values = new ArrayList<>();

    private void mapListToValues(ListNode head) {
        while (head != null) {
            values.add(head.val);
            head = head.next;
        }
    }

    private TreeNode convertListToBST(int left, int right) {

        if (left > right) {
            return null;
        }

        int mid = (left + right) / 2;

        TreeNode node = new TreeNode(values.get(mid));

        if (left == right) {
            return node;
        }

        node.left = convertListToBST(left, mid - 1);
        node.right = convertListToBST(mid + 1, right);

        return node;
    }

    public TreeNode sortedListToBST(ListNode head) {

        mapListToValues(head);

        return convertListToBST(0, values.size() - 1);
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(N)
```

Steps:

1. Convert list → array → O(N)
2. Build BST → O(N)

Total:

```
O(N)
```

### Space Complexity

```
O(N)
```

Extra space is required for the array.

---

# Approach 3: Inorder Traversal Simulation

## Key Insight

For a **BST**, the inorder traversal produces:

```
sorted order
```

Since the linked list is already sorted, we can simulate the **inorder construction** of a BST.

Idea:

```
Left subtree
Root (current list node)
Right subtree
```

While building the tree, we **advance the list pointer** exactly in the same order as inorder traversal.

---

## Algorithm

1. Find the **length of the linked list**.
2. Recursively build BST using index range.
3. Recursively build the **left subtree first**.
4. Use current list node as root.
5. Move list pointer forward.
6. Build the **right subtree**.

---

## Java Implementation

```java
class Solution {

    private ListNode head;

    private int findSize(ListNode head) {
        ListNode ptr = head;
        int count = 0;

        while (ptr != null) {
            ptr = ptr.next;
            count++;
        }

        return count;
    }

    private TreeNode convertListToBST(int left, int right) {

        if (left > right) {
            return null;
        }

        int mid = (left + right) / 2;

        TreeNode leftChild = convertListToBST(left, mid - 1);

        TreeNode node = new TreeNode(head.val);
        node.left = leftChild;

        head = head.next;

        node.right = convertListToBST(mid + 1, right);

        return node;
    }

    public TreeNode sortedListToBST(ListNode head) {

        int size = findSize(head);

        this.head = head;

        return convertListToBST(0, size - 1);
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(N)
```

Each list node is processed exactly once.

### Space Complexity

```
O(log N)
```

Only recursion stack space is used.

Since the tree is balanced:

```
height ≈ log N
```

---

# Approach Comparison

| Approach              | Time       | Space    | Notes                      |
| --------------------- | ---------- | -------- | -------------------------- |
| Find Middle Each Time | O(N log N) | O(log N) | Simple but slower          |
| Convert to Array      | O(N)       | O(N)     | Fast but uses extra memory |
| Inorder Simulation    | O(N)       | O(log N) | Optimal solution           |

---

# Recommended Approach

The **Inorder Simulation approach** is usually considered the **optimal solution** because:

- Linear time
- Minimal extra space
- Elegant use of BST properties
