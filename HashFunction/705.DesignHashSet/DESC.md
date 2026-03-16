# 705. Design HashSet

Design a **HashSet** without using any built-in hash table libraries.

You must implement the `MyHashSet` class with the following operations.

---

## Operations

### `void add(key)`

Insert the value `key` into the HashSet.

### `bool contains(key)`

Return **true** if the value `key` exists in the HashSet, otherwise return **false**.

### `void remove(key)`

Remove the value `key` from the HashSet.

If `key` does not exist in the HashSet, do nothing.

---

# Example

## Input

```
["MyHashSet", "add", "add", "contains", "contains", "add", "contains", "remove", "contains"]
[[], [1], [2], [1], [3], [2], [2], [2], [2]]
```

## Output

```
[null, null, null, true, false, null, true, null, false]
```

## Explanation

```
MyHashSet myHashSet = new MyHashSet();

myHashSet.add(1);      // set = [1]

myHashSet.add(2);      // set = [1, 2]

myHashSet.contains(1); // return true

myHashSet.contains(3); // return false (not found)

myHashSet.add(2);      // set = [1, 2]

myHashSet.contains(2); // return true

myHashSet.remove(2);   // set = [1]

myHashSet.contains(2); // return false (already removed)
```

---

# Constraints

```
0 <= key <= 10^6
At most 10^4 calls will be made to add, remove, and contains
```
