# Rolling Hash Function — Deep Dive

## Overview

The `rollingHashCheck` method answers a single question:

> **Does any substring of length `len` appear more than once in `s`?**

If yes, it returns that substring. If no, it returns `null`.

The cleverness is in _how_ it checks — instead of comparing every pair of substrings (which would be O(n²·len)), it uses a **rolling hash** to reduce the check to O(n) time.

---

## 1. Why Hashing?

Comparing two strings of length `len` takes O(len) time. If you naively checked all `n` substrings against each other, that's O(n² · len). For large inputs, this is too slow.

Hashing lets us represent a substring as a single large number. Comparing two numbers is O(1), so we can check `n` substrings in O(n) time — after paying an O(len) setup cost once.

---

## 2. The Hash Formula

Each character `c` at position `i` within a window is weighted by a power of a **base**:

```
h = c[0]·BASE^(len-1) + c[1]·BASE^(len-2) + ... + c[len-1]·BASE^0   (mod MOD)
```

This is essentially treating the substring as a number written in base `BASE`, where each character contributes a digit. In the code, characters are mapped as `'a' → 1`, `'b' → 2`, ..., `'z' → 26` (using `s.charAt(i) - 'a' + 1`) to avoid zero-valued characters causing hash collisions.

### Initial Window Computation

```java
for (int i = 0; i < len; i++) {
    int c = s.charAt(i) - 'a' + 1;
    h1 = (h1 * BASE1 + c) % MOD1;
}
```

Each iteration does `h = h * BASE + c`. Unrolling it manually reveals the polynomial structure:

| After iteration | Value of h                    |
| --------------- | ----------------------------- |
| i=0             | c[0]                          |
| i=1             | c[0]·BASE + c[1]              |
| i=2             | c[0]·BASE² + c[1]·BASE + c[2] |
| ...             | ...                           |

This is **Horner's method** — an efficient way to evaluate a polynomial without computing large powers explicitly.

---

## 3. The "Rolling" Step — O(1) Window Slide

The key insight: when the window slides one position to the right, only two things change:

- The **leftmost character leaves** the window.
- A **new rightmost character enters** the window.

Instead of recomputing the hash from scratch (O(len)), we update it mathematically in O(1):

```
new_h = (old_h * BASE - out_char * BASE^len + in_char)   (mod MOD)
```

### Why each part?

| Operation               | Effect                                                   |
| ----------------------- | -------------------------------------------------------- |
| `old_h * BASE`          | Shifts all existing characters one power higher          |
| `- out_char * BASE^len` | Cancels the contribution of the character that just left |
| `+ in_char`             | Adds the new character at position 0 (power 0)           |

### In code

```java
long pow1 = 1L;
for (int i = 0; i < len; i++) pow1 = pow1 * BASE1 % MOD1;
// pow1 is now BASE^len mod MOD

h1 = (h1 * BASE1 - outChar * pow1 % MOD1 + inChar + MOD1 * 2) % MOD1;
```

`pow1` (i.e., `BASE^len mod MOD`) is precomputed once before the loop and reused in every slide — this is why it's computed upfront.

The `+ MOD1 * 2` term prevents the intermediate value from going negative before taking modulo, since Java's `%` operator can return negative results for negative operands.

---

## 4. Double Hashing — Why Two Hash Functions?

A single hash can produce **collisions**: two different substrings that happen to produce the same hash value. This would be a false positive — we'd think we found a duplicate when we haven't.

The code uses **two independent hash functions simultaneously**:

```java
private static final long BASE1 = 31L,  MOD1 = 1_000_000_007L;
private static final long BASE2 = 37L,  MOD2 = 998_244_353L;
```

Both `MOD1` and `MOD2` are large primes. The probability of a collision on one hash is roughly `1/MOD ≈ 10⁻⁹`. For _both_ to collide simultaneously, the probability drops to roughly `1/(MOD1 · MOD2) ≈ 10⁻¹⁸` — negligible in practice.

The combined key is stored as a string `"h1#h2"`:

```java
String key = h1 + "#" + h2;
```

This packs both hashes into a single HashMap key without any additional data structures.

---

## 5. Collision Verification — The Safety Net

Even with double hashing, to be 100% correct the code verifies any hash match with an actual string comparison:

```java
if (seen.containsKey(key)) {
    int prevStart = seen.get(key);
    String candidate = s.substring(i, i + len);
    if (candidate.equals(s.substring(prevStart, prevStart + len))) {
        return candidate;   // confirmed true duplicate
    }
}
```

This step costs O(len) only when a hash match is found — which is rare. The amortized cost across the whole scan stays O(n).

---

## 6. The HashMap: Storing Only the Last Seen Position

```java
seen.put(key, i);
```

Notice this **overwrites** any previous entry with the same key. This means the map always holds the most recent starting index for each hash. This is intentional — we only need _one_ prior occurrence to confirm a duplicate, and the verification step confirms correctness.

---

## 7. End-to-End Example

Suppose `s = "banana"`, `len = 3`.

| Window  | i   | Hash key   | Action                                              |
| ------- | --- | ---------- | --------------------------------------------------- |
| `"ban"` | 0   | `h("ban")` | store `key → 0`                                     |
| `"ana"` | 1   | `h("ana")` | store `key → 1`                                     |
| `"nan"` | 2   | `h("nan")` | store `key → 2`                                     |
| `"ana"` | 3   | `h("ana")` | **hit!** verify `"ana" == "ana"` ✓ → return `"ana"` |

The hash of `"ana"` at `i=3` matches the stored hash at `i=1`. The string comparison confirms it's a true match, so `"ana"` is returned as the duplicate.

---

## 8. Complexity Summary

| Phase                             | Time           | Space            |
| --------------------------------- | -------------- | ---------------- |
| Precompute `BASE^len`             | O(len)         | O(1)             |
| Hash first window                 | O(len)         | O(1)             |
| Slide window across `n` positions | O(n)           | O(n) for the map |
| **Total per call**                | **O(n + len)** | **O(n)**         |

Since this is called from a binary search over lengths 1 to n, the full `longestDupSubstring` runs in **O(n log n)** time.

---

## 9. Summary of Design Choices

| Choice                           | Reason                                                                      |
| -------------------------------- | --------------------------------------------------------------------------- |
| Polynomial rolling hash          | Enables O(1) window update instead of O(len) recomputation                  |
| Horner's method for initial hash | Avoids computing large powers; naturally iterative                          |
| `'a' → 1` mapping (not 0)        | Prevents leading zeros from collapsing distinct substrings to the same hash |
| Two independent bases/mods       | Reduces collision probability to astronomically low levels                  |
| Verification on hash match       | Guarantees correctness even in the theoretical presence of collisions       |
| Binary search in outer loop      | Reduces O(n) hash checks × O(n) lengths → O(n log n) overall                |
