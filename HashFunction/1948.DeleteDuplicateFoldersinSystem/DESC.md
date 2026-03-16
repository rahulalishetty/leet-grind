# 1948. Delete Duplicate Folders in System

Due to a bug, there are many duplicate folders in a file system. You are given a **2D array `paths`**, where `paths[i]` is an array representing an **absolute path** to the `i`‑th folder in the file system.

For example:

```
["one", "two", "three"]
```

represents the path:

```
/one/two/three
```

---

## Definition of Identical Folders

Two folders (not necessarily on the same level) are **identical** if they contain the **same non‑empty set of identical subfolders and underlying subfolder structure**.

Important notes:

- The folders do **not** need to be at the root level to be identical.
- If **two or more folders are identical**, then:
  - those folders **and all of their subfolders** are marked for deletion.

---

## Example Explanation

Consider the following file structure:

```
/a
/a/x
/a/x/y
/a/z
/b
/b/x
/b/x/y
/b/z
```

Folders `/a` and `/b` are identical because they contain the same subfolder structure.
Therefore, the following folders are **marked for deletion**:

```
/a
/a/x
/a/x/y
/a/z
/b
/b/x
/b/x/y
/b/z
```

However, if the file system also included:

```
/b/w
```

then `/a` and `/b` would **not** be identical anymore.

Note that even in that case:

```
/a/x
/b/x
```

would still be considered identical.

---

## Important Rule

The file system performs **the deletion only once**.

If new identical folders appear **after deletion**, they are **not deleted again**.

---

## Task

Return the **2D array `ans`** containing the paths of the **remaining folders** after deleting all marked folders.

The order of returned paths **does not matter**.

---

# Examples

## Example 1

**Input**

```
paths = [["a"],["c"],["d"],["a","b"],["c","b"],["d","a"]]
```

**Output**

```
[["d"],["d","a"]]
```

**Explanation**

The file structure contains folders `/a` and `/c` that both contain an empty subfolder named `b`.
Therefore `/a` and `/c` (and their subfolders) are deleted.

---

## Example 2

**Input**

```
paths = [
["a"],["c"],
["a","b"],["c","b"],
["a","b","x"],["a","b","x","y"],
["w"],["w","y"]
]
```

**Output**

```
[["c"],["c","b"],["a"],["a","b"]]
```

**Explanation**

Folders `/a/b/x` and `/w` both contain an empty subfolder named `y`, so they are identical.

They are marked and deleted.

Note:

After deletion `/a` and `/c` become identical, but they are **not removed** because the deletion process runs **only once**.

---

## Example 3

**Input**

```
paths = [["a","b"],["c","d"],["c"],["a"]]
```

**Output**

```
[["c"],["c","d"],["a"],["a","b"]]
```

**Explanation**

All folders are unique in the file system, so nothing is deleted.

---

# Constraints

```
1 <= paths.length <= 2 * 10^4
1 <= paths[i].length <= 500
1 <= paths[i][j].length <= 10
1 <= sum(paths[i][j].length) <= 2 * 10^5
```

Additional constraints:

- `paths[i][j]` consists only of **lowercase English letters**
- No two paths lead to the same folder
- For any folder not at the root level, **its parent folder will also appear in the input**
