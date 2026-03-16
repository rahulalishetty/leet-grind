# 354. Russian Doll Envelopes

## Problem Statement

You are given a 2D array of integers **envelopes** where:

```
envelopes[i] = [wi, hi]
```

- `wi` = width of the envelope
- `hi` = height of the envelope

One envelope can fit inside another **only if both dimensions are strictly greater**:

```
w1 < w2  AND  h1 < h2
```

You must determine the **maximum number of envelopes that can be nested (Russian dolled)** inside each other.

⚠️ **Important constraint:**
You **cannot rotate** an envelope.

---

## Goal

Return the **maximum number of envelopes** you can place one inside another.

---

## Example 1

### Input

```
envelopes = [[5,4],[6,4],[6,7],[2,3]]
```

### Output

```
3
```

### Explanation

One optimal nesting sequence is:

```
[2,3] → [5,4] → [6,7]
```

So the maximum number of envelopes that can be nested is **3**.

---

## Example 2

### Input

```
envelopes = [[1,1],[1,1],[1,1]]
```

### Output

```
1
```

### Explanation

All envelopes have identical dimensions, so none can fit inside another.

Thus, the maximum nesting possible is **1**.

---

## Constraints

- `1 <= envelopes.length <= 10^5`
- `envelopes[i].length == 2`
- `1 <= wi, hi <= 10^5`

---

## Key Observations

- Both **width and height must increase** for one envelope to fit into another.
- Envelopes **cannot be rotated**, meaning width must match width comparison and height must match height comparison.
- The problem essentially asks for the **longest chain of envelopes where both dimensions strictly increase**.

---

## Problem Summary

Given many envelopes with width and height:

```
[width, height]
```

Find the **largest sequence** where each envelope fits inside the next.

Example nesting:

```
[w1, h1] → [w2, h2] → [w3, h3] → ...
```

Such that:

```
w1 < w2 < w3 ...
h1 < h2 < h3 ...
```

Return the **maximum length** of such a sequence.
