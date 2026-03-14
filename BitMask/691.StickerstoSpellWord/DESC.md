# 691. Stickers to Spell Word

## Problem Description

We are given **n different types of stickers**. Each sticker contains a **lowercase English word**.

You want to **spell a target string** by cutting individual letters from these stickers and rearranging them.

### Important Rules

- You may **cut individual letters** from a sticker.
- You may **rearrange letters** freely.
- You have **infinite copies of every sticker**.
- The goal is to determine the **minimum number of stickers required** to form the target string.

If it is **impossible** to construct the target string using the available stickers, return **-1**.

---

# Example 1

### Input

```
stickers = ["with", "example", "science"]
target = "thehat"
```

### Output

```
3
```

### Explanation

We can use:

```
2 stickers: "with"
1 sticker: "example"
```

By cutting letters from these stickers and rearranging them, we can form:

```
"thehat"
```

This uses the **minimum number of stickers** required.

---

# Example 2

### Input

```
stickers = ["notice", "possible"]
target = "basicbasic"
```

### Output

```
-1
```

### Explanation

It is **impossible** to form the target `"basicbasic"` using the letters from the given stickers.

---

# Constraints

```
n == stickers.length
```

```
1 <= n <= 50
```

```
1 <= stickers[i].length <= 10
```

```
1 <= target.length <= 15
```

```
stickers[i] and target consist only of lowercase English letters
```

---

# Notes

- Stickers can be used **multiple times**.
- The **order of letters does not matter** since letters can be rearranged.
- The problem asks for the **minimum number of stickers needed** to build the target.
