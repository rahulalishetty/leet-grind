# 472. Concatenated Words

## Problem Description

You are given an array of **unique strings** called `words`.

Your task is to **return all concatenated words** in the list.

A **concatenated word** is defined as a string that is formed by combining **at least two shorter words** from the same list.

The shorter words used to form the concatenated word:

- Must exist in the given `words` array.
- Can be reused.
- Do **not need to be distinct**.

---

## Definition

A word is considered **concatenated** if:

- It can be constructed by joining **two or more words** from the list.
- Each component word must already exist in the input array.

---

## Example 1

### Input

```
words = ["cat","cats","catsdogcats","dog","dogcatsdog","hippopotamuses","rat","ratcatdogcat"]
```

### Output

```
["catsdogcats","dogcatsdog","ratcatdogcat"]
```

### Explanation

- **"catsdogcats"**
  - cats + dog + cats

- **"dogcatsdog"**
  - dog + cats + dog

- **"ratcatdogcat"**
  - rat + cat + dog + cat

Each of the above words is formed using **two or more smaller words** from the array.

---

## Example 2

### Input

```
words = ["cat","dog","catdog"]
```

### Output

```
["catdog"]
```

### Explanation

```
catdog = cat + dog
```

Thus `"catdog"` is a concatenated word.

---

## Constraints

```
1 <= words.length <= 10^4
1 <= words[i].length <= 30
words[i] consists only of lowercase English letters
All strings in words are unique
1 <= sum(words[i].length) <= 10^5
```

---
