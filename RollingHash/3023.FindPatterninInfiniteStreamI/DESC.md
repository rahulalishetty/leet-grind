import pypandoc

content = """

# 3023. Find Pattern in Infinite Stream I

## Problem Statement

You are given:

- A binary array `pattern`
- An object `stream` of class **InfiniteStream** representing a **0-indexed infinite stream of bits**

The class `InfiniteStream` contains the following function:

```
int next()
```

This function reads a single bit (either `0` or `1`) from the stream and returns it.

Your task is to **return the first starting index where the pattern matches the bits read from the stream**.

For example, if the pattern is:

```
[1, 0]
```

and the stream is:

```
[0, 1, 0, 1, ...]
```

the first match is the highlighted section:

```
[0, 1, 0, 1, ...]
     ^^^^^
```

which starts at index **1**.

---

# Example 1

### Input

```
stream = [1,1,1,0,1,1,1,...]
pattern = [0,1]
```

### Output

```
3
```

### Explanation

The first occurrence of `[0,1]` appears in:

```
[1,1,1,0,1,...]
       ^^^^
```

The pattern starts at index **3**.

---

# Example 2

### Input

```
stream = [0,0,0,0,...]
pattern = [0]
```

### Output

```
0
```

### Explanation

The pattern `[0]` appears immediately at the start of the stream.

---

# Example 3

### Input

```
stream = [1,0,1,1,0,1,1,0,1,...]
pattern = [1,1,0,1]
```

### Output

```
2
```

### Explanation

The first occurrence of the pattern `[1,1,0,1]` appears in:

```
[1,0,1,1,0,1,...]
     ^^^^^^^^^
```

which begins at index **2**.

---

# Constraints

- `1 <= pattern.length <= 100`
- `pattern` consists only of `0` and `1`
- `stream` consists only of `0` and `1`
- The input is generated such that the pattern's start index exists within the **first 10^5 bits** of the stream.
  """

path = "/mnt/data/3023_find_pattern_in_infinite_stream_i.md"

pypandoc.convert_text(content, "md", format="md", outputfile=path, extra_args=['--standalone'])

path
