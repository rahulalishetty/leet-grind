# 642. Design Search Autocomplete System

Design a **search autocomplete system** for a search engine.

Users type characters forming a sentence that ends with a special character:

```
#
```

The system must return the **top 3 historical sentences** that share the same prefix as the current input.

---

# Problem Description

You are given:

```
sentences[]
times[]
```

Where:

- `sentences[i]` → previously typed sentence
- `times[i]` → number of times the sentence was typed

The **hot degree** of a sentence is defined as:

```
number of times the exact sentence was previously typed
```

---

# Rules

For each input character **except `#`**, return the **top 3 hot sentences** that match the current prefix.

Sorting rules:

1. Higher **hot degree first**
2. If equal frequency → **ASCII order** (lexicographically smaller first)

If fewer than 3 matches exist → return all matches.

---

# Special Character

When the user types:

```
#
```

This indicates:

- The sentence is complete
- The system stores this sentence
- Return an **empty list**

---

# Class Specification

Implement the class:

```
AutocompleteSystem
```

---

## Constructor

```
AutocompleteSystem(String[] sentences, int[] times)
```

Initializes the system with historical data.

---

## Method

```
List<String> input(char c)
```

Behavior:

- If `c != '#'`
  - Append character to current prefix
  - Return **top 3 matching sentences**

- If `c == '#'`
  - Store the typed sentence
  - Reset prefix
  - Return `[]`

---

# Example

## Input

```
["AutocompleteSystem", "input", "input", "input", "input"]

[[["i love you", "island", "iroman", "i love leetcode"], [5,3,2,2]],
 ["i"],
 [" "],
 ["a"],
 ["#"]]
```

---

## Output

```
[null,
 ["i love you","island","i love leetcode"],
 ["i love you","i love leetcode"],
 [],
 []]
```

---

# Explanation

```
AutocompleteSystem obj =
new AutocompleteSystem(
["i love you","island","iroman","i love leetcode"],
[5,3,2,2]
);
```

---

### Step 1

```
input("i")
```

Prefix:

```
i
```

Matching sentences:

```
i love you (5)
island (3)
iroman (2)
i love leetcode (2)
```

Top 3 after sorting:

```
["i love you","island","i love leetcode"]
```

Note:

```
"i love leetcode" < "iroman"
because space ASCII(32) < r ASCII(114)
```

---

### Step 2

```
input(" ")
```

Prefix:

```
i
```

Matching sentences:

```
i love you
i love leetcode
```

Output:

```
["i love you","i love leetcode"]
```

---

### Step 3

```
input("a")
```

Prefix:

```
i a
```

No matching sentences.

Output:

```
[]
```

---

### Step 4

```
input("#")
```

Sentence completed:

```
"i a"
```

This sentence is stored in the system.

Output:

```
[]
```

---

# Constraints

```
1 <= n <= 100
1 <= sentences[i].length <= 100
1 <= times[i] <= 50
```

Character `c` can be:

```
lowercase letter
space
#
```

Input sentence length:

```
1 to 200 characters
```

Maximum calls:

```
5000 calls to input()
```

---

# Key Design Challenge

Efficiently support:

```
prefix search
ranking by frequency
lexicographic tie-breaking
dynamic updates
```
